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

public class ScriptQuery {
	private String getByGuid;
	private String getByName;
	private String getScriptsByAduGuid;
	private String getScriptConfigItems;
	private String getScriptPublicAssets;
	private String getScriptDataItems;
	private String getScriptEvents;
	private String countScriptTemplateInstance;
	private String getScriptAdminUnits;
	private String getOtherScripts;
	private String getScriptByScriptTemplate;
	private String getScriptAppliesToAssets;
	private String getScriptTemplateInstance;
	private String createLink;

	public String getCreateLink() {
		return createLink;
	}

	public String getByGuid() {
		return getByGuid;
	}

	public String getByName() {
		return getByName;
	}

	public String getScriptsByAduGuid() {
		return getScriptsByAduGuid;
	}

	public String getScriptConfigItems() {
		return getScriptConfigItems;
	}

	public String getScriptPublicAssets() {
		return getScriptPublicAssets;
	}

	public String getScriptDataItems() {
		return getScriptDataItems;
	}

	public String getScriptEvents() {
		return getScriptEvents;
	}

	public String countScriptTemplateInstance() {
		return countScriptTemplateInstance;
	}

	public String getScriptAdminUnits() {
		return getScriptAdminUnits;
	}

	public String getOtherScripts() {
		return getOtherScripts;
	}

	public String getScriptByScriptTemplate() {
		return getScriptByScriptTemplate;
	}

	public String getScriptAppliesToAssets() {
		return getScriptAppliesToAssets;
	}

	public String getScriptTemplateInstance() {
		return getScriptTemplateInstance;
	}

}
