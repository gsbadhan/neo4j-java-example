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

public class AdminUnitQuery {
	private String updateByGuid;
	private String getAdminUnitByGuid;
	private String adminUnitIsAdminUnit;
	private String createLink;

	public String getCreateLink() {
		return createLink;
	}

	public String getUpdateByGuid() {
		return updateByGuid;
	}

	public String getAdminUnitByGuid() {
		return getAdminUnitByGuid;
	}

	public String getAdminUnitIsAdminUnit() {
		return adminUnitIsAdminUnit;
	}

}
