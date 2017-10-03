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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.digi.lg.neo4j.core.DBRowUUID.AssetUID;
import org.digi.lg.neo4j.core.DBRowUUID.ClassUID;
import org.digi.lg.neo4j.core.DBRowUUID.ContractTypeUID;
import org.digi.lg.neo4j.core.DBRowUUID.DataItemUID;
import org.digi.lg.neo4j.core.DBRowUUID.EventsUID;
import org.digi.lg.neo4j.core.DBRowUUID.ProductClassUID;
import org.digi.lg.neo4j.core.DBRowUUID.ScriptTemplateUID;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.DataItem;
import org.digi.lg.neo4j.pojo.model.Events;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.pojo.services.ConnectionLinks;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.pojo.services.GraphNode;
import org.digi.lg.neo4j.pojo.services.LinkPojo;
import org.digi.lg.neo4j.pojo.services.LinkSet;
import org.digi.lg.neo4j.pojo.services.Node;
import org.digi.lg.neo4j.pojo.services.OrgNode;
import org.digi.lg.neo4j.pojo.services.RegisterAgentAsset;
import org.digi.lg.neo4j.pojo.services.RegisterAgentMetricAsset;
import org.digi.lg.neo4j.pojo.services.Results;
import org.digi.lg.neo4j.pojo.services.UpdateAgentAsset;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface ModelCRUDService {

	Vertex saveUpdateNode(String label, Map<String, Object> properties);

	List<Vertex> addAdminUnit(String principalGuid, String aduGuid, String contractGuid, String name,
			String principalId, ContractVertex contractInfo, String parentAduGuid, String parentOrgGuid);

	Map<String, Vertex> addPrincipal(String personGuid, String adminUnitGuid, String contractGuid,
			String contractTypeGuid, String classPersonAssetGuid, String classPersonClassGuid, String personOrgGuid,
			String classPersonOrgGuid, String classPersonCompanyGuid, String classPersonProductsGuid,
			String classPersonEventGuid, String classPrincipalBundleGuid, String classPrincipalContactsGuid,
			String principalId, String accountName, String bootStrapKey, boolean isDataSyncFlag, Integer isAdminFlag);

	Results<Boolean> addContractType(String contractType, ContractTypeUID contractTypeUID, Vertex ctDerivedClass,
			String appGuid, String parentContractTypeGuid, String termDataguid, String termEventGuid,
			String termEventTypeGid, String termActionGuid, String termActionTypeGuid, String termServiceGuid,
			String termMashupGuid, Map<String, Object> propertyValueMap, String name, String contractGuid);

	Boolean addLicenceTerm(String contractType, Vertex ctDerivedClass, String appGuid, String parentContractTypeGuid,
			Map<String, Object> propertyValueMap, String name, String contractGuid);

	Vertex addNode(String newGuid, OrgNode orgNode, List<Vertex> relationVertices, Vertex belongsVertx, String label,
			boolean DbSyncFlag);

	Vertex addOrgNode(String newGuid, OrgNode orgNode, List<Vertex> relationVertices, Vertex belongsVertx, String label,
			boolean isDataSync);

	Vertex addAssetNode(String newGuid, AssetUID assetUID, OrgNode orgNode, List<Vertex> relationVertices,
			Vertex belongsVertx, boolean isDataSync);

	Vertex addClassNode(String classNewGuid, ClassUID classUID, Node<Object> classNode, Vertex relationVertex,
			boolean model, boolean isDataSync);

	DataItem addDataItem(String newDataItemGuid, DataItemUID dataItemUID, GraphNode orgNode,
			List<Vertex> classRelationVertices, Vertex belongs, String type, boolean isDataSync);

	Events addEvent(GraphNode orgNode, EventsUID EventsUID, List<Vertex> classRelationVertices, Vertex belongs,
			String type, boolean isDataSync);

	Vertex addProductClassNode(String classNewGuid, ProductClassUID productClassUID, Node<Object> classNode, Vertex relationVertex, boolean isDataSync);

	Map<String, Vertex> associateDataItems(String srcClassGuid, Optional<String> srcClasLabel,
			List<String> dataItemGuids);

	Map<String, Vertex> associateEvents(String srcClassGuid, Optional<String> srcClasLabel, List<String> eventsGuids);

	Map<String, Vertex> associateAggDataItems(String srcClassGuid, Optional<String> srcClasLabel,
			Map<String, String> dataItemGuids);

	Vertex associatePrinicpal(Principal userId, String appId, AdminUnit aduGuid, Principal principalGuid)
			throws Exception;

	boolean linkAsset(String userId, String appId, List<ConnectionLinks> links);

	boolean linkAsset(List<ConnectionLinks> links);

	boolean unLinkAsset(String userId, String appId, List<ConnectionLinks> links);

	boolean unLinkAsset(List<ConnectionLinks> links);

	boolean unLinkAll(String userId, String appId, String gatewayGuid, Optional<String> gatewayLabel);

	boolean unLinkAll(String gatewayGuid, Optional<String> gatewayLabel);

	Results<UpdateAgentAsset> updateAgentAsset(String userId, String appId, UpdateAgentAsset agentAssets);

	Results<UpdateAgentAsset> updateAgentAsset(UpdateAgentAsset agentAssets);

	Results<RegisterAgentAsset> registerAgentAsset(String userId, String appId, RegisterAgentAsset message);

	List<List<LinkPojo>> createLink(String userId, String appId, LinkSet linkSet);

	boolean modifyLink(String userId, String appId, String sourceLabel, String destLabel, String oldSourceGuid,
			String newSourceGuid, String oldDestGuid, String newDestGuid);

	Vertex createAuthToken(String tokenGuid, String encryptedToken, String adminUnitGuid, long expiryDate,
			String principalId);

	boolean deleteAuthToken(String tokenGuid);

	Results<JsonObject> addScriptInstance(String userId, String appId, String scriptTemplateGuid, String scriptGuid,
			String eventsGuid, boolean dbSync);

	void addScriptInstanceDS(String scriptTemplateGuid, String scriptGuid, String scriptName, String eventsGuid,
			String eventsName, String domainClassGuid);

	Results<JsonObject> addScriptTemplate(String userId, String appId, ScriptTemplateUID scriptTemplateUID,
			String scriptTemplateId, String scriptTemplateName, String scriptTemplateScope, JsonArray configItems,
			JsonArray eventTypes, JsonArray dataItems, JsonArray productClasses, boolean dbSync);

	Map<String, Vertex> associatePricipalToCT(String principalGuid, String adminUnitGuid, String contractGuid,
			String contractTypeGuid, String classPrincipalClassGuid, String accountname, String principalOrgGuid);

	Map<String, Vertex> associatePricipalToContractType(String principalId, String appId, String contractTypeId,
			String accountName, String contractId);

	Results<Boolean> associateAssetToAccount(List<String> assetGuids, String location, String accountName,
			Vertex parentVertex, boolean isDBSync);

	Results<JsonObject> updateConfigInfo(String userId, String appId, String scriptGuid, JsonArray configItems,
			JsonArray assets);

	Results<JsonObject> addMashupScript(String userId, String appId, String mashupGuid, String mashupName,
			String mashupPath, String scriptRepoGuid, String scriptRepoName, String scriptRepoUrl,
			String scriptRepouser, String scriptRepopwd, String scriptRepolocation);

	Map<String, Vertex> addPrincipalToDbSync(String principalGuid, String classPersonAssetGuid,
			String classPersonClassGuid, String personOrgGuid, String classPersonOrgGuid, String classPersonCompanyGuid,
			String classPersonProductsGuid, String classPersonEventGuid, String classPrincipalBundleGuid,
			String classPrincipalContactsGuid, String principalId, String accountName, String bootStrapKey);

	List<List<LinkPojo>> createLinkForDBSync(LinkSet linkSet);

	<TRX> boolean modifyClassIsClass(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid);

	<TRX> boolean modifyOrgHasAsset(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid);

	<TRX> boolean modifyOrgHasOrg(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid);

	<TRX> boolean modifyAssetHasAsset(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid);

	<TRX> boolean modifyAssetIsClass(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid);

	<TRX> boolean modifyOrgIsClass(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid);

	<TRX> boolean modifyProductClassIsClass(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid);

	<TRX> boolean modifyClassIsProductClass(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid);

	<TRX> boolean deleteNode(TRX trx, String guid, String label);

	Results<JsonObject> removeNode(String nodeGuid, String label);

	Results<Boolean> deleteLinks(String userId, String appId, org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest);

	Results<Boolean> createLinks(String userId, String appId, org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest);

	Results<RegisterAgentMetricAsset> registerMetricGateway(RegisterAgentMetricAsset registerAgentAsset);
	Vertex addLicenceAssetNode(String newGuid, AssetUID assetUID, OrgNode orgNode, List<Vertex> relationVertices,
			Vertex belongsVertx, boolean isDataSync);

}
