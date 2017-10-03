/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.domain.services;

import static org.digi.lg.neo4j.core.ProcedureConstants.ACROSS_DOMAIN;
import static org.digi.lg.neo4j.core.ProcedureConstants.ADMIN_UNIT_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.ADU_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.APP_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.ASSET_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.CLASS_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.CONTRACT_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.CONTRACT_TYPE_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.CONTRACT_TYPE_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.CONTRACT_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.DI_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.EVENTS_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.ORG_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.PRINCIPAL_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.SCRIPT_VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.USER_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.VERTEX_ID;
import static org.digi.lg.neo4j.core.ProcedureConstants.VERTEX_LABEL;
import static org.digi.lg.neo4j.dao.DaoUtil.getStr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.queries.ProceduresQuery;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationServiceImpl extends ServiceProvider implements AuthorizationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

	private final ProceduresQuery proceduresQuery;

	public AuthorizationServiceImpl() {
		this.proceduresQuery = QueryLoader.proceduresQuery();
	}

	@Override
	public List<Vertex> isAuthorized(String userId, String appId, ContractVertex contractVertex, Vertex vertexGuid,
			boolean acrossDomain) {
		List<Vertex> result = new ArrayList<>();
		try {
			String label = vertexGuid.getLabel() != null ? vertexGuid.getLabel()
					: getStr(vertexGuid.getNode().get(SchemaConstants.PROP_CATEGORY));

			// first check in cache
			result = authorizeCache.get(userId, appId, contractVertex.getContractType().getGuid(),
					contractVertex.getContract().getGuid(), vertexGuid.getGuid(), label);
			if (!result.isEmpty())
				return result;

			Map<String, Object> params = new HashMap<>(7);
			params.put(USER_ID, userId);
			params.put(APP_ID, appId);
			params.put(CONTRACT_TYPE_ID, contractVertex.getContractType().getGuid());
			params.put(CONTRACT_ID, contractVertex.getContract().getGuid());
			params.put(VERTEX_ID, vertexGuid.getGuid());
			params.put(VERTEX_LABEL, label);
			params.put(ACROSS_DOMAIN, acrossDomain);
			result = procedureDao.getVertices(proceduresQuery.getIsAuthorized(), params);

			// at the end put into cache
			if (!result.isEmpty())
				authorizeCache.put(userId, appId, contractVertex.getContractType().getGuid(),
						contractVertex.getContract().getGuid(), vertexGuid.getGuid(), label, result);
		} catch (Exception e) {
			LOGGER.error("error occured in isAuthorized userId:{},appId:{},error:{}", userId, appId, e);
		}
		return result;
	}

	@Override
	public List<Vertex> authorizeOrg(ContractType contractType, Contract contract, Vertex vertex,
			boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(4);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(CONTRACT_ID, contract.getGuid());
		params.put(ORG_VERTEX_ID, vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeOrg(), params);
	}

	@Override
	public List<Vertex> authorizeAssets(ContractType contractType, Contract contract, Vertex vertex,
			boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(4);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(CONTRACT_ID, contract.getGuid());
		params.put(ASSET_VERTEX_ID, vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeAsset(), params);
	}

	@Override
	public List<Vertex> authorizeClass(ContractType contractType, Vertex vertex, boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(3);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(CLASS_VERTEX_ID, vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeClass(), params);
	}

	@Override
	public List<Vertex> authorizeProductClass(ContractType contractType, Vertex vertex, boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(3);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(CLASS_VERTEX_ID, vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeModelClass(), params);
	}

	@Override
	public List<Vertex> authorizeDataItem(ContractType contractType, Contract contract, Vertex dataItemVertex,
			boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(3);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(DI_VERTEX_ID, dataItemVertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeDataItem(), params);
	}

	@Override
	public List<Vertex> authorizeEvents(ContractType contractType, Contract contract, Vertex eventVertex,
			boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(3);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(EVENTS_VERTEX_ID, eventVertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeEvents(), params);
	}

	@Override
	public List<Vertex> authorizeContractType(ContractType contractType, Vertex contractTypeVertex,
			boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(3);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(CONTRACT_TYPE_VERTEX_ID, contractTypeVertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeContractType(), params);
	}

	@Override
	public List<Vertex> authorizeContract(ContractType contractType, Vertex contractVertex, boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(3);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(CONTRACT_VERTEX_ID, contractVertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeContract(), params);
	}

	@Override
	public List<Vertex> authorizeAdminUnit(ContractType contractType, Vertex adminUnitVertex, boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(3);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(ADU_VERTEX_ID, adminUnitVertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeAdminUnit(), params);
	}

	@Override
	public List<Vertex> authorizeScript(ContractType contractType, Vertex scriptVertex, boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(3);
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(SCRIPT_VERTEX_ID, scriptVertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizeScript(), params);
	}

	@Override
	public List<Vertex> authorizePrincipal(AdminUnit adminUnit, ContractType contractType, Vertex principalVertex,
			boolean acrossDomain) {
		Map<String, Object> params = new HashMap<>(4);
		params.put(ADMIN_UNIT_ID, adminUnit.getGuid());
		params.put(CONTRACT_TYPE_ID, contractType.getGuid());
		params.put(PRINCIPAL_VERTEX_ID, principalVertex.getGuid());
		params.put(ACROSS_DOMAIN, acrossDomain);
		return procedureDao.getVertices(proceduresQuery.getAuthorizePrincipal(), params);
	}

}
