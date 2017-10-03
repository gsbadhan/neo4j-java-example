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
import static org.digi.lg.neo4j.dao.DaoUtil.parseLabel;
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
import org.digi.lg.neo4j.pojo.model.DataItem;
import org.digi.lg.neo4j.queries.DataItemQuery;
import org.neo4j.driver.v1.Record;

public class DataItemDaoImpl extends CRUD implements DataItemDao {
	private final BaseDao baseDao;
	private final DataItemQuery dataItemQuery;

	protected DataItemDaoImpl(final BaseDao baseDao, final CommonDao commonDao, final DataItemQuery dataItemQuery) {
		super(baseDao, commonDao);
		this.baseDao = checkNotNull(baseDao);
		this.dataItemQuery = checkNotNull(dataItemQuery);
	}

	@Override
	public <TRX> DataItem save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_DATA_ITEM, params);
		return new DataItem(save(trx, SchemaConstants.LABEL_DATA_ITEM, params));
	}

	@Override
	public <TRX> DataItem getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, dataItemQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return DataItem.rowMapper(parseNode(records, "di"));
	}

	@Override
	public <TRX> List<DataItem> getByContractType(TRX trx, String contractTypeId) {
		List<DataItem> dataItems = new ArrayList<>();
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.CONTRACT_TYPE_ID, contractTypeId);
		List<Record> records = baseDao.executeQuery(trx, dataItemQuery.getDataItemByContractType(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		records.forEach(record -> dataItems.add(DataItem.rowMapper(parseNode(record, "di"))));
		return dataItems;
	}

	@Override
	public <TRX> List<Edge> getEdgesByProductClass(TRX trx, String dataItemId, String classId) {
		List<Edge> edgeList = new ArrayList<>();
		Map<String, Object> bindParams = new HashMap<>(2);
		bindParams.put(BindConstants.SRC, dataItemId);
		bindParams.put(BindConstants.DEST, classId);
		List<Record> records = baseDao.executeQuery(trx, dataItemQuery.getEdgesByProductClass(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		records.forEach(record -> edgeList.add(new Edge(parseRelationship(record, "rid"))));
		return edgeList;
	}

	@Override
	public <TRX> List<DataItem> getDataItemByProductClass(TRX trx, String productClassId) {
		List<DataItem> dataItemList = new ArrayList<>();
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.SRC, productClassId);
		List<Record> records = baseDao.executeQuery(trx, dataItemQuery.getDataItemHasProductClass(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		records.forEach(record -> dataItemList.add(DataItem.rowMapper(parseNode(record, "di"))));
		return dataItemList;
	}

	@Override
	public <TRX> List<DataItem> getDataItemByProductClass(TRX trx, String productClassId, String dataItemName) {
		List<DataItem> dataItemList = new ArrayList<>();
		Map<String, Object> bindParams = new HashMap<>(2);
		bindParams.put(BindConstants.SRC, productClassId);
		bindParams.put(BindConstants.DEST, dataItemName);
		List<Record> records = baseDao.executeQuery(trx, dataItemQuery.getDataItemByProductClass(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		records.forEach(record -> dataItemList.add(DataItem.rowMapper(parseNode(record, "di"))));
		return dataItemList;
	}

	@Override
	public <TRX> List<Vertex> getDataItems(TRX trx, String contractTypeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, contractTypeGuid);
		List<Record> records = baseDao.executeQuery(trx, dataItemQuery.getDomainDataItems(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		List<Vertex> dataItems = new ArrayList<>();
		records.forEach(record -> dataItems.add(new Vertex(parseLabel(record), parseNode(record, "di"))));
		return dataItems;
	}

	@Override
	public <TRX> boolean isNodeConnected(TRX trx, String srcLabel, String srcPropLabel, String srcPropValue,
			String dstLabel, String dstPropName, String dstPropValue, Relationship relationship, Direction direction) {

		return super.isExist(trx, srcLabel, srcPropLabel, srcPropValue, dstLabel, dstPropName, dstPropValue,
				relationship, direction);

	}

	@Override
	public <TRX> Boolean deleteNode(TRX trx, String nodeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, nodeGuid);
		baseDao.executeQuery(trx, dataItemQuery.getDetachDeleteNode(), bindParams);
		return true;
	}

}
