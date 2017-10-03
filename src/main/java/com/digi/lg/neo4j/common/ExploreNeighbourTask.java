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
import java.util.Set;
import java.util.concurrent.Callable;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.domain.services.AuthorizationService;
import org.digi.lg.neo4j.domain.services.DataProviderService;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.pojo.services.LinkPojo;
import org.digi.lg.neo4j.pojo.services.Node;

public class ExploreNeighbourTask implements Callable<Void> {

	private static final String NODES = "nodes";
	private static final String LINKS = "links";
	private final DataProviderService dataProviderService;
	private final AuthorizationService authorizationService;
	private final String userId;
	private final ContractVertex contractVertex;
	private final Vertex neighbourVertex;
	private final String appId;
	private final Set<Vertex> result;
	private final Map<String, Object> nodesMap;
	private final Vertex guidVertex;
	private final Map<String, Node<Object>> nodes;
	private final List<LinkPojo> links;
	private final Direction direction;
	private final boolean acrossDomain;

	public ExploreNeighbourTask(DataProviderService dataProviderService, AuthorizationService authorizationService,
			String userId, String appId, ContractVertex contractVertex, Vertex neighbourVertex,
			Set<Vertex> authorizeVerticesSet, Map<String, Object> nodesMap, Vertex guidVertex,
			Map<String, Node<Object>> nodes, List<LinkPojo> links, Direction direction, boolean acrossDomain) {
		this.dataProviderService = dataProviderService;
		this.authorizationService = authorizationService;
		this.userId = userId;
		this.contractVertex = contractVertex;
		this.neighbourVertex = neighbourVertex;
		this.appId = appId;
		this.result = authorizeVerticesSet;
		this.nodesMap = nodesMap;
		this.guidVertex = guidVertex;
		this.nodes = nodes;
		this.links = links;
		this.direction = direction;
		this.acrossDomain = acrossDomain;
	}

	@Override
	public Void call() throws Exception {
		try {
			boolean neighbourExists = false;
			String neighbourGuid = neighbourVertex.getGuid();

			if (result.contains(neighbourVertex)) {
				neighbourExists = true;
				nodes.put(neighbourGuid, dataProviderService.getNode(neighbourVertex, true));
			} else {
				List<Vertex> neighborChain = authorizationService.isAuthorized(userId, appId, contractVertex,
						neighbourVertex, acrossDomain);
				if (!neighborChain.isEmpty())
					neighbourExists = true;
				neighborChain.forEach(result::add);
			}

			if (!neighbourExists) {
				return null;
			}

			nodes.put(neighbourGuid, dataProviderService.getNode(neighbourVertex, true));

			Map<String, String> innerEdge = new HashMap<>();
			LinkPojo link = new LinkPojo();
			link.setSource(guidVertex.getGuid());
			link.setRelation(neighbourVertex.getRelation().getLabel());
			if (neighbourVertex.getRelation().getProperties() != null)
				neighbourVertex.getRelation().getProperties().keySet().forEach(
						prop -> innerEdge.put(prop, getStr(neighbourVertex.getRelation().getProperties().get(prop))));

			link.setRelationatt(innerEdge);
			link.setDirection(direction.name());
			link.setTarget(neighbourGuid);
			links.add(link);

			nodesMap.put(NODES, nodes.values());
			nodesMap.put(LINKS, links);
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

}
