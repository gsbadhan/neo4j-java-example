/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.pojo.model;

import java.util.Map;

public class ClassMeta {
	private String guid;
	private String name;
	private String label;
	private String textSearch;

	public ClassMeta() {
	}

	public ClassMeta(String guid, String name, String label, String textSearch) {
		super();
		this.guid = guid;
		this.name = name;
		this.label = label;
		this.textSearch = textSearch;
	}

	public ClassMeta(Object guid, Object name, Object label, Object textSearch) {
		super();
		this.guid = guid != null ? guid.toString() : null;
		this.name = name != null ? name.toString() : null;
		this.label = label != null ? label.toString() : null;
		this.textSearch = textSearch != null ? textSearch.toString() : null;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTextSearch() {
		return textSearch;
	}

	public void setTextSearch(String textSearch) {
		this.textSearch = textSearch;
	}

	public static ClassMeta rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new ClassMeta(map.get("guid"), map.get("name"), map.get("lbl"), map.get("txt_search"));
	}
}
