/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.cache;

import static com.google.common.base.Preconditions.checkNotNull;

import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.dao.AdminUnitDao;
import org.digi.lg.neo4j.dao.AppDao;
import org.digi.lg.neo4j.dao.ClassDao;
import org.digi.lg.neo4j.dao.ContractDao;
import org.digi.lg.neo4j.dao.ContractTypeDao;
import org.digi.lg.neo4j.dao.OrgDao;
import org.digi.lg.neo4j.dao.PrincipalDao;
import org.digi.lg.neo4j.dao.ProductClassDao;
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.App;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.pojo.model.Org;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.pojo.model.ProductClass;
import org.neo4j.driver.v1.Session;

public class GuidCache extends AbstractCache<String, Object> {
	private final GraphFactory graphFactory;
	private final AppDao appDao;
	private final PrincipalDao principalDao;
	private final AdminUnitDao adminUnitDao;
	private final ContractDao contractDao;
	private final ContractTypeDao contractTypeDao;
	private final OrgDao orgDao;
	private final ClassDao classDao;
	private final ProductClassDao productClassDao;

	protected GuidCache(final GraphFactory graphFactory, final AppDao appDao, final PrincipalDao principalDao,
			final AdminUnitDao adminUnitDao, final ContractDao contractDao, final ContractTypeDao contractTypeDao,
			final OrgDao orgDao, final ClassDao classDao, final ProductClassDao productClassDao) {
		super(CacheArea.GUID.name());
		this.graphFactory = checkNotNull(graphFactory);
		this.appDao = checkNotNull(appDao);
		this.principalDao = checkNotNull(principalDao);
		this.adminUnitDao = checkNotNull(adminUnitDao);
		this.contractDao = checkNotNull(contractDao);
		this.contractTypeDao = checkNotNull(contractTypeDao);
		this.orgDao = checkNotNull(orgDao);
		this.classDao = checkNotNull(classDao);
		this.productClassDao = checkNotNull(productClassDao);
	}

	public App getApp(String guid) {
		Vertex vertex = (Vertex) super.get(guid);
		if (vertex != null) {
			return new App(vertex);
		}
		Session session = graphFactory.readSession();
		App app = appDao.getAppByGuid(session, guid);
		if (app != null)
			super.put(guid, app.getVertex());
		graphFactory.closeSession(session);
		return app;
	}

	public Principal getPrincipalByGuid(String guid) {
		Vertex vertex = (Vertex) super.get(guid);
		if (vertex != null) {
			return new Principal(vertex);
		}
		Session session = graphFactory.readSession();
		Principal principal = principalDao.getByGuid(session, guid);
		super.put(guid, (principal != null ? principal.getVertex() : null));
		if (principal != null)
			super.put(principal.getPrincipalId(), guid);
		graphFactory.closeSession(session);
		return principal;
	}

	public Principal getPrincipalByPrincipalId(String principalId) {
		String guid = (String) super.get(principalId);
		if (guid != null) {
			return getPrincipalByGuid(guid);
		}
		Session session = graphFactory.readSession();
		Principal principal = principalDao.getByPrincipalId(session, principalId);
		if (principal != null) {
			super.put(principal.getGuid(), principal.getVertex());
			super.put(principalId, principal.getGuid());
		}
		graphFactory.closeSession(session);
		return principal;
	}

	public AdminUnit getAdminUnit(String guid) {
		Vertex vertex = (Vertex) super.get(guid);
		if (vertex != null) {
			return new AdminUnit(vertex);
		}
		Session session = graphFactory.readSession();
		AdminUnit adminUnit = adminUnitDao.getAdminUnitByGuid(session, guid);
		if (adminUnit != null)
			super.put(guid, adminUnit.getVertex());
		graphFactory.closeSession(session);
		return adminUnit;
	}

	public Contract getContract(String guid) {
		Vertex vertex = (Vertex) super.get(guid);
		if (vertex != null) {
			return new Contract(vertex);
		}
		Session session = graphFactory.readSession();
		Contract ct = contractDao.getContract(session, guid);
		if (ct != null)
			super.put(guid, ct.getVertex());
		graphFactory.closeSession(session);
		return ct;
	}

	public ContractType getContractType(String guid) {
		Vertex vertex = (Vertex) super.get(guid);
		if (super.get(guid) != null) {
			return new ContractType(vertex);
		}
		Session session = graphFactory.readSession();
		ContractType contractType = contractTypeDao.getContractTypeByGuid(session, guid);
		if (contractType != null)
			super.put(guid, contractType.getVertex());
		graphFactory.closeSession(session);
		return contractType;
	}

	public Org getOrg(String guid) {
		Vertex vertex = (Vertex) super.get(guid);
		if (vertex != null) {
			return new Org(vertex);
		}
		Session session = graphFactory.readSession();
		Org org = orgDao.getOrgByGuid(session, guid);
		if (org != null)
			super.put(guid, org.getVertex());
		graphFactory.closeSession(session);
		return org;
	}

	public ClassX getClassX(String label, String guid) {
		Vertex vertex = (Vertex) super.get(guid);
		if (vertex != null) {
			return new ClassX(vertex);
		}
		Session session = graphFactory.readSession();
		ClassX classX = null;
		if (label != null) {
			classX = classDao.getByGuid(session, label, guid);
		} else {
			classX = classDao.getByGuid(session, guid);
		}
		if (classX != null)
			super.put(guid, classX.getVertex());
		graphFactory.closeSession(session);
		return classX;
	}

	public ProductClass getProductClass(String guid) {
		Vertex vertex = (Vertex) super.get(guid);
		if (vertex != null) {
			return new ProductClass(vertex);
		}
		Session session = graphFactory.readSession();
		ProductClass prdctClass = null;
		prdctClass = productClassDao.getByGuid(session, guid);
		if (prdctClass != null)
			super.put(guid, prdctClass.getVertex());
		graphFactory.closeSession(session);
		return prdctClass;
	}

}
