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

import java.util.Properties;

import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class AuthTokenDaoImplTestIT {
	private Properties configs;
	private GraphFactory graphFactory;
	private AuthTokenDao authTokenDao;
	private BaseDao baseDao;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.authTokenDao = new AuthTokenDaoImpl(baseDao, QueryLoader.authTokenQuery());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddUpdate() {
		Session session = graphFactory.writeSession();
		String tokenGuid = "tkn-1001";
		boolean deleted = authTokenDao.deleteAuthToken(session, tokenGuid);
		assertTrue(deleted);
		graphFactory.closeSession(session);
	}

}
