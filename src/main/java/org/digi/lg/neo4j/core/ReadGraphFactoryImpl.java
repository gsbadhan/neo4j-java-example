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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;

import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class ReadGraphFactoryImpl implements GraphFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadGraphFactoryImpl.class);
	private Driver driver;
	private final String url;
	private final String user;
	private final String password;

	public ReadGraphFactoryImpl(final String url, final String user, final String password) {
		this.url = checkNotNull(url);
		this.user = checkNotNull(user);
		this.password = checkNotNull(password);
	}

	@VisibleForTesting
	public void init(Driver driver) {
		this.driver = driver;
	}

	@Override
	public void init() {
		Config config = Config.build().withMaxTransactionRetryTime(10, TimeUnit.SECONDS).withMaxIdleSessions(10)
				.withLeakedSessionsLogging().toConfig();
		driver = GraphDatabase.driver(this.url, AuthTokens.basic(this.user, this.password), config);
		LOGGER.info("read graph factory loaded...");
	}

	@Override
	public Session readSession() {
		return driver.session(AccessMode.READ);
	}

	@Override
	public Session writeSession() {
		// not applicable for read operation
		return null;
	}

	@Override
	public void shutdown() {
		if (driver != null)
			driver.close();
	}

	@Override
	public void closeSession(Session session) {
		if (session != null)
			session.close();
	}

}
