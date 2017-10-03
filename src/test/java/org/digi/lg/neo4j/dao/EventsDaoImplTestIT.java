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

import java.util.List;
import java.util.Properties;

import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;

public class EventsDaoImplTestIT {

	private Properties configs;
	private GraphFactory graphFactory;
	private EventsDao eventsDao;
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
		this.eventsDao = new EventsDaoImpl(baseDao, commonDao, QueryLoader.eventQuery());
	}

	@Test
	public void testGetEventsClasses() {
		Session session = graphFactory.writeSession();
		String contractTypeGuid = "mndt-ctt1001";
		List<Record> list = null;
		try {
			list = eventsDao.getEventsClasses(session, contractTypeGuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		graphFactory.closeSession(session);

		assertTrue(list != null);
	}

}