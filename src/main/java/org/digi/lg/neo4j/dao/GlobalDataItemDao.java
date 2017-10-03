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

import org.neo4j.driver.v1.Record;

public interface GlobalDataItemDao {

	<TRX> List<Record> globalDataItemIsClass(TRX trx, String destClassLabel, Map<String, Object> params);

}
