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
import static org.digi.lg.neo4j.dao.DaoUtil.srcLabelQueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.ErrorCodes;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.exception.DataException;
import org.digi.lg.neo4j.pojo.model.Asset;
import org.digi.lg.neo4j.pojo.model.ProductClass;
import org.digi.lg.neo4j.queries.AssetQuery;
import org.neo4j.driver.v1.Record;

public class AssetDaoImpl extends CRUD implements AssetDao {

	private final BaseDao baseDao;
	private final AssetQuery assetQuery;

	protected AssetDaoImpl(final BaseDao baseDao, final CommonDao commonDao, final AssetQuery assetQuery) {
		super(baseDao, commonDao);
		this.baseDao = checkNotNull(baseDao);
		this.assetQuery = checkNotNull(assetQuery);
	}

	@Override
	public <TRX> Asset save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_ASSET, params);
		return new Asset(super.save(trx, SchemaConstants.LABEL_ASSET, params));
	}

	@Override
	public <TRX> Asset saveUpdate(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_ASSET, params);
		return new Asset(super.saveUpdate(trx, SchemaConstants.LABEL_ASSET, params));
	}

	@Override
	public <TRX> Asset getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, assetQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return Asset.rowMapper(parseNode(records, "at"));
	}

	@Override
	public <TRX> Asset getBySearialNumberSearch(TRX trx, String serialNumber) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_SERIAL_NUMBER, serialNumber);
		List<Record> records = baseDao.executeQuery(trx, assetQuery.getBySerialNumberSearch(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return Asset.rowMapper(parseNode(records, "at"));
	}

	@Override
	public <TRX> Edge deleteAssetBelongsOrg(TRX trx, String assetGuid, String orgGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetGuid,
				SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, orgGuid, Relationship.BELONGS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge deleteAssetIsClass(TRX trx, String assetGuid, String classGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetGuid,
				SchemaConstants.LABEL_CLASS, SchemaConstants.PROP_HDMFID_NAME, classGuid, Relationship.IS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge assetBelongsOrg(TRX trx, String assetGuid, String orgGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetGuid,
				SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, orgGuid, Relationship.BELONGS,
				Direction.OUT);
	}

	@Override
	public <TRX> Object getCompatible(TRX trx, String assetGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, assetGuid);
		List<Record> records = baseDao.executeQuery(trx, assetQuery.getCompatible(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return records.get(0).asMap().get("value");
	}

	@Override
	public <TRX> void deleteAssetIsClassAll(TRX trx, String assetGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, assetGuid);
		baseDao.executeQuery(trx, assetQuery.deleteAssetIsClassAll(), bindParams);
	}

	@Override
	public <TRX> Edge assetIsClass(TRX trx, String assetGuid, String classGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetGuid,
				SchemaConstants.LABEL_CLASS, SchemaConstants.PROP_HDMFID_NAME, classGuid, Relationship.IS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge assetIsClass(TRX trx, String assetGuid, String assetLabel, String classGuid, String classLabel) {
		return super.createEdge(trx, assetLabel, SchemaConstants.PROP_HDMFID_NAME, assetGuid, classLabel,
				SchemaConstants.PROP_HDMFID_NAME, classGuid, Relationship.IS, Direction.OUT);
	}

	@Override
	public <TRX> Asset getByGuid(TRX trx, String assetGuid, String assetLabel) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, assetGuid);
		List<Record> records = baseDao.executeQuery(trx,
				srcLabelQueryBuilder(assetQuery.getByGuidAndLabel(), assetLabel), bindParams);
		if (records == null || records.isEmpty())
			return null;

		return Asset.rowMapper(parseNode(records, "at"));
	}

	@Override
	public <TRX> ProductClass getProductClass(TRX trx, String assetGuid, String astLabel) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, assetGuid);
		List<Record> records = baseDao.executeQuery(trx, srcLabelQueryBuilder(assetQuery.getByProductClass(), astLabel),
				bindParams);
		if (records == null || records.isEmpty())
			return null;
		if (records.size() > 1)
			throw new DataException("multiple productClasses found for asset:{}..!!", assetGuid,
					ErrorCodes.INVALID_PARAMETER);

		return ProductClass.rowMapper(parseNode(records, "pc"));
	}

	@Override
	public <TRX> List<Asset> getAssetByAssetName(TRX trx, String productClassName, String assetName) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.SRC, productClassName);
		bindParams.put(BindConstants.DEST, assetName);
		List<Record> records = baseDao.executeQuery(trx, assetQuery.getAssetByAssetName(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();

		List<Asset> assets = new LinkedList<>();
		records.forEach(record -> assets.add(new Asset(new Vertex(parseLabel(record), parseNode(record, "at")))));
		return assets;
	}

	@Override
	public <TRX> List<Asset> getAssetBySerialNumber(TRX trx, String productClassName, String serialNumber) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.SRC, productClassName);
		bindParams.put(BindConstants.DEST, serialNumber);
		List<Record> records = baseDao.executeQuery(trx, assetQuery.getAssetBySerialNumber(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();

		List<Asset> assets = new LinkedList<>();
		records.forEach(record -> assets.add(new Asset(new Vertex(parseLabel(record), parseNode(record, "at")))));
		return assets;
	}

	@Override
	public <TRX> List<Asset> getAssetByOrg(TRX trx, String orgGuid, String serialNumber) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.SRC, orgGuid);
		bindParams.put(BindConstants.DEST, serialNumber);
		List<Record> records = baseDao.executeQuery(trx, assetQuery.getAssetByOrg(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();

		List<Asset> assets = new LinkedList<>();
		records.forEach(record -> assets.add(new Asset(new Vertex(parseLabel(record), parseNode(record, "at")))));
		return assets;
	}

	@Override
	public <TRX> List<Vertex> getAssetByProductClass(TRX trx, String contractId, String contractTypeId,
			String productClassId) {
		Map<String, Object> bindParams = new HashMap<>(3);
		bindParams.put(BindConstants.CONTRACT_ID, contractId);
		bindParams.put(BindConstants.CONTRACT_TYPE_ID, contractTypeId);
		bindParams.put(BindConstants.PRODUCT_CLASS_ID, productClassId);
		String query = assetQuery.getGetAssetByClass();
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		List<Vertex> bundles = new LinkedList<>();
		for (Record record : records) {
			bundles.add(new Vertex(parseNode(record, "astx")));
		}
		return bundles;

	}
	
	@Override
	public <TRX> List<Asset> getAssetByProductClassGuid(TRX trx, String productClassId) {
		Map<String, Object> bindParams = new HashMap<>(3);
		bindParams.put(BindConstants.PRODUCT_CLASS_ID, productClassId);
		String query = assetQuery.getGetAssetByProductClassGuid();
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		List<Asset> assets = new LinkedList<>();
		records.forEach(record -> assets.add(new Asset(new Vertex(parseLabel(record), parseNode(record, "at")))));

		return assets;
	}

	@Override
	public <TRX> List<Asset> getAllLicenceAssets(TRX trx) {
		List<Record> records = baseDao.executeQuery(trx, assetQuery.getAllLicenceAssets(), null);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		List<Asset> assets = new LinkedList<>();
		records.forEach(record -> assets.add(new Asset(new Vertex(parseLabel(record), parseNode(record, "at")))));
		return assets;
	}


	@Override
	public <TRX> Edge assetBelongsAsset(TRX trx, String assetGuid, String assetBelogsGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetGuid,
				SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetBelogsGuid, Relationship.BELONGS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge assetHasAsset(TRX trx, String assetHasGuid, String assetGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetHasGuid,
				SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetGuid, Relationship.HAS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge deleteAssetHasAsset(TRX trx, String srcAssetGuid, String destAssetGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, srcAssetGuid,
				SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, destAssetGuid, Relationship.HAS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge deleteAssetBelongsAsset(TRX trx, String destAssetGuid, String srcAssetGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, destAssetGuid,
				SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, srcAssetGuid, Relationship.BELONGS,
				Direction.OUT);
	}

	@Override
	public <TRX> List<Vertex> getAssetByClass(TRX trx, String productClassId) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PRODUCT_CLASS_ID, productClassId);
		String query = assetQuery.getGetAssetByClass();
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		List<Vertex> bundles = new LinkedList<>();
		for (Record record : records) {
			bundles.add(new Vertex(parseNode(record, "astx")));
		}
		return bundles;
	}

	@Override
	public <TRX> Boolean deleteNode(TRX trx, String nodeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, nodeGuid);
		baseDao.executeQuery(trx, assetQuery.getDetachDeleteNode(), bindParams);
		return true;
	}

	@Override
	public <TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction) {
		return super.deleteEdge(trx, srcLabel, SchemaConstants.PROP_HDMFID_NAME, srcGuid, destLabel,
				SchemaConstants.PROP_HDMFID_NAME, destGuid, relType, direction);
	}

	@Override
	public <TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType,
			Map<String, Object> params) {
		String query = createEdgeQueryBuilder(assetQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), relType);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Asset getLicenceAssetBybootStrapOrg(TRX trx, String orgGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, orgGuid);
		List<Record> records = baseDao.executeQuery(trx,assetQuery.getLicenceAssetBybootStrapOrg(), bindParams);
		if (records == null || records.isEmpty())
			return null;

		return Asset.rowMapper(parseNode(records, "at"));
	}

	

	
}
