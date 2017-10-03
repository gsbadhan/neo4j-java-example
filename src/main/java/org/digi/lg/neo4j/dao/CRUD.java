/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.digi.lg.neo4j.core.BindConstants.DEST;
import static org.digi.lg.neo4j.core.BindConstants.SRC;
import static org.digi.lg.neo4j.core.BindConstants.VALUE;
import static org.digi.lg.neo4j.dao.DaoUtil.getClassMetaParams;
import static org.digi.lg.neo4j.dao.DaoUtil.getParamMap;
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;

import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.exception.DataException;
import org.neo4j.driver.v1.Record;

public abstract class CRUD {
	private final BaseDao baseDao;
	private final CommonDao commonDao;

	public CRUD(final BaseDao baseDao) {
		this.baseDao = checkNotNull(baseDao);
		this.commonDao = null;
	}

	public CRUD(final BaseDao baseDao, final CommonDao commonDao) {
		this.baseDao = checkNotNull(baseDao);
		this.commonDao = checkNotNull(commonDao);
	}

	protected <TRX> boolean isExist(TRX trx, String srcLabel, String srcProperty, String srcPropertyValue,
			String destLabel, String destProperty, String destPropertyValue, Relationship relationship,
			Direction direction) {
		String query = null;
		switch (direction) {
		case IN:
			query = new StringBuilder("MATCH (s:").append(handleHyphen(srcLabel)).append("{").append(srcProperty)
					.append(":").append("{src}})<-[:").append(relationship.getType()).append("]-").append("(d:")
					.append(handleHyphen(destLabel)).append("{").append(destProperty).append(":").append("{dest}})")
					.append("RETURN COUNT(d)>0 AS cnt").toString();
			break;
		case OUT:
			query = new StringBuilder("MATCH (s:").append(handleHyphen(srcLabel)).append("{").append(srcProperty)
					.append(":").append("{src}})-[:").append(relationship.getType()).append("]->").append("(d:")
					.append(handleHyphen(destLabel)).append("{").append(destProperty).append(":").append("{dest}})")
					.append("RETURN COUNT(d)>0 AS cnt").toString();
			break;
		case BOTH:
			query = new StringBuilder("MATCH (s:").append(handleHyphen(srcLabel)).append("{").append(srcProperty)
					.append(":").append("{src}})-[r]-").append("(d:").append(handleHyphen(destLabel)).append("{")
					.append(destProperty).append(":").append("{dest}})").append("RETURN COUNT(d)>0 AS cnt").toString();
			break;
		}
		return baseDao.executeQuery(trx, query, getParamMap(SRC, srcPropertyValue, DEST, destPropertyValue)).get(0)
				.get("cnt", false);
	}

	protected <TRX> boolean isExist(TRX trx, String label, String property, String value) {
		String query = new StringBuilder("MATCH (s:").append(handleHyphen(label)).append("{").append(property)
				.append(":").append("{src}})").append(" RETURN COUNT(s)>0 AS cnt").toString();
		return baseDao.executeQuery(trx, query, getParamMap(SRC, value)).get(0).get("cnt", false);
	}

