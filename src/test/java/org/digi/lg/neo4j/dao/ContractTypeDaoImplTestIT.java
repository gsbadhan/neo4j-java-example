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

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class ContractTypeDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private ContractTypeDao contractTypeDao;
	private BaseDao baseDao;
	private CommonDao commonDao;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.commonDao = new CommonDaoImpl(baseDao, QueryLoader.commonQuery());
		this.contractTypeDao = new ContractTypeDaoImpl(baseDao, commonDao, QueryLoader.contractTypeQuery());
	}

	@Test
	public void testGetContractTypeInHasClassVertices() {
		Session session = graphFactory.readSession();
		String guid = "ctt-1001";
		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Vertex> list = contractTypeDao.getContractTypeInHasClassVertices(session, params);
		assertNotNull(list);
		graphFactory.closeSession(session);
	}

	@Test
	public void testGetContractTypeInIsContractType() {
		Session session = graphFactory.readSession();
		String guid = "ctt-1001";
		List<Vertex> list = contractTypeDao.getContractTypeInIsContractType(session, guid);
		assertNotNull(list);
		graphFactory.closeSession(session);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

}
