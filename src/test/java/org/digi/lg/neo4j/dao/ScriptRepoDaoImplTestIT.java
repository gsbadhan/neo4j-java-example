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

import java.util.List;
import java.util.Properties;

import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class ScriptRepoDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private ScriptRepoDao scriptRepoDao;
	private BaseDao baseDao;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.scriptRepoDao = new ScriptRepoDaoImpl(baseDao, QueryLoader.ScriptRepoQuery());
	}

	@Test
	public void testGetScriptRepo() {
		Session session = graphFactory.readSession();
		String aduGuid = "f9c92109-37b3-4ef4-b36e-387807448fa5";
		List<Vertex> count = scriptRepoDao.getScriptRepo(session);
		assertNotNull(count);
		graphFactory.closeSession(session);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

}
