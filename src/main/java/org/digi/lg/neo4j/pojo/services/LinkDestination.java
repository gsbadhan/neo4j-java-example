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

public class LinkDestination {
	private String nodeId;
	private String nodeLabel;
	private String direction;
	private String linkType;

	public LinkDestination() {
	}

	public LinkDestination(String nodeId, String nodeLabel, String direction, String linkType) {
		super();
		this.nodeId = nodeId;
		this.nodeLabel = nodeLabel;
		this.direction = direction;
		this.linkType = linkType;
	}

	public String getDirection() {
		return direction;
	}

	public LinkDestination setDirection(String direction) {
		this.direction = direction;
		return this;
	}

	public String getLinkType() {
		return linkType;
	}

	public LinkDestination setLinkType(String linkType) {
		this.linkType = linkType;
		return this;
	}

	public String getNodeId() {
		return nodeId;
	}

	public LinkDestination setNodeId(String nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public String getNodeLabel() {
		return nodeLabel;
	}

	public void setNodeLabel(String nodeLabel) {
		this.nodeLabel = nodeLabel;
	}

}
