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

import org.digi.lg.neo4j.pojo.model.EventType;

public interface EventTypeDao {
	<TRX> EventType save(TRX trx, Map<String, Object> params);

	<TRX> EventType getByGuid(TRX trx, String guid);

}
