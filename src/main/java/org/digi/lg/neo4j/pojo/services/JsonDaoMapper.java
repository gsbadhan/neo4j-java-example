/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.pojo.services;

import static org.digi.lg.neo4j.dao.DaoUtil.getLong;
import static org.digi.lg.neo4j.dao.DaoUtil.getStr;

import java.util.HashMap;
import java.util.Map;

import org.digi.lg.neo4j.core.DBToJson;
import org.digi.lg.neo4j.core.JsonConstants;
import org.digi.lg.neo4j.core.JsonToDB;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;

import io.vertx.core.json.JsonObject;

public class JsonDaoMapper {
	private JsonDaoMapper() {
	}

	public static String getDbToJson(String dbColumn) {
		return DBToJson.get(dbColumn);
	}

	public static JsonObject getJsonFromDataShard(Vertex dsVertex) {
		JsonObject dsInfo = new JsonObject();
		dsInfo.put(JsonConstants.SOURCE_NAME, getStr(dsVertex.getNode().get(SchemaConstants.PROP_NAME)));
		dsInfo.put(JsonConstants.SOURCE_GUID, getStr(dsVertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME)));
		dsInfo.put(JsonConstants.MSG_GATEWAY, getStr(dsVertex.getNode().get(SchemaConstants.PROP_MSG_GATEWAY)));
		dsInfo.put(JsonConstants.IP, getStr(dsVertex.getNode().get(SchemaConstants.PROP_IP)));
		dsInfo.put(JsonConstants.PORT, getLong(dsVertex.getNode().get(SchemaConstants.PROP_PORT)));
		dsInfo.put(JsonConstants.API_GATEWAY, getStr(dsVertex.getNode().get(SchemaConstants.PROP_API_GATEWAY)));
		return dsInfo;
	}

	/**
	 * convert JSON K,V to DB compatible parameter map.
	 * 
	 * @param node
	 * @return
	 */
	public static Map<String, Object> getParameterMapFromNode(Node<Object> node) {
		return getParameterMapFromNode(node, new HashMap<>());
	}

	/**
	 * convert JSON K,V to DB compatible parameter map.
	 * 
	 * @param node
	 * @param paramMap
	 * @return
	 */
	public static Map<String, Object> getParameterMapFromNode(Node<Object> node, Map<String, Object> paramMap) {
		node.forEach((k, v) -> {
			if (!k.startsWith("$") && v != null)
				paramMap.put(JsonToDB.get(k), v);
		});
		return paramMap;
	}

	public static Node<Object> getNodeFromVertex(Vertex vertex) {
		Node<Object> node = new Node<>();
		vertex.getNode().forEach((k, v) -> {
			node.put(DBToJson.get(k), v);
		});
		return node;
	}
}
