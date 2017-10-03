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

import java.util.List;

import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.pojo.services.ContractVertex;

public interface AuthorizationService {

	List<Vertex> authorizeOrg(ContractType contractType, Contract contract, Vertex orgVertex, boolean acrossDomain);

	List<Vertex> authorizeAssets(ContractType contractType, Contract contract, Vertex assetVertex,
			boolean acrossDomain);

	List<Vertex> authorizeClass(ContractType contractType, Vertex classVertex, boolean acrossDomain);

	List<Vertex> authorizeProductClass(ContractType contractType, Vertex vertex, boolean acrossDomain);

	List<Vertex> authorizeDataItem(ContractType contractType, Contract contract, Vertex dataItemVertex,
			boolean acrossDomain);

	List<Vertex> authorizeEvents(ContractType contractType, Contract contract, Vertex eventVertex,
			boolean acrossDomain);

	List<Vertex> authorizeContractType(ContractType contractType, Vertex contractTypeVertex, boolean acrossDomain);

	List<Vertex> authorizeContract(ContractType contractType, Vertex contractVertex, boolean acrossDomain);

	List<Vertex> authorizeAdminUnit(ContractType contractType, Vertex adminUnitVertex, boolean acrossDomain);

	List<Vertex> authorizeScript(ContractType contractType, Vertex scriptVertex, boolean acrossDomain);

	List<Vertex> authorizePrincipal(AdminUnit adminUnit, ContractType contractType, Vertex principalVertex,
			boolean acrossDomain);

	List<Vertex> isAuthorized(String userId, String appId, ContractVertex contractVertex, Vertex vertexGuid,
			boolean acrossDomain);

}