	protected <TRX> Vertex save(TRX trx, String label, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, dynamicSaveQueryBuilder(label, params), params);
		if (records == null || records.isEmpty())
			return null;
		Vertex vertex = new Vertex(label, parseNode(records, "c"));
		if (commonDao != null)
			commonDao.addUpdateClassMeta(trx, vertex.getLabel(), getClassMetaParams(vertex));
		return vertex;
	}

	protected <TRX> Vertex saveUpdate(TRX trx, String label, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, dynamicSaveUpdateQueryBuilder(label, params), params);
		if (records == null || records.isEmpty())
			return null;
		Vertex vertex = new Vertex(label, parseNode(records, "c"));
		if (commonDao != null)
			commonDao.addUpdateClassMeta(trx, vertex.getLabel(), getClassMetaParams(vertex));
		return vertex;
	}

	protected <TRX> Edge createEdge(TRX transaction, String srcLabel, String srcProperty, String srcPropertyVal,
			String destLabel, String destProperty, String destPropertyVal, Relationship relationship,
			Direction direction) {
		String query = null;

		switch (direction) {
		case IN:
			query = new StringBuilder("MATCH (s:").append(handleHyphen(srcLabel)).append("{").append(srcProperty)
					.append(":").append("{src}}),(d:").append(handleHyphen(destLabel)).append("{").append(destProperty)
					.append(":").append("{dest}}) MERGE (s)<-[r:").append(relationship.getType()).append("]-")
					.append("(d) ").append("RETURN r").toString();
			break;
		case OUT:
			query = new StringBuilder("MATCH (s:").append(handleHyphen(srcLabel)).append("{").append(srcProperty)
					.append(":").append("{src}}),(d:").append(handleHyphen(destLabel)).append("{").append(destProperty)
					.append(":").append("{dest}}) MERGE (s)-[r:").append(relationship.getType()).append("]->")
					.append("(d) ").append("RETURN r").toString();
			break;
		case BOTH:
			break;
		}

		List<Record> records = baseDao.executeQuery(transaction, query,
				getParamMap(SRC, srcPropertyVal, DEST, destPropertyVal));
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "r"));
	}

	protected <TRX> Edge createEdge(TRX transaction, String srcLabel, String srcProperty, String srcPropertyVal,
			String destLabel, String destProperty, String destPropertyVal, String edgeProperty, String edgeValue,
			Relationship relationship, Direction direction) {
		String query = null;

		switch (direction) {
		case IN:
			query = new StringBuilder("MATCH (s:").append(handleHyphen(srcLabel)).append("{").append(srcProperty)
					.append(":").append("{src}}),(d:").append(handleHyphen(destLabel)).append("{").append(destProperty)
					.append(":").append("{dest}}) MERGE (s)<-[r:").append(relationship.getType()).append("]-")
					.append("(d) ").append("SET r.").append(edgeProperty).append("={value}").append("RETURN r")
					.toString();
			break;
		case OUT:
			query = new StringBuilder("MATCH (s:").append(handleHyphen(srcLabel)).append("{").append(srcProperty)
					.append(":").append("{src}}),(d:").append(handleHyphen(destLabel)).append("{").append(destProperty)
					.append(":").append("{dest}}) MERGE (s)-[r:").append(relationship.getType()).append("]->")
					.append("(d) ").append("SET r.").append(edgeProperty).append("={value}").append("RETURN r")
					.toString();
			break;
		case BOTH:
			break;
		}

		List<Record> records = baseDao.executeQuery(transaction, query,
				getParamMap(SRC, srcPropertyVal, DEST, destPropertyVal, VALUE, edgeValue));
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "r"));
	}

	protected <TRX> Edge deleteEdge(TRX transaction, String srcLabel, String srcProperty, String srcPropertyVal,
			String destLabel, String destProperty, String destPropertyVal, Relationship relationship,
			Direction direction) {
		String query = null;

		switch (direction) {
		case IN:
			query = new StringBuilder("MATCH (s:").append(handleHyphen(srcLabel)).append("{").append(srcProperty)
					.append(":").append("{src}})").append("<-[r:").append(relationship.getType()).append("]-")
					.append("(d:").append(handleHyphen(destLabel)).append("{").append(destProperty).append(":")
					.append("{dest}}) ").append("DELETE r RETURN r").toString();
			break;
		case OUT:
			query = new StringBuilder("MATCH (s:").append(handleHyphen(srcLabel)).append("{").append(srcProperty)
					.append(":").append("{src}})").append("-[r:").append(relationship.getType()).append("]->")
					.append("(d:").append(handleHyphen(destLabel)).append("{").append(destProperty).append(":")
					.append("{dest}}) ").append("DELETE r RETURN r").toString();
			break;
		case BOTH:
			break;
		}

		List<Record> records = baseDao.executeQuery(transaction, query,
				getParamMap(SRC, srcPropertyVal, DEST, destPropertyVal));
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "r"));
	}

	private String dynamicSaveQueryBuilder(String label, Map<String, Object> params) {
		StringBuilder paramSetBuilder = new StringBuilder(" ");
		params.keySet().forEach(key -> {
			if (paramSetBuilder.length() > 2) {
				paramSetBuilder.append(',');
			}
			paramSetBuilder.append(key).append(':').append('{' + key + '}');
		});
		return new StringBuilder().append("CREATE (c:").append(handleHyphen(label)).append("{")
				.append(paramSetBuilder.toString()).append("}) RETURN c").toString();
	}

	private String dynamicSaveUpdateQueryBuilder(String label, Map<String, Object> params) {
		StringBuilder paramSetBuilder = new StringBuilder("SET ");
		params.keySet().forEach(key -> {
			if (!key.equals(SchemaConstants.PROP_HDMFID_NAME)) {
				if (paramSetBuilder.length() > 5) {
					paramSetBuilder.append(',');
				}
				paramSetBuilder.append("c.").append(DaoUtil.handleExclamation(key)).append('=')
						.append('{' + DaoUtil.handleExclamation(key) + '}');
			}
		});
		return new StringBuilder().append("MERGE (c:").append(handleHyphen(label)).append(" {guid:{guid}}) ")
				.append(paramSetBuilder.toString()).append(" RETURN c").toString();
	}

	protected void checkConstraints(final String label, Map<String, Object> params) {
		if (label.equals(SchemaConstants.LABEL_CLASS) && !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("class db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_ASSET) && !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("asset db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_EVENTS) && !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("events db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_DATA_ITEM) && !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("dataItem db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_SCRIPT_TEMPLATE)
				&& !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("scriptTemplate db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_CONTRACT_TYPE)
				&& !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("contractType db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_CONTRACT) && !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("contract db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_ADMIN_UNIT)
				&& !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("adminUnit db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_CONFIG_ITEM)
				&& !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("configItem db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_EVENT_TYPE)
				&& !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("eventType db_uuid missing");
		} else if (label.equals(SchemaConstants.LABEL_PRODUCT_CLASS)
				&& !params.containsKey(SchemaConstants.PROP_DB_UUID)) {
			throw new DataException("productClass db_uuid missing");
		}
	}
}
