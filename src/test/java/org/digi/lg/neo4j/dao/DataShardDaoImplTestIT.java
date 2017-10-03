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

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.pojo.model.DataShard;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class DataShardDaoImplTestIT {
	private Properties configs;
	private GraphFactory graphFactory;
	private DataShardDao dataShardDao;
	private BaseDao baseDao;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.dataShardDao = new DataShardDaoImpl(baseDao, QueryLoader.dataShardQuery());
	}

	@After
	public void tearDown() throws Exception {
		graphFactory.shutdown();
	}

	@Test
	public void getDataShard() {
		Session session = graphFactory.writeSession();
		String fromlabel = "org";
		String fromGuid = "org-000-111-123";
		Direction direction = Direction.OUT;
		List<DataShard> dataShards = dataShardDao.getDataShard(session, fromlabel, fromGuid, direction);
		assertNotNull(dataShards);
		graphFactory.closeSession(session);
	}

}
