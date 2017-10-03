
package com.neo4j.core;

import org.neo4j.driver.v1.Session;

public interface GraphFactory {

	void init();

	Session readSession();

	Session writeSession();

	void closeSession(Session session);

	void shutdown();

}
