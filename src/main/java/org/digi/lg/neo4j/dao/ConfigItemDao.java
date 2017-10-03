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

import org.digi.lg.neo4j.pojo.model.ConfigItem;

public interface ConfigItemDao {
	<TRX> ConfigItem save(TRX trx, Map<String, Object> params);

	<TRX> ConfigItem getByGuid(TRX trx, String guid);

	<TRX> Boolean deleteNode(TRX trx, String nodeGuid);

}
