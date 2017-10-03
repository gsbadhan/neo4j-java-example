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
import java.util.Map;
import java.util.Properties;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class PrincipalDaoImplTestIT {
	private Properties configs;
	private GraphFactory graphFactory;
	private PrincipalDao principalDao;
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
		this.principalDao = new PrincipalDaoImpl(baseDao, commonDao, QueryLoader.principalQuery());
	}

	@Test
	public void testAddUpdate() {
		Session session = graphFactory.writeSession();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(SchemaConstants.PROP_HDMFID_NAME, "pr-1001-test");
		paramMap.put(SchemaConstants.PROP_NAME, "per-1001-testggg");
		Principal principal = principalDao.save(session, paramMap);
		graphFactory.closeSession(session);
		assertNotNull(principal);
	}

	@Test
	public void testAddUpdateWithExtraParams() {
		Session session = graphFactory.writeSession();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(SchemaConstants.PROP_HDMFID_NAME, "pr-1001-test");
		paramMap.put(SchemaConstants.PROP_NAME, "per-1001-test");
		paramMap.put(SchemaConstants.PROP_AUTH_TOKEN, "xxx-yyy-zz");
		Principal principal = principalDao.save(session, paramMap);
		graphFactory.closeSession(session);
		assertNotNull(principal);
	}

	@Test
	public void testPrincipalBelongsAdminUnit() {
		Session session = graphFactory.writeSession();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(BindConstants.SRC, "pr-1001");
		paramMap.put(BindConstants.DEST, "ad-1001");
		paramMap.put(BindConstants.IS_ADMIN, SchemaConstants.IS_ADMIN_YES);
		Edge edge = principalDao.principalBelongsAdminUnit(session, paramMap);
		graphFactory.closeSession(session);
		assertNotNull(edge);
	}

	@Test
	public void testDetachPrincipalBelongsAdminUnit() {
		Session session = graphFactory.writeSession();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(BindConstants.SRC, "pr-1001");
		paramMap.put(BindConstants.DEST, "ad-1001");
		paramMap.put(BindConstants.IS_ADMIN, SchemaConstants.IS_ADMIN_NO);
		Edge edge = principalDao.detachPrincipalBelongsAdminUnit(session, paramMap);
		graphFactory.closeSession(session);
		assertNotNull(edge);
	}

}
