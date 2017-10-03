/*******************************************************************************
* Copyright (c) 2017  Wipro Digital. All rights reserved.
*
* Contributors:
*     Wipro Digital - Looking Glass Team.
*     
*     
*     Apr 26, 2017  
 *******************************************************************************/

package org.digi.lg.neo4j.pojo.services;

public class ScriptRepoPojo {

	private String guid;
	private String scripturl;
	private String scriptlocation;
	private String gitpwd;
	private String gituser;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getScripturl() {
		return scripturl;
	}

	public void setScripturl(String scripturl) {
		this.scripturl = scripturl;
	}

	public String getScriptlocation() {
		return scriptlocation;
	}

	public void setScriptlocation(String scriptlocation) {
		this.scriptlocation = scriptlocation;
	}

	public String getGitpwd() {
		return gitpwd;
	}

	public void setGitpwd(String gitpwd) {
		this.gitpwd = gitpwd;
	}

	public String getGituser() {
		return gituser;
	}

	public void setGituser(String gituser) {
		this.gituser = gituser;
	}



}
