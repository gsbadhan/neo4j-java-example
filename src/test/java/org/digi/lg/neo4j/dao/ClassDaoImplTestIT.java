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

import static org.digi.lg.neo4j.dao.DaoUtil.getParamMap;
import static org.digi.lg.neo4j.dao.DaoUtil.mandatory;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.DataTypes;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.Properties.PropNameSuffix;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class ClassDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private ClassDao classDao;
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
		this.classDao = new ClassDaoImpl(baseDao, commonDao, QueryLoader.classQuery());
	}

	@Test
	public void testGetVertex() {
		Session session = graphFactory.writeSession();
		Vertex vertex = null;
		try {
			vertex = classDao.getVertex(session, Vertex.class, SchemaConstants.LABEL_PRINCIPAL, "name", "gurpreet");
		} catch (Exception e) {
			e.printStackTrace();
		}
		graphFactory.closeSession(session);

		assertTrue(vertex != null);
		assertTrue(vertex.getNode() != null);
	}

	@Test
	public void testGetInVertex() {
		Session session = graphFactory.writeSession();
		Vertex vertex = null;
		try {
			vertex = classDao.getInVertex(session, SchemaConstants.LABEL_ORG, "name", "lookingglasstest_org", null,
					Optional.empty(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		graphFactory.closeSession(session);

		assertTrue(vertex != null);
		assertTrue(vertex.getNode() != null);
	}

	@Test
	public void testGetOutVertex() {
		Session session = graphFactory.writeSession();
		Vertex vertex = null;
		try {
			vertex = classDao.getOutVertex(session, SchemaConstants.LABEL_PRINCIPAL, "name", "gurpreet",
					Relationship.BELONGS, null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		graphFactory.closeSession(session);

		assertTrue(vertex != null);
		assertTrue(vertex.getNode() != null);
	}

	@Test
	public void testGetByProperty() {
		Session session = graphFactory.writeSession();
		ClassX classX = null;
		classX = classDao.getByProperty(session, ClassX.class, "guid", "org_gu-000-111-444-123");
		graphFactory.closeSession(session);

		assertTrue(classX != null);
		assertTrue(classX.getVertex() != null);
	}

	@Test
	public void testSaveUpdate() {
		Session session = graphFactory.writeSession();
		Map<String, Object> param = new HashMap<>();
		param.put("guid", "x-1001");
		param.put("name", "test-1001");
		param.put("token", "tk23");
		Vertex vertex = classDao.saveUpdate(session, "testp", param);
		assertNotNull(vertex);
		graphFactory.closeSession(session);

	}

	@Test
	public void testCreateReleasebundle() {
		Session session = graphFactory.writeSession();
		String classPrincipalBundleGuid = "release-bundle-1001";
		Vertex vertex = classDao.saveUpdate(session, SchemaConstants.LABEL_CLASS,
				getParamMap(SchemaConstants.PROP_HDMFID_NAME, classPrincipalBundleGuid,
						mandatory(SchemaConstants.PROP_KEY_NAME), DataTypes.STRING.getType(),
						mandatory(SchemaConstants.PROP_BUNDLE_LOCATION), DataTypes.STRING.getType(),
						mandatory(SchemaConstants.PROP_RESOURCE_PROVIDER), DataTypes.STRING.getType(),
						mandatory(SchemaConstants.PROP_BUNDLE_ACCESS_KEY), DataTypes.STRING.getType(),
						mandatory(SchemaConstants.PROP_BUNDLE_PRIVATE_KEY), DataTypes.STRING.getType()));
		assertNotNull(vertex);
		graphFactory.closeSession(session);
	}

	@Test
	public void testIsNodeConnectedTrue() {
		Session session = graphFactory.writeSession();
		Map<String, Object> params = new HashMap<>(2);
		params.put(BindConstants.SRC, "clas-1001");
		params.put(BindConstants.PROP_NAME, "clas2");
		boolean st = classDao.isNodeConnectedIn(session, Optional.of("class_1001"), Relationship.IS, params);
		assertTrue(st);
		graphFactory.closeSession(session);

	}

	@Test
	public void testIsNodeConnectedEmpty() {
		Session session = graphFactory.writeSession();
		Map<String, Object> params = new HashMap<>(2);
		params.put(BindConstants.SRC, "clas-1001");
		params.put(BindConstants.PROP_NAME, "clas2");
		boolean st = classDao.isNodeConnectedIn(session, Optional.empty(), Relationship.IS, params);
		assertTrue(st);
		graphFactory.closeSession(session);

	}

	@Test
	public void getMandatoryPropertyVertex() {
		Session session = graphFactory.writeSession();
		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.SRC, "clas-1001");
		Optional srcLbl = Optional.of("class");
		List<Vertex> parents = classDao.getMandatoryPropertyVertex(session, srcLbl, params);
		assertNotNull(parents);
		graphFactory.closeSession(session);
	}

	@Test
	public void getDomainClass() {
		Session session = graphFactory.writeSession();
		String contractTypeGuid = "mndt-ctt1001";
		String principalGuid = "lgmanturbo";
		ClassX domainClass = classDao.getDomainClass(session, principalGuid, contractTypeGuid);
		assertNotNull(domainClass);
		graphFactory.closeSession(session);
	}

	@Test
	public void testDeleteNode() {
		Session session = graphFactory.writeSession();
		String classGuid = "cls-1003";
		boolean deleted = classDao.deleteNode(session, classGuid);
		assertNotNull(deleted);
		graphFactory.closeSession(session);
	}

	@Test
	public void testDeleteClassIsClass() {
		Session session = graphFactory.writeSession();
		String destClassGuid = "cls-1003";
		String srcClassGuid = "cls-1002";
		Edge deleted = classDao.deleteClassIsClass(session, srcClassGuid, destClassGuid);
		assertNotNull(deleted != null);
		graphFactory.closeSession(session);
	}

	@Test
	public void testGetDomainChildClass() {
		Session session = graphFactory.writeSession();
		String domainClassGuid = "e5d23f27-0dd1-40c6-9b20-a9540d67e0b7";
		String childClassName = PropNameSuffix._EVENTS.getValue();
		ClassX eventClass = classDao.getDomainChildClass(session, domainClassGuid, childClassName);
		assertNotNull(eventClass);
		graphFactory.closeSession(session);
	}
}
