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

/**
 * Constants must be in camel case only, these are generic constants for JSON
 * request/response.
 *
 */
public class JsonConstants {
	private JsonConstants() {
	}

	public static final String $APP_ID = "$appId";
	public static final String $USER_ID = "$userId";
	public static final String $_GUID = "$_guid";
	public static final String $CONTRACT_ID = "$contractId";
	public static final String $CLASS_ID = "$classId";
	public static final String $CLASS_IDS = "$classIds";
	public static final String $PARENT_ID = "$parentId";
	public static final String $MODEL = "$model";

	public static final String SOURCE_ID = "sourceId";
	public static final String DEST_ID = "destId";

	public static final String CLASS = "class";
	public static final String ORG = "org";
	public static final String PRODUCT_CLASS = "productClass";
	public static final String APP = "app";
	public static final String CONTRACT = "contract";
	public static final String CONTRACT_TYPE = "contractType";
	public static final String ADMIN_UNIT = "adminUnit";
	public static final String PRINCIPAL = "principal";
	public static final String ASSET = "asset";
	public static final String SCRIPT_TEMPLATE = "scriptTemplate";
	public static final String SCRIPT = "script";
	public static final String TERM_DATA = "termData";
	public static final String TERM_EVENT = "termEvent";
	public static final String TERM_EVENT_TYPE = "termEventType";
	public static final String TERM_ACTION = "termAction";
	public static final String TERM_ACTION_TYPE = "termActionType";
	public static final String TERM_SERVICE = "termService";
	public static final String CONTRACT_GROUP = "contactGroup";
	public static final String EVENT_DESCRIPTION = "eventDescription";

	public static final String APP_ID = "appId";
	public static final String USER_ID = "userId";
	public static final String EMAIL_ID = "emailId";
	public static final String NAME = "name";
	public static final String URL = "url";
	public static final String GIT_PWD = "gitPwd";
	public static final String GIT_USER = "gitUser";
	public static final String GIT_LOCATION = "gitLocation";

	public static final String IP = "ip";
	public static final String PORT = "port";
	public static final String CONTRACT_ID = "contractId";
	public static final String CLASS_IDS = "classIds";
	public static final String QUERY = "query";
	public static final String PAGE_OFFSET = "pageOffset";
	public static final String PARENT_ID = "parentId";
	public static final String ID = "id";
	public static final String FILTER_MAP = "filterMap";
	public static final String PAGE_INDEX = "pageIndex";
	public static final String GLOBAL_CONTRACT = "_globalContract";
	public static final String EVENT_HDMF_ID = "event.hdmf.id";
	public static final String IDS = "ids";
	public static final String STATUS = "status";
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public final static String KEY_CACHE_AUTH_PARAM = "authKeyParam";
	public final static String KEY_CACHE_AUTH_TOKEN = "authToken";
	public final static String KEY_CACHE_AUTH_MESSAGE = "authData";
	public final static String HDMF_MESSAGE_GATEWAY_IP = "hdmf.message.gateway.ip";
	public final static String EVENTS = "events";
	public final static String EVENT = "event";
	public final static String AUTH_CACHE = "authCache";
	public final static String SETTINGS = "settings";
	public final static String ADDRESS_SUFFIX = "addressSuffix";
	public final static String CONTRACT_TYPE_VERTEX = "contractTypeVertex";
	public final static String ACCOUNT_NAME = "accountName";
	public final static String REQUEST_START_TIME = "reqStartTime";
	public final static String TYPE = "type";
	public final static String INCOMING_ADDRESS_SUFFX = "incoming.address.suffix";
	public final static String GRAPH_SYNC = "graphSync";
	public final static String PAARENT_ADU_VERTEX = "parentAdminUnitVertex";
	public final static String PAARENT_ORG_VERTEX = "parentOrgVertex";
	public final static String RESULT = "result";
	public final static String LABEL = "label";
	public final static String EMPTY_STRING = "";
	public static final String SHARD_LOCATION_KEY = "shardLocation";
	public static final String PRINCIPAL_GUID = "principalGuid";
	public static final String ADU_GUID = "aduGuid";
	public static final String CONTRACT_GUID = "contractGuid";
	public static final String CLASS_GUID = "classGuid";
	public static final String DATA_ITEM = "dataItem";
	public static final String GUID = "guid";
	public static final String AGG = "agg";
	public static final String ORG_ID = "orgId";
	public static final String ASSET_ID = "assetId";
	public static final String REQ_ORG_ID = "newOrgId";
	public static final String CONTRACT_TYPE_IDS = "contractTypeIds";
	public static final String CONTRACT_IDS = "contractIds";
	public static final String TERM_DATA_GUID = "termDataGuid";
	public static final String TERM_EVENT_GUID = "termEventGuid";
	public static final String TERM_EVENT_TYPE_GUID = "termEventTypeGuid";
	public static final String TERM_ACTION_GUID = "termActionGuid";
	public static final String TERM_ACTION_TYPE_GUID = "termActionTypeGuid";
	public static final String TERM_SERVICE_GUID = "termServiceGuid";
	public static final String SCRIPT_GUID = "scriptGuid";
	public static final String MASHUP_GUID = "mashupGuid";

