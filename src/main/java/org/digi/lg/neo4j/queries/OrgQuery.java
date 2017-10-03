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

public class OrgQuery {
	private String getOrgByGuid;
	private String orgHasOrg;
	private String orgBelongsOrg;
	private String orgHasDataShard;
	private String orgIsContract;
	private String isParentConnectedToChild;
	private String orgHasContract;
	private String getBootStrapKey;
	private String getBootStrapKeyV2;
	private String getImmediateOrgOfAsset;
	private String getBootStrapKeyByOrgAndSrn;
	private String getOrgsByUser;
	private String getOrgsByType;
	private String detachDeleteNode;
	private String createLink;

	public String getCreateLink() {
		return createLink;
	}

	public String getDetachDeleteNode() {
		return detachDeleteNode;
	}

	public String getOrgByGuid() {
		return getOrgByGuid;
	}

	public String getOrgHasOrg() {
		return orgHasOrg;
	}

	public String getOrgBelongsOrg() {
		return orgBelongsOrg;
	}

	public String getOrgHasDataShard() {
		return orgHasDataShard;
	}

	public String getOrgIsContract() {
		return orgIsContract;
	}

	public String getIsParentConnectedToChild() {
		return isParentConnectedToChild;
	}

	public String getOrgHasContract() {
		return orgHasContract;
	}

	public String getBootStrapKey() {
		return getBootStrapKey;
	}

	public String getBootStrapKeyV2() {
		return getBootStrapKeyV2;
	}

	public String getImmediateOrgOfAsset() {
		return getImmediateOrgOfAsset;
	}

	public String getBootStrapKeyByOrgAndSrn() {
		return getBootStrapKeyByOrgAndSrn;
	}

	public String getOrgsByUser() {
		return getOrgsByUser;
	}

	public String getOrgsByType() {
		return getOrgsByType;
	}

}
