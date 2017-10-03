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
public class ScriptTemplate implements Serializable {
	private Vertex vertex;

	public ScriptTemplate() {
	}

	public ScriptTemplate(Vertex vertex) {
		super();
		this.vertex = vertex;
	}

	public String getGuid() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
	}

	public String getName() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_NAME));
	}

	public String getType() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_TYPE));
	}

	public String getScope() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_SCOPE));
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	public String getLabel() {
		return SchemaConstants.LABEL_SCRIPT_TEMPLATE;
	}

	public static ScriptTemplate rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new ScriptTemplate(new Vertex(SchemaConstants.LABEL_SCRIPT_TEMPLATE, map));
	}

}
