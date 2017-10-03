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

import org.digi.lg.neo4j.pojo.model.App;
import org.neo4j.driver.v1.Record;

public interface AppDao {
	<TRX> App addUpdate(TRX trx, Map<String, Object> params);

	<TRX> List<Record> appHasContractType(TRX trx, Map<String, Object> params);

	<TRX> App getAppByName(TRX transaction, String name);

	<TRX> App getAppById(TRX transaction, String guid);

	<TRX> App getAppByGuid(TRX transaction, String guid);

	public <TRX> App getAppByContractType(TRX trx, String contractTypeGuid);

}
