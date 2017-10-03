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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Org;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class OrgDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private OrgDao orgDao;
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
		this.orgDao = new OrgDaoImpl(baseDao, commonDao, QueryLoader.orgQuery());
	}

	@Test
	public void testAddUpdate() {
		Session session = graphFactory.writeSession();
		Map<String, Object> params = new HashMap<>(3);
		params.put(BindConstants.PROP_HDMFID_NAME, "org-testguid");
		params.put(BindConstants.PROP_NAME, "org3");
		params.put(BindConstants.PROP_TYPE, "org");

		Org org = orgDao.addUpdate(session, params);
		assertNotNull(org);
		graphFactory.closeSession(session);

	}

	@Test
	public void testIsNodeConnectedTrue() {
		Session session = graphFactory.writeSession();

		boolean st = orgDao.isNodeConnected(session, SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME,
				"org-1001", SchemaConstants.LABEL_ORG, BindConstants.PROP_NAME, "org3", Relationship.HAS,
				Direction.OUT);
		assertTrue(st);
		graphFactory.closeSession(session);

	}

	@Test
	public void testIsNodeConnectedEmpty() {
		Session session = graphFactory.writeSession();
		Map<String, Object> params = new HashMap<>(2);
		params.put(BindConstants.SRC, "org-1001");
		params.put(BindConstants.PROP_NAME, "org3");
		params.put(BindConstants.PROP_TYPE, "org");
		boolean st = orgDao.isNodeConnected(session, SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME,
				"org-1001", null, null, "org3", Relationship.HAS, Direction.OUT);
		assertTrue(st);
		graphFactory.closeSession(session);

	}

	@Test
	public void testGetBootStrapKeyByOrgAndSrn() {
		Session session = graphFactory.writeSession();
		String orgName = "org2001";
		String serialNumber = "srn-3010";
		String results = orgDao.getBootStrapKeyByOrgAndSrn(session, orgName, serialNumber);
		assertTrue(results != null);
		graphFactory.closeSession(session);

	}

	@Test
	public void testGetOrgsByType() {
		Session session = graphFactory.writeSession();
		String contractGuid = "mndt-ct1001";
		String assetGuid = "mndt-ast1001";
		String type = "dealer";
		List<Vertex> list = orgDao.getOrgsByType(session, contractGuid, assetGuid, type);
		assertTrue(list != null);
		graphFactory.closeSession(session);
	}

	@Test
	public void testDeleteNode() {
		Session session = graphFactory.writeSession();
		String nodeGuid = "org-1001";
		boolean deleted = orgDao.deleteNode(session, nodeGuid);
		assertTrue(deleted);
		graphFactory.closeSession(session);
	}

	@Test
	public void testDeleteOrgHasAsset() {
		Session session = graphFactory.writeSession();
		String orgGuid = "org-1001";
		String assetGuid = "ast-1001";
		Edge deleted = orgDao.deleteOrgHasAsset(session, orgGuid, assetGuid);
		assertTrue(deleted != null);
		graphFactory.closeSession(session);
	}

	@Test
	public void testDeleteOrgHasOrg() {
		Session session = graphFactory.writeSession();
		String orgGuid1 = "org-1001";
		String orgGuid2 = "org-1002";
		Edge deleted = orgDao.deleteOrgHasOrg(session, orgGuid1, orgGuid2);
		assertTrue(deleted != null);
		graphFactory.closeSession(session);
	}

	@Test
	public void testDeleteOrgBelongsOrg() {
		Session session = graphFactory.writeSession();
		String orgGuid1 = "org-1001";
		String orgGuid2 = "org-1002";
		Edge deleted = orgDao.deleteOrgBelongsOrg(session, orgGuid1, orgGuid2);
		assertTrue(deleted != null);
		graphFactory.closeSession(session);
	}

	@Test
	public void testOrgHasAsset() {
		Session session = graphFactory.writeSession();
		String orgGuid = "org-1001";
		String assetGuid = "org-1002";
		Edge created = orgDao.orgHasAsset(session, orgGuid, assetGuid);
		assertTrue(created != null);
		graphFactory.closeSession(session);
	}

	@Test
	public void testOrgIsClass() {
		Session session = graphFactory.writeSession();
		String orgGuid = "org-1001";
		String classGuid = "cls-1002";
		Edge created = orgDao.orgIsClass(session, orgGuid, classGuid);
		assertTrue(created != null);
		graphFactory.closeSession(session);
	}

	@Test
	public void testDeleteOrgIsClass() {
		Session session = graphFactory.writeSession();
		String orgGuid = "org-1001";
		String clsGuid = "cls-1002";
		Edge deleted = orgDao.deleteOrgIsClass(session, orgGuid, clsGuid);
		assertTrue(deleted != null);
		graphFactory.closeSession(session);
	}

}
