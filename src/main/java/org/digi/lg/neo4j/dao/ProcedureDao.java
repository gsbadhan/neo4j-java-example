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

import org.digi.lg.neo4j.core.Vertex;

public interface ProcedureDao {
	List<Vertex> getVertices(String query, Map<String, Object> params);

	List<Map<String, Object>> getMap(String query, Map<String, Object> params);

	List<List<Map<String, Object>>> getListMap(String query, Map<String, Object> params);

}
