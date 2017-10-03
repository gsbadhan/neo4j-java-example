package org.digi.lg.neo4j.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DaoUtilTestIT {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetBooostrapKey() {
		String name = "jcborg";
		String bootStrapKey = DaoUtil.getBooostrapKey(name);
		assertNotNull(bootStrapKey);
		assertTrue(bootStrapKey.length() > 10);
	}

}
