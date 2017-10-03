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

public class ConfigItemQuery {
	private String getByGuid;
	private String detachDeleteNode;

	public String getDetachDeleteNode() {
		return detachDeleteNode;
	}

	public String getByGuid() {
		return getByGuid;
	}

}