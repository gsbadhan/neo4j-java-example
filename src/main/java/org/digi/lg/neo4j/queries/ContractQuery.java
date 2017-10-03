/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.queries;

public class ContractQuery {
	private String contractHasAdminUnit;
	private String personAppContract;
	private String getByGuid;
	private String contractInHasOrgVertices;
	private String getOrgByContractGuid;
	private String createLink;

	public String getCreateLink() {
		return createLink;
	}

	public String getContractHasAdminUnit() {
		return contractHasAdminUnit;
	}

	public String getPersonAppContract() {
		return personAppContract;
	}

	public String getByGuid() {
		return getByGuid;
	}

	public String getContractInHasOrgVertices() {
		return contractInHasOrgVertices;
	}

	public String getOrgByContractGuid() {
		return getOrgByContractGuid;
	}

}
