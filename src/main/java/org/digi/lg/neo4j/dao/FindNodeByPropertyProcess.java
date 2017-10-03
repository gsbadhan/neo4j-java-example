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

import static org.digi.lg.neo4j.cache.CacheProvider.guidCache;
import static org.digi.lg.neo4j.core.GraphContext.getConfig;
import static org.digi.lg.neo4j.dao.DaoProvider.baseDao;
import static org.digi.lg.neo4j.dao.DaoProvider.graphReadOnlyFactory;
import static org.digi.lg.neo4j.pojo.services.JsonDaoMapper.getDbToJson;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.digi.lg.neo4j.cache.CacheProvider;
import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.StopException;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.domain.services.AuthorizationService;
import org.digi.lg.neo4j.domain.services.DataProviderService.FindProperty;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.pojo.services.Node;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.lg.neo4j.common.AsyncQueryBaseTask;
import com.digi.lg.neo4j.common.AsyncQueryTaskManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class FindNodeByPropertyProcess {
	private static final Logger LOGGER = LoggerFactory.getLogger(FindNodeByPropertyProcess.class);
	private static final int PAGE_SIZE = Integer.parseInt(getConfig().getOrDefault("max.page.size", 15).toString());
	private static final int STOP_FIND = Integer.parseInt(getConfig().getOrDefault("stop.find.after", 100).toString());
	private static final int CACHE_EXPIRE_TIME = 5;
	private final Queue<Node<Object>> masterData = new ConcurrentLinkedQueue<>();
	private Queue<AsyncQueryBaseTask> taskQueue;
	private AsyncQueryTaskManager taskManger;
	private boolean isDone = false;
	private final static LoadingCache<String, FindNodeByPropertyProcess> cache = CacheBuilder.newBuilder()
			.maximumSize(5000).expireAfterAccess(CACHE_EXPIRE_TIME, TimeUnit.MINUTES)
			.build(new CacheLoader<String, FindNodeByPropertyProcess>() {
				@Override
				public FindNodeByPropertyProcess load(String key) throws Exception {
					return null;
				}
			});

	private FindNodeByPropertyProcess(AuthorizationService authorizationService, String appId, String userId,
			FindProperty property, String propertyValue) {
		this.taskQueue = new LinkedBlockingQueue<>(1);
		this.taskManger = new AsyncQueryTaskManager(taskQueue);
		taskQueue.add(new FindNodeTask(authorizationService, appId, userId, property, propertyValue));
	}

	public static List<Node<Object>> get(AuthorizationService authorizationService, String appId, String userId,
			FindProperty property, String propertyValue, int pageOffset) {
		FindNodeByPropertyProcess process = null;
		String cacheKey = getCacheKey(userId, appId, property, propertyValue);
		try {
			process = cache.getIfPresent(cacheKey);
			if (process == null) {
				process = new FindNodeByPropertyProcess(authorizationService, appId, userId, property, propertyValue);
				cache.put(cacheKey, process);
			}
			while (true) {
				if (process.dataAvailable(pageOffset))
					break;
			}
			return process.getMasterData().parallelStream().skip(pageOffset).limit(PAGE_SIZE)
					.collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error("error occured in FindNodeByPropertyProcess:get", e);
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

	private class FindNodeTask extends AsyncQueryBaseTask {
		private AuthorizationService authorizationService;
		private String appId;
		private String userId;
		private FindProperty property;
		private String propertyValue;

		public FindNodeTask(AuthorizationService authorizationService, String appId, String userId,
				FindProperty property, String propertyValue) {
			super(taskManger, "findNodeTask");
			this.authorizationService = authorizationService;
			this.appId = appId;
			this.userId = userId;
			this.property = property;
			this.propertyValue = propertyValue;
		}

		@Override
		public void collect() {
			Session session = null;
			try {
				ContractVertex contractVertex = CacheProvider.personAppContractCache.get(userId, appId);
				if (contractVertex == null) {
					LOGGER.debug("contractInfo found for userId {},appId {}", userId, appId);
					return;
				}
				String query = null;
				Map<String, Object> bindParams = new HashMap<>(2);
				switch (property) {
				case GUID:
					query = "MATCH (cm:class_meta) WHERE LOWER(cm.guidx) CONTAINS LOWER({guid}) RETURN cm.labelx as lbl,cm.guidx as guid";
					bindParams.put(BindConstants.PROP_HDMFID_NAME, propertyValue);
					break;
				case NAME:
					query = "MATCH (cm:class_meta) WHERE LOWER(cm.namex) CONTAINS LOWER({name}) RETURN cm.labelx as lbl,cm.guidx as guid";
					bindParams.put(BindConstants.PROP_NAME, propertyValue);
					break;
				case LABEL:
					query = "MATCH (cm:class_meta) WHERE LOWER(cm.labelx) CONTAINS LOWER({label}) RETURN cm.labelx as lbl,cm.guidx as guid";
					bindParams.put(BindConstants.PROP_LABEL, propertyValue);
					break;
				case PRINCIPAL_ID:
					query = "MATCH (p:principal) WHERE LOWER(p.principal_id) CONTAINS LOWER({principalId}) RETURN p.guid as guid,LABELS(p)[0] as lbl";
					bindParams.put(BindConstants.PROP_PRINCIPAL_ID, propertyValue);
					break;
				case SERIAL_NUMBER:
					query = "MATCH (at:asset) WHERE LOWER(at.serial_number) CONTAINS LOWER({serialNumber}) RETURN at.guid as guid,LABELS(at)[0] as lbl";
					bindParams.put(BindConstants.PROP_SERIAL_NUMBER, propertyValue);
					break;
				case ANY:
					LOGGER.info("searching property from full-graph..");
					query = "MATCH (n) WHERE LOWER(n.property_x) CONTAINS LOWER({propertyX}) AND NOT (n:class_meta) WITH labels(n)[0] as lbl,n.guid as guid WHERE lbl IS NOT NULL RETURN lbl,guid"
							.replaceFirst(SchemaConstants.PROPERTY_X, property.getColumn());
					bindParams.put(BindConstants.PROPERTY_X, propertyValue);
					break;
				}
				session = graphReadOnlyFactory.readSession();
				StatementResult rst = baseDao.executeQueryx(session, query, bindParams);
				rst.forEachRemaining(record -> {
					ClassX classX = guidCache.getClassX(record.get("lbl").asString(), record.get("guid").asString());
					if (classX == null)
						return;
					List<Vertex> isVertexAuthorized = authorizationService.isAuthorized(userId, appId, contractVertex,
							classX.getVertex(), false);
					if (!isVertexAuthorized.isEmpty()) {
						Vertex vertex = isVertexAuthorized.get(0);
						Node<Object> tNode = new Node<>();
						vertex.getNode().forEach((k, v) -> {
							if (k.equals(SchemaConstants.PROP_DB_UUID))
								return;
							tNode.put(getDbToJson(k), v);
							tNode.put(SchemaConstants.LABEL, getDbToJson(vertex.getLabel()));
						});
						masterData.add(tNode);
						if (masterData.size() >= STOP_FIND)
							throw new StopException("stop finding,reached limt[" + STOP_FIND
									+ "], refine your search or increase `stop.find.after` !");
					}
				});
			} catch (StopException ste) {
				LOGGER.info("findNodeTask:", ste.getMessage());
			} catch (Exception e) {
				LOGGER.error("error in findNodeTask", e);
			} finally {
				graphReadOnlyFactory.closeSession(session);
			}
		}

		@Override
		public void postMerge() {
			if (taskQueue.isEmpty()) {
				isDone = true;
			}
		}

	}

	private Queue<Node<Object>> getMasterData() {
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
