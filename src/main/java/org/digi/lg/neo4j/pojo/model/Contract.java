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
import java.util.Optional;

import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.dao.DaoUtil;

@SuppressWarnings("serial")
public class Contract implements Serializable {
	private Vertex vertex;

	public Contract() {
	}

	public Contract(Vertex vertex) {
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

	public String getType() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_TYPE));
	}

	public String getCategory() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_CATEGORY));
	}

	public String getDBUUId() {
		return DaoUtil.getStr(vertex.getNode().get(SchemaConstants.PROP_DB_UUID));
	}

	public Optional<Long> getStartDate() {
		Long startDate = DaoUtil.getLong(vertex.getNode().get(SchemaConstants.PROP_START_DATE));
		return Optional.ofNullable(startDate);
	}

	public Optional<Long> getEndDate() {
		Long endDate = DaoUtil.getLong(vertex.getNode().get(SchemaConstants.PROP_END_DATE));
		return Optional.ofNullable(endDate);
	}

	public boolean isValidContract() {
		return isValidContract(getStartDate(), getEndDate());
	}

	public static boolean isValidContract(Optional<Long> startDate, Optional<Long> endDate) {
		if (!startDate.isPresent() || startDate.get() <= 0)
			return false;
		if (!endDate.isPresent() || endDate.get() <= 0 || startDate.get() > endDate.get())
			return false;
		if (System.currentTimeMillis() > endDate.get())
			return false;
		return true;
	}

	public String getLabel() {
		return SchemaConstants.LABEL_CONTRACT;
	}

	public static Contract rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;
		return new Contract(new Vertex(SchemaConstants.LABEL_CONTRACT, map));
	}

}
