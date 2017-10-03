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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.queries.GlobalDataItemQuery;
import org.neo4j.driver.v1.Record;

public class GlobalDataItemDaoImpl implements GlobalDataItemDao {
	private final BaseDao baseDao;
	private final GlobalDataItemQuery globalDataItemQuery;

	protected GlobalDataItemDaoImpl(final BaseDao baseDao, final GlobalDataItemQuery globalDataItemQuery) {
		this.baseDao = checkNotNull(baseDao);
		this.globalDataItemQuery = checkNotNull(globalDataItemQuery);
	}

	@Override
	public <TRX> List<Record> globalDataItemIsClass(TRX trx, String destClassLabel, Map<String, Object> params) {
		String query = DaoUtil.destLabelQueryBuilder(globalDataItemQuery.getGlobalDataItemIsClass(),
				DaoUtil.handleHyphen(destClassLabel));
		return baseDao.executeQuery(trx, query, params);
	}

}
