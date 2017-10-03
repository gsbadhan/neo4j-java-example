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

import static org.junit.Assert.assertFalse;

import java.util.Properties;

import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class ProductClassmplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private ProductClassDao productClassDao;
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
		this.productClassDao = new ProductClassDaoImpl(baseDao, commonDao, QueryLoader.productClassQuery());
	}

	@Test
	public void testIsAssetNameExistUnderClass() {
		Session session = graphFactory.writeSession();
		String prdctClassName = "hp probook g10";
		String assetName = "hp-asset-12346";
		boolean isExist = productClassDao.isAssetNameExistUnderClass(session, prdctClassName, assetName);
		assertFalse(isExist);
		graphFactory.closeSession(session);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

}
