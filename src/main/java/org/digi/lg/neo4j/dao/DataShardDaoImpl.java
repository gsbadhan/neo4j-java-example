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
import static org.digi.lg.neo4j.dao.DaoUtil.destLabelQueryBuilder;
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;
import static org.digi.lg.neo4j.dao.DaoUtil.relationQueryBuilder;
import static org.digi.lg.neo4j.dao.DaoUtil.srcLabelQueryBuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.ErrorCodes;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.exception.DataException;
import org.digi.lg.neo4j.pojo.model.DataShard;
import org.digi.lg.neo4j.queries.DataShardQuery;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;

public class DataShardDaoImpl extends CRUD implements DataShardDao {
	private final BaseDao baseDao;
	private final DataShardQuery dataShardQuery;

	protected DataShardDaoImpl(final BaseDao baseDao, final DataShardQuery dataShardQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.dataShardQuery = checkNotNull(dataShardQuery);
	}

	@Override
	public <TRX> DataShard incrementCount(TRX trx) {
		List<Record> records = baseDao.executeQuery(trx, dataShardQuery.getIncrementCount(),
				(Map<String, Object>) null);
		if (records == null || records.isEmpty())
			return null;

		return DataShard.rowMapper(parseNode(records, "ds"));
	}

	@Override
	public <TRX> Edge dsBelongsClass(TRX trx, String destClassLabel, Map<String, Object> params) {
		String query = destLabelQueryBuilder(dataShardQuery.getDsBelongsClass(), handleHyphen(destClassLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge dsBelongsOrg(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, dataShardQuery.getDsBelongsOrg(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge classHasDs(TRX trx, String anyClassLabel, Map<String, Object> params) {
		String query = srcLabelQueryBuilder(dataShardQuery.getClassHasDs(), handleHyphen(anyClassLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> List<DataShard> getDataShard(TRX trx, String fromlabel, String fromGuid, Direction direction) {
		String query = null;
		switch (direction) {
		case IN:
			query = dataShardQuery.getInDsFromGuid();
			break;
		case OUT:
			query = dataShardQuery.getOutDsFromGuid();
			break;
		default:
			throw new DataException("invalid direction param:" + direction, ErrorCodes.INVALID_PARAMETER);
		}
		query = relationQueryBuilder(srcLabelQueryBuilder(query, fromlabel),
				org.digi.lg.neo4j.core.Relationship.HAS.getType());
		List<Record> records = baseDao.executeQuery(trx, query, DaoUtil.getParamMap(BindConstants.SRC, fromGuid));

		if (records == null || records.isEmpty())
			return Collections.emptyList();

		List<DataShard> dsList = new LinkedList<>();
		records.forEach(record -> dsList.add(DataShard.rowMapper(((InternalNode) record.asMap().get("ds")).asMap())));
		return dsList;
	}

	@Override
	public <TRX> Edge dsBelongsScript(TRX trx, String dsGuid, String scriptGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_DS, SchemaConstants.PROP_HDMFID_NAME, dsGuid,
				SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid, Relationship.BELONGS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge dsBelongsScriptTemplate(TRX trx, String dsGuid, String scriptTemplateGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_DS, SchemaConstants.PROP_HDMFID_NAME, dsGuid,
				SchemaConstants.LABEL_SCRIPT_TEMPLATE, SchemaConstants.PROP_HDMFID_NAME, scriptTemplateGuid,
				Relationship.BELONGS, Direction.OUT);
	}

}
