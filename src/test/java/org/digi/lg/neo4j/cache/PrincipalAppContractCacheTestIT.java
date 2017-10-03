/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.cache;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.dao.BaseDao;
import org.digi.lg.neo4j.dao.BaseDaoImpl;
import org.digi.lg.neo4j.dao.ClassDao;
import org.digi.lg.neo4j.dao.ClassDaoImpl;
import org.digi.lg.neo4j.dao.CommonDao;
import org.digi.lg.neo4j.dao.CommonDaoImpl;
import org.digi.lg.neo4j.dao.ContractDao;
import org.digi.lg.neo4j.dao.ContractDaoImpl;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PrincipalAppContractCacheTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private ContractDao contractDao;
	private ClassDao classDao;
	private BaseDao baseDao;
	private CommonDao commonDao;
	private PrincipalAppContractCache personAppContractCache;

	@Before
	public void setUp() throws Exception {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.commonDao = new CommonDaoImpl(baseDao, QueryLoader.commonQuery());
		this.contractDao = new ContractDaoImpl(baseDao, commonDao, QueryLoader.contractQuery());
		this.contractDao = new ContractDaoImpl(baseDao, commonDao, QueryLoader.contractQuery());
		this.classDao = new ClassDaoImpl(baseDao, commonDao, QueryLoader.classQuery());
		personAppContractCache = new PrincipalAppContractCache(graphFactory, contractDao, classDao);
	}

	@Test
	public void testGetContractVertex() {
		String principalId = "lgmanturbo@gmail.com";
		String appId = "WiproAdmin";
		// should fetch from DB
		ContractVertex contractVertex = personAppContractCache.get(principalId, appId);
		assertNotNull(contractVertex);

		// should fetch from cache
		contractVertex = personAppContractCache.get(principalId, appId);

		assertNotNull(contractVertex);
	}

	@Test
	public void testExpireContract() {
		String principalId = "lgmanturbo@gmail.com";
		String appId = "WiproAdmin";
		// should fetch from DB
		ContractVertex contractVertex = personAppContractCache.get(principalId, appId);
		assertTrue(contractVertex == null);
	}

	@Test
	public void testVailidContract() {
		String principalId = "lgmanturbo@gmail.com";
		String appId = "WiproAdmin";
		// should fetch from DB
		ContractVertex contractVertex = personAppContractCache.get(principalId, appId);
		assertTrue(contractVertex != null);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

}
