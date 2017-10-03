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

public class Properties {

	/**
	 * used as value in name property as suffix
	 *
	 */
	public enum PropNameSuffix {
		_CLASSES("_classes"), _EVENTS("_events"), _DATA_ITEMS("_data_items"), _CLASS_ORG("_orgs"), _COMPANY(
				"_company"), _PRODUCT("_products"), _RELEASE_BUNDLE("_releaseBundle"), _CONTACTS("_contacts");

		private String value;

		private PropNameSuffix(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * used as value for "type" property
	 *
	 */
	public enum PropTypeValue {
		_CLASSES("domain_class"), _DATA_ITEM("data_item"), _EVENTS("events"), _CLASS_ORG("orgs"), _COMPANY(
				"companies"), _PRODUCT("products"), _RELEASE_BUNDLE("bundles"), _CONTACTS("contacts");

		private String value;

		private PropTypeValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * Used as prefix for property i.e. name in All Terms label
	 *
	 */
	public enum TermsPrefix {
		TERM_DATA("trmDt_"), TERM_EVENT("trmEvnt_"), TERM_EVENT_TYPE("trmEvntTyp_"), TERM_ACTION(
				"trmActn_"), TERM_ACTION_TYPE("termActnTyp_"), TERM_SERVICE("trmSrvc_"), TERM_MASHUP("termMshp_");

		private String value;

		private TermsPrefix(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

}
