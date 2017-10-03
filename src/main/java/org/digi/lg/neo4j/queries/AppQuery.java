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

public class AppQuery {

	private String addUpdate;
	private String appHasContractType;
	private String getAppByName;
	private String getAppById;
	private String getAppByGuid;
	private String getAppByContractType;

	public String getAddUpdate() {
		return addUpdate;
	}

	public String appHasContractType() {
		return appHasContractType;
	}

	public String getAppByName() {
		return getAppByName;
	}

	public String getAppById() {
		return getAppById;
	}

	public String getAppByGuid() {
		return getAppByGuid;
	}

	public String getAppByContractType() {
		return getAppByContractType;
	}

}
