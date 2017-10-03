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
import org.digi.lg.neo4j.pojo.model.TermData;

public interface TermDataDao {
	<TRX> TermData save(TRX trx, Map<String, Object> params);

	<TRX> TermData getByGuid(TRX trx, String guid);

	<TRX> Edge termHasVertex(TRX trx, String sourceLabel, String sourceGuid, String destLabel, String destGuid,
			Relationship rel, Direction dir);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);

	<TRX> Boolean deleteNode(TRX trx, String nodeGuid);

}
