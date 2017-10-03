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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.cache.CacheProvider;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuthorizationServiceImplTestIT {

	private AuthorizationService authorizationService;

	@Before
	public void setUp() throws Exception {
		authorizationService = new AuthorizationServiceImpl();
	}

	@After
	public void tearDown() throws Exception {
		authorizationService = null;
	}

	@Test
	public void testIsAuthorized() {
		String userId = "p1@gmail.com";
		String appId = "WiproAdmin";
		ContractVertex contractVertex = CacheProvider.personAppContractCache.get(userId, appId);
		Vertex vertexGuid = CacheProvider.guidCache.getOrg("org-1001").getVertex();
		List<Vertex> vertexs = authorizationService.isAuthorized(userId, appId, contractVertex, vertexGuid, true);
		assertNotNull(vertexs);
	}

	@Test
	public void testAuthorizeOrg() {
		Map<String, Object> data = new HashMap<>(1);
		data.put(SchemaConstants.PROP_HDMFID_NAME, "ctt-1001");
		ContractType contractType = new ContractType(new Vertex(data));
		data = new HashMap<>(1);
		data.put(SchemaConstants.PROP_HDMFID_NAME, "ct-1001");
		Contract contract = new Contract(new Vertex(data));
		data = new HashMap<>(1);
		data.put(SchemaConstants.PROP_HDMFID_NAME, "org-1001");
		Vertex vertex = new Vertex(data);
		List<Vertex> vertexs = authorizationService.authorizeOrg(contractType, contract, vertex, true);
		assertNotNull(vertexs);
	}

	@Test
	public void testAuthorizeClass() {
		Map<String, Object> data = new HashMap<>(1);
		data.put(SchemaConstants.PROP_HDMFID_NAME, "ctt-1001");
		ContractType contractType = new ContractType(new Vertex(data));
		data = new HashMap<>(1);
		data.put(SchemaConstants.PROP_HDMFID_NAME, "clas-1001");
		Vertex vertex = new Vertex(data);
		List<Vertex> vertexs = authorizationService.authorizeClass(contractType, vertex, true);
		assertNotNull(vertexs);
	}

	@Test
	public void testAuthorizeAssets() {
		Map<String, Object> data = new HashMap<>(1);
		data.put(SchemaConstants.PROP_HDMFID_NAME, "ctt-1001");
		ContractType contractType = new ContractType(new Vertex(data));
		data = new HashMap<>(1);
		data.put(SchemaConstants.PROP_HDMFID_NAME, "ct-1001");
		Contract contract = new Contract(new Vertex(data));
		data = new HashMap<>(1);
		data.put(SchemaConstants.PROP_HDMFID_NAME, "ast-1001");
		Vertex vertex = new Vertex(data);
		List<Vertex> vertexs = authorizationService.authorizeAssets(contractType, contract, vertex, true);
		assertNotNull(vertexs);
	}
	
}
