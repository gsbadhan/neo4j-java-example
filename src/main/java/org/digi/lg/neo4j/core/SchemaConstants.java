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

public class SchemaConstants {
	private SchemaConstants() {
	}

	public static final String ID_SERVICE_URL = "http://localhost:9002.lg/id/";

	// sign of "!" is used for mandatory properties in database as prefix
	public static final String MANDATORY_PROP_SYMBOL = "!";

	public static final String SUPER_ORG = "Wipro";

	public static final String LABEL = "label";
	public static final String LABEL_APP = "app";
	public static final String LABEL_CLASS = "class";
	public static final String LABEL_ORG = "org";
	public static final String LABEL_ASSET = "asset";
	public static final String LABEL_CONTRACT_TYPE = "contract_type";
	public static final String LABEL_CONTRACT = "contract";
	public static final String LABEL_PRINCIPAL = "principal";
	public static final String LABEL_ADMIN_UNIT = "admin_unit";
	public static final String LABEL_TERM_DATA = "term_data";
	public static final String LABEL_TERM_EVENT = "term_event";
	public static final String LABEL_TERM_EVENT_TYPE = "term_event_type";
	public static final String LABEL_TERM_ACTION = "term_action";
	public static final String LABEL_TERM_ACTION_TYPE = "term_action_type";
	public static final String LABEL_TERM_SERVICE = "term_service";
	public static final String LABEL_TERM_MASHUP = "term_mashup";
	public static final String LABEL_SCRIPT_REPO = "script_repo";
	public static final String LABEL_ACTION = "action";
	public static final String LABEL_NOTIFICATION = "notification";
	public static final String LABEL_DATA_SHARD = "data_shard";

	public static final String LABEL_AUTH_TOKEN = "auth_token";
	public static final String REL_LABEL_TOKEN = "token";
	public static final String REL_LABEL_IS = "is";
	public static final String REL_LABEL_BELONGS = "belongs";
	public static final String REL_LABEL_HASA = "has";
	public static final String REL_LABEL_CONNECTEDTO = "connectedto";
	public static final String REL_LABEL_COMPATIBLE = "compatible";
	public static final String REL_LABEL_TEMPLATE = "template";
	public static final String REL_LABEL_GENERATES = "generates";
	public static final String REL_LABEL_TRIGGER = "trigger";
	public static final String REL_LABEL_APPLIESTO = "appliesto";
	public static final String REL_LABEL_CONTAINS = "contains";

	public static final String SUPER_PARENT_NODE_ID = "wiprohdmforg04july2016";

	public static final String PROP_AUTH_TOKEN = "auth_token";
	public static final String PROP_BOOT_STRAP_KEY = "boot_strap_key";

	public static final String PROP_API_KEY = "apikey";
	public static final String PROP_HDMFID_NAME = "guid";
	public static final String PROP_USER_NAME = "username";
	public static final String PROP_NAME = "name";
	public static final String PROP_TYPE = "type";
	public static final String PROP_TYPE_LIST = "type_list";
	public static final String PROP_LABEL = "label";
	public static final String PROP_CATEGORY = "category";
	public static final String PROPNAME_APP_ID = "app_id";
	public static final String PROP_START_DATE = "start_date";
	public static final String PROP_END_DATE = "end_date";
	public static final String PROP_SERIAL_NUMBER = "serial_number";
	public static final String PROP_ACTDATE = "activation_date";
	public static final String PROP_REGDATE = "registration_date";

	public static final String PROP_LOCATION = "location";

	public static final String PROP_MANDATORY = "mandatory";
	public static final String PROP_DATATYPE = "datatype";
	public static final String PROP_TAG = "tag";
	public static final String PROP_SCOPE = "scope";
	public static final String PROP_CONTROL = "control";
	public static final String PROP_VIEW = "view";

	public static final String PROP_PRINCIPAL_ID = "principal_id";
	public static final String ADMIN_APP = "WiproAdmin";
	public static final String WARRANTY_APP = "WarrantyApp";

	public static final String COMMAND_NAME_PUBLISH = "Command Initiated";
	public static final String COMMAND_NAME_SUBSCRIBE = "Command Pending";

	public static final String PROP_TIMESTAMP = "timestamp";

	public static final String PROP_PATH = "path";

	public static final String PROP_EVENT_NAME = "eventname";
	public static final String PROP_EVENT_DESC = "description";
	public static final String PROP_EVENT_STATE = "state";
	public static final String PROP_EVENT_PRIORITY = "priority";
	public static final String PROP_GIT_LOCATION = "git_location";
	public static final String PROP_GIT_USER = "git_user";
	public static final String PROP_GIT_PWD = "git_pwd";
	public static final String PROP_GIT_URL = "git_url";

