/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.domain.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.exception.DataException;
import org.digi.lg.neo4j.pojo.model.Asset;
import org.digi.lg.neo4j.pojo.model.DataItem;
import org.digi.lg.neo4j.pojo.model.Org;
import org.digi.lg.neo4j.pojo.services.AssetPojo;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.pojo.services.NeighbourPojo;
import org.digi.lg.neo4j.pojo.services.Node;
import org.digi.lg.neo4j.pojo.services.Results;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface DataProviderService {

	/**
	 * set dynamic column in ANY in worst case
	 *
	 */
	public enum FindProperty {
		GUID(SchemaConstants.PROP_HDMFID_NAME), NAME(SchemaConstants.PROP_NAME), PRINCIPAL_ID(
				SchemaConstants.PROP_PRINCIPAL_ID), LABEL(
						SchemaConstants.LABEL), SERIAL_NUMBER(SchemaConstants.PROP_SERIAL_NUMBER), ANY("");
		private String column;

		private FindProperty(String column) {
			this.column = column;
		}

		public String getColumn() {
			return this.column;
		}

		public void setColumn(String value) {
			this.column = value;
		}

	}

	Node<Object> getNode(final Vertex vertex, boolean isTypeRequired);

	Results<List<Node<Object>>> findNodeByProperty(final String appId, final String userId, final FindProperty property,
			final String propertyValue, final Optional<Integer> pageOffset);

	Results<List<Node<Object>>> findAssetByProperty(final String appId, final String userId,
			final FindProperty property, final String propertyValue, final Optional<Integer> pageOffset);

	Map<String, Object> expandNode(final String appId, final String userId, final Optional<String> guidLabel,
			final String guid, final boolean onlyAssets, final Optional<Integer> pageOffset, boolean acrossDomain);

	Results<List<NeighbourPojo>> expandNodeV2(final String appId, final String userId, final Optional<String> guidLabel,
			final String guid, final boolean onlyAssets, final Optional<Integer> pageOffset, boolean acrossDomain);

	ContractVertex getContractInfo(final String principalId, final String appId);

	Vertex getVertex(final Optional<String> label, final String propertyName, final String propertyValue);

	@Deprecated
	Vertex getAuthorizedVertex(final String appId, final String userId, final String guid, boolean searchInSameDomain);

	Vertex getAuthorizedVertex(final String appId, final String userId, final String guid,
			final Optional<String> guidLabel);

	Node<Object> getAuthorizedNode(final String appId, final String userId, final String guid,
			boolean searchInSameDomain);

	JsonObject getDataShardInfo(final Vertex vertex);

	JsonObject getDataShardInfo(final String label, final String guid);

	boolean isOrgExist(final String parentLabel, final String parentPropLabel, final String parentPropGuid,
			final String childType, final String childPropName, final String childName, final Relationship relType,
			Direction direction);

	boolean isClassExist(final String label, final String name);

	JsonObject getParent(final String userId, final String appId, Optional<Integer> pageOffset);

	Node<Object> getClasses(final String userId, final String appId, final String guid, Optional<String> label,
			Optional<Integer> pageOffset);

	@SuppressWarnings("rawtypes")
	Results<List<Node>> getAssetByOrg(String userId, String appId, String orgGuid, Optional<String> label,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset);

	Node<Object> getDataItem(String userId, String appId, String classGuid, Optional<String> label,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset);

	JsonArray getAssets(String userId, String appId, String classGuid, Optional<String> label,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset);

	Node<Object> getOrgAndAssets(String userId, String appId, String classGuid, Optional<String> label,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset);

	Collection<Node<String>> getNodeProperties(String userId, String appId, String classGuid,
			Optional<String> classLabel);

	Node<Object> getDIClasses(String userId, String appId, Map<String, Map<String, String>> filters,
			Optional<Integer> pageOffset);

	Results<Map<String, String>> getBootStrapKey(String productName, String serialNumber);

	Results<Map<String, String>> getBootStrapKeyByOrgAndSerialNum(String orgName, String serialNumber);

	/**
	 * 
	 * @param userAId:userID
	 *            of A party.
	 * @param userAAppId:
	 *            appID of A party.
	 * @param userBToken:
	 *            encrypted token of B party.
	 * @return
	 * @throws DataException
	 */
	Results<Boolean> validatePerson(String principalId);

	Results<JsonArray> getScriptTemplate(String userId, String appId);

	Results<JsonArray> getAssetbyProduct(String productClassGuid);

	Results<JsonObject> getServiceInfo(String userId, String appId);

	Results<JsonObject> getSciptConfigInfo(String scriptGuid);

	Results<JsonArray> findCompatible(String userId, String appId, List<String> assets);

	Results<JsonArray> getScriptDetails(String scriptGuid, JsonObject assetsJson);

	Results<JsonArray> getCIAndScopeByScript(String scriptTemplateName);

	Results<String> getMashupInfo(String userId, String appId, String scriptpath);

	/**
	 * get tree of `events` class linked to user's `contract_type`
	 * 
	 * @param userId
	 * @param appId
	 * @return
	 */
	Results<JsonObject> getEventsClasses(String userId, String appId);

	List<DataItem> getDataItemsfromProductClass(String productClassId);

	List<DataItem> getDataItemsFromProductClass(String productClassId, String dataItemName);

	List<AssetPojo> getDsAggData(String userId, String appId, JsonObject query);

	Results<List<AssetPojo>> getDataItemById(String userId, String appId, JsonObject query);

	Results<List<Vertex>> getAllProducClasses();

	Results<List<Node<Object>>> getOrgsByUser(String userId, String appId, Map<String, Map<String, String>> filters,
			Optional<Integer> pageOffset);

	Results<List<Node<Object>>> getDataItems(String userId, String appId, Map<String, Map<String, String>> filters,
			Optional<Integer> pageOffset);

	Results<JsonArray> getEventsAndDIByUser(String userId, String appId, JsonObject query);

	Results<JsonArray> getMashupRepoDetails();

	Results<JsonArray> getMashup(String userId, String appId);

	Results<JsonArray> termAssociation(JsonArray jarray, Vertex termvertex);

	Results<JsonArray> getResponseForUpdateConfigDetails(String scriptGuid, JsonObject assetsJson);

	Results<JsonArray> getAssetbyClass(String classGuid, String classLabel);

	Results<JsonArray> getContractTypesForUser(String userId, String appId);

	Results<List<Node<Object>>> getOrgsByType(String userId, String appId, String assetGuid, String type);

	boolean isExist(String parentLabel, String parentPropLabel, String parentPropGuid, String childType,
			String childPropLabel, String childName, Relationship relType, Direction direction);

	boolean isExist(final String label, final String property, final String value);

	List<Map<String, Object>> haveChildren(String guid, String srcLabel, String destLabel, String direction,
			String edgeLabel, Long depth);

	List<Asset> getAssetByProductClass(String productClass, String assetId);

	List<Asset> getAssetByOrg(String orgGuid, String serialNumber);

	Asset getLicenceAsset(String orgGuid);
	List<Asset> getAllLicenceAsset();

	Org getOrgBootStrapKey(String bootStrapKey);

	Asset saveUpdate(Map<String, Object> params);

	@SuppressWarnings("rawtypes")
	Results<List<Node>> getAssetsByUser(String userId, String appId, Map<String, Map<String, String>> filters,
			Optional<Integer> pageOffset);

}