	public static final String PRINCIPAL_ID = "principalId";
	public static final String MODEL_GUID = "modelGuid";
	public static final String PARENT_CONTRACT_TYPE_GUID = "parentContractTypeGuid";
	public static final String APPLIES_LIST = "appliesList";
	public static final String CONFIG = "config";
	public static final String UPDATE = "update";
	public static final String CT_DERIVED_CLASS_ID = "ctDerivedClassId";
	public static final String ASSET_IDS = "assetIds";
	public static final String LOCATION = "location";
	public static final String ACCESS_KEY = "accessKey";
	public static final String EVENTS_GUID = "eventsGuid";
	public static final String SCRIPT_TEMPLATE_GUID = "scriptTemplateGuid";
	public static final String AUTH_KEY_PARAM = "authKeyParam";
	public static final String AUTH_TOKEN = "authToken";
	public static final String PARENT = "parent";
	public static final String NODES = "nodes";
	public static final String SOURCE_NAME = "sourceName";
	public static final String SOURCE_GUID = "sourceGuid";
	public static final String MSG_GATEWAY = "msgGateway";
	public static final String API_GATEWAY = "apiGateway";
	public static final String SCRIPT_NAME = "scriptName";
	public static final String MASHUP_NAME = "mashupName";
	public static final String MASHUP_PATH = "mashupPath";

	public static final String CONFIG_ITEM_GUID = "configItemGuid";
	public static final String CONFIG_ITEM_ARRAY = "configItemArray";
	public static final String EVENT_GUID = "eventGuid";
	public static final String EVENT_NAME = "eventName";
	public static final String EVENT_TYPE = "eventType";
	public static final String EVENT_TYPE_ARRAY = "eventTypeArray";
	public static final String DATA_ITEM_GUID = "dataItemGuid";
	public static final String DATA_ITEM_ARRAY = "dataItemArray";
	public static final String DS_GUID = "dsGuid";
	public static final String SCOPE = "scope";
	public static final String SERIAL_NUMBER = "serialNumber";
	public static final String PROP_ACTDATE = "activationDate";
	public static final String AVAILABLE_SERVICES = "availableServices";
	public static final String SUBSCRIBED_SERVICES = "subscribedServices";
	public static final String VALUE = "value";
	public static final String DS = "ds";
	public static final String ASSETS = "assets";
	public static final String SCRIPT_TEMPLATE_NAME = "scriptTemplateName";
	public static final String DI = "DI";
	public static final String SCRIPT_INFO = "scriptInfo";
	public static final String SCRIPT_INTANCE = "scriptInstance";
	public static final String BOOT_STRAP_KEY = "bootStrapKey";
	public static final String INPUT = "input";
	public static final String DS_VERTEX = "dsVertex";
	public static final String SCRIPT_TEMP_INFO = "scriptTempInfo";
	public static final String MASHUP_INFO = "mashupInfo";
	public static final String SCRIPTREPO_INFO = "scriptRepo";

	public static final String APPLIES = "applies";
	public static final String NONE = "NONE";
	public static final String ORG_NAME = "orgName";
	public static final String CLASS_LIST = "classList";
	public static final String MODELS = "models";
	public static final String CLASS_ = "class_";
	public static final String ASSOCIATED_APP_ID = "associatedAppId";
	public static final String API_KEY = "apiKey";

	public static final String PRODUCT_NAME = "productName";
	public static final String ASSET_NAME = "assetName";
	public final static String TOKEN = "token";
	public static final String AVERAGE = "avg";
	public static final String BUNDLE_ARRAY = "bundleArray";
	public static final String DATAITEMS = "dataItems";
	public static final String ADMIN_UNIT_ID = "adminUnitId";
	public static final String ACTIVE_STATE = "activeState";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String UNIT_OF_MEASUREMENT = "unitOfMeasurement";
	public static final String MINIMUM_VALUE = "minimumValue";
	public static final String MAXIMUM_VALUE = "maximumValue";
	public static final String FREQUENCY_OF_MEASUREMENT = "frequencyOfMeasurement";
	public static final String LABLE_GUIDS = "labelGuids";
	public static final String DI_IDS = "diIds";
	public static final String EVENTS_IDS = "eventIds";
	public static final String LABEL_GUIDS = "labelGuids";
	public static final String IS_DERIVED = "isderived";
	public static final String DI_TYPE = "diType";
	public static final String ALIAS = "alias";
	public static final String ADMIN_UNIT_GUID = "adminUnitGuid";
	public static final String PATH = "path";
	public static final String MODEL = "model";
	public static final String ALIAS_NAME = "aliasName";
	public static final String ALL = "all";
	public static final String TERM_GUID = "termGuid";
	public static final String PARENT_LABEL = "parentLabel";
	public static final String ACROSS_DOMAIN = "acrossDomain";
	public static final String API_GATEWAY_URL = "apiGatewayUrl";
	public static final String HASH_KEY = "hashKey";
	public static final String ASSET_GUID = "assetGuid";
	public static final String CLASS_LABEL = "classLabel";

	public static final String SOURCE_LABEL = "sourceLabel";
	public static final String DEST_LABEL = "destLabel";
	public static final String DOMAIN_CLASS_GUID = "domainClassGuid";
	public static final String MAX_BUSINESS_RULES = "max_Business_Rules";
	public static final String MAX_DATA_ITEM = "max_Data_Item";
	public static final String MAX_CONCURRENT_ASSET_SESSIONS = "max_Concurrent_Asset_Sessions";
	public static final String MAX_CONCURRENT_API_SESSIONS = "max_Concurrent_API_Sessions";
	public static final String MODIFY_NODE = "modifyNode";
	public static final String MAX_ASSET_COUNT = "max_asset_count";
	public static final String DELETE = "delete";
	public static final String CURRENT_ASSET_COUNT = "current_asset_count";
	public static final String INFLUX_SYNC = "influxSync";

}
