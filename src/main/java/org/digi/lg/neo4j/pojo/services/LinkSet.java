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

import com.google.gson.Gson;

public class LinkSet {

	private List<LinkSource> links;

	public List<LinkSource> getLinks() {
		return links;
	}

	public LinkSet setLinks(List<LinkSource> links) {
		this.links = links;

		return this;
	}

	public LinkSet addLinkSet(LinkSource link) {
		if (links == null) {
			links = new ArrayList<>();
		}
		links.add(link);
		return this;
	}

	public static void main(String[] args) {
		LinkSet set = new LinkSet();
		set.addLinkSet(
				new LinkSource().setNodeId("2333")
						.addLinkDestination(
								new LinkDestination().setNodeId("23234").setLinkType("HAS").setDirection("OUT"))
				.addLinkDestination(new LinkDestination().setNodeId("2323eee4").setLinkType("HAS").setDirection("OUT"))
				.addLinkDestination(new LinkDestination().setNodeId("2733647").setLinkType("HAS").setDirection("OUT")));

		String json=new Gson().toJson(set);
		
		 

	}

}
