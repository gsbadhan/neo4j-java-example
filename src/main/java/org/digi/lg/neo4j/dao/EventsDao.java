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

import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.model.Events;
import org.neo4j.driver.v1.Record;

public interface EventsDao {

	<TRX> Events save(TRX trx, Map<String, Object> params);

	<TRX> Events getByGuid(TRX trx, String guid);

	<TRX> Edge eventsHasEventype(TRX trx, String eventsGuid, String eventTypeGuid);

	<TRX> List<Events> getByContractType(TRX trx, String contractTypeId);

	<TRX> List<Record> getEventsClasses(TRX trx, String contractTypeGuid);

	<TRX> List<Events> getEventsByScriptByAssetId(TRX trx, String assetGuid, ClassX eventClass);

	<TRX> List<Events> getEventsByScriptByProductClassId(TRX trx, String productClassGuid, ClassX eventClass);

	<TRX> Boolean deleteNode(TRX trx, String nodeGuid);

}
