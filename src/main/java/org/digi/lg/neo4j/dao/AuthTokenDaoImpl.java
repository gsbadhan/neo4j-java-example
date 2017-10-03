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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.pojo.model.AuthToken;
import org.digi.lg.neo4j.queries.AuthTokenQuery;
import org.neo4j.driver.v1.Record;

public class AuthTokenDaoImpl extends CRUD implements AuthTokenDao {
	private final BaseDao baseDao;
	private final AuthTokenQuery authTokenQuery;

	protected AuthTokenDaoImpl(final BaseDao baseDao, final AuthTokenQuery authTokenQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.authTokenQuery = checkNotNull(authTokenQuery);
	}

	@Override
	public <TRX> AuthToken add(TRX trx, Map<String, Object> params) {
		return new AuthToken(save(trx, SchemaConstants.LABEL_AUTH_TOKEN, params));
	}

	@Override
	public <TRX> AuthToken getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, authTokenQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return AuthToken.rowMapper(parseNode(records, "at"));
	}

	@Override
	public <TRX> Edge adminUnitHasToken(TRX trx, String adminUnitGuid, String authTokenGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_ADMIN_UNIT, SchemaConstants.PROP_HDMFID_NAME, adminUnitGuid,
				SchemaConstants.LABEL_AUTH_TOKEN, SchemaConstants.PROP_HDMFID_NAME, authTokenGuid, Relationship.TOKEN,
				Direction.OUT);
	}

	@Override
	public <TRX> Boolean deleteAuthToken(TRX trx, String tokenGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, tokenGuid);
		baseDao.executeQuery(trx, authTokenQuery.detachDeleteToken(), bindParams);
		return true;
	}

}
