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

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.neo4j.driver.v1.Session;

import com.google.gson.Gson;

public class ReadGraphFactoryImplTestIT extends Runner {

	private Properties configs;

	@Before
	public void setup() throws Exception {
		InputStream graphConfigFile = GraphContext.class.getClassLoader().getResourceAsStream("graph-config.json");
		Gson graphConfigGson = new Gson();
		configs = graphConfigGson.fromJson(new InputStreamReader(graphConfigFile), Properties.class);
		graphConfigFile.close();
	}

	@Test
	public void testInit() throws Exception {
		GraphFactory graphFactory = new ReadGraphFactoryImpl(configs.getProperty("db.read.url"),
				configs.getProperty("db.read.user"), configs.getProperty("db.read.password"));
		assertTrue(graphFactory != null);
		graphFactory.init();
		assertTrue(graphFactory.readSession() != null);

	}

	@Test
	public void testReadSession() {
		GraphFactory graphFactory = new ReadGraphFactoryImpl(configs.getProperty("db.read.url"),
				configs.getProperty("db.read.user"), configs.getProperty("db.read.password"));
		assertTrue(graphFactory != null);
		graphFactory.init();
		Session readSession = graphFactory.readSession();
		assertTrue(readSession != null);
		graphFactory.closeSession(readSession);
	}

	@Test
	public void testShutdown() {
		GraphFactory graphFactory = new ReadGraphFactoryImpl(configs.getProperty("db.read.url"),
				configs.getProperty("db.read.user"), configs.getProperty("db.read.password"));
		assertTrue(graphFactory != null);
		graphFactory.init();

		graphFactory.shutdown();
	}

	@Override
	public Description getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run(RunNotifier notifier) {
		// TODO Auto-generated method stub

	}

}
