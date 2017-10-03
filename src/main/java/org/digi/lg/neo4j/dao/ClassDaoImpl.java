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
import static org.digi.lg.neo4j.dao.DaoUtil.createEdgeQueryBuilder;
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
import static org.digi.lg.neo4j.dao.DaoUtil.parseLabel;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;
import static org.digi.lg.neo4j.dao.DaoUtil.relationQueryBuilder;
import static org.digi.lg.neo4j.dao.DaoUtil.srcLabelQueryBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Properties.PropTypeValue;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.core.Vertex.Relation;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.queries.ClassQuery;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;

public class ClassDaoImpl extends CRUD implements ClassDao {
	private final BaseDao baseDao;
	private final CommonDao commonDao;
	private final ClassQuery classQuery;

	public ClassDaoImpl(final BaseDao baseDao, final CommonDao commonDao, final ClassQuery classQuery) {
		super(baseDao, commonDao);
		this.baseDao = checkNotNull(baseDao);
		this.commonDao = checkNotNull(commonDao);
		this.classQuery = checkNotNull(classQuery);
	}

	@Override
	public <TRX> Edge classHasClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params) {
		String query = createEdgeQueryBuilder(classQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), Relationship.HAS);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge classBelongsClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params) {
		String query = createEdgeQueryBuilder(classQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), Relationship.BELONGS);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge classIsClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params) {
		String query = createEdgeQueryBuilder(classQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), Relationship.IS);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge classTemplateGlobalClasess(TRX trx, String srcLabel, Map<String, Object> params) {
		String query = srcLabelQueryBuilder(classQuery.getClassTemplateGlobalClasess(), handleHyphen(srcLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge classTemplateGlobalProducts(TRX trx, String srcLabel, Map<String, Object> params) {
		String query = srcLabelQueryBuilder(classQuery.getClassTemplateGlobalProducts(), handleHyphen(srcLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge classTemplateGlobalOrg(TRX trx, String srcLabel, Map<String, Object> params) {
		String query = srcLabelQueryBuilder(classQuery.getClassTemplateGlobalOrg(), handleHyphen(srcLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge classHasDataShard(TRX trx, String srcLabel, Map<String, Object> params) {
		String query = srcLabelQueryBuilder(classQuery.getClassHasDataShard(), handleHyphen(srcLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> ClassX getByGuid(TRX trx, String label, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		String query = classQuery.getByGuid().replaceFirst(BindConstants.SRC_LBL, handleHyphen(label));
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null || records.isEmpty())
			return null;
		return ClassX.rowMapper(label, parseNode(records, "c"));
	}

	@Override
	public <TRX> ClassX getByGuid(TRX trx, String guid) {
		ClassX classX = null;
		String label = commonDao.getLabel(trx, guid);
		if (label != null) {
			classX = getByGuid(trx, label, guid);
		} else {
			classX = getByProperty(trx, ClassX.class, SchemaConstants.PROP_HDMFID_NAME, guid);
		}
		return classX;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <TRX, RET> RET getVertex(TRX trx, Class<RET> returnType, String label, String property,
			String propertyValue) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(property, propertyValue);
		String query = classQuery.getVertexQuery().replaceFirst(BindConstants.SRC_LBL, handleHyphen(label))
				.replaceFirst(SchemaConstants.PROPERTY_X, property).replaceFirst(BindConstants.PROPERTY_X, property);
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null || records.isEmpty())
			return null;

		if (returnType == Vertex.class) {
			Vertex vertex = new Vertex();
			vertex.setLabel(label);
			vertex.setNode(parseNode(records, "c"));
			return (RET) vertex;
		} else if (returnType == ClassX.class) {
			Vertex vertex = new Vertex();
			vertex.setLabel(label);
			vertex.setNode(parseNode(records, "c"));
			return (RET) new ClassX(vertex);
		} else if (returnType == List.class) {
			// TODO:return list of vertices
		} else {
			throw new RuntimeException();
		}
		return null;
	}

	@Override
	public <TRX> Vertex getInVertex(TRX trx, String label, String property, String propertyValue,
			Relationship relationship, Optional<String> destLabel, boolean withParentNode) {
		Vertex vertex = null;
		if (withParentNode) {
			vertex = getVertex(trx, Vertex.class, label, property, propertyValue);
			if (vertex == null)
				return null;
		} else {
			vertex = new Vertex();
		}
		vertex.setLabel(label);

		return getInOutVertex(trx, Vertex.class, vertex, Direction.IN, relationship, label, property, propertyValue,
				destLabel);
	}

	@Override
	public <TRX> Vertex getOutVertex(TRX trx, String srcLabel, String property, String propertyValue,
			Relationship relationship, Optional<String> destLabel, boolean withParentNode) {
		Vertex vertex = null;
		if (withParentNode) {
			vertex = getVertex(trx, Vertex.class, srcLabel, property, propertyValue);
			if (vertex == null)
				return null;
		} else {
			vertex = new Vertex();
		}
		vertex.setLabel(srcLabel);

		return getInOutVertex(trx, Vertex.class, vertex, Direction.OUT, relationship, srcLabel, property, propertyValue,
				destLabel);
	}

	@Override
	public <TRX> Vertex getBothVertex(TRX trx, String label, String property, String propertyValue,
			Relationship relationship, Optional<String> destLabel, boolean withParentNode) {
		Vertex vertex = null;
		if (withParentNode) {
			vertex = getVertex(trx, Vertex.class, label, property, propertyValue);
			if (vertex == null)
				return null;
		} else {
			vertex = new Vertex();
		}
		vertex.setLabel(label);

		return getInOutVertex(trx, Vertex.class, vertex, Direction.BOTH, relationship, label, property, propertyValue,
				destLabel);
	}

	@SuppressWarnings("unchecked")
	private <TRX, RET> RET getInOutVertex(TRX trx, Class<RET> returnType, Vertex parentVertex, Direction direction,
			Relationship relation, String srcLabel, String property, String propertyValue, Optional<String> destLabel) {
		String destClassKeyword = BindConstants.DEST_LBL;
		String destClassLbl = null;
		List<Vertex> adjacentList = new LinkedList<>();
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(property, propertyValue);

		String query = null;
		switch (direction) {
		case IN:
			query = classQuery.getInVertices();
			break;
		case OUT:
			query = classQuery.getOutVertices();
			break;
		case BOTH:
			query = classQuery.getBothVertices();
			break;
		}
		if (query == null)
			return null;

		if (destLabel.isPresent()) {
			destClassLbl = destLabel.get();
		} else {
			destClassKeyword = BindConstants.REPLACE_DEST_LBL;
			destClassLbl = "";
		}
		query = query.replaceFirst(BindConstants.SRC_LBL, handleHyphen(srcLabel))
				.replaceFirst((SchemaConstants.COLON + SchemaConstants.REL),
						(relation != null ? (SchemaConstants.COLON + relation.getType()) : ""))
				.replaceFirst(SchemaConstants.PROPERTY_X, property).replaceFirst(BindConstants.PROPERTY_X, property)
				.replaceFirst(destClassKeyword, handleHyphen(destClassLbl));
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null)
			return null;

		if (returnType == Vertex.class) {
			records.forEach(record -> {
				Vertex vertex = new Vertex(((InternalNode) record.asMap().get("x")).asMap());
				vertex.setLabel(record.asMap().get("lbl").toString());
				Map<String, Object> edgeProperties = ((org.neo4j.driver.v1.types.Relationship) record.asMap().get("r"))
						.asMap();
				String edgeLabel = record.asMap().get("rlbl").toString();
				vertex.setRelation(new Relation(edgeLabel, direction.name(), edgeProperties));
				adjacentList.add(vertex);
			});
		} else if (returnType == List.class) {
			// TODO:return list of vertices
		} else {
			throw new RuntimeException();
		}

		if (!adjacentList.isEmpty()) {
			switch (direction) {
			case IN:
				parentVertex.setInVertices(adjacentList);
				break;
			case OUT:
				parentVertex.setOutVertices(adjacentList);
				break;
			case BOTH:
				parentVertex.setBothVertices(adjacentList);
			}
		}
		return (RET) parentVertex;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <TRX, RET> RET getByProperty(TRX trx, Class<RET> returnType, String property, String propertyValue) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(property, propertyValue);
		String query = classQuery.getByProperty().replaceFirst(SchemaConstants.PROPERTY_X, property)
				.replaceFirst(BindConstants.PROPERTY_X, property);
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null || records.isEmpty())
			return null;

		if (returnType == Vertex.class) {
			Vertex vertex = new Vertex();
			vertex.setLabel(records.get(0).asMap().get("lbl").toString());
			vertex.setNode(parseNode(records, "c"));
			return (RET) vertex;
		} else if (returnType == ClassX.class) {
			Vertex vertex = new Vertex();
			vertex.setLabel(records.get(0).asMap().get("lbl").toString());
			vertex.setNode(parseNode(records, "c"));
			return (RET) new ClassX(vertex);
		} else if (returnType == List.class) {
			// TODO:return list of vertices
		} else {
			throw new RuntimeException();
		}
		return null;
	}

	@Override
	public <TRX> Vertex saveUpdate(TRX trx, String label, Map<String, Object> params) {
		checkConstraints(label, params);
		return super.saveUpdate(trx, label, params);
	}

	@Override
	public <TRX> Vertex save(TRX trx, String label, Map<String, Object> params) {
		checkConstraints(label, params);
		return super.save(trx, label, params);
	}

	@Override
	public <TRX> boolean isNodeConnectedIn(TRX trx, Optional<String> srcLbl,
			org.digi.lg.neo4j.core.Relationship relType, Map<String, Object> params) {
		String query = classQuery.getIsParentConnectedToChild();
		if (srcLbl.isPresent()) {
			query = query.replaceFirst(BindConstants.SRC_LBL, handleHyphen(srcLbl.get()));
		} else {
			query = query.replaceFirst(BindConstants.REPLACE_SRC_LBL, "");
		}

		query = query.replaceFirst(BindConstants.REL, relType.getType());
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return false;

		return (Boolean) records.get(0).asMap().get("st");
	}

	@Override
	public <TRX> Edge classHasDataItem(TRX trx, String srcLabel, Map<String, Object> params) {
		String query = srcLabelQueryBuilder(classQuery.getClassHasDataItem(), handleHyphen(srcLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge classHasAggDataItem(TRX trx, String srcLabel, Map<String, Object> params) {
		String query = srcLabelQueryBuilder(classQuery.getClassHasAggDataItem(), handleHyphen(srcLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge classHasEvents(TRX trx, String srcLabel, Map<String, Object> params) {
		String query = srcLabelQueryBuilder(classQuery.getClassHasEvents(), handleHyphen(srcLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> boolean isNodeConnectedIn(TRX trx, Optional<String> srcLbl, Optional<String> destLbl,
			Relationship relType, Map<String, Object> params) {
		String query = classQuery.getIsConnectedIn();
		query = (srcLbl.isPresent() ? query.replaceFirst(BindConstants.SRC_LBL, handleHyphen(srcLbl.get()))
				: query.replaceFirst(BindConstants.REPLACE_SRC_LBL, ""));
		query = (destLbl.isPresent() ? query.replaceFirst(BindConstants.DEST_LBL, handleHyphen(destLbl.get()))
				: query.replaceFirst(BindConstants.REPLACE_DEST_LBL, ""));

		query = query.replaceFirst(BindConstants.REL, relType.getType());
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return false;

		return (Boolean) records.get(0).asMap().get("st");
	}

	@Override
	public <TRX> List<Vertex> getMandatoryPropertyVertex(TRX trx, Optional<String> srcLbl, Map<String, Object> params) {
		String query = classQuery.getMandatoryPropertyVertex();
		query = (srcLbl.isPresent() ? query.replaceFirst(BindConstants.SRC_LBL, handleHyphen(srcLbl.get()))
				: query.replaceFirst(BindConstants.REPLACE_SRC_LBL, ""));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return Collections.emptyList();

		List<Vertex> list = new LinkedList<>();
		records.forEach(record -> list.add(new Vertex(DaoUtil.getStr(record.asMap().get("lbl")),
				((InternalNode) record.asMap().get("d")).asMap())));
		return list;
	}

	@Override
	public <TRX> Edge classConnnectedToClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params) {
		String query = createEdgeQueryBuilder(classQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), Relationship.CONNECTED_TO);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge unLinkClassConnnectedToClass(TRX trx, String srcLabel, String destLabel,
			Map<String, Object> paramMap) {
		String query = createEdgeQueryBuilder(classQuery.getDeleteOutLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), Relationship.CONNECTED_TO);
		List<Record> records = baseDao.executeQuery(trx, query, paramMap);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> void unlinkAllClassesConnnectedToGateway(TRX trx, String srcLabel, Map<String, Object> paramMap) {
		String query = relationQueryBuilder(
				srcLabelQueryBuilder(classQuery.getDeleteAllOutLink(), handleHyphen(srcLabel)),
				Relationship.CONNECTED_TO.getType());
		baseDao.executeQuery(trx, query, paramMap);
	}

	@Override
	public <TRX> Edge createEdge(TRX transaction, String srcLabel, String srcProperty, String srcPropertyVal,
			String destLabel, String destProperty, String destPropertyVal, Relationship relationship,
			Direction direction) {
		return super.createEdge(transaction, srcLabel, srcProperty, srcPropertyVal, destLabel, destProperty,
				destPropertyVal, relationship, direction);
	}

	@Override
	public <TRX> ClassX getByName(TRX trx, String label, String name) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_NAME, name);
		String query = classQuery.getByName().replaceFirst(BindConstants.SRC_LBL, handleHyphen(label));
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null || records.isEmpty())
			return null;
		return ClassX.rowMapper(label, parseNode(records, "c"));
	}

	@Override
	public <TRX> List<Vertex> getClasssAndProductClasses(TRX trx, String srcLabel, String property,
			String propertyValue, Relationship relationship, Direction direction) {
		List<Vertex> adjacentList = new LinkedList<>();
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(property, propertyValue);
		String query = classQuery.getClassAndProductClass();
		query = query.replaceFirst(BindConstants.SRC_LBL, handleHyphen(srcLabel))
				.replaceFirst((SchemaConstants.COLON + SchemaConstants.REL),
						(relationship != null ? (SchemaConstants.COLON + relationship.getType()) : ""))
				.replaceFirst(SchemaConstants.PROPERTY_X, property).replaceFirst(BindConstants.PROPERTY_X, property);
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();

		records.forEach(record -> {
			Vertex vertex = new Vertex(parseNode(record, "x"));
			vertex.setLabel(parseLabel(record));
			Map<String, Object> edgeProperties = parseRelationship(record, "r");
			String edgeLabel = record.asMap().get("rlbl").toString();
			vertex.setRelation(new Relation(edgeLabel, direction.name(), edgeProperties));
			adjacentList.add(vertex);
		});
		return adjacentList;
	}

	@Override
	public <TRX> Edge deleteClassIsClass(TRX trx, String srcClassGuid, String destClassGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_CLASS, SchemaConstants.PROP_HDMFID_NAME, srcClassGuid,
				SchemaConstants.LABEL_CLASS, SchemaConstants.PROP_HDMFID_NAME, destClassGuid, Relationship.IS,
				Direction.OUT);
	}

	@Override
	public <TRX> ClassX getDomainClass(TRX trx, String principalGuid, String contractTypeGuid) {
		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.PROP_HDMFID_NAME, contractTypeGuid);
		params.put(BindConstants.PROP_PRINCIPAL_GUID, principalGuid);
		params.put(BindConstants.PROP_TYPE, PropTypeValue._CLASSES.getValue());
		List<Record> records = baseDao.executeQuery(trx, classQuery.getDomainClassByContractType(), params);
		if (records == null || records.isEmpty())
			return null;
		return new ClassX(new Vertex(parseLabel(records), parseNode(records, "cls")));
	}

	@Override
	public <TRX> boolean isClassExist(TRX trx, String label, String property, String value) {
		return super.isExist(trx, label, property, value);
	}

	@Override
	public <TRX> Boolean deleteNode(TRX trx, String nodeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, nodeGuid);
		baseDao.executeQuery(trx, classQuery.getDetachDeleteNode(), bindParams);
		return true;
	}

	@Override
	public <TRX> boolean isNodeConnected(TRX trx, String srcLabel, String srcPropLabel, String srcPropValue,
			String dstLabel, String dstPropName, String dstPropValue, Relationship relationship, Direction direction) {
		return super.isExist(trx, srcLabel, srcPropLabel, srcPropValue, dstLabel, dstPropName, dstPropValue,
				relationship, direction);
	}

	@Override
	public <TRX> ClassX getDomainChildClass(TRX trx, String domainClassGuid, String childClassName) {
		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.PROP_HDMFID_NAME, domainClassGuid);
		params.put(BindConstants.REGEX, childClassName);
		List<Record> records = baseDao.executeQuery(trx, classQuery.getDomainChildClass(), params);
		if (records == null || records.isEmpty())
			return null;
		return new ClassX(new Vertex(parseLabel(records), parseNode(records, "cls")));
	}

	@Override
	public <TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction) {
		return super.deleteEdge(trx, srcLabel, SchemaConstants.PROP_HDMFID_NAME, srcGuid, destLabel,
				SchemaConstants.PROP_HDMFID_NAME, destGuid, relType, direction);
	}

	@Override
	public <TRX> ClassX getDomainClassByContract(TRX trx, String contractGuid, String domainClassGuid) {
		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.SRC, contractGuid);
		params.put(BindConstants.DEST, domainClassGuid);
		List<Record> records = baseDao.executeQuery(trx, classQuery.getDomainClassByContract(), params);
		if (records == null || records.isEmpty())
			return null;
		return new ClassX(new Vertex(parseLabel(records), parseNode(records, "cls")));
	}

}
