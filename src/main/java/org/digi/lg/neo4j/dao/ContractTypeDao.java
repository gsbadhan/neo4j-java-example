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

import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.ContractType;

public interface ContractTypeDao {

	<TRX> ContractType save(TRX trx, Map<String, Object> params);

	<TRX> Edge contractTypeHasContract(TRX trx, Map<String, Object> params);

	<TRX> Edge contractTypeIsContractType(TRX trx, Map<String, Object> params);

	<TRX> ContractType getContractTypeByGuid(TRX transaction, String guid);

	<TRX> ContractType getContractTypeByToken(TRX transaction, String token);

	<TRX> Edge contractTypeHasTermData(TRX trx, Map<String, Object> params);

	<TRX> Edge contractTypeHasTermEvent(TRX trx, Map<String, Object> params);

	<TRX> Edge contractTypeHasTermEventType(TRX trx, Map<String, Object> params);

	<TRX> Edge contractTypeHasTermAction(TRX trx, Map<String, Object> params);

	<TRX> Edge contractTypeHasTermActionType(TRX trx, Map<String, Object> params);

	<TRX> Edge contractTypeHasTermService(TRX trx, Map<String, Object> params);

	<TRX> Edge contractTypeHasTermMashUP(TRX trx, Map<String, Object> params);

	<TRX> List<Vertex> getContractTypeInHasClassVertices(TRX trx, Map<String, Object> params);

	<TRX> Edge contractTypeIsDerivedClass(TRX trx, String label, Map<String, Object> params);

	<TRX> List<Vertex> getProductClassByContractType(TRX trx, String contractTypeId);

	<TRX> List<Vertex> getContractTypeInIsContractType(TRX trx, String contractTypeId);

	<TRX> List<ContractType> getContractsByPrincipal(TRX trx, String principalId);

}
