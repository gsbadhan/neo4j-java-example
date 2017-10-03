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

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class Mashup implements Serializable {
	private Vertex vertex;

	private String guid;

	private String name;

	private String path;


	public Mashup() {

	}

	public Mashup(String guidV, String nameV, String pathV) {
		this.guid = guidV;
		this.name = nameV;
		this.path = pathV;
	}

	public Mashup(Vertex vertex) {
		super();
		this.vertex = vertex;
	}

	public String getGuid() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
	}

	public String getName() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_NAME));
	}

	public String getPath() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_PATH));
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
		return SchemaConstants.LABEL_MASHUP;
	}

	public static Mashup rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new Mashup(new Vertex(SchemaConstants.LABEL_MASHUP, map));
	}

}
