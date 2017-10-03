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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.pojo.model.ConfigItem;
import org.digi.lg.neo4j.queries.ConfigItemQuery;
import org.neo4j.driver.v1.Record;

public class ConfigItemDaoImpl extends CRUD implements ConfigItemDao {
	private final BaseDao baseDao;
	private final ConfigItemQuery configItemQuery;

	protected ConfigItemDaoImpl(final BaseDao baseDao, final ConfigItemQuery configItemQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.configItemQuery = checkNotNull(configItemQuery);
	}

	@Override
	public <TRX> ConfigItem save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_CONFIG_ITEM, params);
		return new ConfigItem(saveUpdate(trx, SchemaConstants.LABEL_CONFIG_ITEM, params));
	}

	@Override
	public <TRX> ConfigItem getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, configItemQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return ConfigItem.rowMapper(DaoUtil.parseNode(records, "ci"));
	}

	@Override
	public <TRX> Boolean deleteNode(TRX trx, String nodeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, nodeGuid);
		baseDao.executeQuery(trx, configItemQuery.getDetachDeleteNode(), bindParams);
		return true;
	}

}
