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
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LgDbSecurityTestIT {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncryptAndDcrypt() {
		String input = "hi i am hacker, can you stop me!!";
		String encrypt = LgDbSecurity.encrypt(input);
		assertNotNull(encrypt);
		String dcrypt = LgDbSecurity.dcrypt(encrypt);
		assertNotNull(dcrypt);
		assertTrue(input.equals(dcrypt));
	}

}
