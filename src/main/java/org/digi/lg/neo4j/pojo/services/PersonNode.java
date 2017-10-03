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

public class PersonNode extends Node<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@RequestParam
	public String get$parentId() {
		return (String) get(JsonConstants.$PARENT_ID);
	}

	public PersonNode set$parentId(String parentId) {
		super.put(JsonConstants.$PARENT_ID, parentId);
		return this;
	}

	@RequestParam
	public String getAdminUnitId() {
		return (String) get(JsonConstants.ADMIN_UNIT_ID);
	}

	public PersonNode setAdminUnitId(String adminUnitId) {
		super.put(JsonConstants.ADMIN_UNIT_ID, adminUnitId);
		return this;
	}

	public String getActiveState() {
		return (String) get(JsonConstants.ACTIVE_STATE);
	}

	public PersonNode setActiveState(String activestate) {
		super.put(JsonConstants.ACTIVE_STATE, activestate);
		return this;
	}

	@RequestParam
	public String getPrincipalId() {
		return (String) get(JsonConstants.PRINCIPAL_ID);
	}

	public PersonNode setPrincipalId(String principalId) {
		super.put(JsonConstants.PRINCIPAL_ID, principalId);
		return this;
	}

	@RequestParam
	public String getName() {
		return (String) get(JsonConstants.NAME);
	}

	public PersonNode setName(String name) {
		super.put(JsonConstants.NAME, name);
		return this;
	}

	public String getToken() {
		return (String) get(JsonConstants.AUTH_TOKEN);
	}

	public PersonNode setToken(String token) {
		super.put(JsonConstants.AUTH_TOKEN, token);
		return this;
	}

	@Override
	public String toString() {

		return new Gson().toJson(this);
	}

}
