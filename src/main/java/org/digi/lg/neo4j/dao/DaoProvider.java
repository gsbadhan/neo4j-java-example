/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.dao;

import org.digi.lg.neo4j.core.GraphContext;
import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.GraphFactoryImpl;
import org.digi.lg.neo4j.core.ReadGraphFactoryImpl;
import org.digi.lg.neo4j.queries.QueryLoader;

/**
 * 
 * This class used by Service layer or API layer
 */
public class DaoProvider {
	protected DaoProvider() {
	}

	protected static final GraphFactory graphFactory;
	protected static final GraphFactory graphReadOnlyFactory;
	protected static final BaseDao baseDao;
	public static final AppDao appDao;
	public static final ContractTypeDao contractTypeDao;
	public static final ContractDao contractDao;
	public static final AdminUnitDao adminUnitDao;
	public static final PrincipalDao principalDao;
	public static final ClassDao classDao;
	public static final ProductClassDao productClassDao;
	public static final CommonDao commonDao;
	public static final OrgDao orgDao;
	public static final GlobalDataItemDao globalDataItemDao;
	public static final DataShardDao dataShardDao;
	public static final AssetDao assetDao;
	public static final EventsDao eventsDao;
	public static final DataItemDao dataItemDao;
	public static final ProcedureDao procedureDao;
	public static final TermDataDao termDataDao;
	public static final TermEventDao termEventDao;
	public static final TermEventTypeDao termEventTypeDao;
	public static final TermActionDao termActionDao;
	public static final TermActionTypeDao termActionTypeDao;
	public static final TermServiceDao termServiceDao;
	public static final TermMashupDao termMashupDao;

	public static final AuthTokenDao authTokenDao;
	public static final ConfigItemDao configItemDao;
	public static final EventTypeDao eventTypeDao;
	public static final ScriptDao scriptDao;
	public static final ScriptTemplateDao scriptTemplateDao;
	public static final MashUpDao mashUpDao;
	public static final ScriptRepoDao scriptRepoDao;

	/*
	 * initialize all daoImpl classes here, so it should load one time in JVM
	 * container.
	 */
	static {
		// read-write graph factory
		graphFactory = new GraphFactoryImpl(GraphContext.getConfig().getProperty("db.url"),
				GraphContext.getConfig().getProperty("db.user"), GraphContext.getConfig().getProperty("db.password"));
		graphFactory.init();

		// read only graph factory
		graphReadOnlyFactory = new ReadGraphFactoryImpl(GraphContext.getConfig().getProperty("db.read.url"),
				GraphContext.getConfig().getProperty("db.read.user"),
				GraphContext.getConfig().getProperty("db.read.password"));
		graphReadOnlyFactory.init();

		baseDao = new BaseDaoImpl(graphFactory);
		commonDao = new CommonDaoImpl(baseDao, QueryLoader.commonQuery());
		appDao = new AppDaoImpl(baseDao, QueryLoader.appQuery());
		contractTypeDao = new ContractTypeDaoImpl(baseDao, commonDao, QueryLoader.contractTypeQuery());
		contractDao = new ContractDaoImpl(baseDao, commonDao, QueryLoader.contractQuery());
		adminUnitDao = new AdminUnitDaoImpl(baseDao, commonDao, QueryLoader.adminUnitQuery());
		principalDao = new PrincipalDaoImpl(baseDao, commonDao, QueryLoader.principalQuery());
		classDao = new ClassDaoImpl(baseDao, commonDao, QueryLoader.classQuery());
		productClassDao = new ProductClassDaoImpl(baseDao, commonDao, QueryLoader.productClassQuery());
		orgDao = new OrgDaoImpl(baseDao, commonDao, QueryLoader.orgQuery());
		dataShardDao = new DataShardDaoImpl(baseDao, QueryLoader.dataShardQuery());
		assetDao = new AssetDaoImpl(baseDao, commonDao, QueryLoader.assetQuery());
		eventsDao = new EventsDaoImpl(baseDao, commonDao, QueryLoader.eventQuery());
		dataItemDao = new DataItemDaoImpl(baseDao, commonDao, QueryLoader.dataItemQuery());

		procedureDao = new ProcedureDaoImpl(graphFactory, graphReadOnlyFactory, baseDao);
		globalDataItemDao = new GlobalDataItemDaoImpl(baseDao, QueryLoader.globalDataItemQuery());
		termDataDao = new TermDataDaoImpl(baseDao, QueryLoader.termDataQuery());
		termEventDao = new TermEventDaoImpl(baseDao, QueryLoader.termEventQuery());
		termEventTypeDao = new TermEventTypeDaoImpl(baseDao, QueryLoader.termEventTypeQuery());
		termActionDao = new TermActionDaoImpl(baseDao, QueryLoader.termActionQuery());
		termActionTypeDao = new TermActionTypeDaoImpl(baseDao, QueryLoader.termActionTypeQuery());
		termServiceDao = new TermServiceDaoImpl(baseDao, QueryLoader.termServiceQuery());
		termMashupDao = new TermMashupDaoImpl(baseDao, QueryLoader.termMashupQuery());

		authTokenDao = new AuthTokenDaoImpl(baseDao, QueryLoader.authTokenQuery());
		configItemDao = new ConfigItemDaoImpl(baseDao, QueryLoader.configItemQuery());
		eventTypeDao = new EventTypeDaoImpl(baseDao, QueryLoader.eventTypeQuery());
		scriptDao = new ScriptDaoImpl(baseDao, QueryLoader.scriptQuery());
		scriptTemplateDao = new ScriptTemplateDaoImpl(baseDao, QueryLoader.scriptTemplateQuery());
		mashUpDao = new MashUpDaoImpl(baseDao, QueryLoader.MashUpQuery());
		scriptRepoDao = new ScriptRepoDaoImpl(baseDao, QueryLoader.ScriptRepoQuery());

	}

	public static GraphFactory getGraphFactory() {
		return graphFactory;
	}

}
