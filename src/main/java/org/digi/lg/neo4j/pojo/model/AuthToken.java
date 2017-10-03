/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.pojo.model;

import static org.digi.lg.neo4j.dao.DaoUtil.getStr;

import java.io.Serializable;
import java.util.Map;

import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;

@SuppressWarnings("serial")
public class AuthToken implements Serializable {
	private Vertex vertex;

	public AuthToken() {
	}

	public AuthToken(Vertex vertex) {
		super();
		this.vertex = vertex;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	public String getGuid() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
	}

	public String getLabel() {
		return SchemaConstants.LABEL_AUTH_TOKEN;
	}

	public static AuthToken rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new AuthToken(new Vertex(SchemaConstants.LABEL_AUTH_TOKEN, map));
	}

}
