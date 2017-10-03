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

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GraphTransaction {
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphTransaction.class);

	private GraphTransaction() {
	}

	public static Transaction start(Session session) {
		checkNotNull(session);
		return session.beginTransaction();
	}

	/**
	 * close session
	 * 
	 * @param transaction
	 */
	public static void close(Transaction transaction) {
		if (transaction != null) {
			try {
				transaction.close();
			} catch (Exception e) {
				LOGGER.error("error occured in close", e);
			}
		}
	}

	/**
	 * commit data within session.
	 * 
	 * @param transaction
	 */
	public static void commit(Transaction transaction) {
		if (transaction != null) {
			try {
				transaction.success();
			} catch (Exception e) {
				LOGGER.error("error occured in commit", e);
			}
		}
	}

	/**
	 * roll-back committed data per commit.
	 * 
	 * @param transaction
	 */
	public static void rollback(Transaction transaction) {
		if (transaction != null) {
			try {
				transaction.failure();
			} catch (Exception e) {
				LOGGER.error("error occured in rollback", e);
			}
		}
	}

}
