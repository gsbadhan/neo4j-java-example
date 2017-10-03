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

public class MashUpQuery {
	private String getByGuid;
	private String getByPath;
	private String getMashUpInfoByAduGuid;
	private String getmashupHasScriptRepo;
	private String getScriptrepro;
	private String getScriptRepobyPath;

	public String getByGuid() {
		return getByGuid;
	}

	public String getByPath() {
		return getByPath;
	}

	public String getMashUpInfoByAduGuid() {
		return getMashUpInfoByAduGuid;
	}

	public void setMashUpInfoByAduGuid(String getMashUpInfoByAduGuid) {
		this.getMashUpInfoByAduGuid = getMashUpInfoByAduGuid;
	}

	public String getmashupHasScriptRepo() {
		return getmashupHasScriptRepo;
	}

	public void setmashupHasScriptRepo(String getmashupHasScriptRepo) {
		this.getmashupHasScriptRepo = getmashupHasScriptRepo;
	}

	public String getScriptrepro() {
		return getScriptrepro;
	}

	public void setScriptrepro(String getScriptrepro) {
		this.getScriptrepro = getScriptrepro;
	}

	public String getScriptRepobyPath() {
		return getScriptRepobyPath;
	}

	public void setScriptRepobyPath(String getScriptRepobyPath) {
		this.getScriptRepobyPath = getScriptRepobyPath;
	}

	
}
