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
import org.digi.lg.neo4j.pojo.model.DataItem;

public interface DataItemDao {

	<TRX> DataItem save(TRX trx, Map<String, Object> params);

	<TRX> DataItem getByGuid(TRX trx, String guid);

	<TRX> List<DataItem> getByContractType(TRX trx, String contractTypeId);

	<TRX> List<Edge> getEdgesByProductClass(TRX trx, String dataItemId, String classId);

	<TRX> List<DataItem> getDataItemByProductClass(TRX trx, String productClassId);

	<TRX> List<DataItem> getDataItemByProductClass(TRX trx, String productClassId, String dataItemName);

	<TRX> List<Vertex> getDataItems(TRX trx, String contractTypeGuid);

	<TRX> boolean isNodeConnected(TRX trx, String srcLabel, String srcPropLabel, String srcPropValue, String dstLabel,
			String dstPropName, String dstPropValue, Relationship relationship, Direction direction);

	<TRX> Boolean deleteNode(TRX trx, String nodeGuid);

}
