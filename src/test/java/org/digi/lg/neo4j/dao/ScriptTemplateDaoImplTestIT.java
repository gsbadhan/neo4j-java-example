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

public class ScriptTemplateDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private ScriptTemplateDao scriptDao;
	private BaseDao baseDao;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.scriptDao = new ScriptTemplateDaoImpl(baseDao, QueryLoader.scriptTemplateQuery());
	}

	@Test
	public void testGetScriptsByAduGuid() {
		Session session = graphFactory.readSession();
		String aduGuid = "ad-1001";
		List<Vertex> list = scriptDao.getScriptTemplateInfoByAduGuid(session, aduGuid);
		assertNotNull(list);
		graphFactory.closeSession(session);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

}
