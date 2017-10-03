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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.model.Events;
import org.digi.lg.neo4j.queries.EventsQuery;
import org.neo4j.driver.v1.Record;

public class EventsDaoImpl extends CRUD implements EventsDao {
	private final BaseDao baseDao;
	private final EventsQuery eventQuery;

	protected EventsDaoImpl(final BaseDao baseDao, final CommonDao commonDao, final EventsQuery eventQuery) {
		super(baseDao, commonDao);

		this.baseDao = checkNotNull(baseDao);
		this.eventQuery = checkNotNull(eventQuery);
	}

	@Override
	public <TRX> Events save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_EVENTS, params);
		return new Events(save(trx, SchemaConstants.LABEL_EVENTS, params));
	}

	@Override
	public <TRX> Events getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, eventQuery.getByGuid(), bindParams);
		if (records != null && !records.isEmpty()) {
			return Events.rowMapper(parseNode(records, "et"));
		}
		return null;
	}

	@Override
	public <TRX> Edge eventsHasEventype(TRX trx, String eventsGuid, String eventTypeGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_EVENTS, SchemaConstants.PROP_HDMFID_NAME, eventsGuid,
				SchemaConstants.LABEL_EVENT_TYPE, SchemaConstants.PROP_HDMFID_NAME, eventTypeGuid, Relationship.HAS,
				Direction.OUT);
	}

	@Override
	public <TRX> List<Record> getEventsClasses(TRX trx, String contractTypeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, contractTypeGuid);
		List<Record> list = baseDao.executeQuery(trx, eventQuery.getEventsClassesTreeA(), bindParams);
		if (list == null || list.isEmpty()) {
			list = baseDao.executeQuery(trx, eventQuery.getEventsClassesTreeB(), bindParams);
		}
		return list;
	}

	@Override
	public <TRX> List<Events> getByContractType(TRX trx, String contractTypeId) {
		List<Events> events = new ArrayList<>();
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.CONTRACT_TYPE_ID, contractTypeId);
		List<Record> records = baseDao.executeQuery(trx, eventQuery.getEventsByContractType(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		records.forEach(record -> events.add(Events.rowMapper(parseNode(record, "et"))));

		return events;
	}

	@Override
	public <TRX> List<Events> getEventsByScriptByAssetId(TRX trx, String assetGuid, ClassX eventClass) {
		Map<String, Object> bindParams = new HashMap<>(2);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, assetGuid);
		bindParams.put(BindConstants.CGUID, eventClass.getGuid());
		List<Record> records = baseDao.executeQuery(trx, eventQuery.getEventsByScriptByAssetId(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Events> events = new LinkedList<>();
		records.forEach(record -> events.add(Events.rowMapper(parseNode(record, "et"))));
		return events;
	}

	@Override
	public <TRX> List<Events> getEventsByScriptByProductClassId(TRX trx, String productClassGuid, ClassX eventClass) {
		Map<String, Object> bindParams = new HashMap<>(2);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, productClassGuid);
		bindParams.put(BindConstants.CGUID, eventClass.getGuid());
		List<Record> records = baseDao.executeQuery(trx, eventQuery.getEventsByScriptProductId(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Events> events = new LinkedList<>();
		records.forEach(record -> events.add(Events.rowMapper(parseNode(record, "et"))));
		return events;
	}

	@Override
	public <TRX> Boolean deleteNode(TRX trx, String nodeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, nodeGuid);
		baseDao.executeQuery(trx, eventQuery.getDetachDeleteNode(), bindParams);
		return true;
	}

}
