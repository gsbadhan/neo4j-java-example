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
import org.digi.lg.neo4j.pojo.model.TermAction;
import org.digi.lg.neo4j.queries.TermActionQuery;
import org.neo4j.driver.v1.Record;

public class TermActionDaoImpl extends CRUD implements TermActionDao {
	private final BaseDao baseDao;
	private final TermActionQuery actionQuery;

	protected TermActionDaoImpl(final BaseDao baseDao, final TermActionQuery actionQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.actionQuery = checkNotNull(actionQuery);
	}

	@Override
	public <TRX> TermAction save(TRX trx, Map<String, Object> params) {
		return new TermAction(save(trx, SchemaConstants.LABEL_TERM_ACTION, params));
	}

	@Override
	public <TRX> TermAction getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, actionQuery.getByGuid(), bindParams);
		if (records != null && !records.isEmpty()) {
			return TermAction.rowMapper(parseNode(records, "ta"));
		}
		return null;
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
		String query = createEdgeQueryBuilder(actionQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), relType);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Boolean deleteNode(TRX trx, String nodeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, nodeGuid);
		baseDao.executeQuery(trx, actionQuery.getDetachDeleteNode(), bindParams);
		return true;
	}

}
