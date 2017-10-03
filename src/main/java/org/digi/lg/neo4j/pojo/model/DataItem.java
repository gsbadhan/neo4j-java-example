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
public class DataItem implements Serializable {
	private Vertex vertex;

	private String guid;

	private String name;

	private String type;

	private String source;
	private String alias;

	public DataItem() {

	}

	public DataItem(String guidV, String nameV, String typeV, String sourceV) {
		this.guid = guidV;
		this.name = nameV;
		this.type = typeV;
		this.source = sourceV;
	}

	public DataItem(Vertex vertex) {
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

	public String getUnitOfMeasurement() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_UOM));
	}

	public String getFrequencyOfMeasurement() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_UOF));
	}

	public String getMaximunValue() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_MAX_VALUE));
	}

	public String getMinimumValue() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_MIN_VALUE));
	}

	public String getAgg() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_AVG));
	}

	public String getSource() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_SOURCE));
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	public String getLabel() {
		return SchemaConstants.LABEL_DATA_ITEM;
	}

	public static DataItem rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new DataItem(new Vertex(SchemaConstants.LABEL_DATA_ITEM, map));
	}

	public String getAlias() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_ALIAS_NAME));
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
