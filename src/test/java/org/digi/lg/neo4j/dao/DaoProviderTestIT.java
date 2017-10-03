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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DaoProviderTestIT {

	@Test
	public void testStaticBlock() {
		class TestAbstractDaoService extends DaoProvider {

		}
		TestAbstractDaoService service = new TestAbstractDaoService();
		assertTrue(service != null);
		assertTrue(service.graphFactory != null);
	}

}
