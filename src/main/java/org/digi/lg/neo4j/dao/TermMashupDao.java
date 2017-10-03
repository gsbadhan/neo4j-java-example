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
import org.digi.lg.neo4j.pojo.model.TermMashUp;

public interface TermMashupDao {
	<TRX> TermMashUp save(TRX trx, Map<String, Object> params);

	<TRX> TermMashUp getByGuid(TRX trx, String guid);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction);

	<TRX> Boolean deleteNode(TRX trx, String nodeGuid);

}
