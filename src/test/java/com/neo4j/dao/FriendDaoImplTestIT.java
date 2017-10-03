package com.neo4j.dao;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

import com.neo4j.core.BaseDao;
import com.neo4j.core.BaseDaoImpl;
import com.neo4j.core.GraphContext;
import com.neo4j.core.GraphFactory;
import com.neo4j.core.GraphFactoryImpl;
import com.neo4j.core.GraphTransaction;

public class FriendDaoImplTestIT {
	private FriendDao employeeDao;
	private BaseDao baseDao;
	private GraphFactory graphFactory;

	@Before
	public void setUp() {
		String url = GraphContext.getConfig().getProperty("db.url");
		String user = GraphContext.getConfig().getProperty("db.user");
		String password = GraphContext.getConfig().getProperty("db.password");

		graphFactory = new GraphFactoryImpl(url, user, password);
		baseDao = new BaseDaoImpl(graphFactory);
		employeeDao = new FriendDaoImpl(baseDao);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSave() {
		Session session = graphFactory.writeSession();
		Transaction trx = GraphTransaction.start(session);

		Record record = employeeDao.save(trx, "john", 20);
		System.out.println("emp:" + record);

		GraphTransaction.close(trx);
		graphFactory.closeSession(session);
	}

}
