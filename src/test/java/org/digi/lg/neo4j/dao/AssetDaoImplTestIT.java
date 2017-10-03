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

import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.pojo.model.Asset;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class AssetDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private AssetDao assetDao;
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
		this.assetDao = new AssetDaoImpl(baseDao, commonDao, QueryLoader.assetQuery());
	}

	@Test
	public void testGetCompatible() {
		Session session = graphFactory.writeSession();
		String assetGuid = "t1001";
		Object record = assetDao.getCompatible(session, assetGuid);
		assertNotNull(record);
		graphFactory.closeSession(session);

	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

	@Test
	public void testDeleteNode() {
		Session session = graphFactory.writeSession();
		String nodeGuid = "ast-1001";
		boolean deleted = assetDao.deleteNode(session, nodeGuid);
		assertNotNull(deleted);
		graphFactory.closeSession(session);

	}

	@Test
	public void testDeleteAssetBelongsOrg() {
		Session session = graphFactory.writeSession();
		String assetGuid = "ast-1001";
		String orgGuid = "org-1001";
		Edge deleted = assetDao.deleteAssetBelongsOrg(session, assetGuid, orgGuid);
		assertNotNull(deleted != null);
		graphFactory.closeSession(session);

	}

	@Test
	public void testDeleteAssetIsClass() {
		Session session = graphFactory.writeSession();
		String assetGuid = "ast-1001";
		String classGuid = "cls-1001";
		Edge deleted = assetDao.deleteAssetIsClass(session, assetGuid, classGuid);
		assertNotNull(deleted != null);
		graphFactory.closeSession(session);

	}

	@Test
	public void testAssetBelongsOrg() {
		Session session = graphFactory.writeSession();
		String assetGuid = "ast-1001";
		String orgGuid = "org-1001";
		Edge deleted = assetDao.assetBelongsOrg(session, assetGuid, orgGuid);
		assertNotNull(deleted != null);
		graphFactory.closeSession(session);

	}

	@Test
	public void testAssetIsClass() {
		Session session = graphFactory.writeSession();
		String assetGuid = "ast-1001";
		String classGuid = "cls-1001";
		Edge deleted = assetDao.assetIsClass(session, assetGuid, classGuid);
		assertNotNull(deleted != null);
		graphFactory.closeSession(session);

	}

	@Test
	public void testAssetBelongsAsset() {
		Session session = graphFactory.writeSession();
		String assetGuid1 = "ast-1001";
		String assetGuid2 = "ast-1002";
		Edge created = assetDao.assetBelongsAsset(session, assetGuid1, assetGuid2);
		assertNotNull(created != null);
		graphFactory.closeSession(session);
	}

	@Test
	public void testAssetHasAsset() {
		Session session = graphFactory.writeSession();
		String assetGuid1 = "ast-1001";
		String assetGuid2 = "ast-1002";
		Edge created = assetDao.assetHasAsset(session, assetGuid1, assetGuid2);
		assertNotNull(created != null);
		graphFactory.closeSession(session);
	}

	@Test
	public void testDeleteAssetHasAsset() {
		Session session = graphFactory.writeSession();
		String assetGuid1 = "ast-1001";
		String assetGuid2 = "ast-1002";
		Edge deleted = assetDao.deleteAssetHasAsset(session, assetGuid1, assetGuid2);
		assertNotNull(deleted != null);
		graphFactory.closeSession(session);

	}

	@Test
	public void testDeleteAssetBelongsAsset() {
		Session session = graphFactory.writeSession();
		String assetGuid1 = "ast-1001";
		String assetGuid2 = "ast-1002";
		Edge deleted = assetDao.deleteAssetBelongsAsset(session, assetGuid1, assetGuid2);
		assertNotNull(deleted != null);
		graphFactory.closeSession(session);

	}
	@Test
	public void testassetlicence() {
		Session session = graphFactory.readSession();
	
		List<Asset> deleted = assetDao.getAllLicenceAssets(session);
		assertNotNull(deleted != null);
		graphFactory.closeSession(session);

	}
}
