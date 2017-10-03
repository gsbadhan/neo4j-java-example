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

public class LinkSource {
	private String nodeId;
	private String nodeLabel;
	private List<LinkDestination> targets;

	public LinkSource() {
	}

	public LinkSource(String nodeId, String nodeLabel, List<LinkDestination> targets) {
		super();
		this.nodeId = nodeId;
		this.nodeLabel = nodeLabel;
		this.targets = targets;
	}

	public List<LinkDestination> getTargets() {
		return targets;
	}

	public LinkSource setTargets(List<LinkDestination> targets) {
		this.targets = targets;
		return this;
	}

	public LinkSource addLinkDestination(LinkDestination target) {
		if (targets == null) {
			targets = new ArrayList<>();
		}
		targets.add(target);
		return this;
	}

	public String getNodeId() {
		return nodeId;
	}

	public LinkSource setNodeId(String nodeId) {
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
