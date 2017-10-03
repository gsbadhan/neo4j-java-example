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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Body {

	private List<AssetNode> assets = new ArrayList<AssetNode>();
	private AssetNode gateway;
	private String source;
	private String destination;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private List<ConnectionLinks> connectionLinks = new ArrayList<ConnectionLinks>();
	private List<Unlink> unlink = new ArrayList<Unlink>();

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Body() {
	}

	/**
	 * 
	 * @param assets
	 * @param gateway
	 */
	public Body(List<AssetNode> assets, AssetNode gateway) {
		this.assets = assets;
		this.gateway = gateway;
	}

	/**
	 * 
	 * @return The assets
	 */
	public List<AssetNode> getAssets() {
		return assets;
	}

	/**
	 * 
	 * @param assets
	 *            The assets
	 */
	public void setAssets(List<AssetNode> assets) {
		this.assets = assets;
	}

	/**
	 * 
	 * @return The gateway
	 */
	public AssetNode getGateway() {
		return gateway;
	}

	/**
	 * 
	 * @param gateway
	 *            The gateway
	 */
	public void setGateway(AssetNode gateway) {
		this.gateway = gateway;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public void setAdditionalProperty(Map<String, Object> value) {
		this.additionalProperties.putAll(value);
	}

	public List<ConnectionLinks> getConnectionLinks() {
		return connectionLinks;
	}

	public void setConnectionLinks(List<ConnectionLinks> connectionLinks) {
		this.connectionLinks = connectionLinks;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public List<Unlink> getUnlink() {
		return unlink;
	}

	public void setUnlink(List<Unlink> unlink) {
		this.unlink = unlink;
	}

}
