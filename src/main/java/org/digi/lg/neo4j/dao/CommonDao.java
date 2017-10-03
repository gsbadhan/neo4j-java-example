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

import org.digi.lg.neo4j.pojo.model.ClassMeta;
import org.neo4j.driver.v1.Record;

public interface CommonDao {

	/**
	 * save label,guid,name
	 * 
	 * @param transaction
	 * @param classLabel
	 * @param params
	 * @return
	 */
	<TRX> List<Record> addUpdateClassMeta(TRX trx, String classLabel, Map<String, Object> params);

	<TRX> List<ClassMeta> searchFromClassMeta(TRX trx, String text);

	<TRX> List<ClassMeta> searchFromGraph(TRX trx, String property, String text);

	<TRX> String getLabel(TRX trx, String guid);

}
