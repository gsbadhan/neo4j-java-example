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

import java.util.Map;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.pojo.model.Principal;

public interface PrincipalDao {

	<TRX> Principal save(TRX trx, Map<String, Object> params);

	<TRX> Edge principalBelongsAdminUnit(TRX trx, Map<String, Object> paramMap);

	<TRX> Edge detachPrincipalBelongsAdminUnit(TRX trx, Map<String, Object> paramMap);

	<TRX> Principal getByGuid(TRX trx, String guid);

	<TRX> Principal getByPrincipalId(TRX trx, String principalId);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);

	<TRX> Principal saveUpdate(TRX trx, Map<String, Object> params);
}
