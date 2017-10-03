/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.core;

import static org.digi.lg.neo4j.dao.DaoUtil.getStr;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("serial")
public class Vertex implements Serializable {
	private String label;
	private Map<String, Object> node;
	private List<Vertex> inVertices;
	private List<Vertex> outVertices;
	private List<Vertex> bothVertices;
	private Relation relation;

	public Vertex() {
	}

	public Vertex(String label, Map<String, Object> node) {
		super();
		this.label = label;
		this.node = node;
	}

	public Vertex(Map<String, Object> data) {
		super();
		this.node = data;
	}

	public Vertex(String label, Map<String, Object> node, Relation relation) {
		super();
		this.label = label;
		this.node = node;
		this.relation = relation;
	}

	public Map<String, Object> getNode() {
		return node;
	}

	public void setNode(Map<String, Object> node) {
		this.node = node;
	}

	public List<Vertex> getInVertices() {
		return inVertices;
	}

	public void setInVertices(List<Vertex> inVertices) {
		this.inVertices = inVertices;
	}

	public List<Vertex> getOutVertices() {
		return outVertices;
	}

	public void setOutVertices(List<Vertex> outVertices) {
		this.outVertices = outVertices;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getGuid() {
		return node != null ? getStr(node.get(SchemaConstants.PROP_HDMFID_NAME)) : null;
	}

	public String getName() {
		return node != null ? getStr(node.get(SchemaConstants.PROP_NAME)) : null;
	}

	public String getDBGuid() {
		return node != null ? getStr(node.get(SchemaConstants.PROP_DB_UUID)) : null;
	}

	public List<Vertex> getBothVertices() {
		return bothVertices;
	}

	public void setBothVertices(List<Vertex> bothVertices) {
		this.bothVertices = bothVertices;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	/**
	 * 
	 * @author gurpreet.singh
	 *
	 *         use to identified Edge type
	 */
	public static class Relation implements Serializable {
		private String label;
		private String direction;
		private Map<String, Object> properties;

		public Relation() {
		}

		public Relation(String label, String direction, Map<String, Object> properties) {
			super();
			this.label = label;
			this.direction = direction;
			this.properties = properties;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getDirection() {
			return direction;
		}

		public void setDirection(String direction) {
			this.direction = direction;
		}

		public Map<String, Object> getProperties() {
			return properties;
		}

		public void setProperties(Map<String, Object> properties) {
			this.properties = properties;
		}

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (obj.getClass() != getClass())
			return false;

		final Vertex vertex = (Vertex) obj;
		return Objects.equals(getGuid(), vertex.getGuid());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getGuid());
	}

	@Override
	public String toString() {
		return "Vertex [label=" + label + ", data=" + node + ", inVertices=" + inVertices + ", outVertices="
				+ outVertices + "]";
	}

}
