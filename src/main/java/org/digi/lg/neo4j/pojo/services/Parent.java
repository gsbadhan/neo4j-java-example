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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Parent {

	private String parentGuid;
	private String asset;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Parent() {
	}

	/**
	 * 
	 * @param parentguid
	 */
	public Parent(String parentguid) {
		this.parentGuid = parentguid;
	}

	/**
	 * 
	 * @return The parentguid
	 */
	public String getParentguid() {
		return parentGuid;
	}

	/**
	 * 
	 * @param parentguid
	 *            The parentguid
	 */
	public void setParentguid(String parentguid) {
		this.parentGuid = parentguid;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

}
