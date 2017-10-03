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

public class DataShardQuery {
	private String incrementCount;
	private String dsBelongsClass;
	private String dsBelongsOrg;
	private String classHasDs;
	private String outDsFromGuid;
	private String inDsFromGuid;

	public String getIncrementCount() {
		return incrementCount;
	}

	public String getDsBelongsClass() {
		return dsBelongsClass;
	}

	public String getDsBelongsOrg() {
		return dsBelongsOrg;
	}

	public String getClassHasDs() {
		return classHasDs;
	}

	public String getOutDsFromGuid() {
		return outDsFromGuid;
	}

	public String getInDsFromGuid() {
		return inDsFromGuid;
	}

}
