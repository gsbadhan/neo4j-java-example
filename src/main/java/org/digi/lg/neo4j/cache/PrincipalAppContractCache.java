/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.cache;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;

import java.util.List;

import org.digi.lg.neo4j.core.DBRowUUID.ContractUID;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.dao.ClassDao;
import org.digi.lg.neo4j.dao.ContractDao;
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.App;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrincipalAppContractCache extends AbstractCache<String, ContractVertex> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalAppContractCache.class);
	private final GraphFactory graphFactory;
	private final ContractDao contractDao;
	private final ClassDao classDao;
	private final static String DELIMETER = "$@$";

	protected PrincipalAppContractCache(final GraphFactory graphFactory, final ContractDao contractDao,
			final ClassDao classDao) {
		super(CacheArea.PERSON_APP_CONTARCT.name());
		this.graphFactory = checkNotNull(graphFactory);
		this.contractDao = checkNotNull(contractDao);
		this.classDao = checkNotNull(classDao);
	}

	public ContractVertex get(String principalId, String appId) {
		ContractVertex contractVertex = super.get(keyFormat(principalId, appId));
		if (contractVertex != null) {
			return contractVertex;
		}
		Session session = graphFactory.readSession();
		try {
			List<Record> records = contractDao.getContract(session, principalId, appId);
			if (records == null || records.isEmpty())
				return null;
			if (records.size() > 1) {
				LOGGER.info("invalid:mulitple contract-chain found for principalId:{},appId:{} ..!! ", principalId,
						appId);
				return null;
			}
			Principal prsn = Principal.rowMapper(parseNode(records, "pr"));
			AdminUnit adu = AdminUnit.rowMapper(parseNode(records, "ad"));
			boolean isAdmin = false;
			Object isAdminProp = records.get(0).asMap().get("isAdmin");
			if (isAdminProp != null) {
				Integer isAdminValue = Integer.parseInt(isAdminProp.toString());
				isAdmin = isAdminValue == 1 ? true : false;
			}
			adu.setIsAdmin(isAdmin);

			Contract ct = Contract.rowMapper(parseNode(records, "ct"));

			if (!ct.isValidContract()) {
				LOGGER.info("invalid contract found for principalId:{},appId:{} ..!! ", principalId, appId);
				return null;
			}
			ContractType ctt = ContractType.rowMapper(parseNode(records, "ctt"));
			App app = App.rowMapper(parseNode(records, "ap"));
			ClassX domainClass = classDao.getDomainClass(session, prsn.getGuid(), ctt.getGuid());
			if (domainClass == null) {
				domainClass = classDao.getDomainClassByContract(session, ct.getGuid(),
						ContractUID.getDomainGuid(ct.getDBUUId()));
			}
			if (domainClass == null)
				LOGGER.info("warning:domain class not found for principalId:{},appId:{} ..!! ", principalId, appId);

			contractVertex = new ContractVertex(prsn, adu, ct, ctt, app, domainClass);
			put(keyFormat(principalId, appId), contractVertex);
		} catch (Exception e) {
			LOGGER.error("error occured in getContractVertex", e);
		} finally {
			graphFactory.closeSession(session);
		}

		return contractVertex;
	}

	private String keyFormat(String principalId, String appId) {
		return (principalId + DELIMETER + appId);
	}

}
