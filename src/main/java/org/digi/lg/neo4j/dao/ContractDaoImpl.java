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
import static org.digi.lg.neo4j.dao.DaoUtil.getParamMap;
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.Org;
import org.digi.lg.neo4j.queries.ContractQuery;
import org.neo4j.driver.v1.Record;

public class ContractDaoImpl extends CRUD implements ContractDao {
	private final BaseDao baseDao;
	private final ContractQuery contractQuery;

	public ContractDaoImpl(final BaseDao baseDao, final CommonDao commonDao, final ContractQuery contractQuery) {
		super(baseDao, commonDao);
		this.baseDao = checkNotNull(baseDao);
		this.contractQuery = checkNotNull(contractQuery);
	}

	@Override
	public <TRX> Contract save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_CONTRACT, params);
		return new Contract(save(trx, SchemaConstants.LABEL_CONTRACT, params));
	}

	@Override
	public <TRX> Contract saveUpdate(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_CONTRACT, params);
		return new Contract(saveUpdate(trx, SchemaConstants.LABEL_CONTRACT, params));
	}

	@Override
	public <TRX> Edge contractHasAdminUnit(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractQuery.getContractHasAdminUnit(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> List<Record> getContract(TRX trx, String principalId, String appId) {
		Map<String, Object> params = new HashMap<>(2);
		params.put(BindConstants.PROP_PRINCIPAL_ID, principalId);
		params.put(BindConstants.PROPNAME_APP_ID, appId);
		return baseDao.executeQuery(trx, contractQuery.getPersonAppContract(), params);
	}

	@Override
	public <TRX> Contract getContract(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, contractQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return Contract.rowMapper(parseNode(records, "ct"));
	}

	@Override
	public <TRX> List<Vertex> getContractInHasOrgVertices(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractQuery.getContractInHasOrgVertices(), params);
		if (records == null || records.isEmpty())
			return Collections.emptyList();

		List<Vertex> list = new LinkedList<>();
		records.forEach(record -> {
			Vertex v = new Vertex();
			v.setLabel(record.get("lbl").toString());
			v.setNode(getParamMap(SchemaConstants.PROP_HDMFID_NAME, record.get("guid").asString(),
					SchemaConstants.PROP_NAME, record.get("name").asString()));
			list.add(v);
		});
		return list;
	}

	@Override
	public <TRX> Org getOrgByContract(TRX trx, String contractGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, contractGuid);
		List<Record> records = baseDao.executeQuery(trx, contractQuery.getOrgByContractGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return Org.rowMapper(parseNode(records, "or"));
	}

	@Override
	public <TRX> Edge deleteLink(TRX trx, String srcLabel, String srcClassGuid, String destLabel, String destClassGuid,
			Relationship relType, Direction direction) {

		return super.deleteEdge(trx, srcLabel, SchemaConstants.PROP_HDMFID_NAME, srcClassGuid, destLabel,
				SchemaConstants.PROP_HDMFID_NAME, destClassGuid, relType, direction);

	}

	@Override
	public <TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType,
			Map<String, Object> params) {
		String query = createEdgeQueryBuilder(contractQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), relType);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

}
