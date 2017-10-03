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

/**
 * @author APARVATH
 *
 */
public class Account {

	String token;
	String name;
	String accountName;
	String principalId;
	String parent;
	String parentAdminUnit;
	String parentOrg;
	String $userId;
	String $parentId;
	String parentLabel;
	String isAdminFlag;

	String serialNumber;
	String location;

	public String getIsAdminFlag() {
		return isAdminFlag;
	}

	public void setIsAdminFlag(String isAdminFlag) {
		this.isAdminFlag = isAdminFlag;
	}

	public String get$parentId() {
		return $parentId;
	}

	public void set$parentId(String $parentId) {
		this.$parentId = $parentId;
	}

	public String getParentLabel() {
		return parentLabel;
	}

	public void setParentLabel(String parentLabel) {
		this.parentLabel = parentLabel;
	}

	public String get$userid() {
		return $userId;
	}

	public void set$userid(String $userid) {
		this.$userId = $userid;
	}

	public String getParentAdminUnit() {
		return parentAdminUnit;
	}

	public void setParentAdminUnit(String parentAdminUnit) {
		this.parentAdminUnit = parentAdminUnit;
	}

	public String getParentOrg() {
		return parentOrg;
	}

	public void setParentOrg(String parentOrg) {
		this.parentOrg = parentOrg;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getContractToken() {
		return contractToken;
	}

	public void setContractToken(String contractToken) {
		this.contractToken = contractToken;
	}

	String $appId;
	String contractToken;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String get$appid() {
		return $appId;
	}

	public void set$appid(String appid) {
		this.$appId = appid;
	}

	public String getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}
