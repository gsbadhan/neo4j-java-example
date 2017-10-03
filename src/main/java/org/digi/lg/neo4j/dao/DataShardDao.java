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

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.pojo.model.DataShard;

public interface DataShardDao {

	<TRX> DataShard incrementCount(TRX trx);

	<TRX> List<DataShard> getDataShard(TRX trx, String fromlabel, String fromGuid, Direction direction);

	<TRX> Edge dsBelongsClass(TRX trx, String destClassLabel, Map<String, Object> params);

	<TRX> Edge dsBelongsOrg(TRX trx, Map<String, Object> params);

	<TRX> Edge classHasDs(TRX trx, String anyClassLabel, Map<String, Object> params);

	<TRX> Edge dsBelongsScript(TRX trx, String dsGuid, String scriptGuid);

	<TRX> Edge dsBelongsScriptTemplate(TRX trx, String dsGuid, String scriptTemplateGuid);
}
