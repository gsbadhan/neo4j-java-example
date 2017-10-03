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
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.GraphTransaction;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class CRUDTestIT {
	private Properties configs;
	private GraphFactory graphFactory;
	private BaseDao baseDao;
	private CommonDao commonDao;
	private CRUDTestClass crudTestClass;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.commonDao = new CommonDaoImpl(baseDao, QueryLoader.commonQuery());
		this.crudTestClass = new CRUDTestClass(baseDao, commonDao);
	}

	@Test
	public void testSave() {
		Session session = graphFactory.writeSession();
		org.neo4j.driver.v1.Transaction transaction = GraphTransaction.start(session);
		String label = SchemaConstants.LABEL_ORG;
		Map<String, Object> params = new HashMap<>(3);
		params.put(SchemaConstants.PROP_HDMFID_NAME, "orgtest-1001");
		params.put(SchemaConstants.PROP_NAME, "orgtest");
		params.put(SchemaConstants.PROP_BOOT_STRAP_KEY, "123xxx890nn");
		Vertex vertex = crudTestClass.save(transaction, label, params);
		GraphTransaction.close(transaction);
		graphFactory.closeSession(session);
		assertNotNull(vertex);
	}

	@Test
	public void testSaveUpdate() {
		Session session = graphFactory.writeSession();
		org.neo4j.driver.v1.Transaction transaction = GraphTransaction.start(session);
		String label = SchemaConstants.LABEL_ORG;
		Map<String, Object> params = new HashMap<>(3);
		params.put(SchemaConstants.PROP_HDMFID_NAME, "orgtest-1001");
		params.put(SchemaConstants.PROP_NAME, "orgtest");
		params.put(SchemaConstants.PROP_BOOT_STRAP_KEY, "123xxx890nn");
		Vertex vertex = crudTestClass.saveUpdate(transaction, label, params);
		GraphTransaction.close(transaction);
		graphFactory.closeSession(session);
		assertNotNull(vertex);
	}

	@Test
	public void testCreateEdgeInDirection() {
		Session session = graphFactory.writeSession();
		String srcLabel = SchemaConstants.LABEL_ORG;
		String srcProperty = SchemaConstants.PROP_HDMFID_NAME;
		String srcPropertyVal = "org-1001";
		String destLabel = SchemaConstants.LABEL_ASSET;
		String destProperty = SchemaConstants.PROP_HDMFID_NAME;
		String destPropertyVal = "ast-9001";
		Relationship relationship = Relationship.HAS;
		Direction direction = Direction.IN;
		Edge edge = crudTestClass.createEdge(session, srcLabel, srcProperty, srcPropertyVal, destLabel, destProperty,
				destPropertyVal, relationship, direction);
		graphFactory.closeSession(session);
		assertNotNull(edge);
	}

	@Test
	public void testCreateEdgeOutDirection() {
		Session session = graphFactory.writeSession();
		String srcLabel = SchemaConstants.LABEL_ORG;
		String srcProperty = SchemaConstants.PROP_HDMFID_NAME;
		String srcPropertyVal = "org-1001";
		String destLabel = SchemaConstants.LABEL_ASSET;
		String destProperty = SchemaConstants.PROP_HDMFID_NAME;
		String destPropertyVal = "ast-9001";
		Relationship relationship = Relationship.HAS;
		Direction direction = Direction.OUT;
		Edge edge = crudTestClass.createEdge(session, srcLabel, srcProperty, srcPropertyVal, destLabel, destProperty,
				destPropertyVal, relationship, direction);
		graphFactory.closeSession(session);
		assertNotNull(edge);
	}

	@Test
	public void testDeleteInEdge() {
		Session session = graphFactory.writeSession();
		String srcLabel = SchemaConstants.LABEL_ORG;
		String srcProperty = SchemaConstants.PROP_HDMFID_NAME;
		String srcPropertyVal = "org-1001";
		String destLabel = SchemaConstants.LABEL_ASSET;
		String destProperty = SchemaConstants.PROP_HDMFID_NAME;
		String destPropertyVal = "ast-9001";
		Relationship relationship = Relationship.HAS;
		Direction direction = Direction.IN;
		Edge edge = crudTestClass.deleteEdge(session, srcLabel, srcProperty, srcPropertyVal, destLabel, destProperty,
				destPropertyVal, relationship, direction);
		graphFactory.closeSession(session);
		assertNotNull(edge);

	}

	@Test
	public void testDeleteOutEdge() {
		Session session = graphFactory.writeSession();
		String srcLabel = SchemaConstants.LABEL_ORG;
		String srcProperty = SchemaConstants.PROP_HDMFID_NAME;
		String srcPropertyVal = "org-1001";
		String destLabel = SchemaConstants.LABEL_ASSET;
		String destProperty = SchemaConstants.PROP_HDMFID_NAME;
		String destPropertyVal = "ast-9001";
		Relationship relationship = Relationship.HAS;
		Direction direction = Direction.OUT;
		Edge edge = crudTestClass.deleteEdge(session, srcLabel, srcProperty, srcPropertyVal, destLabel, destProperty,
				destPropertyVal, relationship, direction);
		graphFactory.closeSession(session);
		assertNotNull(edge);

	}

	@Test
	public void testIsExist() {
		Session session = graphFactory.writeSession();
		org.neo4j.driver.v1.Transaction transaction = GraphTransaction.start(session);
		String label = SchemaConstants.LABEL_CLASS;
		String property = "name";
		String value = "lgmanturbo_classes";
		boolean isExist = crudTestClass.isExist(transaction, label, property, value);
		GraphTransaction.close(transaction);
		graphFactory.closeSession(session);
		assertTrue(isExist);
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

	// Dummy class to test Abstract CRUD class
	private class CRUDTestClass extends CRUD {
		public CRUDTestClass(BaseDao baseDao, CommonDao commonDao) {
			super(baseDao, commonDao);
		}
	}

}
