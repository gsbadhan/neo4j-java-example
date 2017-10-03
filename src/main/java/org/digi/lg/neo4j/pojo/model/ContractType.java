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
public class ContractType implements Serializable {
	private Vertex vertex;

	public ContractType() {
	}

	public ContractType(Vertex vertex) {
		super();
		this.vertex = vertex;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public String getGuid() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
	}

	public String getName() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_NAME));
	}

	public String getAuthToken() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_AUTH_TOKEN));
	}

	public String getApiKey() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_API_KEY));
	}

	public String getCategory() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_CATEGORY));
	}

	public String getLabel() {
		return SchemaConstants.LABEL_CONTRACT_TYPE;
	}

	public static ContractType rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		ContractType contractType = new ContractType(new Vertex(SchemaConstants.LABEL_CONTRACT_TYPE, map));
		return contractType;
	}

}
