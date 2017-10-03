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

import static org.digi.lg.neo4j.dao.DaoUtil.getLong;
import static org.digi.lg.neo4j.dao.DaoUtil.getStr;

import java.util.Map;

import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;

public class DataShard {
	private Vertex vertex;

	public DataShard() {
	}

	public DataShard(Vertex vertex) {
		super();
		this.vertex = vertex;
	}

	public String getGuid() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
	}

	public String getName() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_NAME));
	}

	public String getMsgGateway() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_MSG_GATEWAY));
	}

	public String getIP() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_IP));
	}

	public String getPort() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_PORT));
	}

	public Long getCount() {
		return getLong(vertex.getNode().get(SchemaConstants.PROP_COUNT));
	}

	public String getLabel() {
		return SchemaConstants.LABEL_DS;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public static DataShard rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new DataShard(new Vertex(SchemaConstants.LABEL_DS, map));
	}
}
