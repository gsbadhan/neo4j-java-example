/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package com.neo4j.core;

import org.neo4j.driver.v1.Session;

public interface GraphFactory {

	void init();

	Session readSession();

	Session writeSession();

	void closeSession(Session session);

	void shutdown();

}
