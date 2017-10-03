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
import org.digi.lg.neo4j.pojo.model.DataItem;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

public class DataItemDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private BaseDao baseDao;
	private CommonDao commonDao;
	private DataItemDao dataItemDao;

	@Before
	public void setup() {
		this.configs = GraphContext.getConfig();
		this.graphFactory = new GraphFactoryImpl(configs.getProperty("db.url"), configs.getProperty("db.user"),
				configs.getProperty("db.password"));
		this.graphFactory.init();
		this.baseDao = new BaseDaoImpl(this.graphFactory);
		this.commonDao = new CommonDaoImpl(baseDao, QueryLoader.commonQuery());
		this.dataItemDao = new DataItemDaoImpl(baseDao, commonDao, QueryLoader.dataItemQuery());
	}

	@Test
	public void testGetDataItemByProductClass() {
		Session session = graphFactory.writeSession();
		String productClassId = "prdt-class-1001";
		String dataItemName = "min";
		List<DataItem> dataItem = dataItemDao.getDataItemByProductClass(session, productClassId, dataItemName);
		assertNotNull(dataItem);
		graphFactory.closeSession(session);
	}

}