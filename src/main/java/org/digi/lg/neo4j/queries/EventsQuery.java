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

public class EventsQuery {
	private String getByGuid;
	private String getEventsClassesTreeA;
	private String getEventsClassesTreeB;
	private String getEventsByContractType;
	private String getEventsByScriptByAssetId;
	private String detachDeleteNode;

	public String getDetachDeleteNode() {
		return detachDeleteNode;
	}

	public String getEventsByScriptProductId() {
		return getEventsByScriptProductId;
	}

	private String getEventsByScriptProductId;

	public String getEventsByScriptByAssetId() {
		return getEventsByScriptByAssetId;
	}

	public String getEventsByContractType() {
		return getEventsByContractType;
	}

	public String getByGuid() {
		return getByGuid;
	}

	public String getEventsClassesTreeA() {
		return getEventsClassesTreeA;
	}

	public String getEventsClassesTreeB() {
		return getEventsClassesTreeB;
	}

}
