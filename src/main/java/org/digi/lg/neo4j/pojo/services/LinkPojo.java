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

import java.util.Map;

public class LinkPojo {
	private String source;
	private String sourceLabel;
	private String relation;
	private String target;
	private String direction;
	private Map<String, String> relationatt;

	public String getSource() {
		return source;
	}

	public LinkPojo setSource(String $source) {
		this.source = $source;
		return this;
	}

	public String getRelation() {
		return relation;
	}

	public LinkPojo setRelation(String $relation) {
		this.relation = $relation;
		return this;
	}

	public String getTarget() {
		return target;
	}

	public LinkPojo setTarget(String $target) {
		this.target = $target;
		return this;
	}

	public String getDirection() {
		return direction;
	}

	public LinkPojo setDirection(String direction) {
		this.direction = direction;

		return this;
	}

	public Map<String, String> getRelationatt() {
		return relationatt;
	}

	public LinkPojo setRelationatt(Map<String, String> relationatt) {
		this.relationatt = relationatt;
		return this;
	}

	public String getSourceLabel() {
		return sourceLabel;
	}

	public void setSourceLabel(String sourceLabel) {
		this.sourceLabel = sourceLabel;
	}

}
