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
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class AssetNode {

	private String name;
	private String org;
	private String site;
	private Parent parent;
	private String username;
	private String serialNumber;
	private String guid;
	private List<Classes> classes = new ArrayList<>();
	private List<Map<String, Object>> properties = new ArrayList<>();
	private List<Map<String, Object>> events = new ArrayList<>();
	private List<Map<String, Object>> dataitems = new ArrayList<>();
	private List<Map<String, Object>> rawmessages = new ArrayList<>();
	private ProductClass model;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public AssetNode() {
	}

	/**
	 * 
	 * @param classes
	 * @param site
	 * @param name
	 * @param parent
	 * @param org
	 */
	public AssetNode(String name, String org, String site, Parent parent, List<Classes> classes) {
		this.name = name;
		this.org = org;
		this.site = site;
		this.parent = parent;
		this.classes = classes;
	}

	public AssetNode(String name, String org, String site, Parent parent, List<Classes> classes, ProductClass model) {
		this.name = name;
		this.org = org;
		this.site = site;
		this.parent = parent;
		this.classes = classes;
		this.model = model;
	}

	/**
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return The org
	 */
	public String getOrg() {
		return org;
	}

	/**
	 * 
	 * @param org
	 *            The org
	 */
	public void setOrg(String org) {
		this.org = org;
	}

	/**
	 * 
	 * @return The site
	 */
	public String getSite() {
		return site;
	}

	/**
	 * 
	 * @param site
	 *            The site
	 */
	public void setSite(String site) {
		this.site = site;
	}

	/**
	 * 
	 * @return The parent
	 */
	public Parent getParent() {
		return parent;
	}

	/**
	 * 
	 * @param parent
	 *            The parent
	 */
	public void setParent(Parent parent) {
		this.parent = parent;
	}

	/**
	 * 
	 * @return The models
	 */
	public List<Classes> getClasses() {
		return classes;
	}

	/**
	 * 
	 * @param classes
	 *            The models
	 */
	public void setClasses(List<Classes> classes) {
		this.classes = classes;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public List<Map<String, Object>> getEvents() {
		return events;
	}

	public void setEvents(List<Map<String, Object>> events) {
		this.events = events;
	}

	public List<Map<String, Object>> getProperties() {
		return properties;
	}

	public void setProperties(List<Map<String, Object>> properties) {
		this.properties = properties;
	}

	public List<Map<String, Object>> getDataitems() {
		return dataitems;
	}

	public void setDataitems(List<Map<String, Object>> dataitems) {
		this.dataitems = dataitems;
	}

	public List<Map<String, Object>> getRawmessages() {
		return rawmessages;
	}

	public void setRawmessages(List<Map<String, Object>> rawmessages) {
		this.rawmessages = rawmessages;
	}

	public ProductClass getModel() {
		return model;
	}

	public void setModel(ProductClass model) {
		this.model = model;
	}

}
