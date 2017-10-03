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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;

@RunWith(MockitoJUnitRunner.class)
public class GraphFactoryImplTest {

	@Mock
	private Driver driver;
	@Mock
	private Session session;
	private String url = "bolt://localhost:7687";
	private String user = "neo4j";
	private String password = "test";

	private GraphFactoryImpl graphFactory;

	@Before
	public void setUp() throws Exception {
		graphFactory = new GraphFactoryImpl(url, user, password);
		graphFactory.init(driver);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadSession() throws Exception {
		when(driver.session(AccessMode.READ)).thenReturn(session);
		Session readSession = graphFactory.readSession();
		assertNotNull(readSession);

	}

	@Test
	public void testWriteSession() throws Exception {
		when(driver.session(AccessMode.WRITE)).thenReturn(session);
		Session writeSession = graphFactory.writeSession();
		assertNotNull(writeSession);

	}

	@Test
	public void testShutdown() throws Exception {
		doNothing().when(driver).close();
		graphFactory.shutdown();
		verify(driver).close();
	}

	@Test
	public void testCloseSession() throws Exception {
		doNothing().when(session).close();
		graphFactory.closeSession(session);
		verify(session).close();
	}
}
