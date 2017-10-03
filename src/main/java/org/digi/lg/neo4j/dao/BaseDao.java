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
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

public interface BaseDao {
	/**
	 * use for select/write query with Session/Transaction
	 * 
	 * @param transaction
	 * @param query
	 * @param bindParams
	 * @return
	 */
	<TRX> List<Record> executeQuery(TRX transaction, String query, Map<String, Object> bindParams);

	/**
	 * use for select(read)/write query without passing Session/Transaction
	 * 
	 * @param query
	 * @param bindParams
	 * @return
	 */
	List<Record> executeQuery(String query, Map<String, Object> bindParams);

	/**
	 * use for select(read)/write query without passing Session/Transaction
	 * 
	 * @param query
	 * @param bindParams
	 * @return
	 */
	StatementResult executeQueryx(Session session, String query, Map<String, Object> bindParams);

}
