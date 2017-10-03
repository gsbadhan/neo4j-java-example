/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.core;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class GraphTransactionTest {

	@Mock
	private Session session;

	@Mock
	private Transaction transaction;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@PrepareForTest(value = { GraphTransaction.class })
	public void testStart() throws Exception {
		when(session.beginTransaction()).thenReturn(transaction);
		GraphTransaction.start(session);
	}

	@Test
	@PrepareForTest(value = { GraphTransaction.class })
	public void testClose() throws Exception {
		GraphTransaction.close(transaction);
		verifyStatic();
		GraphTransaction.close(transaction);
	}

	@Test
	@PrepareForTest(value = { GraphTransaction.class })
	public void testCommit() throws Exception {
		GraphTransaction.commit(transaction);
		verifyStatic();
		GraphTransaction.commit(transaction);
	}

	@Test
	@PrepareForTest(value = { GraphTransaction.class })
	public void testRollback() throws Exception {
		GraphTransaction.rollback(transaction);
		verifyStatic();
		GraphTransaction.rollback(transaction);
	}

}
