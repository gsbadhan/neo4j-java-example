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
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.App;
import org.digi.lg.neo4j.queries.AppQuery;
import org.neo4j.driver.v1.Record;

public class AppDaoImpl implements AppDao {
	private final BaseDao baseDao;
	private final AppQuery appQuery;

	protected AppDaoImpl(final BaseDao baseDao, final AppQuery appQuery) {
		this.baseDao = checkNotNull(baseDao);
		this.appQuery = checkNotNull(appQuery);
	}

	@Override
	public <TRX> App addUpdate(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, appQuery.getAddUpdate(), params);
		Vertex vertex = new Vertex(SchemaConstants.LABEL_APP, parseNode(records, "ap"));
		return new App(vertex);
	}

	@Override
	public <TRX> List<Record> appHasContractType(TRX trx, Map<String, Object> params) {
		return baseDao.executeQuery(trx, appQuery.appHasContractType(), params);
	}

	@Override
	public <TRX> App getAppByName(TRX trx, String name) {
		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.PROP_NAME, name);
		List<Record> records = baseDao.executeQuery(trx, appQuery.getAppByName(), params);
		if (records != null) {
			return App.rowMapper(parseNode(records, "ap"));
		}
		return null;
	}

	@Override
	public <TRX> App getAppById(TRX trx, String guid) {
		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.PROP_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, appQuery.getAppById(), params);
		if (records != null) {
			return App.rowMapper(parseNode(records, "ap"));
		}
		return null;
	}

	@Override
	public <TRX> App getAppByGuid(TRX trx, String guid) {
		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, appQuery.getAppByGuid(), params);
		if (records != null) {
			return App.rowMapper(parseNode(records, "ap"));
		}
		return null;
	}

	@Override
	public <TRX> App getAppByContractType(TRX trx, String contractTypeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, contractTypeGuid);
		List<Record> records = baseDao.executeQuery(trx, appQuery.getAppByContractType(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return App.rowMapper(parseNode(records, "ap"));
	}

}
