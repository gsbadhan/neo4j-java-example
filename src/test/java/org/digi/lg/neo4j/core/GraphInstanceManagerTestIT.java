package org.digi.lg.neo4j.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GraphInstanceManagerTestIT {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadInstances() {
		try {
			GraphInstanceManager.loadInstances();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
