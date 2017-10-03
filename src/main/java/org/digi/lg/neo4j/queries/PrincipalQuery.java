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

public class PrincipalQuery {
	private String principalBelongsAdminUnit;
	private String detachPrincipalBelongsAdminUnit;
	private String getByGuid;
	private String getByPrincipalId;
	private String createLink;

	public String getCreateLink() {
		return createLink;
	}

	public String getPrincipalBelongsAdminUnit() {
		return principalBelongsAdminUnit;
	}

	public String getByGuid() {
		return getByGuid;
	}

	public String getDetachPrincipalBelongsAdminUnit() {
		return detachPrincipalBelongsAdminUnit;
	}

	public String getByPrincipalId() {
		return getByPrincipalId;
	}

}
