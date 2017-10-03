/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.queries;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QueryLoaderTestIT {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQueryLoader() {
		// first time loading
		AppQuery appQuery = QueryLoader.appQuery();
		assertNotNull(appQuery);
		// second time loading
		AppQuery appQuery2 = QueryLoader.appQuery();
		assertNotNull(appQuery2);
	}

}
