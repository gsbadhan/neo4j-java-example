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

import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.pojo.model.AuthToken;

public interface AuthTokenDao {
	<TRX> AuthToken add(TRX trx, Map<String, Object> params);

	<TRX> Edge adminUnitHasToken(TRX trx, String adminUnitGuid, String authTokenGuid);

	<TRX> AuthToken getByGuid(TRX transaction, String guid);

	<TRX> Boolean deleteAuthToken(TRX trx, String tokenGuid);

}
