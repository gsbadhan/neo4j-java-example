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
public class Asset implements Serializable {
	private Vertex vertex;

	public Asset() {
	}

	public Asset(Vertex vertex) {
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

	public String getSerialNumber() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_SERIAL_NUMBER));
	}

	public String getUsername() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_USER_NAME));
	}

	public String getDBUUID() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_DB_UUID));
	}

	public String getRegDate() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_REGDATE));
	}

	public String getMaxAssetCount() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.MAX_ASSET_COUNT));
	}

	public String getCurrentAssetCount() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.CURRENT_ASSET_COUNT));
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	public String getLabel() {
		return SchemaConstants.LABEL_ASSET;
	}

	public static Asset rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new Asset(new Vertex(SchemaConstants.LABEL_ASSET, map));
	}

}
