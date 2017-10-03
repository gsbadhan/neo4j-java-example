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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.GraphTransaction;
import org.digi.lg.neo4j.pojo.model.App;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

public class AppDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private AppDao appDao;
	private BaseDao baseDao;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.appDao = new AppDaoImpl(baseDao, QueryLoader.appQuery());
	}

	@Test
	public void testAddUpdate() {
		Session session = graphFactory.writeSession();
		Transaction transaction = GraphTransaction.start(session);
		Map<String, Object> params = new HashMap<>();
		params.put("guid", "testApp");
		params.put("name", "testApp");
		params.put("appid", "testApp");
		App app = appDao.addUpdate(transaction, params);
		GraphTransaction.commit(transaction);
		GraphTransaction.close(transaction);
		graphFactory.closeSession(session);

		assertTrue(app != null);
		assertTrue(app.getVertex() != null);
	}

	@Test
	public void testGetAppId() {
		Session session = graphFactory.readSession();
		String guid = "wiproadmin";
		App app = appDao.getAppById(session, guid);
		graphFactory.closeSession(session);

		assertTrue(app != null);
		assertTrue(app.getVertex() != null);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

}
