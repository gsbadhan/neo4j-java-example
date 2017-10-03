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

public class AuthTokenQuery {
	private String getByGuid;
	private String detachDeleteToken;

	public String getByGuid() {
		return getByGuid;
	}

	public String detachDeleteToken() {
		return detachDeleteToken;
	}

}
