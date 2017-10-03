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

public class ProductClassQuery {
	private String classHasDataItem;
	private String classHasEvents;
	private String classHasAggDataItem;
	private String createLink;
	private String isAssetNameExistUnderClass;
	private String getByGuid;
	private String getByName;
	private String getAllProductClasses;

	public String getCreateLink() {
		return createLink;
	}

	public String getAllProductClasses() {
		return getAllProductClasses;
	}

	public String getClassHasDataItem() {
		return classHasDataItem;
	}

	public String getClassHasEvents() {
		return classHasEvents;
	}

	public String getClassHasAggDataItem() {
		return classHasAggDataItem;
	}

	public String getIsAssetNameExistUnderClass() {
		return isAssetNameExistUnderClass;
	}

	public String getByGuid() {
		return getByGuid;
	}

	public String getByName() {
		return getByName;
	}

}
