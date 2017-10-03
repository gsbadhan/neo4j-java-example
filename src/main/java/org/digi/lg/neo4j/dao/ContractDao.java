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

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.Org;
import org.neo4j.driver.v1.Record;

public interface ContractDao {

	<TRX> Contract save(TRX trx, Map<String, Object> params);

	<TRX> Contract saveUpdate(TRX trx, Map<String, Object> params);

	<TRX> Edge contractHasAdminUnit(TRX trx, Map<String, Object> params);

	<TRX> List<Record> getContract(TRX trx, String principalId, String appId);

	<TRX> Contract getContract(TRX trx, String guid);

	<TRX> List<Vertex> getContractInHasOrgVertices(TRX trx, Map<String, Object> params);

	<TRX> Org getOrgByContract(TRX trx, String contractGuid);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcClassGuid, String destLabel, String destClassGuid,
			Relationship relType, Direction direction);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);

}
