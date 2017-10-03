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

import static org.digi.lg.neo4j.dao.DaoProvider.adminUnitDao;
import static org.digi.lg.neo4j.dao.DaoProvider.orgDao;
import static org.digi.lg.neo4j.dao.DaoUtil.getParamMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.digi.lg.neo4j.cache.CacheProvider;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.JsonConstants;
import org.digi.lg.neo4j.core.ModifyNode;
import org.digi.lg.neo4j.core.ModifyNode.Node;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.dao.DaoProvider;
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.Org;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.pojo.services.ConnectionLinks;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.pojo.services.LinkDestination;
import org.digi.lg.neo4j.pojo.services.LinkPojo;
import org.digi.lg.neo4j.pojo.services.LinkSet;
import org.digi.lg.neo4j.pojo.services.LinkSource;
import org.digi.lg.neo4j.pojo.services.OrgNode;
import org.digi.lg.neo4j.pojo.services.RegisterAgentAsset;
import org.digi.lg.neo4j.pojo.services.Results;
import org.digi.lg.neo4j.pojo.services.UpdateAgentAsset;
import org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.Asset;
import org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.Classes;
import org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.ProductClass;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Session;

import com.google.gson.Gson;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ModelCRUDServiceImplTestIT {

	private ModelCRUDService modelCRUDService;

	private GraphFactory graphFactory;

	private ModifyNode modifyNode;
	private Node srcNode;
	private Node destNode;

	@Before
	public void setUp() throws Exception {
		modelCRUDService = new ModelCRUDServiceImpl(new DataProviderServiceImpl(new AuthorizationServiceImpl()));
		graphFactory = DaoProvider.getGraphFactory();
		modifyNode = new ModifyNode();
		srcNode = modifyNode.new Node();
		destNode = modifyNode.new Node();
	}

	@Test
	public void testAddPrincipal() {
		String personGuid = UUID.randomUUID().toString();
		String adminUnitGuid = UUID.randomUUID().toString();
		String contractGuid = UUID.randomUUID().toString();
		String contractTypeGuid = "mndt-ctt1001";
		String classPersonAssetGuid = UUID.randomUUID().toString();
		String classPersonClassGuid = UUID.randomUUID().toString();
		String personOrgGuid = UUID.randomUUID().toString();
		String classPersonOrgGuid = UUID.randomUUID().toString();
		String classPersonCompanyGuid = UUID.randomUUID().toString();
		String classPersonProductsGuid = UUID.randomUUID().toString();
		String classPersonEventGuid = UUID.randomUUID().toString();
		String classPersonBundleGuid = UUID.randomUUID().toString();
		String classPersonContactsGuid = UUID.randomUUID().toString();
		String principalId = "gurpreetAcctIdTest";
		String accountName = "gurpreetAcctTest";
		Map<String, Vertex> jsonObject = modelCRUDService.addPrincipal(personGuid, adminUnitGuid, contractGuid,
				contractTypeGuid, classPersonAssetGuid, classPersonClassGuid, personOrgGuid, classPersonOrgGuid,
				classPersonCompanyGuid, classPersonProductsGuid, classPersonEventGuid, classPersonBundleGuid,
				classPersonContactsGuid, principalId, accountName, null, false, 1);
		assertNotNull(jsonObject);
	}

	@Test
	public void testAddContractType() {

		String contractTypeGuid = "con-gu-000-111-444-12455";
		String contractTypeName = "ContractTypeName1";
		String authToken = "123456789-09878654";
		String apiKey = "111111-222222";

		String contractGuid = "mndt-ct1001";
		String appGuid = "hiltiapp";
		String parentContractTypeGuid = "wiprocontracttype";
		String termDataguid = "term-data-r45894-44";
		String termEventGuid = "term-event-r45894-44";
		;
		String termEventTypeGuid = "term-event-type-r45894-44";
		String termActionGuid = "term-action-r45894-44";
		String termActionTypeGuid = "term-action-type-r45894-44";
		String termServiceGuid = "term-service-type-r45894-44";
		String termMashupGuid = "term-mashup-type-r45894-44";

		String name = "test";
		Map<String, Object> parameterMap = new HashMap<>();
		String isDerivedClassId = "derviced-class-4234234dee";
		parameterMap.put(SchemaConstants.PROP_HDMFID_NAME, isDerivedClassId);
		Vertex isDerived = new Vertex();
		isDerived.setLabel("product_class");
		isDerived.setNode(parameterMap);
		Map<String, Object> propertyValueMap = new HashMap<String, Object>();
		propertyValueMap.put(SchemaConstants.PROP_HDMFID_NAME, contractTypeGuid);
		propertyValueMap.put(SchemaConstants.PROP_NAME, contractTypeName);
		propertyValueMap.put(SchemaConstants.PROP_AUTH_TOKEN, authToken);
		propertyValueMap.put(SchemaConstants.PROP_API_KEY, apiKey);

		org.digi.lg.neo4j.core.Vertex contType = modelCRUDService.saveUpdateNode(SchemaConstants.LABEL_CONTRACT_TYPE,
				propertyValueMap);
		assertNotNull(contType);

		Results<Boolean> contTypeUpdated = modelCRUDService.addContractType(contractTypeGuid, null, isDerived, appGuid,
				parentContractTypeGuid, termDataguid, termEventGuid, termEventTypeGuid, termActionGuid,
				termActionTypeGuid, termServiceGuid, termMashupGuid, propertyValueMap, name, contractGuid);

		assertTrue(contTypeUpdated.getData());
	}

	@Test
	public void testAddAdminUnit() {
		String appId = "wiproAdmin";
		String principalGuid = "pr-100101";
		String aduGuid = "adu-100101";
		String contractGuid = "ct-100101";
		String name = "testAddAdminUnit";
		String principalId = "g@mail.com";
		String contractTypeGuid = "ctt-100101";
		String parentAduGuid = "add-100100";
		String parentAduName = "parenr-org";
		String parentOrgGuid = "org-100100";
		String parentOrgName = "parent-adu";
		Session session = graphFactory.writeSession();
		// create parent org
		Org org = orgDao.addUpdate(session,
				getParamMap(SchemaConstants.PROP_HDMFID_NAME, parentOrgGuid, SchemaConstants.PROP_NAME, parentOrgName));
		assertNotNull(org);
		// create parent adu
		AdminUnit adu = adminUnitDao.save(session,
				getParamMap(SchemaConstants.PROP_HDMFID_NAME, parentAduGuid, SchemaConstants.PROP_NAME, parentAduName));
		assertNotNull(adu);

		ContractVertex contractInfo = CacheProvider.personAppContractCache.get(principalId, appId);
		List<Vertex> vertexs = modelCRUDService.addAdminUnit(principalGuid, aduGuid, contractGuid, name, principalId,
				contractInfo, parentAduGuid, parentOrgGuid);

		assertNotNull(vertexs);
		assertFalse(vertexs.isEmpty());
	}

	@Test
	public void testAddNode() {
		String newGuid = "";
		OrgNode orgNode = new OrgNode();
		List<Vertex> relationVertices = new LinkedList<>();
		Vertex belongsVertx = new Vertex();
		String label = "asset";
		Vertex retNode = modelCRUDService.addNode(newGuid, orgNode, relationVertices, belongsVertx, label, true);
		assertNotNull(retNode);
	}

	@Test
	public void testAssociateDataItems() {
		String srcClassGuid = "mdclas-1001";
		Optional<String> srcClasLabel = Optional.of(SchemaConstants.LABEL_PRODUCT_CLASS);
		List<String> dataItemGuids = new ArrayList<>(5);
		dataItemGuids.add("di-1001");
		dataItemGuids.add("di-1002");
		dataItemGuids.add("di-1003");
		dataItemGuids.add("di-1004");
		dataItemGuids.add("di-1005");
		Map<String, Vertex> vertxMap = modelCRUDService.associateDataItems(srcClassGuid, srcClasLabel, dataItemGuids);
		assertNotNull(vertxMap);
	}

	@Test
	public void testAssociateEvents() {
		String srcClassGuid = "mdclas-1001";
		Optional<String> srcClasLabel = Optional.of(SchemaConstants.LABEL_PRODUCT_CLASS);
		List<String> dataItemGuids = new ArrayList<>(5);
		dataItemGuids.add("evts-1001");
		dataItemGuids.add("evts-1002");
		dataItemGuids.add("evts-1003");
		dataItemGuids.add("evts-1004");
		dataItemGuids.add("evts-1005");
		Map<String, Vertex> vertxMap = modelCRUDService.associateEvents(srcClassGuid, srcClasLabel, dataItemGuids);
		assertNotNull(vertxMap);
	}

	@Test
	public void testAssociateAggDataItems() {
		String srcClassGuid = "mdclas-1001";
		Optional<String> srcClasLabel = Optional.of(SchemaConstants.LABEL_PRODUCT_CLASS);
		Map<String, String> dataItemGuids = new HashMap<>(2);
		dataItemGuids.put("di-1006", "100");
		dataItemGuids.put("di-1007", "90");
		Map<String, Vertex> vertxMap = modelCRUDService.associateAggDataItems(srcClassGuid, srcClasLabel,
				dataItemGuids);
		assertNotNull(vertxMap);
	}

	@Test
	public void testAssociatePrinicpal() throws Exception {
		@SuppressWarnings("unchecked")
		Principal loginPrincipal = new Principal(new Vertex((Map<String, Object>) new HashMap<String, Object>()
				.put(SchemaConstants.PROP_HDMFID_NAME, "p1@gmail.com")));
		@SuppressWarnings("unchecked")
		Principal principal = new Principal(new Vertex(
				(Map<String, Object>) new HashMap<String, Object>().put(SchemaConstants.PROP_HDMFID_NAME, "pr-1002")));
		@SuppressWarnings("unchecked")
		AdminUnit adu = new AdminUnit(new Vertex(
				(Map<String, Object>) new HashMap<String, Object>().put(SchemaConstants.PROP_HDMFID_NAME, "ad-1001")));
		String appId = "WiproAdmin";
		Vertex vertex = modelCRUDService.associatePrinicpal(loginPrincipal, appId, adu, principal);
		assertNotNull(vertex);
	}

	@Test
	public void testLinkAsset() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		List<ConnectionLinks> links = new ArrayList<>(2);
		links.add(new ConnectionLinks("ctt-1003", null, "ctt-1003", null));
		boolean linksCreated = modelCRUDService.linkAsset(userId, appId, links);
		assertTrue(linksCreated);
	}

	@Test
	public void testUnLinkAsset() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		List<ConnectionLinks> links = new ArrayList<>(2);
		links.add(new ConnectionLinks("org-1001", "org", "ast-2001", "asset"));
		links.add(new ConnectionLinks("org-1001", "org", "ast-2001", "asset"));
		boolean linksCreated = modelCRUDService.unLinkAsset(userId, appId, links);
		assertTrue(linksCreated);
	}

	@Test
	public void testUnLinkAll() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		String gatewayGuid = "ast-1041";
		Optional<String> gatewayLabel = Optional.of("asset");
		boolean linksCreated = modelCRUDService.unLinkAll(userId, appId, gatewayGuid, gatewayLabel);
		assertTrue(linksCreated);
	}

	@Test
	public void testCreateLink() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		LinkSet linkSet = new LinkSet();
		List<LinkSource> linkSource = new ArrayList<>(2);
		List<LinkDestination> linkDestinations = new ArrayList<>(1);
		linkDestinations.add(new LinkDestination("ast-2001", "asset", "OUT", "HAS"));
		linkSource.add(new LinkSource("org-1001", "org", linkDestinations));
		linkSet.setLinks(linkSource);
		List<List<LinkPojo>> links = modelCRUDService.createLink(userId, appId, linkSet);
		assertNotNull(links);
	}

	@Test
	public void testModifyOrgAssetLink() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		String orgGuid = "org-1001";
		String assetGuid = "ast-2001";
		String newOrgGuid = "org-3001";
		// boolean isModified = modelCRUDService.modifyOrgAssetLink(userId,
		// appId, orgGuid, assetGuid, newOrgGuid);
		// assertTrue(isModified);
	}

	@Test
	public void testCreateAuthToken() {
		String tokenGuid = "tkn-1001";
		String encryptedToken = "ASDFQW#@#$$#%%^&JGHHF";
		String adminUnitGuid = "ad-1001";
		long expiryDate = 10000678;
		String principalId = "pr-1001";
		Vertex vertex = modelCRUDService.createAuthToken(tokenGuid, encryptedToken, adminUnitGuid, expiryDate,
				principalId);
		assertNotNull(vertex);
	}

	@Test
	public void testAddScriptInstance() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		String scriptTemplateGuid = "spt-10001";
		String scriptGuid = "sp-1001";
		String eventsGuid = "evts-1001";
		Results<JsonObject> results = modelCRUDService.addScriptInstance(userId, appId, scriptTemplateGuid, scriptGuid,
				eventsGuid, false);
		assertNotNull(results);
	}

	@Test
	public void testAddScriptTemplate() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		String scriptTemplateGuid = "spt-10001";
		String scriptTemplateName = "sp-test";
		String scriptTemplateScope = "public";

		JsonArray configItems = new JsonArray();
		JsonObject configItem = new JsonObject();
		configItem.put(JsonConstants.NAME, "config-item-1001");
		configItems.add(configItem);

		JsonArray eventTypes = new JsonArray();
		JsonObject eventType = new JsonObject();
		eventType.put(JsonConstants.NAME, "evt-type-1001");
		eventType.put(JsonConstants.SCOPE, SchemaConstants.PROP_SCOPE);
		eventTypes.add(eventType);

		JsonArray dataItems = new JsonArray();
		JsonObject dataitem = new JsonObject();
		dataitem.put(JsonConstants.GUID, "ditm-1001");
		dataItems.add(dataitem);

		JsonArray productClasses = new JsonArray();
		JsonObject prdtClass = new JsonObject();
		prdtClass.put(JsonConstants.GUID, "prdtCls-1001");
		productClasses.add(prdtClass);

		Results<JsonObject> results = modelCRUDService.addScriptTemplate(userId, appId, null, scriptTemplateGuid,
				scriptTemplateName, scriptTemplateScope, configItems, eventTypes, dataItems, productClasses, false);
		assertNotNull(results);
	}

	/*
	 * @Test public void testAddMashUp() { String userId = "p1@gmail.com";
	 * String appId = "WiproAdmin"; String MashupGuid = "spt-10001"; String
	 * MashupName = "sp-test"; String MashupPath = "public";
	 * 
	 * Results<JsonObject> results = modelCRUDService.addMashupScript(userId,
	 * appId, MashupGuid, MashupName, MashupPath); assertNotNull(results); }
	 */

	@Test
	public void testDeleteAuthToken() {
		String tokenGuid = "tkn-1001";
		boolean deleted = modelCRUDService.deleteAuthToken(tokenGuid);
		assertTrue(deleted);
	}

	@Test
	public void testUpdateConfigInfo() {
		String scriptGuid = "scp-1001";
		JsonArray configItems = new JsonArray();
		JsonObject configItem1 = new JsonObject();
		configItem1.put("label", SchemaConstants.LABEL_CONFIG_ITEM);
		configItem1.put("guid", "cofg-1001");
		configItem1.put("value", "20");
		configItems.add(configItem1);
		JsonArray assets = new JsonArray();
		JsonObject ast = new JsonObject();
		ast.put("label", SchemaConstants.LABEL_ASSET);
		ast.put("guid", "ast-1001");
		Results<JsonObject> dataShard = modelCRUDService.updateConfigInfo(null, null, scriptGuid, configItems, assets);
		assertNotNull(dataShard);
	}

	@Test
	public void testUpdateAgentAsset() {
		String userId = null;
		String appId = null;
		UpdateAgentAsset message = new UpdateAgentAsset();
		List<Asset> assets = new ArrayList<>();
		String serialNumber1 = "srn2001";
		ProductClass productClass1 = new ProductClass("prdt_clas1");
		List<Classes> classes1 = new ArrayList<>();
		classes1.add(new Classes("clas1"));
		classes1.add(new Classes("clas2"));
		assets.add(new Asset(serialNumber1, classes1, productClass1));

		message.setAsset(assets);
		Results<UpdateAgentAsset> results = modelCRUDService.updateAgentAsset(userId, appId, message);
		assertNotNull(results);
		assertNotNull(results.getData());
	}

	@Test
	public void testRegisterAgentAsset() {
		RegisterAgentAsset agentAsset = new RegisterAgentAsset();
		List<org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Asset> assets = new ArrayList<>();
		org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Asset assest1 = new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Asset();
		// bootstrap key of Org
		assest1.setBootStrapKey("bt-2001");
		// Asset's serial number
		assest1.setSerialNumber("srn-3010");
		assest1.setProductClass(new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.ProductClass("prdt_clas2"));
		List<org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Classes> classes1 = new ArrayList<>();
		classes1.add(new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Classes("clas2001"));
		classes1.add(new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Classes("clas2002"));
		assest1.setClasses(classes1);
		assets.add(assest1);
		agentAsset.setAsset(assets);
		System.out.println(new Gson().toJson(agentAsset));
		Results<RegisterAgentAsset> results = modelCRUDService.registerAgentAsset(null, null, agentAsset);
		assertNotNull(results);
		assertNotNull(results.getData());
		System.out.println(new Gson().toJson(results.getData()));
	}

	@Test
	public void testAssociatePricipalToContractType() {
		String principalId = "lgmanturbo@gmail.com";
		String appId = "WiproAdmin";
		String contractTypeId = "newCTT-1001";
		String contractId = "newCT-1001";
		String accountName = "lgmanturbo";
		Map<String, Vertex> map = modelCRUDService.associatePricipalToContractType(principalId, appId, contractTypeId,
				accountName, contractId);
		assertNotNull(map);
	}

	@Test
	public void testRemoveNodeWithOrgLabel() {
		String nodeGuid = "org-1002";
		String label = "org";
		Results<JsonObject> map = modelCRUDService.removeNode(nodeGuid, label);
		assertNotNull(map);
	}

	@Test
	public void testRemoveNodeWithClassLabel() {
		String nodeGuid = "clas-1003";
		String label = "class";
		Results<JsonObject> map = modelCRUDService.removeNode(nodeGuid, label);
		assertNotNull(map);
	}

	@Test
	public void testRemoveNodeWithAssetLabel() {
		String nodeGuid = "ast-1002";
		String label = "asset";
		Results<JsonObject> map = modelCRUDService.removeNode(nodeGuid, label);
		assertNotNull(map);
	}

	@Test
	public void testCreateLinkForAssetHasAsset() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";

		srcNode.setGuid("ast-1001");
		srcNode.setLabel("asset");

		destNode.setGuid("ast-1002");
		destNode.setLabel("asset");

		Results<Boolean> results = modelCRUDService.createLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testCreateLinkForOrgAsset() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";

		srcNode.setGuid("org-1001");
		srcNode.setLabel("org");

		destNode.setGuid("ast-1002");
		destNode.setLabel("asset");

		Results<Boolean> results = modelCRUDService.createLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testCreateLinkForClassClass() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";
		srcNode.setGuid("clas-1001");
		srcNode.setLabel("class");

		destNode.setGuid("clas-1003");
		destNode.setLabel("class");
		Results<Boolean> results = modelCRUDService.createLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testCreateLinkForOrgOrg() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";

		srcNode.setGuid("org-1001");
		srcNode.setLabel("org");

		destNode.setGuid("org-1002");
		destNode.setLabel("org");
		Results<Boolean> results = modelCRUDService.createLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testCreateLinkForAssetClass() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";

		srcNode.setGuid("ast-1001");
		srcNode.setLabel("asset");

		destNode.setGuid("clas-1002");
		destNode.setLabel("class");
		Results<Boolean> results = modelCRUDService.createLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testCreateLinkForOrgClass() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";

		srcNode.setGuid("org-1001");
		srcNode.setLabel("org");

		destNode.setGuid("clas-1002");
		destNode.setLabel("class");
		Results<Boolean> results = modelCRUDService.createLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testCreateLinkForClassProductClass() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";

		srcNode.setGuid("cls-1001");
		srcNode.setLabel("class");

		destNode.setGuid("productClass-1002");
		destNode.setLabel("productClass");
		Results<Boolean> results = modelCRUDService.createLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testCreateLinkForProductClassClass() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";

		srcNode.setGuid("productClass-1001");
		srcNode.setLabel("product_class");

		destNode.setGuid("clas-1002");
		destNode.setLabel("class");
		Results<Boolean> results = modelCRUDService.createLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testDeleteLinkForProductClassClass() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";

		srcNode.setGuid("productClass-1001");
		srcNode.setLabel("product_class");

		destNode.setGuid("clas-1002");
		destNode.setLabel("class");
		Results<Boolean> results = modelCRUDService.deleteLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testDeleteLinkForOrgClass() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";

		srcNode.setGuid("org-1001");
		srcNode.setLabel("org");

		destNode.setGuid("clas-1002");
		destNode.setLabel("class");
		Results<Boolean> results = modelCRUDService.deleteLinks(userId, appId, srcNode, destNode);

		assertNotNull(results.getData());
	}

	@Test
	public void testAssociateAssetToAccount() {
		List<String> assetGuids = new LinkedList<>();
		assetGuids.add("0_9fb701e0-c470-470b-b5de-fa96567242a5");
		String location = "bangalore";
		String accountName = "gurpreetAct";
		Vertex parentVertex = DaoProvider.orgDao.getOrgByGuid(null, "mndt-org1001").getVertex();
		boolean isDBSync = true;
		Results<Boolean> status = modelCRUDService.associateAssetToAccount(assetGuids, location, accountName,
				parentVertex, isDBSync);
		assertTrue(status.getData());
	}
}
