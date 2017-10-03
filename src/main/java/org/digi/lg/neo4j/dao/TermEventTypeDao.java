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

import java.util.Map;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.pojo.model.TermEventType;

public interface TermEventTypeDao {
	<TRX> TermEventType save(TRX trx, Map<String, Object> params);

	<TRX> TermEventType getByGuid(TRX trx, String guid);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);

	<TRX> Boolean deleteNode(TRX trx, String nodeGuid);

}
