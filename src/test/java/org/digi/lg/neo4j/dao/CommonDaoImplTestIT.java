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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.GraphTransaction;
import org.digi.lg.neo4j.domain.services.DataProviderService.FindProperty;
import org.digi.lg.neo4j.pojo.model.ClassMeta;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

public class CommonDaoImplTestIT {
	private Properties configs;
	private GraphFactory graphFactory;
	private CommonDao commonDao;
	private BaseDao baseDao;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.commonDao = new CommonDaoImpl(baseDao, QueryLoader.commonQuery());
	}

	@Test
	public void testAddUpdate() {

		Session session = graphFactory.writeSession();
		Transaction transaction = GraphTransaction.start(session);
		Map<String, Object> params = new HashMap<>();
		List<Record> records = null;
		for (int i = 1; i <= 10000; i++) {

			params.put("guid", "guid-000-" + i);
			params.put("name", "test" + i);
			try {
				records = commonDao.addUpdateClassMeta(transaction, ("Class_" + params.get("guid")), params);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		GraphTransaction.commit(transaction);
		GraphTransaction.close(transaction);
		graphFactory.closeSession(session);

		assertTrue(records != null);
		assertTrue(records.size() == 1);

	}

	@Test
	public void testSearchFromClassMeta() {
		Session session = graphFactory.readSession();
		List<ClassMeta> list = commonDao.searchFromClassMeta(session, "gu");
		assertTrue(list != null);
		assertTrue(list.size() >= 1);
		graphFactory.closeSession(session);
	}

	@Test
	public void testSearchFromGraph() {
		Session session = graphFactory.readSession();
		FindProperty property = FindProperty.ANY;
		property.setColumn("name");
		List<ClassMeta> list = commonDao.searchFromGraph(session, property.getColumn(), "gu");
		assertTrue(list != null);
		assertTrue(list.size() >= 1);
		graphFactory.closeSession(session);
	}

	@Test
	public void testGetLabel() {
		Session session = graphFactory.readSession();
		String label = commonDao.getLabel(session, "org_gu-000-111-444-123");
		assertTrue(label != null);
		graphFactory.closeSession(session);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

}
