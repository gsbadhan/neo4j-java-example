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

public class ContractTypeNode extends Node<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getType() {
		return (String) get(JsonConstants.TYPE);
	}

	public ContractTypeNode setType(String $type) {
		super.put(JsonConstants.TYPE, $type);
		return this;
	}

	public String getAuthToken() {
		return (String) get(JsonConstants.AUTH_TOKEN);
	}

	public ContractTypeNode setAuthToken(String AuthToken) {
		super.put(JsonConstants.AUTH_TOKEN, AuthToken);
		return this;
	}

	public String getApikey() {
		return (String) get(JsonConstants.API_KEY);
	}

	public ContractTypeNode setApikey(String $apikey) {
		super.put(JsonConstants.API_KEY, $apikey);
		return this;
	}
	public String getMaxDI() {
		return (String) get(JsonConstants.MAX_DATA_ITEM);
	}

	public ContractTypeNode setMaxDI(String $maxdi) {
		super.put(JsonConstants.MAX_DATA_ITEM, $maxdi);
		return this;
	}
	public String getMaxAssetSession() {
		return (String) get(JsonConstants.MAX_CONCURRENT_ASSET_SESSIONS);
	}

	public ContractTypeNode setMaxAssetSession(String $MaxAssetSession) {
		super.put(JsonConstants.MAX_CONCURRENT_ASSET_SESSIONS, $MaxAssetSession);
		return this;
	}
	
	public String getMaxAPISession() {
		return (String) get(JsonConstants.MAX_CONCURRENT_ASSET_SESSIONS);
	}

	public ContractTypeNode setMaxAPISession(String $MaxAPISession) {
		super.put(JsonConstants.MAX_CONCURRENT_API_SESSIONS, $MaxAPISession);
		return this;
	}
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	@RequestParam
	public String get$contractId() {
		return (String) get(JsonConstants.$CONTRACT_ID);
	}

	public ContractTypeNode set$contractId(String contractId) {
		super.put(JsonConstants.$CONTRACT_ID, contractId);
		return this;
	}

	public String getName() {
		return (String) get(JsonConstants.NAME);
	}

	public ContractTypeNode setName(String name) {
		super.put(JsonConstants.NAME, name);
		return this;
	}

}
