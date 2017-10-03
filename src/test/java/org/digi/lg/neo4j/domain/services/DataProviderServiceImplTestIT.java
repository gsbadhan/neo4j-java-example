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

import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.domain.services.DataProviderService.FindProperty;
import org.digi.lg.neo4j.pojo.services.NeighbourPojo;
import org.digi.lg.neo4j.pojo.services.Node;
import org.digi.lg.neo4j.pojo.services.Results;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DataProviderServiceImplTestIT {

	private AuthorizationService authorizationService;
	private DataProviderService dataProviderService;

	@Before
	public void setUp() throws Exception {
		authorizationService = new AuthorizationServiceImpl();
		dataProviderService = new DataProviderServiceImpl(authorizationService);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testFindNodeByPropertyByGuid() {
		String appId = "WiproAdmin";
		String userId = "gurpreet@gmail.com";
		FindProperty property = FindProperty.GUID;
		String propertyValue = "gu-000-111-444-123";
		Results<List<Node<Object>>> results = dataProviderService.findNodeByProperty(appId, userId, property,
				propertyValue, Optional.of(0));
		assertNotNull(results);
	}

	@Test
	public void testFindNodeByPropertyByName() {
		String appId = "WiproAdmin";
		String userId = "wiprolookingglass@gmail.com";
		FindProperty property = FindProperty.NAME;
		String propertyValue = "l";
		Results<List<Node<Object>>> results = dataProviderService.findNodeByProperty(appId, userId, property,
				propertyValue, Optional.of(0));
		assertNotNull(results);
		results = dataProviderService.findNodeByProperty(appId, userId, property, propertyValue,
				Optional.of(results.getData().size()));
		assertNotNull(results);
	}

	@Test
	public void testFindNodeByPropertyByLabel() {
		String appId = "WiproAdmin";
		String userId = "gurpreet@gmail.com";
		FindProperty property = FindProperty.LABEL;
		String propertyValue = "class_prsn_gu";
		Results<List<Node<Object>>> results = dataProviderService.findNodeByProperty(appId, userId, property,
				propertyValue, Optional.of(0));
		assertNotNull(results);
	}

	@Test
	public void testFindNodeByPropertyByPrincipalId() {
		String appId = "WiproAdmin";
		String userId = "gurpreet@gmail.com";
		FindProperty property = FindProperty.PRINCIPAL_ID;
		String propertyValue = "gurpreet@gmail.com";
		Results<List<Node<Object>>> results = dataProviderService.findNodeByProperty(appId, userId, property,
				propertyValue, Optional.of(0));
		assertNotNull(results);
	}

	@Test
	public void testFindNodeByPropertyByAnyProperty() {
		String appId = "WiproAdmin";
		String userId = "gurpreet@gmail.com";
		FindProperty property = FindProperty.ANY;
		property.setColumn("name");
		String propertyValue = "gu";
		Results<List<Node<Object>>> results = dataProviderService.findNodeByProperty(appId, userId, property,
				propertyValue, Optional.of(0));
		assertNotNull(results);
	}

	@Test
	public void testFindNodeByPropertyBySerialNumber() {
		String appId = "WiproAdmin";
		String userId = "gurpreet@gmail.com";
		FindProperty property = FindProperty.SERIAL_NUMBER;
		String propertyValue = "gu";
		Results<List<Node<Object>>> results = dataProviderService.findNodeByProperty(appId, userId, property,
				propertyValue, Optional.of(0));
		assertNotNull(results);
	}

	@Test
	public void testExpandNode() {
		String appId = "WiproAdmin";
		String userId = "p1@gmail.com";
		Optional<String> guidLabel = Optional.of(SchemaConstants.LABEL_PRINCIPAL);
		String guid = "ad-1001";
		boolean onlyAssets = false;
		Map<String, Object> results = dataProviderService.expandNode(appId, userId, guidLabel, guid, onlyAssets,
				Optional.empty(), false);
		assertNotNull(results);
		assertNotNull(new ResultPojo().setResult(results).toString());
	}

	@Test
	public void testExpandNodeV2() {
		String appId = "WiproAdmin";
		String userId = "p1@gmail.com";
		Optional<String> guidLabel = Optional.of(SchemaConstants.LABEL_ADMIN_UNIT);
		String guid = "ad-1001";
		boolean onlyAssets = false;
		Results<List<NeighbourPojo>> results = dataProviderService.expandNodeV2(appId, userId, guidLabel, guid,
				onlyAssets, Optional.of(0), false);
		assertNotNull(results);
		System.out.println(new ResultPojo().setResult(results).toString());
		results = dataProviderService.expandNodeV2(appId, userId, guidLabel, guid, onlyAssets,
				Optional.of(results.getPageOffset()), false);
		System.out.println(new ResultPojo().setResult(results).toString());
		results = dataProviderService.expandNodeV2(appId, userId, guidLabel, guid, onlyAssets,
				Optional.of(results.getPageOffset()), false);
		System.out.println(new ResultPojo().setResult(results).toString());
	}

	@Test
	public void testGetParent() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		JsonObject jsonObject = dataProviderService.getParent(userId, appId, Optional.empty());
		assertNotNull(jsonObject);
	}

	@Test
	public void testGetClasses() {
		String userId = "wiprolookingglass@gmail.com";
		String appId = "WiproAdmin";
		String guid = "wiprolookingglass@gmail.com1001";
		Optional<String> label = Optional.of("class");
		Node<Object> classes = dataProviderService.getClasses(userId, appId, guid, label, Optional.empty());
		assertNotNull(classes);
	}

	@Test
	public void testGetAssetByOrg() {
		String userId = "lgmanturbo@gmail.com";
		String appId = "WiproAdmin";
		String orgGuid = "mndt-org1001";
		Optional<String> label = null;
		Map<String, Map<String, String>> filters = null;
		Optional<Integer> pageOffset = Optional.of(0);
		Results<List<Node>> data = dataProviderService.getAssetByOrg(userId, appId, orgGuid, label, filters,
				pageOffset);
		assertNotNull(data);
	}

	@Test
	public void testGetDataItem() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		String classGuid = "mdclas-1001";
		Optional<String> label = Optional.of(SchemaConstants.LABEL_PRODUCT_CLASS);
		Map<String, Map<String, String>> filters = null;
		Optional<Integer> pageOffset = Optional.of(0);
		Node<Object> data = dataProviderService.getDataItem(userId, appId, classGuid, label, filters, pageOffset);
		assertNotNull(data);
	}

	@Test
	public void testGetNodeProperties() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		String classGuid = "prnt-1001";
		Optional<String> classLabel = Optional.of(SchemaConstants.LABEL_CLASS);
		Collection<Node<String>> data = dataProviderService.getNodeProperties(userId, appId, classGuid, classLabel);
		assertNotNull(data);
	}

	@Test
	public void testGetDIClasses() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		Map<String, Map<String, String>> filters = null;
		Optional<Integer> pageOffset = Optional.of(0);
		Node<Object> data = dataProviderService.getDIClasses(userId, appId, filters, pageOffset);
		assertNotNull(data);
	}

	@Test
	public void testGetAssets() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		Map<String, Map<String, String>> filters = null;
		Optional<Integer> pageOffset = Optional.of(0);
		String label = SchemaConstants.LABEL_ORG;
		String classGuid = "org-1001";
		JsonArray data = dataProviderService.getAssets(userId, appId, classGuid, Optional.of(label), filters,
				pageOffset);
		assertNotNull(data);
	}

	@Test
	public void testGetBootStrapKey() {
		String productName = "hp probook G410";
		String assetName = "ast-HP-Probook";
		Results<Map<String, String>> results = dataProviderService.getBootStrapKey(productName, assetName);
		assertNotNull(results);
	}

	@Test
	public void testGetScriptTemplate() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		Results<JsonArray> results = dataProviderService.getScriptTemplate(userId, appId);
		assertNotNull(results);
	}

	@Test
	public void testGetAssetbyProduct() {
		String productClassGuid = "prdt_clas-1001";
		Results<JsonArray> results = dataProviderService.getAssetbyProduct(productClassGuid);
		assertNotNull(results);
	}

	@Test
	public void testGetScriptConfigItems() {
		String scriptGuid = "scp-1001";
		Results<JsonObject> results = dataProviderService.getSciptConfigInfo(scriptGuid);
		assertNotNull(results);
	}

	@Test
	public void testFindCompatible() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		List<String> assets = new LinkedList<>();
		assets.add("t1001");
		assets.add("t1002");
		Results<JsonArray> results = dataProviderService.findCompatible(userId, appId, assets);
		assertNotNull(results);
	}

	@Test
	public void testGetScriptDetails() {
		String scriptGuid = "scp-1001";
		Map<String, Object> assetMap = new HashMap<>(1);
		assetMap.put("ast-2001", "asset");
		JsonObject assetsJson = new JsonObject(assetMap);
		Results<JsonArray> results = dataProviderService.getScriptDetails(scriptGuid, assetsJson);
		assertNotNull(results);
	}

	@Test
	public void testEventsClasses() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		Results<JsonObject> results = dataProviderService.getEventsClasses(userId, appId);
		assertNotNull(results);
	}

	@Test
	public void testGetOrgsByUser() {
		String userId = "wiprolookingglass@gmail.com";
		String appId = "WiproAdmin";
		Results<List<Node<Object>>> results = dataProviderService.getOrgsByUser(userId, appId, null, null);
		assertNotNull(results);
	}

	@Test
	public void testGetDataItems() {
		String userId = "wiprolookingglass@gmail.com";
		String appId = "WiproAdmin";
		Results<List<Node<Object>>> results = dataProviderService.getDataItems(userId, appId, null, null);
		assertNotNull(results);
	}

	@Test
	public void testGetMashupInfo() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";
		String mashupath = "/getAssetsInfo";
		Results<String> results = dataProviderService.getMashupInfo(userId, appId, mashupath);
		assertNotNull(results);
	}

	@Test
	public void testFindAssetByProperty() {
		String appId = "WiproAdmin";
		String userId = "wiprolookingglass@gmail.com";
		// test by guid
		FindProperty property = FindProperty.GUID;
		String propertyValue = "001";
		Results<List<Node<Object>>> results = dataProviderService.findAssetByProperty(appId, userId, property,
				propertyValue, Optional.of(0));
		assertNotNull(results);

		int nextPage = results.getData().size();
		results = dataProviderService.findAssetByProperty(appId, userId, property, propertyValue,
				Optional.of(nextPage));
		assertNotNull(results);

		// test by name
		property = FindProperty.NAME;
		propertyValue = "ast";
		results = dataProviderService.findAssetByProperty(appId, userId, property, propertyValue, Optional.of(0));
		assertNotNull(results);

		// test by serial number
		property = FindProperty.SERIAL_NUMBER;
		propertyValue = "sr";
		results = dataProviderService.findAssetByProperty(appId, userId, property, propertyValue, Optional.of(0));
		assertNotNull(results);

	}

	@Test
	public void testGetOrgsByType() {
		String userId = "lgmanturbo@gmail.com";
		String appId = "WiproAdmin";
		String assetGuid = "mndt-ast1001";
		String type = "dealer";
		Results<List<Node<Object>>> results = dataProviderService.getOrgsByType(userId, appId, assetGuid, type);
		assertNotNull(results);
	}

	@Test
	public void testContractTypesForUser() {
		String userId = "lookingglassdemo@gmail.com";
		String appId = "WiproAdmin";
		Results<JsonArray> results = dataProviderService.getContractTypesForUser(userId, appId);
		assertNotNull(results);
	}

	@Test
	public void testHaveChildren() {
		String guid = "clas-1002";
		String srcLabel = "class";
		String destLabel = "class";
		String direction = "IN";
		String edgeLabel = "is";
		Long depth = 1L;
		List<Map<String, Object>> results = dataProviderService.haveChildren(guid, srcLabel, destLabel, direction,
				edgeLabel, depth);
		assertNotNull(results);
	}

	@Test
	public void testGetResponseForUpdateConfigDetails() {
		String scriptGuid = "clas-1002";
		JsonObject assetsJson = new JsonObject();
		assetsJson.put("0_8534763c-ce1e-4d9e-9021-63eee6f24854", "product_class");

		Results<JsonArray> results = dataProviderService.getResponseForUpdateConfigDetails(scriptGuid, assetsJson);
		assertNotNull(results);
	}

}
