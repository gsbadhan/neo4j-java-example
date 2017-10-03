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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.google.gson.Gson;

@Generated("org.jsonschema2pojo")
public class AgentMessage {

	private Header header;
	private Body body;
	private Map<String, Object> additionalProperties = new HashMap<>();

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public AgentMessage() {
	}

	/**
	 * 
	 * @param properties
	 * @param header
	 */
	public AgentMessage(Header header, Body body) {
		this.header = header;
		this.body = body;
	}

	/**
	 * 
	 * @return The header
	 */
	public Header getHeader() {
		return header;
	}

	/**
	 * 
	 * @param header
	 *            The header
	 */
	public AgentMessage setHeader(Header header) {
		this.header = header;
		return this;
	}

	/**
	 * 
	 * @return The properties
	 */
	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	/**
	 * 
	 * @param properties
	 *            The properties
	 */
	public AgentMessage setProperties(Body properties) {
		this.body = properties;
		return this;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public AgentMessage setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	/**
	 * @param agentMessage
	 * @return
	 */
	public List<String> retrieveClassFromMessage() {
		List<AssetNode> assets = this.getBody().getAssets();
		if (assets == null || assets.isEmpty())
			return Collections.emptyList();
		List<String> classNamesList = new ArrayList<>();
		AssetNode asset = assets.get(0);
		List<Classes> classes = asset.getClasses();
		if (classes == null || classes.isEmpty())
			return Collections.emptyList();
		classes.forEach(clas -> classNamesList.add(clas.getName()));
		return classNamesList;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public static AgentMessage result() {
		return new AgentMessage(new Header().setGwk(""), null);
	}

}
