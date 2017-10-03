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

import org.digi.lg.neo4j.core.JsonConstants;

import com.google.gson.Gson;

public class GraphNode extends Node<Object> {
	private static final long serialVersionUID = 1L;

	public String getType() {
		return (String) get(JsonConstants.TYPE);
	}

	public GraphNode setType(String $type) {
		super.put(JsonConstants.TYPE, $type);
		return this;
	}

	public String getUnitOfMeasurement() {
		return (String) get(JsonConstants.UNIT_OF_MEASUREMENT);
	}

	public GraphNode setUnitOfMeasurement(String $UnitOfMeasurement) {
		super.put(JsonConstants.UNIT_OF_MEASUREMENT, $UnitOfMeasurement);
		return this;
	}

	public String getFrequencyOfMeasurement() {
		return (String) get(JsonConstants.FREQUENCY_OF_MEASUREMENT);
	}

	public GraphNode setgetFrequencyOfMeasurement(String $getFrequencyOfMeasurement) {
		super.put(JsonConstants.FREQUENCY_OF_MEASUREMENT, $getFrequencyOfMeasurement);
		return this;
	}

	public String getMaximunValue() {
		return (String) get(JsonConstants.MAXIMUM_VALUE);
	}

	public GraphNode setMaximunValue(String $MaximunValue) {
		super.put(JsonConstants.MAXIMUM_VALUE, $MaximunValue);
		return this;
	}

	public String getMinimumValue() {
		return (String) get(JsonConstants.MINIMUM_VALUE);
	}

	public GraphNode setMinimumValue(String $MinimumValue) {
		super.put(JsonConstants.MINIMUM_VALUE, $MinimumValue);
		return this;
	}

	@RequestParam
	public String get$parentId() {
		return (String) get(JsonConstants.$PARENT_ID);
	}

	public GraphNode set$parentId(String parentId) {
		super.put(JsonConstants.$PARENT_ID, parentId);
		return this;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	@RequestParam
	public String get$classId() {
		return (String) get(JsonConstants.$CLASS_ID);
	}

	public GraphNode set$classId(String classId) {
		super.put(JsonConstants.$CLASS_ID, classId);
		return this;
	}

}
