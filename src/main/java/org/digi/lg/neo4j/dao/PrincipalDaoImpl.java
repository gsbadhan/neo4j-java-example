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
import static org.digi.lg.neo4j.dao.DaoUtil.createEdgeQueryBuilder;
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.queries.PrincipalQuery;
import org.neo4j.driver.v1.Record;

public class PrincipalDaoImpl extends CRUD implements PrincipalDao {
	private final BaseDao baseDao;
	private final PrincipalQuery principalQuery;

	protected PrincipalDaoImpl(final BaseDao baseDao, final CommonDao commonDao, final PrincipalQuery principalQuery) {
		super(baseDao, commonDao);
		this.baseDao = checkNotNull(baseDao);
		this.principalQuery = checkNotNull(principalQuery);
	}

	@Override
	public <TRX> Principal save(TRX trx, Map<String, Object> params) {
		return new Principal(save(trx, SchemaConstants.LABEL_PRINCIPAL, params));
	}

	@Override
	public <TRX> Edge principalBelongsAdminUnit(TRX trx, Map<String, Object> paramMap) {
		if (!paramMap.containsKey(BindConstants.IS_ADMIN)) {
			paramMap.put(BindConstants.IS_ADMIN, SchemaConstants.IS_ADMIN_NO);
		}
		List<Record> records = baseDao.executeQuery(trx, principalQuery.getPrincipalBelongsAdminUnit(), paramMap);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge detachPrincipalBelongsAdminUnit(TRX trx, Map<String, Object> paramMap) {
		List<Record> records = baseDao.executeQuery(trx, principalQuery.getDetachPrincipalBelongsAdminUnit(), paramMap);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Principal getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, principalQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return Principal.rowMapper(parseNode(records, "p"));
	}

	@Override
	public <TRX> Principal getByPrincipalId(TRX trx, String principalId) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_PRINCIPAL_ID, principalId);
		List<Record> records = baseDao.executeQuery(trx, principalQuery.getByPrincipalId(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return Principal.rowMapper(parseNode(records, "p"));
	}

	@Override
	public <TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction) {

		return super.deleteEdge(trx, srcLabel, SchemaConstants.PROP_HDMFID_NAME, srcGuid, destLabel,
				SchemaConstants.PROP_HDMFID_NAME, destGuid, relType, direction);

	}

	@Override
	public <TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType,
			Map<String, Object> params) {
		String query = createEdgeQueryBuilder(principalQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), relType);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Principal saveUpdate(TRX trx, Map<String, Object> params) {
		return new Principal(super.saveUpdate(trx, SchemaConstants.LABEL_PRINCIPAL, params));
	}

}
