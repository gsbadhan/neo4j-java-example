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

public class ContractTypeQuery {
	private String contractTypeHasContract;
	private String contractTypeIsContractType;
	private String getContractTypeByGuid;

	private String getContractTypeByToken;
	private String contractTypeHasTermData;
	private String contractTypeHasTermEvent;
	private String contractTypeHasTermEventType;
	private String contractTypeHasTermAction;
	private String contractTypeHasTermActionType;
	private String contractTypeHasTermService;
	private String contractTypeHasTermMashUp;
	private String getProductClassesByContractType;
	private String contractTypeInHasClassVertices;
	private String contractTypeIsDerivedClass;
	private String getContractTypeInIsContractType;
	private String personAppContractTypes;

	public String getPersonAppContractTypes() {
		return personAppContractTypes;
	}

	public String getProductClassesByContractType() {
		return getProductClassesByContractType;
	}

	public String getContractTypeHasTermData() {
		return contractTypeHasTermData;
	}

	public String getContractTypeHasTermEvent() {
		return contractTypeHasTermEvent;
	}

	public String getContractTypeHasTermEventType() {
		return contractTypeHasTermEventType;
	}

	public String getContractTypeHasTermAction() {
		return contractTypeHasTermAction;
	}

	public String getContractTypeHasTermActionType() {
		return contractTypeHasTermActionType;
	}

	public String getContractTypeHasTermService() {
		return contractTypeHasTermService;
	}

	public String getContractTypeHasContract() {
		return contractTypeHasContract;
	}

	public String getContractTypeIsContractType() {
		return contractTypeIsContractType;
	}

	public String getContractTypeByGuid() {
		return getContractTypeByGuid;
	}

	public String getContractTypeByToken() {
		return getContractTypeByToken;
	}

	public String getContractTypeInHasClassVertices() {
		return contractTypeInHasClassVertices;
	}

	public String getContractTypeIsDerivedClass() {
		return contractTypeIsDerivedClass;
	}

	public String getContractTypeHasTermMashUp() {
		return contractTypeHasTermMashUp;
	}

	public void setContractTypeHasTermMashUp(String contractTypeHasTermMashUp) {
		this.contractTypeHasTermMashUp = contractTypeHasTermMashUp;
	}

	public String getContractTypeInIsContractType() {
		return getContractTypeInIsContractType;
	}

}
