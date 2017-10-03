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
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.pojo.model.TermData;
import org.digi.lg.neo4j.queries.TermDataQuery;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;

public class TermDataDaoImpl extends CRUD implements TermDataDao {

	private final BaseDao baseDao;
	private final TermDataQuery termDataQuery;

	protected TermDataDaoImpl(final BaseDao baseDao, final TermDataQuery termDataQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.termDataQuery = checkNotNull(termDataQuery);
	}

	@Override
	public <TRX> TermData save(TRX trx, Map<String, Object> params) {
		return new TermData(save(trx, SchemaConstants.LABEL_TERM_DATA, params));
	}

	@Override
	public <TRX> TermData getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, termDataQuery.getByGuid(), bindParams);
		if (records != null && !records.isEmpty()) {
			return TermData.rowMapper(((InternalNode) records.get(0).asMap().get("td")).asMap());
		}
		return null;
	}

	@Override
	public <TRX> Edge termHasVertex(TRX trx, String sourceLabel, String sourceGuid, String destLabel, String destGuid,
			Relationship rel, Direction dir) {
		return super.createEdge(trx, sourceLabel, SchemaConstants.PROP_HDMFID_NAME, sourceGuid, destLabel,
				SchemaConstants.PROP_HDMFID_NAME, destGuid, rel, dir);
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
		String query = createEdgeQueryBuilder(termDataQuery.getCreateLink(), handleHyphen(srcLabel),
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
		baseDao.executeQuery(trx, termDataQuery.getDetachDeleteNode(), bindParams);
		return true;
	}

}
