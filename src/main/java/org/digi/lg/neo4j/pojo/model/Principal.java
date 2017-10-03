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

import java.io.Serializable;
import java.util.Map;

import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.dao.DaoUtil;

@SuppressWarnings("serial")
public class Principal implements Serializable {
	private Vertex vertex;

	public Principal() {
	}

	public Principal(Vertex vertex) {
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
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
	}

	public String getName() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_NAME));
	}

	public String getPrincipalId() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_PRINCIPAL_ID));
	}

	public String getLabel() {
		return SchemaConstants.LABEL_PRINCIPAL;
	}

	public static Principal rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new Principal(new Vertex(SchemaConstants.LABEL_PRINCIPAL, map));
	}

}
