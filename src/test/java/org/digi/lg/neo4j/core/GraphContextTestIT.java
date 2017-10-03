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

import org.junit.Test;

public class GraphContextTestIT {

	@Test
	public void testGetConfig() throws Exception {
		assertTrue(GraphContext.getConfig() != null);
	}

}
