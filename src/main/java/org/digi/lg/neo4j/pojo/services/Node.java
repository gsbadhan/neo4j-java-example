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

import org.digi.lg.neo4j.core.JsonConstants;

public class Node<V> extends HashMap<String, V> {

	private static final long serialVersionUID = 1L;

	protected V guid;
	protected V name;
	protected V startDate;
	protected V endDate;
	protected V token;
	protected V label;
	protected V principalId;

	public V getStartdate() {
		return startDate;
	}

	public V getToken() {
		return token;
	}

	public Node<V> setToken(V token) {
		super.put(JsonConstants.AUTH_TOKEN, token);
		return this;
	}

	public Node<V> setStartdate(V startdate) {
		super.put(JsonConstants.START_DATE, startdate);
		return this;
	}

	public V getEnddate() {
		return endDate;
	}

	public Node<V> setEnddate(V enddate) {
		super.put(JsonConstants.END_DATE, enddate);
		return this;
	}

	public V getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(V principalId) {
		this.principalId = principalId;
	}

	public V getGuid() {
		return get(JsonConstants.GUID);
	}

	public Node<V> setGuid(V guid) {
		super.put(JsonConstants.GUID, guid);
		return this;
	}

	public V getName() {
		return get(JsonConstants.NAME);
	}

	public Node<V> setName(V name) {
		super.put(JsonConstants.NAME, name);
		return this;
	}

	public V getLabel() {
		return get(JsonConstants.LABEL);
	}

	public Node<V> setLabel(V label) {
		super.put(JsonConstants.LABEL, label);
		return this;
	}

	public Node<V> putX(String arg0, V arg1) {
		super.put(arg0, arg1);
		return this;
	}

	/**
	 * Returns a clone of node instance only with properties/tags by removing
	 * service argument properties (whose name starts with "$")
	 * 
	 * @return
	 */
	public Node<V> onlyProps() {
		Node<V> newNode = new Node<>();
		this.forEach((k, v) -> {
			if (!k.startsWith("$")) {
				newNode.put(k, v);
			}
		});
		return newNode;
	}

}
