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

public class OrgNode extends Node<Object> {

	private static final long serialVersionUID = 1L;

	public String getBootStrapKey() {
		return (String) get(JsonConstants.BOOT_STRAP_KEY);
	}

	public OrgNode setBootStrapKey(String key) {
		super.put(JsonConstants.BOOT_STRAP_KEY, key);
		return this;
	}

	public String getType() {
		return (String) get(JsonConstants.TYPE);
	}

	public OrgNode setType(String $type) {
		super.put(JsonConstants.TYPE, $type);
		return this;
	}

	@RequestParam
	public String getOrgId() {
		return (String) get(JsonConstants.ORG_ID);
	}

	public OrgNode setOrgId(String orgId) {
		super.put(JsonConstants.ORG_ID, orgId);
		return this;
	}

	@RequestParam
	public String get$parentId() {
		return (String) get(JsonConstants.$PARENT_ID);
	}

	public OrgNode set$parentId(String parentId) {
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

	public OrgNode set$classId(String classId) {
		super.put(JsonConstants.$CLASS_ID, classId);
		return this;
	}

	@RequestParam
	public boolean get$model() {
		return (boolean) (get(JsonConstants.$MODEL) != null ? get(JsonConstants.$MODEL) : false);
	}

	public OrgNode set$model(boolean model) {
		super.put(JsonConstants.$MODEL, model);
		return this;
	}

}
