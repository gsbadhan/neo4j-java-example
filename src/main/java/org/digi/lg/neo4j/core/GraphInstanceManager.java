/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.core;

import org.digi.lg.neo4j.domain.services.AuthorizationService;
import org.digi.lg.neo4j.domain.services.AuthorizationServiceImpl;
import org.digi.lg.neo4j.domain.services.DataProviderService;
import org.digi.lg.neo4j.domain.services.DataProviderServiceImpl;
import org.digi.lg.neo4j.domain.services.ModelCRUDService;
import org.digi.lg.neo4j.domain.services.ModelCRUDServiceImpl;

public class GraphInstanceManager {
	private GraphInstanceManager() {
	}

	private static DataProviderService dataProviderService;
	private static AuthorizationService authorizationService;
	private static ModelCRUDService crudService;

	public static void loadInstances() {
		getAuthorizationServiceInstance();
		getDataProviderServiceInstance();
		getModelCRUDServiceInstance();
	}

	public static DataProviderService getDataProviderServiceInstance() {
		authorizationService = getAuthorizationServiceInstance();
		if (dataProviderService == null) {
			dataProviderService = new DataProviderServiceImpl(authorizationService);
		}
		return dataProviderService;
	}

	public static AuthorizationService getAuthorizationServiceInstance() {
		if (authorizationService == null) {
			authorizationService = new AuthorizationServiceImpl();
		}
		return authorizationService;
	}

	public static ModelCRUDService getModelCRUDServiceInstance() {
		dataProviderService = getDataProviderServiceInstance();
		if (crudService == null) {
			crudService = new ModelCRUDServiceImpl(dataProviderService);
		}
		return crudService;
	}
}
