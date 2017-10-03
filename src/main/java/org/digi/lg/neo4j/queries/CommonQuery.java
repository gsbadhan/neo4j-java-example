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

public class CommonQuery {
	private String addUpdateClassMeta;
	private String searchFromText;
	private String searchFromGraph;
	private String getLabelName;

	public String getAddUpdateClassMeta() {
		return addUpdateClassMeta;
	}

	public String getSearchFromText() {
		return searchFromText;
	}

	public String getSearchFromGraph() {
		return searchFromGraph;
	}

	public String getGetLabelName() {
		return getLabelName;
	}

}
