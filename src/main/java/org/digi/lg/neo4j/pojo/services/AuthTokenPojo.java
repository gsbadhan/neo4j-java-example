/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.pojo.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.text.StrBuilder;

public class AuthTokenPojo {
	private String tokenGuid;
	private List<String> contractTypeIds;
	private String $appId;
	private String $userId;
	private boolean isPrincipalIdExist;
	private String principalBId;
	private long expiryDate;
	private List<String> assetGuids;
	private List<String> tokens;

	public boolean isPrincipalIdExist() {
		return isPrincipalIdExist;
	}

	public void setPrincipalIdExist(boolean isPrincipalIdExist) {
		this.isPrincipalIdExist = isPrincipalIdExist;
	}

	public String getTokenGuid() {
		return tokenGuid;
	}

	public void setTokenGuid(String tokenGuid) {
		this.tokenGuid = tokenGuid;
	}

	public List<String> getContractTypeIds() {
		return contractTypeIds;
	}

	public void setContractTypeIds(List<String> contractTypeIds) {
		this.contractTypeIds = contractTypeIds;
	}

	public String getPrincipalBId() {
		return principalBId;
	}

	public String get$appid() {
		return $appId;
	}

	public void set$appid(String $appid) {
		this.$appId = $appid;
	}

	public String get$userid() {
		return $userId;
	}

	public void set$userid(String $userid) {
		this.$userId = $userid;
	}

	public void setPrincipalBId(String principalBId) {
		this.principalBId = principalBId;
	}

	public long getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(long expiryDate) {
		this.expiryDate = expiryDate;
	}

	public List<String> getAssetGuids() {
		return assetGuids;
	}

	public void setAssetGuids(List<String> assetGuids) {
		this.assetGuids = assetGuids;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Generate token string for Encryption.
	 * 
	 * @param tokenGuid
	 * @param contractTypeIds
	 * @param principalAId
	 * @param principalBId
	 * @param expiryDate
	 * @param serialNumbers
	 * @return
	 */
	public static String generateTokenString(String tokenGuid, List<String> tokens, String principalBId,
			List<String> assetGuids) {
		return new StrBuilder("tokenGuid:").append(tokenGuid).append(":tokens:").append(tokens).append(":principalBId:")
				.append(principalBId).append(":assetGuids:").append(assetGuids).toString();
	}

	/**
	 * Generate AuthToken from String.
	 * 
	 * @param tokenString
	 * @return
	 */
	public static AuthTokenPojo stringToAuthToken(String tokenString) {
		StringTokenizer stringTokenizer = new StringTokenizer(tokenString, ":");
		Map<String, Object> tokenMap = new HashMap<>();
		while (stringTokenizer.hasMoreTokens()) {
			tokenMap.put(stringTokenizer.nextToken(),
					(stringTokenizer.hasMoreElements()) ? stringTokenizer.nextToken() : null);
		}
		AuthTokenPojo authTokenPojo = new AuthTokenPojo();
		authTokenPojo.setTokenGuid(tokenMap.get("tokenGuid").toString());
		authTokenPojo.setTokens(
				Arrays.asList(tokenMap.get("tokens").toString().replaceAll("^\\[|]$", "").split("\\s*,\\s*")));
		authTokenPojo.setAssetGuids((tokenMap.get("assetGuids") != null
				? Arrays.asList(tokenMap.get("assetGuids").toString().replaceAll("^\\[|]$", "").split("\\s*,\\s*"))
				: null));
		authTokenPojo.setPrincipalBId(tokenMap.get("principalBId").toString());
		return authTokenPojo;
	}

}
