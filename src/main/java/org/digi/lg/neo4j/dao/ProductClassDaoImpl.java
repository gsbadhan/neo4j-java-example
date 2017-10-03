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
import static org.digi.lg.neo4j.dao.DaoUtil.getParamMap;
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.ProductClass;
import org.digi.lg.neo4j.queries.ProductClassQuery;
import org.neo4j.driver.v1.Record;

public class ProductClassDaoImpl extends CRUD implements ProductClassDao {
	private final BaseDao baseDao;
	private final ProductClassQuery productClassQuery;

	protected ProductClassDaoImpl(final BaseDao baseDao, final CommonDao commonDao,
			final ProductClassQuery modelClassQuery) {
		super(baseDao, commonDao);
		this.baseDao = checkNotNull(baseDao);
		this.productClassQuery = checkNotNull(modelClassQuery);
	}

	@Override
	public <TRX> Vertex save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_PRODUCT_CLASS, params);
		return super.save(trx, SchemaConstants.LABEL_PRODUCT_CLASS, params);
	}

	@Override
	public <TRX> ProductClass getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, productClassQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return ProductClass.rowMapper(parseNode(records, "pc"));
	}

	@Override
	public <TRX> Edge productClassHasDataItem(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, productClassQuery.getClassHasDataItem(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge productClassHasEvents(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, productClassQuery.getClassHasEvents(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge productClassHasAggDataItem(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, productClassQuery.getClassHasAggDataItem(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> boolean isAssetNameExistUnderClass(TRX trx, String prdctClassName, String assetName) {
		List<Record> records = baseDao.executeQuery(trx, productClassQuery.getIsAssetNameExistUnderClass(),
				getParamMap(BindConstants.SRC, prdctClassName, BindConstants.DEST, assetName));
		return records.get(0).get("st", false);
	}

	@Override
	public <TRX> ProductClass getByName(TRX trx, String name) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_NAME, name);
		List<Record> records = baseDao.executeQuery(trx, productClassQuery.getByName(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return ProductClass.rowMapper(parseNode(records, "pc"));
	}

	@Override
	public <TRX> List<Vertex> getAllProductClasses(TRX trx) {
		List<Vertex> productClassList = new ArrayList<>();
		List<Record> records = baseDao.executeQuery(trx, productClassQuery.getAllProductClasses(), null);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		records.forEach(record -> productClassList.add(new Vertex(parseNode(record, "pc"))));
		return productClassList;

	}

	@Override
	public <TRX> Edge deleteProductClassIsClass(TRX trx, String srcClassGuid, String destClassGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_PRODUCT_CLASS, SchemaConstants.PROP_HDMFID_NAME,
				srcClassGuid, SchemaConstants.LABEL_CLASS, SchemaConstants.PROP_HDMFID_NAME, destClassGuid,
				Relationship.IS, Direction.OUT);
	}

	@Override
	public <TRX> Edge productClassIsClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params) {
		String query = createEdgeQueryBuilder(productClassQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), Relationship.IS);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge deleteClassIsProductClass(TRX trx, String srcClassGuid, String destClassGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_CLASS, SchemaConstants.PROP_HDMFID_NAME, srcClassGuid,
				SchemaConstants.LABEL_PRODUCT_CLASS, SchemaConstants.PROP_HDMFID_NAME, destClassGuid, Relationship.IS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge classIsProductClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params) {
		String query = createEdgeQueryBuilder(productClassQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), Relationship.IS);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge deleteProductClassIsProductClass(TRX trx, String srcClassGuid, String destClassGuid) {

		return super.deleteEdge(trx, SchemaConstants.LABEL_PRODUCT_CLASS, SchemaConstants.PROP_HDMFID_NAME,
				srcClassGuid, SchemaConstants.LABEL_PRODUCT_CLASS, SchemaConstants.PROP_HDMFID_NAME, destClassGuid,
				Relationship.IS, Direction.OUT);

	}

	@Override
	public <TRX> Edge productClassIsProductClass(TRX trx, String srcLabel, String destLabel,
			Map<String, Object> params) {

		String query = createEdgeQueryBuilder(productClassQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), Relationship.IS);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));

	}

	@Override
	public <TRX> Edge deleteLink(TRX trx, String srcLabel, String srcClassGuid, String destLabel, String destClassGuid,
			Relationship relType, Direction direction) {

		return super.deleteEdge(trx, srcLabel, SchemaConstants.PROP_HDMFID_NAME, srcClassGuid, destLabel,
				SchemaConstants.PROP_HDMFID_NAME, destClassGuid, relType, direction);

	}

	@Override
	public <TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType,
			Map<String, Object> params) {
		String query = createEdgeQueryBuilder(productClassQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), relType);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

}
