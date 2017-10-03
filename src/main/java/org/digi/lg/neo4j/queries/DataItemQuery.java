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

public class DataItemQuery {
	private String getByGuid;
	private String getDataItemByContractType;
	private String getEdgesByProductClass;
	private String dataItemHasProductClass;
	private String getDataItemsByClass;
	private String getDataItemByProductClass;
	private String getDomainDataItems;
	private String detachDeleteNode;

	public String getDetachDeleteNode() {
		return detachDeleteNode;
	}

	public String getEdgesByProductClass() {
		return getEdgesByProductClass;
	}

	public String getDataItemsByClass() {
		return getDataItemsByClass;
	}

	public String getDataItemHasProductClass() {
		return dataItemHasProductClass;
	}

	public String getDataItemByContractType() {
		return getDataItemByContractType;
	}

	public String getByGuid() {
		return getByGuid;
	}

	public String getDataItemByProductClass() {
		return getDataItemByProductClass;
	}

	public String getDomainDataItems() {
		return getDomainDataItems;
	}

}
