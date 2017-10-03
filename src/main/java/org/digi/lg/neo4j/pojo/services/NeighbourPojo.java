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

import java.io.Serializable;

@SuppressWarnings("serial")
public class NeighbourPojo implements Serializable {
	private Node<Object> parent;
	private Node<Object> child;
	private LinkPojo link;

	public NeighbourPojo() {
	}

	public NeighbourPojo(Node<Object> parent) {
		super();
		this.parent = parent;
	}

	public NeighbourPojo(Node<Object> child, LinkPojo link) {
		super();
		this.child = child;
		this.link = link;
	}

	public Node<Object> getParent() {
		return parent;
	}

	public void setParent(Node<Object> parent) {
		this.parent = parent;
	}

	public Node<Object> getChild() {
		return child;
	}

	public void setChild(Node<Object> child) {
		this.child = child;
	}

	public LinkPojo getLink() {
		return link;
	}

	public void setLink(LinkPojo link) {
		this.link = link;
	}

}
