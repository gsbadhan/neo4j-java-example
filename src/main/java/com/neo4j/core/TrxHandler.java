
package com.neo4j.core;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

public abstract class TrxHandler<RET> {
	private final Transaction transaction;
	private final Object[] optionalArgs;

	public TrxHandler(final Session session) {
		this.transaction = GraphTransaction.start(session);
		this.optionalArgs = null;
	}


	public RET execute() throws Exception {
		try {
			RET ret = block(transaction);
			GraphTransaction.commit(transaction);
			return ret;
		} catch (Exception e) {
			GraphTransaction.rollback(transaction);
			throw e;
		} finally {
			GraphTransaction.close(transaction);
		}
	}

	protected Object[] getOptionalArgs() {
		return optionalArgs;
	}

	public abstract RET block(final Transaction transaction);
}
