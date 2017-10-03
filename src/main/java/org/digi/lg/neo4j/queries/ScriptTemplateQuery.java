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

public class ScriptTemplateQuery {
	private String getByGuid;
	private String getByName;
	private String getConfigItem;
	private String getEventType;
	private String getTriggerDataItem;
	private String getScriptTemplateInfoByAduGuid;
	private String getScriptTemplateInfoByContractType;
	private String getScriptTemplateByScript;
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

	public String getConfigItem() {
		return getConfigItem;
	}

	public String getEventType() {
		return getEventType;
	}

	public String getTriggerDataItem() {
		return getTriggerDataItem;
	}

	public String getScriptTemplateInfoByAduGuid() {
		return getScriptTemplateInfoByAduGuid;
	}

	public String getScriptTemplateInfoByContractType() {
		return getScriptTemplateInfoByContractType;
	}

	public String getScriptTemplateByScript() {
		return getScriptTemplateByScript;
	}

}
