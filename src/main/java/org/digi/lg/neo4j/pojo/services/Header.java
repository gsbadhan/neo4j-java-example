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
public class Header {

	private String type; // PING,REGSITER,ASSETREGIETER,UPGRADE,EVENT- MONGO DB
	private String id;
	private String v;
	private String gwguid;
	private String gwmn;
	private String gwsn;
	private String gwk;
	private String org;
	private String site;
	private String timestamp;
	private String timeout;
	private String reqId;

	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Header() {
	}

	/**
	 * 
	 * @param id
	 * @param v
	 * @param gwk
	 * @param gwmn
	 * @param gwguid
	 * @param type
	 * @param gwsn
	 */
	public Header(String type, String id, String v, String gwguid, String gwmn, String gwsn, String gwk, String reqId) {
		this.type = type;
		this.id = id;
		this.v = v;
		this.gwguid = gwguid;
		this.gwmn = gwmn;
		this.gwsn = gwsn;
		this.gwk = gwk;
		this.reqId = reqId;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	/**
	 * 
	 * @return The type
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            The type
	 */
	public Header setType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * 
	 * @return The id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            The id
	 */
	public Header setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * 
	 * @return The v
	 */
	public String getV() {
		return v;
	}

	/**
	 * 
	 * @param v
	 *            The v
	 */
	public Header setV(String v) {
		this.v = v;
		return this;
	}

	/**
	 * 
	 * @return The gwguid
	 */
	public String getGwguid() {
		return gwguid;
	}

	/**
	 * 
	 * @param gwguid
	 *            The gwguid
	 */
	public Header setGwguid(String gwguid) {
		this.gwguid = gwguid;
		return this;
	}

	/**
	 * 
	 * @return The gwmn
	 */
	public String getGwmn() {
		return gwmn;
	}

	/**
	 * 
	 * @param gwmn
	 *            The gwmn
	 */
	public Header setGwmn(String gwmn) {
		this.gwmn = gwmn;
		return this;
	}

	/**
	 * 
	 * @return The gwsn
	 */
	public String getGwsn() {
		return gwsn;
	}

	/**
	 * 
	 * @param gwsn
	 *            The gwsn
	 */
	public Header setGwsn(String gwsn) {
		this.gwsn = gwsn;
		return this;
	}

	/**
	 * 
	 * @return The gwk
	 */
	public String getGwk() {
		return gwk;
	}

	/**
	 * 
	 * @param gwk
	 *            The gwk
	 */
	public Header setGwk(String gwk) {
		this.gwk = gwk;
		return this;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public Header setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeOut) {
		this.timeout = timeOut;
	}
}
