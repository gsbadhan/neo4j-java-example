/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package com.digi.lg.neo4j.common;

import static org.digi.lg.neo4j.dao.DaoUtil.getStr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.dao.DaoProvider;
import org.digi.lg.neo4j.domain.services.AuthorizationService;
import org.digi.lg.neo4j.domain.services.DataProviderService;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.pojo.services.LinkPojo;
import org.digi.lg.neo4j.pojo.services.NeighbourPojo;
import org.digi.lg.neo4j.pojo.services.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExploreNeighbourTask2 implements Callable<Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExploreNeighbourTask2.class);
	private final DataProviderService dataProviderService;
	private final AuthorizationService authorizationService;
	private final String userId;
	private final ContractVertex contractVertex;
	private final String appId;
	private final Queue<NeighbourPojo> masterData;
	private final Vertex guidVertex;
	private final Direction direction;
	private final CountDownLatch latch;
	private final Set<String> alreadyVistiedVertices;
	private boolean acrossDomain;

	public ExploreNeighbourTask2(CountDownLatch latch, DataProviderService dataProviderService,
			AuthorizationService authorizationService, String userId, String appId, ContractVertex contractVertex,
			Queue<NeighbourPojo> masterData, Vertex guidVertex, Direction direction, Set<String> alreadyVistiedVertices,
			boolean acrossDomain) {
		this.dataProviderService = dataProviderService;
		this.authorizationService = authorizationService;
		this.userId = userId;
		this.contractVertex = contractVertex;
		this.appId = appId;
		this.masterData = masterData;
		this.guidVertex = guidVertex;
		this.direction = direction;
		this.latch = latch;
		this.alreadyVistiedVertices = alreadyVistiedVertices;
		this.acrossDomain = acrossDomain;
	}

	@Override
	public Void call() {
		try {
			List<Vertex> neighbourVertices = null;
			if (Direction.IN == direction) {
				Vertex vertices = DaoProvider.classDao.getInVertex(null, guidVertex.getLabel(),
						SchemaConstants.PROP_HDMFID_NAME, guidVertex.getGuid(), null, Optional.empty(), false);
				neighbourVertices = vertices.getInVertices();
			} else if (Direction.OUT == direction) {
				Vertex vertices = DaoProvider.classDao.getOutVertex(null, guidVertex.getLabel(),
						SchemaConstants.PROP_HDMFID_NAME, guidVertex.getGuid(), null, Optional.empty(), false);
				neighbourVertices = vertices.getOutVertices();
			} else {
				return null;
			}

			neighbourVertices.forEach(neighbourVertex -> {
				String neighbourGuid = neighbourVertex.getGuid();
				if (alreadyVistiedVertices.contains(neighbourGuid))
					return;
				List<Vertex> authorizedNeighbour = authorizationService.isAuthorized(userId, appId, contractVertex,
						neighbourVertex, acrossDomain);
				if (authorizedNeighbour.isEmpty())
					return;
				Node<Object> neighbourNode = dataProviderService.getNode(neighbourVertex, true);

				Map<String, String> innerEdge = new HashMap<>(1);
				LinkPojo link = new LinkPojo();
				link.setSource(guidVertex.getGuid());
				link.setRelation(neighbourVertex.getRelation().getLabel());
				if (neighbourVertex.getRelation().getProperties() != null)
					neighbourVertex.getRelation().getProperties()
							.forEach((prop, value) -> innerEdge.put(prop, getStr(value)));
				link.setRelationatt(innerEdge);
				link.setDirection(direction.name());
				link.setTarget(neighbourGuid);
				alreadyVistiedVertices.add(neighbourGuid);
				masterData.add(new NeighbourPojo(neighbourNode, link));
			});
		} catch (Exception e) {
			LOGGER.error("error occured in ExploreNeighbourTask2:call()", e);
		} finally {
			latch.countDown();
		}
		return null;
	}

}
