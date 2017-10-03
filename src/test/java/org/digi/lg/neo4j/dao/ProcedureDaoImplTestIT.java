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

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.Vertex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcedureDaoImplTestIT {
	private Properties configs;
	private GraphFactory graphFactoryRW;
	private GraphFactory graphFactoryRD;
	private BaseDao baseDao;
	private ProcedureDao procedureDao;

	@Before
	public void setUp() throws Exception {
		this.configs = GraphContext.getConfig();
		this.graphFactoryRW = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactoryRW.init();
		// for testing purpose not initializing separate factory.
		graphFactoryRD = graphFactoryRW;
		this.baseDao = new BaseDaoImpl(this.graphFactoryRW);
		this.procedureDao = new ProcedureDaoImpl(graphFactoryRW, graphFactoryRD, baseDao);
	}

	@Test
	public void testGetVertices() {
		Map<String, Object> params = new HashMap<>(3);
		params.put("adminUnitId", "ad-1001");
		params.put("contractTypeId", "ctt-1001");
		params.put("principalVertexId", "pr-1002");
		String query = "call org.digi.lg.neo4j.udf.authorization.authorizePrincipal({adminUnitId},{contractTypeId},{principalVertexId})";
		List<Vertex> vertexs = procedureDao.getVertices(query, params);
		assertNotNull(vertexs);

	}

	@Test
	public void testGetMap() {
		Map<String, Object> params = new HashMap<>(6);
		params.put("userId", "p1@gmail.com");
		params.put("appId", "WiproAdmin");
		params.put("contractTypeId", "ctt-1001");
		params.put("contractId", "ct-1001");
		params.put("filters", null);
		params.put("pageOffset", 0);
		String query = "CALL org.digi.lg.neo4j.udf.data.dataProcedures.getDIClasses({userId},{appId},{contractTypeId},{contractId},{filters},{pageOffset})";
		List<Map<String, Object>> list = procedureDao.getMap(query, params);
		assertNotNull(list);

	}

	@After
	public void tearDown() throws Exception {
	}

}
