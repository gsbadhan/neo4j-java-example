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

public class ProceduresQuery {
	private String isAuthorized;
	private String authorizeOrg;
	private String authorizeClass;
	private String authorizeAsset;
	private String authorizeContractType;
	private String authorizeContract;
	private String authorizeAdminUnit;
	private String authorizePrincipal;
	private String authorizeScript;
	private String authorizeDataItem;
	private String authorizeEvents;
	private String getAssetByOrg;
	private String authorizeModelClass;
	private String getClassDataItems;
	private String getOrgAndAssets;
	private String getDIClasses;
	private String getAllAssets;
	private String getAssetByProductClass;
	private String getAssetByClass;
	private String haveChildren;
	private String getAssetsByUser;

	public String haveChildren() {
		return haveChildren;
	}

	public String getAssetByClass() {
		return getAssetByClass;
	}

	public String getIsAuthorized() {
		return isAuthorized;
	}

	public String getAuthorizeOrg() {
		return authorizeOrg;
	}

	public String getAuthorizeClass() {
		return authorizeClass;
	}

	public String getAuthorizeAsset() {
		return authorizeAsset;
	}

	public String getAuthorizeContractType() {
		return authorizeContractType;
	}

	public String getAuthorizeContract() {
		return authorizeContract;
	}

	public String getAuthorizeAdminUnit() {
		return authorizeAdminUnit;
	}

	public String getAuthorizeScript() {
		return authorizeScript;
	}

	public String getAuthorizePrincipal() {
		return authorizePrincipal;
	}

	public String getAssetByOrg() {
		return getAssetByOrg;
	}

	public String getAuthorizeDataItem() {
		return authorizeDataItem;
	}

	public String getAuthorizeEvents() {
		return authorizeEvents;
	}

	public String getAuthorizeModelClass() {
		return authorizeModelClass;
	}

	public String getClassDataItems() {
		return getClassDataItems;
	}

	public String getOrgAndAssets() {
		return getOrgAndAssets;
	}

	public String getDIClasses() {
		return getDIClasses;
	}

	public String getAllAssets() {
		return getAllAssets;
	}

	public String getAssetByProductClass() {
		return getAssetByProductClass;
	}

	public String getAssetsByUser() {
		return getAssetsByUser;
	}

}
