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
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.App;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;

public class ContractDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private ContractDao contractDao;
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
		this.contractDao = new ContractDaoImpl(baseDao, commonDao, QueryLoader.contractQuery());
	}

	@Test
	public void testSave() {
		Session session = graphFactory.writeSession();
		Map<String, Object> params = new HashMap<>();
		params.put("end_date", "1503401518355");
		params.put("name", "p39");
		params.put("guid", "b70e8469-08d8-40b2-bbba-0767d239a0f0");
		params.put("start_date", "1495625518355");

		Contract contract = contractDao.save(session, params);
		assertNotNull(contract);
		graphFactory.closeSession(session);
	}

	@Test
	public void testGetContract() {
		Session session = graphFactory.writeSession();
		String principalId = "gurpreet@gmail.com";
		String appId = "WiproAdmin";
		List<Record> records = contractDao.getContract(session, principalId, appId);
		assertNotNull(records);
		assertNotNull(Principal.rowMapper(((InternalNode) records.get(0).asMap().get("pr")).asMap()));
		assertNotNull(AdminUnit.rowMapper(((InternalNode) records.get(0).asMap().get("ad")).asMap()));
		assertNotNull(Contract.rowMapper(((InternalNode) records.get(0).asMap().get("ct")).asMap()));
		assertNotNull(ContractType.rowMapper(((InternalNode) records.get(0).asMap().get("ctt")).asMap()));
		assertNotNull(App.rowMapper(((InternalNode) records.get(0).asMap().get("ap")).asMap()));
		graphFactory.closeSession(session);

	}

	@Test
	public void testGetContractGuid() {
		Session session = graphFactory.writeSession();
		String guid = "ct-hnda-1001";
		Contract contract = contractDao.getContract(session, guid);
		assertNotNull(contract);
		graphFactory.closeSession(session);
	}

	@Test
	public void testGetContractInHasClassVertices() {
		Session session = graphFactory.readSession();
		String guid = "ct-1001";
		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Vertex> list = contractDao.getContractInHasOrgVertices(session, params);
		assertNotNull(list);
		graphFactory.closeSession(session);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

}
