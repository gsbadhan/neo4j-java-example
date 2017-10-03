/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.dao;

import static org.digi.lg.neo4j.core.GraphContext.getConfig;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.digi.lg.neo4j.cache.CacheProvider;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.domain.services.AuthorizationService;
import org.digi.lg.neo4j.domain.services.DataProviderService;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.pojo.services.NeighbourPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.lg.neo4j.common.AsyncQueryBaseTask;
import com.digi.lg.neo4j.common.AsyncQueryTaskManager;
import com.digi.lg.neo4j.common.ExploreNeighbourTask2;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

public class ExpandNodeProcess {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpandNodeProcess.class);
	private static final int PAGE_SIZE = Integer.parseInt(getConfig().getOrDefault("max.page.size", 10).toString());
	private static final int CACHE_EXPIRE_TIME = 2;
	private final Queue<NeighbourPojo> masterData = new ConcurrentLinkedQueue<>();
	private Queue<AsyncQueryBaseTask> taskQueue;
	private AsyncQueryTaskManager taskManger;
	private boolean isDone = false;
	private final static LoadingCache<String, ExpandNodeProcess> cache = CacheBuilder.newBuilder().maximumSize(1000)
			.expireAfterAccess(CACHE_EXPIRE_TIME, TimeUnit.MINUTES).build(new CacheLoader<String, ExpandNodeProcess>() {
				@Override
				public ExpandNodeProcess load(String key) throws Exception {
					return null;
				}
			});

	private ExpandNodeProcess(DataProviderService dataProviderService, AuthorizationService authorizationService,
			String appId, String userId, String label, String guid, boolean acrossDomain) {
		this.taskQueue = new LinkedBlockingQueue<>(1);
		this.taskManger = new AsyncQueryTaskManager(taskQueue);
		taskQueue.add(new ExpandNodeTask(dataProviderService, authorizationService, appId, userId, label, guid,
				acrossDomain));
	}

	public static List<NeighbourPojo> get(DataProviderService dataProviderService,
			AuthorizationService authorizationService, String appId, String userId, Optional<String> guidLabel,
			String guid, int pageOffset, boolean acrossDomain) {
		ExpandNodeProcess process = null;
		String label = guidLabel.orElse(null);
		String cacheKey = getCacheKey(userId, appId, label, guid);
		try {
			process = cache.getIfPresent(cacheKey);
			if (process == null) {
				process = new ExpandNodeProcess(dataProviderService, authorizationService, appId, userId, label, guid,
						acrossDomain);
				cache.put(cacheKey, process);
			}
			while (true) {
				if (process.dataAvailable(pageOffset))
					break;
			}

			return process.getMasterData().parallelStream().skip(pageOffset).limit(PAGE_SIZE)
					.collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error("error occured in ExpandNodeProcess:get", e);
		} finally {
			if (process != null && process.getMasterData().isEmpty())
				cache.invalidate(cacheKey);
		}
		return Collections.emptyList();
	}

	private static String getCacheKey(Object... keys) {
		StringBuilder keyBuilder = new StringBuilder();
		for (Object key : keys) {
			keyBuilder.append(key).append(":");
		}
		return keyBuilder.toString();
	}

	private class ExpandNodeTask extends AsyncQueryBaseTask {
		private AuthorizationService authorizationService;
		private DataProviderService dataProviderService;
		private String appId;
		private String userId;
		private String label;
		private String guid;
		private boolean acrossDomain;

		public ExpandNodeTask(DataProviderService dataProviderService, AuthorizationService authorizationService,
				String appId, String userId, String label, String guid, boolean acrossDomain) {
			super(taskManger, "ExpandNodeTask");
			this.authorizationService = authorizationService;
			this.dataProviderService = dataProviderService;
			this.appId = appId;
			this.userId = userId;
			this.label = label;
			this.guid = guid;
			this.acrossDomain = acrossDomain;
		}

		@Override
		public void collect() {
			expandNode(appId, userId, label, guid, acrossDomain);
		}

		@Override
		public void postMerge() {
			if (taskQueue.isEmpty()) {
				isDone = true;
			}
		}

		private void expandNode(String appId, String userId, String guidLabel, String guid, boolean acrossDomain) {
			Set<String> alreadyVistiedVertices = Sets.newConcurrentHashSet();
			try {
				ContractVertex contractVertex = CacheProvider.personAppContractCache.get(userId, appId);
				if (contractVertex == null) {
					LOGGER.info("expandNode: contractInfo not found for userId{},appId{}..!!", userId, appId);
					return;
				}
				ClassX classX = CacheProvider.guidCache.getClassX(label, guid);
				if (classX == null) {
					LOGGER.info("expandNode: data not found for guid{}..!!", guid);
					return;
				}
				Vertex parentVertex = classX.getVertex();
				List<Vertex> authorizeVertex = authorizationService.isAuthorized(userId, appId, contractVertex,
						parentVertex, acrossDomain);
				if (authorizeVertex.isEmpty())
					return;

				alreadyVistiedVertices.add(parentVertex.getGuid());

				expandWorkerPool(appId, userId, contractVertex, parentVertex, alreadyVistiedVertices, acrossDomain);

			} catch (Exception e) {
				LOGGER.error("error occured expandNode: guidLabel:{},appid:{},userid:{},error:{}", guidLabel, appId,
						userId, e);
			}
		}

		private void expandWorkerPool(String appId, String userId, ContractVertex contractVertex, Vertex parentVertex,
				Set<String> alreadyVistiedVertices, boolean acrossDomain) throws InterruptedException {
			ExecutorService executorService = Executors.newWorkStealingPool();
			CountDownLatch latch = new CountDownLatch(2);
			try {
				executorService.submit(new ExploreNeighbourTask2(latch, dataProviderService, authorizationService,
						userId, appId, contractVertex, masterData, parentVertex, Direction.IN, alreadyVistiedVertices,
						acrossDomain));
				executorService.submit(new ExploreNeighbourTask2(latch, dataProviderService, authorizationService,
						userId, appId, contractVertex, masterData, parentVertex, Direction.OUT, alreadyVistiedVertices,
						acrossDomain));
				latch.await();
			} finally {
				executorService.shutdown();
			}
		}

	}

	private Queue<NeighbourPojo> getMasterData() {
		return masterData;
	}

	protected boolean dataAvailable(int offset) throws InterruptedException {
		if (isDone)
			return true;

		if (masterData.size() > offset) {
			return true;
		} else {
			try {
				Thread.sleep(60);
				dataAvailable(offset);
			} catch (InterruptedException e) {
				throw e;
			}
			return true;
		}
	}

}
