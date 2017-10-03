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
import org.digi.lg.neo4j.pojo.model.ProductClass;

public interface ProductClassDao {

	<TRX> Vertex save(TRX trx, Map<String, Object> params);

	<TRX> ProductClass getByGuid(TRX trx, String guid);

	<TRX> Edge productClassHasDataItem(TRX trx, Map<String, Object> params);

	<TRX> Edge productClassHasAggDataItem(TRX trx, Map<String, Object> params);

	<TRX> Edge productClassHasEvents(TRX trx, Map<String, Object> params);

	<TRX> boolean isAssetNameExistUnderClass(TRX trx, String prdctClassName, String assetName);

	<TRX> ProductClass getByName(TRX trx, String name);

	<TRX> List<Vertex> getAllProductClasses(TRX trx);

	<TRX> Edge deleteProductClassIsClass(TRX trx, String srcClassGuid, String destClassGuid);

	<TRX> Edge productClassIsClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params);

	<TRX> Edge deleteClassIsProductClass(TRX trx, String srcClassGuid, String destClassGuid);

	<TRX> Edge classIsProductClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params);

	<TRX> Edge deleteProductClassIsProductClass(TRX trx, String srcClassGuid, String destClassGuid);

	<TRX> Edge productClassIsProductClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcClassGuid, String destLabel, String destClassGuid,
			Relationship relType, Direction direction);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);

}
