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

public class AssetQuery {
	private String getByGuid;
	private String getBySerialNumberSearch;
	private String getCompatible;
	private String deleteAssetIsClassAll;
	private String getByProductClass;
	private String getByGuidAndLabel;
	private String getAssetByAssetName;
	private String getAssetBySerialNumber;
	private String getAssetByOrg;
	private String getAssetByClass;
	private String getAssetByContract;
	private String getAssetByContractType;
	private String detachDeleteNode;
	private String getAssetByProductClassGuid;
	private String getLicenceAssetBybootStrapOrg;
	private String getAllLicenceAssets;

	private String createLink;

	public String getCreateLink() {
		return createLink;
	}

	public String getDetachDeleteNode() {
		return detachDeleteNode;
	}

	public String getGetAssetByContract() {
		return getAssetByContract;
	}

	public String getGetAssetByContractType() {
		return getAssetByContractType;
	}

	public String getGetAssetByClass() {
		return getAssetByClass;
	}

	public String getByGuid() {
		return getByGuid;
	}

	public String getBySerialNumberSearch() {
		return getBySerialNumberSearch;
	}

	public String getCompatible() {
		return getCompatible;
	}

	public String deleteAssetIsClassAll() {
		return deleteAssetIsClassAll;
	}

	public String getByProductClass() {
		return getByProductClass;
	}

	public String getByGuidAndLabel() {
		return getByGuidAndLabel;
	}

	public String getAssetByAssetName() {
		return getAssetByAssetName;
	}

	public String getAssetBySerialNumber() {
		return getAssetBySerialNumber;
	}

	public String getAssetByOrg() {
		return getAssetByOrg;
	}

	public String getGetAssetByProductClassGuid() {
		return getAssetByProductClassGuid;
	}

	public void setGetAssetByProductClassGuid(String getAssetByProductClassGuid) {
		this.getAssetByProductClassGuid = getAssetByProductClassGuid;
	}

	public String getLicenceAssetBybootStrapOrg() {
		return getLicenceAssetBybootStrapOrg;
	}

	public void setLicenceAssetBybootStrapOrg(String getLicenceAssetBybootStrapOrg) {
		this.getLicenceAssetBybootStrapOrg = getLicenceAssetBybootStrapOrg;
	}

	public String getAllLicenceAssets() {
		return getAllLicenceAssets;
	}

	public void getAllLicenceAssets(String getAllLicenceAssets) {
		this.getAllLicenceAssets = getAllLicenceAssets;
	}
	

}
