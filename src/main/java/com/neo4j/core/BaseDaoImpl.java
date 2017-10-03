
package com.neo4j.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDaoImpl implements BaseDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseDaoImpl.class);
	private final GraphFactory graphFactory;

	public BaseDaoImpl(final GraphFactory graphFactory) {
		super();
		this.graphFactory = checkNotNull(graphFactory);
	}

	@Override
	public <TRX> List<Record> executeQuery(TRX transaction, String query, Map<String, Object> bindParams) {

		if (transaction == null) {
			return executeQuery(query, bindParams);
		}

		if (transaction instanceof Session) {
			return execute((Session) transaction, query, bindParams);
		} else {
			return execute((Transaction) transaction, query, bindParams);
		}
	}

	@Override
	public List<Record> executeQuery(String query, Map<String, Object> bindParams) {
		Session session = null;
		try {
			session = graphFactory.writeSession();
			return executeQuery(session, query, bindParams);
		} finally {
			graphFactory.closeSession(session);
		}
	}

	private List<Record> execute(Session session, String query, Map<String, Object> bindParams) {
		LOGGER.debug("query:{},params:{}", query, bindParams);
		if (bindParams == null) {
			return session.run(query).list();
		} else {
			return session.run(query, bindParams).list();
		}
	}

	private List<Record> execute(Transaction trx, String query, Map<String, Object> bindParams) {
		LOGGER.debug("query:{},params:{}", query, bindParams);
		TrxRetryHandler<List<Record>> trxRetryHandler = new TrxRetryHandler<List<Record>>() {
			@Override
			public List<Record> retry() {
				if (bindParams == null) {
					return trx.run(query).list();
				} else {
					return trx.run(query, bindParams).list();
				}
			}
		};
		return trxRetryHandler.execute();

	}

	private StatementResult executex(Session session, String query, Map<String, Object> bindParams) {
		LOGGER.debug("query:{},params:{}", query, bindParams);
		if (bindParams == null) {
			return session.run(query);
		} else {
			return session.run(query, bindParams);
		}
	}

	@Override
	public StatementResult executeQueryx(Session session, String query, Map<String, Object> bindParams) {
		return executex(session, query, bindParams);
	}

}
