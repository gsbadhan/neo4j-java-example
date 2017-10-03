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
import static org.digi.lg.neo4j.dao.DaoUtil.getParamMap;
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.queries.ContractTypeQuery;
import org.neo4j.driver.v1.Record;

public class ContractTypeDaoImpl extends CRUD implements ContractTypeDao {
	private final BaseDao baseDao;
	private final ContractTypeQuery contractTypeQuery;

	protected ContractTypeDaoImpl(final BaseDao baseDao, final CommonDao commonDao,
			final ContractTypeQuery contractTypeQuery) {
		super(baseDao, commonDao);
		this.baseDao = checkNotNull(baseDao);
		this.contractTypeQuery = checkNotNull(contractTypeQuery);
	}

	@Override
	public <TRX> ContractType save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_CONTRACT_TYPE, params);
		return new ContractType(save(trx, SchemaConstants.LABEL_CONTRACT_TYPE, params));
	}

	@Override
	public <TRX> Edge contractTypeHasContract(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeHasContract(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge contractTypeIsContractType(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeIsContractType(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> ContractType getContractTypeByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeByGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return ContractType.rowMapper(parseNode(records, "ctt"));
	}

	@Override
	public <TRX> ContractType getContractTypeByToken(TRX trx, String token) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_AUTH_TOKEN, token);
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeByToken(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return ContractType.rowMapper(parseNode(records, "ctt"));
	}

	@Override
	public <TRX> Edge contractTypeHasTermData(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeHasTermData(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge contractTypeHasTermEvent(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeHasTermEvent(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge contractTypeHasTermEventType(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeHasTermEventType(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge contractTypeHasTermAction(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeHasTermAction(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge contractTypeHasTermActionType(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeHasTermActionType(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge contractTypeHasTermService(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeHasTermService(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> List<Vertex> getContractTypeInHasClassVertices(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeInHasClassVertices(), params);
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
	public <TRX> Edge contractTypeIsDerivedClass(TRX trx, String label, Map<String, Object> params) {
		String query = contractTypeQuery.getContractTypeIsDerivedClass().replaceFirst(BindConstants.SRC_LBL,
				handleHyphen(label));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge contractTypeHasTermMashUP(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeHasTermMashUp(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> List<Vertex> getProductClassByContractType(TRX trx, String contractTypeId) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.CONTRACT_TYPE_ID, contractTypeId);

		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getProductClassesByContractType(),
				bindParams);
		if (records == null || records.isEmpty())
			return null;
		List<Vertex> productClasses = new ArrayList<>();
		records.forEach(record -> productClasses.add(new Vertex(parseNode(record, "pc"))));
		return productClasses;

	}

	@Override
	public <TRX> List<Vertex> getContractTypeInIsContractType(TRX trx, String contractTypeId) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.CONTRACT_TYPE_ID, contractTypeId);
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getContractTypeInIsContractType(),
				bindParams);
		List<Vertex> contractTypes = new ArrayList<>();
		if (records == null || records.isEmpty())
			return contractTypes;

		records.forEach(record -> contractTypes.add(new Vertex(parseNode(record, "ct"))));
		return contractTypes;
	}

	@Override
	public <TRX> List<ContractType> getContractsByPrincipal(TRX trx, String principalId) {
		List<ContractType> contractTypes = new ArrayList<>();

		Map<String, Object> params = new HashMap<>(1);
		params.put(BindConstants.PROP_PRINCIPAL_ID, principalId);
		List<Record> records = baseDao.executeQuery(trx, contractTypeQuery.getPersonAppContractTypes(), params);
		if (records == null || records.isEmpty())
			return contractTypes;

		records.forEach(record -> contractTypes.add(ContractType.rowMapper(parseNode(record, "ctt"))));
		return contractTypes;

	}

}
