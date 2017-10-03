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
import org.digi.lg.neo4j.pojo.model.EventType;
import org.digi.lg.neo4j.queries.EventTypeQuery;
import org.neo4j.driver.v1.Record;

public class EventTypeDaoImpl extends CRUD implements EventTypeDao {
	private final BaseDao baseDao;
	private final EventTypeQuery eventTypeQuery;

	protected EventTypeDaoImpl(final BaseDao baseDao, final EventTypeQuery eventTypeQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.eventTypeQuery = checkNotNull(eventTypeQuery);
	}

	@Override
	public <TRX> EventType save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_EVENT_TYPE, params);
		return new EventType(saveUpdate(trx, SchemaConstants.LABEL_EVENT_TYPE, params));
	}

	@Override
	public <TRX> EventType getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, eventTypeQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return EventType.rowMapper(DaoUtil.parseNode(records, "et"));
	}

}
