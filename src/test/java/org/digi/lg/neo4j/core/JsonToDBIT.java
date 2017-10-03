package org.digi.lg.neo4j.core;

import static org.junit.Assert.assertTrue;

import org.digi.lg.neo4j.core.JsonToDB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsonToDBIT {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDbColumn() {
		String result = JsonToDB.get("Asset");
		assertTrue(result.equals("asset"));

		result = JsonToDB.get("!ProductClass");
		assertTrue(result.equals("!product_class"));

		result = JsonToDB.get("anyColumn1001_hiiBye");
		assertTrue(result.equals("any_column_1001_hii_bye"));

		result = JsonToDB.get("event_   _Data item_");
		assertTrue(result.equals("event_data_item"));

	}

}
