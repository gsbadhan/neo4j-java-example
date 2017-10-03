/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.cache;

import java.util.Collections;
import java.util.List;

import org.digi.lg.neo4j.core.Vertex;

public class AuthorizeCache extends AbstractCache<String, Object> {

	public AuthorizeCache() {
		super(CacheArea.AUTHORIZED.name());
	}

	@SuppressWarnings("unchecked")
	public List<Vertex> get(String userId, String appId, String contractTypeId, String contractId, String vertexGuid,
			String vertexLabel) {
		List<Vertex> authorizedVertex = (List<Vertex>) super.get(
				getKey(userId, appId, contractTypeId, contractId, vertexGuid, vertexLabel));
		if (authorizedVertex == null || authorizedVertex.isEmpty()) {
			return Collections.emptyList();
		}
		return authorizedVertex;
	}

	public void put(String userId, String appId, String contractTypeId, String contractId, String vertexGuid,
			String vertexLabel, List<Vertex> authorizedVertex) {
		if (authorizedVertex == null || authorizedVertex.isEmpty())
			return;
		super.put(getKey(userId, appId, contractTypeId, contractId, vertexGuid, vertexLabel), authorizedVertex);
	}

	private String getKey(String userId, String appId, String contractTypeId, String contractId, String vertexGuid,
			String vertexLabel) {
		return new StringBuilder(userId).append(appId).append(contractTypeId).append(contractId).append(vertexGuid)
				.append(vertexLabel).toString();
	}

}
