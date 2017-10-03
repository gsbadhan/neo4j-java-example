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
import org.digi.lg.neo4j.pojo.model.Org;

public interface OrgDao {

	<TRX> Org addUpdate(TRX trx, Map<String, Object> params);

	<TRX> Org getOrgByGuid(TRX transaction, String guid);

	<TRX> Edge orgHasOrg(TRX trx, Map<String, Object> params);

	<TRX> Edge orgIsContract(TRX trx, Map<String, Object> params);

	<TRX> Edge orgHasContract(TRX trx, Map<String, Object> params);

	<TRX> Edge orgBelongsOrg(TRX trx, Map<String, Object> params);

	<TRX> Edge orgHasDataShard(TRX trx, Map<String, Object> params);

	<TRX> boolean isNodeConnected(TRX trx, String srcLabel, String srcPropLabel, String srcPropValue, String dstLabel,
			String dstPropName, String dstPropValue, Relationship relationship, Direction direction);

	<TRX> Edge deleteOrgHasAsset(TRX trx, String orgGuid, String assetGuid);

	<TRX> Edge orgHasAsset(TRX trx, String orgGuid, String assetGuid);

	<TRX> Org getBootStrapKey(TRX trx, String productName, String serialNumber);

	<TRX> Org getOrgBootStrapKey(TRX trx, String bootStrapKey);

	<TRX> Org getImmediateOrgOfAsset(TRX trx, String srcOrgGuid, String assetGuid);

	<TRX> String getBootStrapKeyByOrgAndSrn(TRX trx, String orgName, String serialNumber);

	<TRX> List<Vertex> getOrgsByUser(TRX trx, String contractGuid);

	<TRX> List<Vertex> getOrgsByType(TRX trx, String contractGuid, String assetGuid, String type);

	<TRX> Edge deleteOrgHasOrg(TRX trx, String srcOrgGuid, String destOrgGuid);

	<TRX> Edge deleteOrgBelongsOrg(TRX trx, String destOrgGuid, String srcOrgGuid);

	<TRX> Edge deleteOrgIsClass(TRX trx, String orgGuid, String classGuid);

	<TRX> Edge orgIsClass(TRX trx, String orgGuid, String classGuid);

	<TRX> Boolean deleteNode(TRX trx, String nodeGuid);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);

}
