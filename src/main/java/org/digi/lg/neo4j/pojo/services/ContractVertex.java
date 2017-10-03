/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.pojo.services;

import java.io.Serializable;

import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.App;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.pojo.model.Principal;

@SuppressWarnings("serial")
public class ContractVertex implements Serializable {

	private Principal principal;
	private AdminUnit adminUnit;
	private Contract contract;
	private ContractType contractType;
	private App app;
	private ClassX domainClass;

	public ContractVertex() {
	}

	public ContractVertex(Principal principal, AdminUnit adminUnit, Contract contract, ContractType contractType,
			App app) {
		super();
		this.principal = principal;
		this.adminUnit = adminUnit;
		this.contract = contract;
		this.contractType = contractType;
		this.app = app;
	}

	public ContractVertex(Principal principal, AdminUnit adminUnit, Contract contract, ContractType contractType,
			App app, ClassX domainClass) {
		super();
		this.principal = principal;
		this.adminUnit = adminUnit;
		this.contract = contract;
		this.contractType = contractType;
		this.app = app;
		this.domainClass = domainClass;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public AdminUnit getAdminUnit() {
		return adminUnit;
	}

	public void setAdminUnit(AdminUnit adminUnit) {
		this.adminUnit = adminUnit;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public ContractType getContractType() {
		return contractType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public ClassX getDomainClass() {
		return domainClass;
	}

	public void setDomainClass(ClassX domainClass) {
		this.domainClass = domainClass;
	}

	@Override
	public String toString() {
		return "ContractVertex [principal=" + principal + ", adminUnit=" + adminUnit + ", contract=" + contract
				+ ", contractType=" + contractType + ", app=" + app + "]";
	}

}
