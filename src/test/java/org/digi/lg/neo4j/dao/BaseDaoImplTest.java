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
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

@RunWith(MockitoJUnitRunner.class)
public class BaseDaoImplTest {

	@Mock
	private Driver driver;
	@Mock
	private Session session;
	@Mock
	private Transaction transaction;
	private String url = "bolt://localhost:7687";
	private String user = "neo4j";
	private String password = "test";
	private GraphFactoryImpl graphFactory;
	private BaseDaoImpl baseDaoImpl;
	private String query = "";
	private Map<String, Object> bindParams = new HashMap<>();

	@Mock
	private StatementResult statementResult;

	@Before
	public void setUp() throws Exception {
		graphFactory = new GraphFactoryImpl(url, user, password);
		graphFactory.init(driver);
		baseDaoImpl = new BaseDaoImpl(graphFactory);
	}

	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExecuteQueryWithTrxWithParams() {
		when(transaction.run(Mockito.anyString(), Mockito.anyMap())).thenReturn(statementResult);
		when(statementResult.list()).thenReturn(Mockito.anyList());
		List<Record> records = baseDaoImpl.executeQuery(transaction, query, bindParams);
		assertNotNull(records);
		Mockito.verify(transaction).run(Mockito.anyString(), Mockito.anyMap());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExecuteQueryWithTrxWithoutParams() {
		when(transaction.run(Mockito.anyString())).thenReturn(statementResult);
		when(statementResult.list()).thenReturn(Mockito.anyList());
		List<Record> records = baseDaoImpl.executeQuery(transaction, query, null);
		assertNotNull(records);
		Mockito.verify(transaction).run(Mockito.anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExecuteQueryWithSessionWithoutParams() {
		when(session.run(Mockito.anyString())).thenReturn(statementResult);
		when(statementResult.list()).thenReturn(Mockito.anyList());
		List<Record> records = baseDaoImpl.executeQuery(session, query, null);
		assertNotNull(records);
		Mockito.verify(session).run(Mockito.anyString());

	}
}
