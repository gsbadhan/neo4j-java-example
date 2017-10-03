
package com.neo4j.core;

import org.neo4j.driver.v1.exceptions.Neo4jException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TrxRetryHandler<RET> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrxRetryHandler.class);
	private static final int MAX_RETRY = 100;

	public RET execute() {
		RET ret = null;
		// time gap in milliseconds
		long timeGap = 30;
		for (int i = 1; i <= MAX_RETRY; i++) {
			try {
				ret = retry();
				break;
			} catch (Neo4jException trxe) {
				if (trxe.code().contains(GraphContext.getConfig().getProperty("trx.retry.errors"))) {
					if (i == MAX_RETRY) {
						LOGGER.debug("all retry TrxRetryHandler:exhausted {}", i);
						break;
					}
					LOGGER.debug("TrxRetryHandler:retrying {}", i);
					sleep(timeGap);
					timeGap = timeGap + 3;
					continue;
				} else {
					throw trxe;
				}
			} catch (Exception trw) {
				LOGGER.error("error not handle in TrxRetryHandler:execute", trw);
			}

			if (i == MAX_RETRY) {
				LOGGER.debug("all retry TrxRetryHandler:exhausted {}", i);
			}
		}
		return ret;

	}

	private void sleep(long timeGap) {
		try {
			Thread.sleep(timeGap);
		} catch (InterruptedException e) {
			LOGGER.error("error occured in sleep", e);
		}
	}

	public abstract RET retry();
}
