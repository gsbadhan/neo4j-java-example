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
import static org.digi.lg.neo4j.dao.DaoUtil.getClassMetaParams;
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
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.queries.AdminUnitQuery;
import org.neo4j.driver.v1.Record;

public class AdminUnitDaoImpl extends CRUD implements AdminUnitDao {
	private final BaseDao baseDao;
	private final AdminUnitQuery adminUnitQuery;
	private final CommonDao commonDao;

	protected AdminUnitDaoImpl(final BaseDao baseDao, final CommonDao commonDao, final AdminUnitQuery adminUnitQuery) {
		super(baseDao, commonDao);
		this.baseDao = checkNotNull(baseDao);
		this.commonDao = checkNotNull(commonDao);
		this.adminUnitQuery = checkNotNull(adminUnitQuery);
	}

	@Override
	public <TRX> AdminUnit updateByGuid(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, adminUnitQuery.getUpdateByGuid(), params);
		if (records == null || records.isEmpty())
			return null;
		Vertex vertex = new Vertex(SchemaConstants.LABEL_ADMIN_UNIT, parseNode(records, "adu"));
		commonDao.addUpdateClassMeta(trx, vertex.getLabel(), getClassMetaParams(vertex));
		return new AdminUnit(vertex);
	}

	@Override
	public <TRX> AdminUnit save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_ADMIN_UNIT, params);
		return new AdminUnit(save(trx, SchemaConstants.LABEL_ADMIN_UNIT, params));
	}

	@Override
	public <TRX> AdminUnit getAdminUnitByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, adminUnitQuery.getAdminUnitByGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return AdminUnit.rowMapper(parseNode(records, "adu"));
	}

	@Override
	public <TRX> Edge adminUnitIsAdminUnit(TRX trx, Map<String, Object> paramMap) {
		List<Record> records = baseDao.executeQuery(trx, adminUnitQuery.getAdminUnitIsAdminUnit(), paramMap);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
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
		String query = createEdgeQueryBuilder(adminUnitQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), relType);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

}
