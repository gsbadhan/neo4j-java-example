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
public class Events implements Serializable {
	private Vertex vertex;
	private String guid;
	private String name;
	private Integer category;

	public Events(String guid, String name, Integer category) {
		this.guid = guid;
		this.name = name;
		this.category = category;
	}

	public Events() {
	}

	public Events(Vertex vertex) {
		super();
		this.vertex = vertex;
	}

	public String getGuid() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
	}

	public String getName() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_NAME));
	}

	public String getType() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_TYPE));
	}

	public String getUsername() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_USER_NAME));
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	public String getLabel() {
		return SchemaConstants.LABEL_EVENTS;
	}

	public static Events rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new Events(new Vertex(SchemaConstants.LABEL_EVENTS, map));
	}

}
