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
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.DataShard;
import org.digi.lg.neo4j.pojo.model.Mashup;
import org.digi.lg.neo4j.pojo.model.ScriptRepo;

public interface ScriptRepoDao {
	<TRX> ScriptRepo add(TRX trx, Map<String, Object> params);

	<TRX> ScriptRepo getByUrl(TRX trx, String url);

	<TRX> List<ScriptRepoDao> getScriptRepoInfo(TRX trx, String fromlabel, String fromGuid, Direction direction);

	<TRX> List<Vertex> getScriptRepo(TRX trx);




}
