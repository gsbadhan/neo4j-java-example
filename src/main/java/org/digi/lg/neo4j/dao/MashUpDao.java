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
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Mashup;

public interface MashUpDao {
	<TRX> Mashup add(TRX trx, Map<String, Object> params);
	<TRX> Edge mashupHasScriptRepo(TRX trx, String anyClassLabel, Map<String, Object> params);

	<TRX> Mashup getByGuid(TRX trx, String guid);
	<TRX> Mashup getScriptRepobyPath(TRX trx, String path);

	<TRX> Mashup getByPath(TRX trx, String guid);
	<TRX> Edge mashupHasAdu(TRX trx, String mashupGuid, String aduGuid);
	<TRX> List<Vertex> getMashupRepoItems(TRX trx, String mashupath);


	<TRX> List<Vertex> getMashupInfoByAduGuid(TRX trx, String aduGuid);


}
