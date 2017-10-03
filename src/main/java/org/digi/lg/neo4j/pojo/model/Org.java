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
public class Org implements Serializable {
	private Vertex vertex;

	public Org() {
	}

	public Org(Vertex vertex) {
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

	public String getBootStrapKey() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_BOOT_STRAP_KEY));
	}

	public String getCategory() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_CATEGORY));
	}

	public String getLabel() {
		return SchemaConstants.LABEL_ORG;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	public static Org rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new Org(new Vertex(SchemaConstants.LABEL_ORG, map));
	}

}