	public static final String LABEL_GLOBAL_ORG_NODE = "global_org";
	public static final String LABEL_GLOBAL_CLASSES_NODE = "global_classes";
	public static final String LABEL_GLOBAL_PRODUCT_NODE = "global_products";
	public static final String LABEL_GLOBAL_ASSET_NODE = "global_asset";
	public static final String LABEL__GLOBAL_DATAITEM_NODE = "global_data_item";

	public static final String GLOBAL_ORG_NODE_ID = "global_org";
	public static final String GLOBAL_CLASSES_NODE_ID = "global_classes";
	public static final String GLOBAL_PRODUCT_NODE_ID = "global_products";
	public static final String GLOBAL_ASSET_NODE_ID = "global_asset";
	public static final String GLOBAL_DATAITEM_NODE_ID = "global_data_item";

	public static final String LABEL_SCRIPT = "script";
	public static final String LABEL_DS = "data_shard";
	public static final String PROP_MSG_GATEWAY = "msg_gateway";
	public static final String PROP_API_GATEWAY = "api_gateway";
	public static final String PROP_PORT = "port";
	public static final String PROP_COUNT = "count";

	public static final String PROP_IP = "ip";
	public static final String ROOT_DS = "DS1";
	public static final String ROOT_SCRIPT_REPO = "SR1";

	public static final String LABEL_DATA_ITEM = "data_item";
	public static final String PROP_UOM = "unit_of_measure";
	public static final String PROP_UOF = "unit_of_frequency";
	public static final String PROP_MAX_VALUE = "max_value";
	public static final String PROP_MIN_VALUE = "min_value";
	public static final String LABEL_EVENTS = "events";

	public static final String _GLOBAL_CONTRACT = "_globalcontract";

	public static final String COMPANY = "company";

	public static final String WIPRO_ORG = "wiproorg";

	public static final String UNDER_SCORE = "_";

	public static final String _ORG = "_org";
	public static final String PROP_TXT_SEARCH = "txt_search";

	public static final String PROPERTY_X = "property_x";

	public static final String COLON = ":";
	public static final String REL = "REL";

	public static final String PROP_ACTIVATE_STATE = "active_state";

	public static final String PROP_IS_ADMIN = "is_admin";
	public static final Integer IS_ADMIN_YES = 1;
	public static final Integer IS_ADMIN_NO = 0;

	public static final String LABEL_PRODUCT_CLASS = "product_class";

	public static final String PROP_ALIAS_NAME = "alias";
	public static final String PROP_AGG = "agg";

	public static final String DI_TYPE = "di_type";

	public static final String PROP_VALIDITY_DATE = "validity_date";

	public static final String LABEL_CONFIG_ITEM = "config_item";

	public static final String LABEL_EVENT_TYPE = "event_type";

	public static final String LABEL_SCRIPT_TEMPLATE = "script_template";

	public static final String PROP_VALUE = "value";

	public static final String REL_LABEL_ISDERIVED = "isderived";

	public static final String PROP_AVG = "avg";

	public static final String PROP_SOURCE = "source";

	public static final String PROP_SOURCE_PATH = "source_path";
	public static final String PROP_TERM_GUID = "term_guid";

	public static final String GUIDS = "guids";

	public static final String PROP_BUNDLE_LOCATION = "bundle_location";
	public static final String PROP_RESOURCE_PROVIDER = "resource_provider";
	public static final String PROP_KEY_NAME = "key_name";
	public static final String PROP_BUNDLE_PRIVATE_KEY = "bundle_private_key";
	public static final String PROP_BUNDLE_ACCESS_KEY = "bundle_access_key";
	public static final String REQUEST = "request";
	public static final String STATUS = "status";

	public static final String LABEL_MASHUP = "mashup";

	public static final String PROP_DB_UUID = "db_uuid";
	public static final String MAX_DATA_ITEM = "max_data_item";
	public static final String MAX_CONCURRENT_ASSET_SESSIONS = "max_concurrent_asset_session";
	public static final String MAX_CONCURRENT_API_SESSIONS = "max_concurrent_api_session";
	public static final String MAX_ASSET_COUNT = "max_asset_count";
	public static final String CURRENT_ASSET_COUNT = "current_asset_count";
	public static final String ID = "id";
	public static final String PROTECTED = "protected";

	public static final String PROP_PRINCIPAL_GUID = "principal_guid";

	public static final String PROP_DOMAIN_CLASS_GUID = "domain_class_guid";
	public static final String DI_AGGR = "DiAggrMap";

}
