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
import java.util.Optional;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.ClassX;

public interface ClassDao {

	<TRX> Vertex save(TRX trx, String label, Map<String, Object> params);

	<TRX> Vertex saveUpdate(TRX trx, String label, Map<String, Object> params);

	<TRX> Edge classHasClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params);

	<TRX> Edge classBelongsClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params);

	<TRX> Edge classIsClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params);

	<TRX> Edge classTemplateGlobalClasess(TRX trx, String srcLabel, Map<String, Object> params);

	<TRX> Edge classTemplateGlobalProducts(TRX trx, String srcLabel, Map<String, Object> params);

	<TRX> Edge classTemplateGlobalOrg(TRX trx, String srcLabel, Map<String, Object> params);

	<TRX> Edge classHasDataShard(TRX trx, String srcLabel, Map<String, Object> params);

	<TRX> ClassX getByGuid(TRX trx, String label, String guid);

	<TRX> ClassX getByGuid(TRX trx, String guid);

	<TRX, RET> RET getByProperty(TRX trx, Class<RET> returnType, String property, String propertyValue);

	<TRX, RET> RET getVertex(TRX trx, Class<RET> returnType, String label, String property, String propertyValue);

	<TRX> Vertex getInVertex(TRX trx, String label, String property, String propertyValue, Relationship relationship,
			Optional<String> destLabel, boolean withParentNode);

	<TRX> Vertex getOutVertex(TRX trx, String srcLabel, String property, String propertyValue,
			Relationship relationship, Optional<String> destLabel, boolean withParentNode);

	<TRX> Vertex getBothVertex(TRX trx, String label, String property, String propertyValue, Relationship relationship,
			Optional<String> destLabel, boolean withParentNode);

	<TRX> boolean isNodeConnectedIn(TRX trx, Optional<String> srcLbl, org.digi.lg.neo4j.core.Relationship relType,
			Map<String, Object> params);

	<TRX> boolean isNodeConnectedIn(TRX trx, Optional<String> srcLbl, Optional<String> destLbl,
			org.digi.lg.neo4j.core.Relationship relType, Map<String, Object> params);

	<TRX> Edge classHasDataItem(TRX trx, String srcLabel, Map<String, Object> params);

	<TRX> Edge classHasAggDataItem(TRX trx, String srcLabel, Map<String, Object> params);

	<TRX> Edge classHasEvents(TRX trx, String srcLabel, Map<String, Object> params);

	<TRX> List<Vertex> getMandatoryPropertyVertex(TRX trx, Optional<String> srcLbl, Map<String, Object> params);

	<TRX> Edge classConnnectedToClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> params);

	<TRX> Edge unLinkClassConnnectedToClass(TRX trx, String srcLabel, String destLabel, Map<String, Object> paramMap);

	<TRX> void unlinkAllClassesConnnectedToGateway(TRX trx, String srcLabel, Map<String, Object> paramMap);

	<TRX> Edge createEdge(TRX transaction, String srcLabel, String srcProperty, String srcPropertyVal, String destLabel,
			String destProperty, String destPropertyVal, Relationship relationship, Direction direction);

	<TRX> ClassX getByName(TRX trx, String classLabel, String name);

	<TRX> List<Vertex> getClasssAndProductClasses(TRX trx, String srcLabel, String property, String propertyValue,
			Relationship relationship, Direction direction);

	<TRX> Edge deleteClassIsClass(TRX trx, String srcClassGuid, String destClassGuid);

	<TRX> ClassX getDomainClass(TRX trx, String principalGuid, String contractTypeGuid);

	<TRX> boolean isClassExist(TRX trx, String label, String property, String value);

	<TRX> Boolean deleteNode(TRX trx, String nodeGuid);

	<TRX> boolean isNodeConnected(TRX trx, String srcLabel, String srcPropLabel, String srcPropValue, String dstLabel,
			String dstPropName, String dstPropValue, Relationship relationship, Direction direction);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction);

	<TRX> ClassX getDomainChildClass(TRX trx, String domainClassGuid, String childClassName);

	<TRX> ClassX getDomainClassByContract(TRX trx, String contractGuid, String domainClassGuid);
}
