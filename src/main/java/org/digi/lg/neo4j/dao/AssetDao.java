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

import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Asset;
import org.digi.lg.neo4j.pojo.model.ProductClass;

public interface AssetDao {

	<TRX> Asset save(TRX trx, Map<String, Object> params);

	<TRX> Asset saveUpdate(TRX trx, Map<String, Object> params);

	<TRX> Asset getByGuid(TRX trx, String guid);

	<TRX> Asset getBySearialNumberSearch(TRX trx, String serialNumber);

	<TRX> Edge deleteAssetBelongsOrg(TRX trx, String assetGuid, String orgGuid);

	<TRX> Edge assetBelongsOrg(TRX trx, String assetGuid, String orgGuid);

	<TRX> Object getCompatible(TRX trx, String assetGuid);

	<TRX> void deleteAssetIsClassAll(TRX trx, String assetGuid);

	<TRX> Edge assetIsClass(TRX trx, String assetGuid, String classGuid);

	<TRX> Edge assetIsClass(TRX trx, String assetGuid, String assetLabel, String classGuid, String classLabel);

	<TRX> List<Asset> getAssetByAssetName(TRX trx, String productClassName, String assetName);

	<TRX> List<Asset> getAssetBySerialNumber(TRX trx, String productClassName, String serialNumber);

	<TRX> Asset getByGuid(TRX trx, String assetGuid, String assetLabel);

	<TRX> ProductClass getProductClass(TRX trx, String assetGuid, String astLabel);

	<TRX> List<Asset> getAssetByOrg(TRX trx, String orgGuid, String serialNumber);

	<TRX> List<Vertex> getAssetByProductClass(TRX trx, String contractId, String contractTypeId, String productClassId);

	<TRX> List<Vertex> getAssetByClass(TRX trx, String productClassId);

	<TRX> Edge assetBelongsAsset(TRX trx, String assetGuid, String assetBelogsGuid);

	<TRX> Edge assetHasAsset(TRX trx, String assetHasGuid, String assetGuid);

	<TRX> Edge deleteAssetHasAsset(TRX trx, String srcAssetGuid, String destAssetGuid);

	<TRX> Edge deleteAssetBelongsAsset(TRX trx, String destAssetGuid, String srcAssetGuid);

	<TRX> Edge deleteAssetIsClass(TRX trx, String assetGuid, String classGuid);

	<TRX> Boolean deleteNode(TRX trx, String nodeGuid);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);
	<TRX> List<Asset> getAssetByProductClassGuid(TRX trx, String productClassId);
	<TRX>Asset getLicenceAssetBybootStrapOrg(TRX trx, String orgGuid);

	<TRX> List<Asset>  getAllLicenceAssets(TRX trx);

	
	
}
