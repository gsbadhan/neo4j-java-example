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

import org.digi.lg.neo4j.dao.DaoProvider;

public class CacheProvider extends DaoProvider {
	public static final GuidCache guidCache;
	public static final NameCache nameCache;
	public static final PrincipalAppContractCache personAppContractCache;
	public static final AuthorizeCache authorizeCache;
	public static final LicenceAssetCache licenceCache;

	static {
		guidCache = new GuidCache(graphFactory, appDao, principalDao, adminUnitDao, contractDao, contractTypeDao,
				orgDao, classDao, productClassDao);
		nameCache = new NameCache(graphFactory, productClassDao);
		personAppContractCache = new PrincipalAppContractCache(graphFactory, contractDao, classDao);
		authorizeCache = new AuthorizeCache();
		licenceCache = new LicenceAssetCache();

	}
}
