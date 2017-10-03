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

public class DataShardPojo {

	private String guid;
	private String apiGatewayUrl;
	private String hashKey;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getApiGatewayUrl() {
		return apiGatewayUrl;
	}

	public void setApiGatewayUrl(String apiGatewayUrl) {
		this.apiGatewayUrl = apiGatewayUrl;
	}

	public String getHashKey() {
		return hashKey;
	}

	public void setHashKey(String hashKey) {
		this.hashKey = hashKey;
	}

}
