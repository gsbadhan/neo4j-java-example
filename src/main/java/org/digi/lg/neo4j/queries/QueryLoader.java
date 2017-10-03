/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.digi.lg.neo4j.core.GraphContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class QueryLoader {
	private final static Logger LOGGER = LoggerFactory.getLogger(QueryLoader.class);
	private static AppQuery appQuery;
	private static ContractTypeQuery contractTypeQuery;
	private static ContractQuery contractQuery;
	private static AdminUnitQuery adminUnitQuery;
	private static PrincipalQuery principalQuery;
	private static ClassQuery classQuery;
	private static ProductClassQuery productClassQuery;
	private static CommonQuery commonQuery;
	private static OrgQuery orgQuery;
	private static GlobalDataItemQuery globalDataItemQuery;
	private static DataShardQuery dataShardQuery;
	private static AssetQuery assetQuery;
	private static EventsQuery eventQuery;
	private static DataItemQuery dataItemQuery;
	private static ProceduresQuery proceduresQuery;
	private static TermDataQuery termDataQuery;
	private static TermEventQuery termEventQuery;
	private static TermEventTypeQuery termEventTypeQuery;
	private static TermActionQuery termActionQuery;
	private static TermActionTypeQuery termActionTypeQuery;
	private static TermServiceQuery termServiceQuery;
	private static TermMashUpQuery termMashupQuery;

	private static AuthTokenQuery authTokenQuery;
	private static ConfigItemQuery configItemQuery;
	private static EventTypeQuery eventTypeQuery;
	private static ScriptQuery scriptQuery;
	private static ScriptTemplateQuery scriptTemplateQuery;
	private static MashUpQuery mashUpQuery;
	private static ScriptRepoQuery scriptRepoQuery;

	private QueryLoader() {
	}

	public static AppQuery appQuery() {
		if (appQuery == null)
			appQuery = new Gson().fromJson(getJsonFile("query-app.json"), AppQuery.class);
		return appQuery;
	}

	public static ContractTypeQuery contractTypeQuery() {
		if (contractTypeQuery == null)
			contractTypeQuery = new Gson().fromJson(getJsonFile("query-contract-type.json"), ContractTypeQuery.class);
		return contractTypeQuery;
	}

	public static ContractQuery contractQuery() {
		if (contractQuery == null)
			contractQuery = new Gson().fromJson(getJsonFile("query-contract.json"), ContractQuery.class);
		return contractQuery;
	}

	public static AdminUnitQuery adminUnitQuery() {
		if (adminUnitQuery == null)
			adminUnitQuery = new Gson().fromJson(getJsonFile("query-admin-unit.json"), AdminUnitQuery.class);
		return adminUnitQuery;
	}

	public static PrincipalQuery principalQuery() {
		if (principalQuery == null)
			principalQuery = new Gson().fromJson(getJsonFile("query-principal.json"), PrincipalQuery.class);
		return principalQuery;
	}

	public static ClassQuery classQuery() {
		if (classQuery == null)
			classQuery = new Gson().fromJson(getJsonFile("query-class.json"), ClassQuery.class);
		return classQuery;
	}

	public static ProductClassQuery productClassQuery() {
		if (productClassQuery == null)
			productClassQuery = new Gson().fromJson(getJsonFile("query-product-class.json"), ProductClassQuery.class);
		return productClassQuery;
	}

	public static CommonQuery commonQuery() {
		if (commonQuery == null)
			commonQuery = new Gson().fromJson(getJsonFile("query-common.json"), CommonQuery.class);
		return commonQuery;
	}

	public static OrgQuery orgQuery() {
		if (orgQuery == null)
			orgQuery = new Gson().fromJson(getJsonFile("query-org.json"), OrgQuery.class);
		return orgQuery;
	}

	private static Reader getJsonFile(String filename) {
		Reader fileReader = null;
		String filepath = null;
		if (Boolean.valueOf(GraphContext.getConfig().getProperty("load.from.path"))) {
			try {
				filepath = GraphContext.getConfig().getProperty("resource.path") + File.separator + filename;
				fileReader = new FileReader(filepath);
			} catch (FileNotFoundException e) {
				LOGGER.error("error occured in getJsonFile from [resource.path]:{},error:{} ..!!", filepath,
						ExceptionUtils.getMessage(e));
			}
		}
		if (fileReader == null) {
			try {
				LOGGER.debug("loading file:{} from default classpath..", filename);
				fileReader = new InputStreamReader(
						QueryLoader.class.getClassLoader().getSystemResourceAsStream(filename));
				LOGGER.debug("loaded file:{} from default classpath..", filename);
			} catch (Exception e) {
				LOGGER.error("error occured in getJsonFile from [classpath] ..!!", ExceptionUtils.getMessage(e));
			}
		}
		return fileReader;
	}

	public static GlobalDataItemQuery globalDataItemQuery() {
		if (globalDataItemQuery == null)
			globalDataItemQuery = new Gson().fromJson(getJsonFile("query-global-data-item.json"),
					GlobalDataItemQuery.class);
		return globalDataItemQuery;
	}

	public static DataShardQuery dataShardQuery() {
		if (dataShardQuery == null)
			dataShardQuery = new Gson().fromJson(getJsonFile("query-data-shard.json"), DataShardQuery.class);
		return dataShardQuery;
	}

	public static AssetQuery assetQuery() {
		if (assetQuery == null)
			assetQuery = new Gson().fromJson(getJsonFile("query-asset.json"), AssetQuery.class);
		return assetQuery;
	}

	public static EventsQuery eventQuery() {
		if (eventQuery == null)
			eventQuery = new Gson().fromJson(getJsonFile("query-events.json"), EventsQuery.class);
		return eventQuery;
	}

	public static DataItemQuery dataItemQuery() {
		if (dataItemQuery == null)
			dataItemQuery = new Gson().fromJson(getJsonFile("query-dataitem.json"), DataItemQuery.class);
		return dataItemQuery;
	}

	public static ProceduresQuery proceduresQuery() {
		if (proceduresQuery == null)
			proceduresQuery = new Gson().fromJson(getJsonFile("query-procedures.json"), ProceduresQuery.class);
		return proceduresQuery;
	}

	public static TermEventQuery termEventQuery() {
		if (termEventQuery == null)
			termEventQuery = new Gson().fromJson(getJsonFile("query-term-event.json"), TermEventQuery.class);
		return termEventQuery;
	}

	public static TermEventTypeQuery termEventTypeQuery() {
		if (termEventTypeQuery == null)
			termEventTypeQuery = new Gson().fromJson(getJsonFile("query-term-event-type.json"),
					TermEventTypeQuery.class);
		return termEventTypeQuery;
	}

	public static TermActionQuery termActionQuery() {
		if (termActionQuery == null)
			termActionQuery = new Gson().fromJson(getJsonFile("query-term-action.json"), TermActionQuery.class);
		return termActionQuery;
	}

	public static TermActionTypeQuery termActionTypeQuery() {
		if (termActionTypeQuery == null)
			termActionTypeQuery = new Gson().fromJson(getJsonFile("query-term-action-type.json"),
					TermActionTypeQuery.class);
		return termActionTypeQuery;
	}

	public static TermServiceQuery termServiceQuery() {
		if (termServiceQuery == null)
			termServiceQuery = new Gson().fromJson(getJsonFile("query-term-service.json"), TermServiceQuery.class);
		return termServiceQuery;
	}

	public static TermDataQuery termDataQuery() {
		if (termDataQuery == null)
			termDataQuery = new Gson().fromJson(getJsonFile("query-term-data.json"), TermDataQuery.class);
		return termDataQuery;
	}

	public static AuthTokenQuery authTokenQuery() {
		if (authTokenQuery == null)
			authTokenQuery = new Gson().fromJson(getJsonFile("query-auth-token.json"), AuthTokenQuery.class);
		return authTokenQuery;
	}

	public static ConfigItemQuery configItemQuery() {
		if (configItemQuery == null)
			configItemQuery = new Gson().fromJson(getJsonFile("query-config-item.json"), ConfigItemQuery.class);
		return configItemQuery;
	}

	public static EventTypeQuery eventTypeQuery() {
		if (eventTypeQuery == null)
			eventTypeQuery = new Gson().fromJson(getJsonFile("query-event-type.json"), EventTypeQuery.class);
		return eventTypeQuery;
	}

	public static ScriptQuery scriptQuery() {
		if (scriptQuery == null)
			scriptQuery = new Gson().fromJson(getJsonFile("query-script.json"), ScriptQuery.class);
		return scriptQuery;
	}

	public static ScriptTemplateQuery scriptTemplateQuery() {
		if (scriptTemplateQuery == null)
			scriptTemplateQuery = new Gson().fromJson(getJsonFile("query-script-template.json"),
					ScriptTemplateQuery.class);
		return scriptTemplateQuery;
	}

	public static MashUpQuery MashUpQuery() {
		if (mashUpQuery == null)
			mashUpQuery = new Gson().fromJson(getJsonFile("query-mashupscript-template.json"), MashUpQuery.class);
		return mashUpQuery;
	}

	public static ScriptRepoQuery ScriptRepoQuery() {
		if (scriptRepoQuery == null)
			scriptRepoQuery = new Gson().fromJson(getJsonFile("query-script-repo.json"), ScriptRepoQuery.class);
		return scriptRepoQuery;
	}

	public static TermMashUpQuery termMashupQuery() {
		if (termMashupQuery == null)
			termMashupQuery = new Gson().fromJson(getJsonFile("query-term-service.json"), TermMashUpQuery.class);
		return termMashupQuery;
	}
}
