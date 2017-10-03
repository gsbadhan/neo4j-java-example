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

public class EmployeeDaoImplTestIT {
	private EmployeeDao employeeDao;
	private BaseDao baseDao;
	private GraphFactory graphFactory;

	@Before
	public void setUp() throws Exception {
		String url = GraphContext.getConfig().getProperty("db.url");
		String user = GraphContext.getConfig().getProperty("db.user");
		String password = GraphContext.getConfig().getProperty("db.password");

		graphFactory = new GraphFactoryImpl(url, user, password);
		baseDao = new BaseDaoImpl(graphFactory);
		employeeDao = new EmployeeDaoImpl(baseDao);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSave() {
		Session session = graphFactory.writeSession();
		Transaction trx = GraphTransaction.start(session);
		
		Map<String, Object> params = new HashMap<>(3);
		Record record = employeeDao.save(trx, params);
		System.out.println("emp:" + record);
		
		GraphTransaction.close(trx);
		graphFactory.closeSession(session);
	}

}
