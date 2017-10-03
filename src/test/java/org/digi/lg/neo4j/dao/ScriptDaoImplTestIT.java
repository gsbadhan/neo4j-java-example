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
import org.digi.lg.neo4j.core.ScriptTemplateScope;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class ScriptDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private ScriptDao scriptDao;
	private BaseDao baseDao;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.scriptDao = new ScriptDaoImpl(baseDao, QueryLoader.scriptQuery());
	}

	@Test
	public void testGetScriptsByAduGuid() {
		Session session = graphFactory.readSession();
		String aduGuid = "ad-1001";
		List<Vertex> list = scriptDao.getScriptsByAduGuid(session, aduGuid);
		assertNotNull(list);
		graphFactory.closeSession(session);
	}

	@Test
	public void testGetScriptConfigItems() {
		Session session = graphFactory.readSession();
		String scriptGuid = "scp-1001";
		List<Vertex> list = scriptDao.getScriptConfigItems(session, scriptGuid);
		assertNotNull(list);
		graphFactory.closeSession(session);
	}

	@Test
	public void testGetScriptAssets() {
		Session session = graphFactory.readSession();
		String scriptGuid = "scp-1001";
		List<Vertex> list = scriptDao.getScriptAssets(session, scriptGuid, ScriptTemplateScope.PUBLIC);
		assertNotNull(list);
		graphFactory.closeSession(session);
	}

	@Test
	public void testCountScriptTemplateInstance() {
		Session session = graphFactory.readSession();
		String astLabel = "asset";
		String assetGuid = "xx-1001";
		String scriptTemplateGuid = "scp-1001";
		Long count = scriptDao.countScriptTemplateInstance(session, scriptTemplateGuid, assetGuid, astLabel);
		assertNotNull(count);
		graphFactory.closeSession(session);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

}
