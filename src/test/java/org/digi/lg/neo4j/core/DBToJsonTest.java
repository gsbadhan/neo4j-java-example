package org.digi.lg.neo4j.core;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBToJsonTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDbToJson() {
		String input = "";
		String output = "";

		input = "product_class";
		output = DBToJson.get(input);
		assertTrue(output.equals("productClass"));

		input = "asset";
		output = DBToJson.get(input);
		assertTrue(output.equals("asset"));

		input = "api_key";
		output = DBToJson.get(input);
		assertTrue(output.equals("apiKey"));

		input = "!release_bundle";
		output = DBToJson.get(input);
		assertTrue(output.equals("!releaseBundle"));

	}

}
