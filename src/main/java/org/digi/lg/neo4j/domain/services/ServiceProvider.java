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

import static org.digi.lg.neo4j.core.GraphInstanceManager.getDataProviderServiceInstance;
import static org.digi.lg.neo4j.dao.DaoUtil.getBooostrapKey;
import static org.digi.lg.neo4j.dao.DaoUtil.getParamMap;
import static org.digi.lg.neo4j.dao.DaoUtil.getStr;
import static org.digi.lg.neo4j.dao.DaoUtil.mandatory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.digi.lg.neo4j.cache.CacheProvider;
import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.DBRowUUID.AdminUnitUID;
import org.digi.lg.neo4j.core.DBRowUUID.ClassUID;
import org.digi.lg.neo4j.core.DBRowUUID.ContractUID;
import org.digi.lg.neo4j.core.DBToJson;
import org.digi.lg.neo4j.core.DataTypes;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Properties;
import org.digi.lg.neo4j.core.Properties.PropNameSuffix;
import org.digi.lg.neo4j.core.Properties.PropTypeValue;
import org.digi.lg.neo4j.core.RegisterRequestType;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.TrxHandler;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.App;
import org.digi.lg.neo4j.pojo.model.Asset;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.DataShard;
import org.digi.lg.neo4j.pojo.model.Org;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.pojo.model.ProductClass;
import org.digi.lg.neo4j.pojo.services.RegisterAgentAsset;
import org.digi.lg.neo4j.pojo.services.Results;
import org.digi.lg.security.utils.KeyGenerator;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;

public class ServiceProvider extends CacheProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProvider.class);

	protected Map<String, Vertex> addPrincipalClasses(Session session, String principalGuid, String adminUnitGuid,
			String contractGuid, String contractTypeGuid, String classPrincipalDataItemGuid,
			String classPrincipalClassGuid, String principalOrgGuid, String classPrincipalOrgGuid,
			String classPrincipalCompanyGuid, String classPrincipalProductsGuid, String classPrincipalEventGuid,
			String classPrincipalBundleGuid, String classPrincipalContactsGuid, String principalId, String accountName,
			String bootStrapKey, Integer isAdminFlag) throws Exception {

		App app = appDao.getAppByContractType(session, contractTypeGuid);
		Org parentOrg = guidCache.getOrg(SchemaConstants.WIPRO_ORG);

		Map<String, Vertex> vertexMap = new TrxHandler<Map<String, Vertex>>(session) {
			@Override
			public Map<String, Vertex> block(Transaction transaction) {
				Map<String, Vertex> vertexMap = new LinkedHashMap<>(10);

				Principal principal = principalDao.save(transaction,
						getParamMap(SchemaConstants.PROP_HDMFID_NAME, principalGuid, SchemaConstants.PROP_NAME,
								accountName, SchemaConstants.PROP_PRINCIPAL_ID, principalId));
				vertexMap.put(principalGuid, principal.getVertex());

				AdminUnit adminUnit = adminUnitDao.save(transaction,
						getParamMap(SchemaConstants.PROP_HDMFID_NAME, adminUnitGuid, SchemaConstants.PROP_NAME,
								accountName, SchemaConstants.PROP_DB_UUID,
								new AdminUnitUID(classPrincipalClassGuid, accountName).get()));
				vertexMap.put(adminUnitGuid, adminUnit.getVertex());

				String contractName = accountName + SchemaConstants.UNDER_SCORE + app.getName();
				Contract contract = contractDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME,
						contractGuid, SchemaConstants.PROP_NAME, contractName, SchemaConstants.PROP_AUTH_TOKEN, "",
						SchemaConstants.PROP_CONTROL, "", SchemaConstants.PROP_START_DATE, System.currentTimeMillis(),
						SchemaConstants.PROP_END_DATE, (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(90l)),
						SchemaConstants.PROP_VIEW, "", SchemaConstants.PROP_DB_UUID,
						new ContractUID(classPrincipalClassGuid, contractName).get()));
				vertexMap.put(contractGuid, contract.getVertex());

				String principalOrgName = new StringBuilder(accountName).append(SchemaConstants._ORG)
						.append(SchemaConstants.UNDER_SCORE).append(principalGuid).toString();
				Org org = orgDao.addUpdate(transaction,
						getParamMap(SchemaConstants.PROP_HDMFID_NAME, principalOrgGuid, SchemaConstants.PROP_NAME,
								principalOrgName, SchemaConstants.PROP_TYPE, SchemaConstants.COMPANY,
								SchemaConstants.PROTECTED, true, SchemaConstants.PROP_BOOT_STRAP_KEY,
								(bootStrapKey != null ? bootStrapKey : getBooostrapKey(principalOrgName))));
				vertexMap.put(principalOrgGuid, org.getVertex());
				return vertexMap;
			}
		}.execute();

		// dynamic classes
		new TrxHandler<Void>(session) {
			@Override
			public Void block(Transaction transaction) {
				Vertex vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._CLASSES, accountName,
						principalId, principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalClassGuid,
						classPrincipalClassGuid, SchemaConstants.LABEL_CLASS, null, PropTypeValue._CLASSES);
				vertexMap.put(classPrincipalClassGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._DATA_ITEMS, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalDataItemGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalDataItemGuid.hashCode()),
						PropTypeValue._DATA_ITEM);
				vertexMap.put(classPrincipalDataItemGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._EVENTS, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalEventGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalEventGuid.hashCode()),
						PropTypeValue._EVENTS);
				vertexMap.put(classPrincipalEventGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._CLASS_ORG, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalOrgGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalOrgGuid.hashCode()),
						PropTypeValue._CLASS_ORG);
				vertexMap.put(classPrincipalOrgGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._COMPANY, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalCompanyGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalCompanyGuid.hashCode()),
						PropTypeValue._COMPANY);
				vertexMap.put(classPrincipalCompanyGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._PRODUCT, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalProductsGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalProductsGuid.hashCode()),
						PropTypeValue._PRODUCT);
				vertexMap.put(classPrincipalProductsGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._CONTACTS, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalContactsGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalContactsGuid.hashCode()),
						PropTypeValue._CONTACTS);
				vertexMap.put(classPrincipalContactsGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._RELEASE_BUNDLE, accountName,
						principalId, principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalBundleGuid,
						classPrincipalClassGuid, SchemaConstants.LABEL_CLASS, null, PropTypeValue._RELEASE_BUNDLE);
				vertexMap.put(classPrincipalBundleGuid, vertex);

				return null;
			}
		}.execute();

		// link classes
		new TrxHandler<Void>(session) {
			@Override
			public Void block(Transaction transaction) {
				orgDao.orgHasOrg(transaction,
						getParamMap(BindConstants.SRC, parentOrg.getGuid(), BindConstants.DEST, principalOrgGuid));
				orgDao.orgBelongsOrg(transaction,
						getParamMap(BindConstants.SRC, principalOrgGuid, BindConstants.DEST, parentOrg.getGuid()));
				orgDao.orgHasContract(transaction,
						getParamMap(BindConstants.SRC, principalOrgGuid, BindConstants.DEST, contractGuid));
				contractTypeDao.contractTypeHasContract(transaction,
						getParamMap(BindConstants.SRC, contractTypeGuid, BindConstants.DEST, contractGuid));
				contractDao.contractHasAdminUnit(transaction,
						getParamMap(BindConstants.SRC, contractGuid, BindConstants.DEST, adminUnitGuid));

				principalDao.principalBelongsAdminUnit(transaction, getParamMap(BindConstants.SRC, principalGuid,
						BindConstants.DEST, adminUnitGuid, BindConstants.IS_ADMIN, isAdminFlag));
				if (app != null) {
					appDao.appHasContractType(transaction,
							getParamMap(BindConstants.SRC, app.getGuid(), BindConstants.DEST, contractTypeGuid));
				}
				// P-AD-C-CT<-has-_classes
				classDao.classHasClass(transaction, vertexMap.get(classPrincipalClassGuid).getLabel(),
						SchemaConstants.LABEL_CONTRACT_TYPE,
						getParamMap(BindConstants.SRC, classPrincipalClassGuid, BindConstants.DEST, contractTypeGuid));
				// P-AD-C-CT<-has-_classes<-is-_data_item
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalDataItemGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalDataItemGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_events
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalEventGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalEventGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_products
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalProductsGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalProductsGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_orgs
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalOrgGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalOrgGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_orgs<-is-_company
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalCompanyGuid).getLabel(),
						vertexMap.get(classPrincipalOrgGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalCompanyGuid, BindConstants.DEST, classPrincipalOrgGuid));
				// P-AD-C-CT<-has-_classes<-is-_releaseBundle
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalBundleGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalBundleGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_contacts
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalContactsGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalContactsGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_orgs<-is-_company<-is-ORG
				classDao.classIsClass(transaction, SchemaConstants.LABEL_ORG,
						vertexMap.get(classPrincipalCompanyGuid).getLabel(), getParamMap(BindConstants.SRC,
								principalOrgGuid, BindConstants.DEST, classPrincipalCompanyGuid));

				globalDataItemDao.globalDataItemIsClass(transaction, vertexMap.get(classPrincipalClassGuid).getLabel(),
						getParamMap(BindConstants.SRC, SchemaConstants.GLOBAL_DATAITEM_NODE_ID, BindConstants.DEST,
								classPrincipalClassGuid));
				classDao.classTemplateGlobalOrg(transaction, vertexMap.get(classPrincipalOrgGuid).getLabel(),
						getParamMap(BindConstants.SRC, classPrincipalOrgGuid, BindConstants.DEST,
								SchemaConstants.GLOBAL_ORG_NODE_ID));
				classDao.classTemplateGlobalClasess(transaction, vertexMap.get(classPrincipalClassGuid).getLabel(),
						getParamMap(BindConstants.SRC, classPrincipalClassGuid, BindConstants.DEST,
								SchemaConstants.GLOBAL_CLASSES_NODE_ID));
				return null;
			}
		}.execute();
		if (vertexMap.isEmpty()) {
			return null;
		}
		return vertexMap;
	}

	protected Map<String, Vertex> addPrincipalClassesToDBSync(Session session, String principalGuid,
			String classPrincipalDataItemGuid, String classPrincipalClassGuid, String principalOrgGuid,
			String classPrincipalOrgGuid, String classPrincipalCompanyGuid, String classPrincipalProductsGuid,
			String classPrincipalEventGuid, String classPrincipalBundleGuid, String classPrincipalContactsGuid,
			String principalId, String accountName, String bootStrapKey) throws Exception {

		Org parentOrg = guidCache.getOrg(SchemaConstants.WIPRO_ORG);
		Map<String, Vertex> vertexMap = new TrxHandler<Map<String, Vertex>>(session) {
			@Override
			public Map<String, Vertex> block(Transaction transaction) {
				Map<String, Vertex> vertexMap = new LinkedHashMap<>(10);

				String principalOrgName = new StringBuilder(accountName).append(SchemaConstants._ORG)
						.append(SchemaConstants.UNDER_SCORE).append(principalOrgGuid).toString();
				Org org = orgDao.addUpdate(transaction,
						getParamMap(SchemaConstants.PROP_HDMFID_NAME, principalOrgGuid, SchemaConstants.PROP_NAME,
								principalOrgName, SchemaConstants.PROP_TYPE, SchemaConstants.COMPANY,
								SchemaConstants.PROP_BOOT_STRAP_KEY,
								(bootStrapKey != null ? bootStrapKey : getBooostrapKey(principalOrgName))));
				vertexMap.put(principalOrgGuid, org.getVertex());
				return vertexMap;
			}
		}.execute();

		// dynamic classes
		new TrxHandler<Void>(session) {
			@Override
			public Void block(Transaction transaction) {

				Vertex vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._CLASSES, accountName,
						principalId, principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalClassGuid,
						classPrincipalClassGuid, SchemaConstants.LABEL_CLASS, null, PropTypeValue._CLASSES);
				vertexMap.put(classPrincipalClassGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._DATA_ITEMS, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalDataItemGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalDataItemGuid.hashCode()),
						PropTypeValue._DATA_ITEM);
				vertexMap.put(classPrincipalDataItemGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._EVENTS, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalEventGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalEventGuid.hashCode()),
						PropTypeValue._EVENTS);
				vertexMap.put(classPrincipalEventGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._CLASS_ORG, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalOrgGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalOrgGuid.hashCode()),
						PropTypeValue._CLASS_ORG);
				vertexMap.put(classPrincipalOrgGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._COMPANY, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalCompanyGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalCompanyGuid.hashCode()),
						PropTypeValue._COMPANY);
				vertexMap.put(classPrincipalCompanyGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._PRODUCT, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalProductsGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalProductsGuid.hashCode()),
						PropTypeValue._PRODUCT);
				vertexMap.put(classPrincipalProductsGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._CONTACTS, accountName, principalId,
						principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalContactsGuid, classPrincipalClassGuid,
						SchemaConstants.LABEL_CLASS, String.valueOf(classPrincipalContactsGuid.hashCode()),
						PropTypeValue._CONTACTS);
				vertexMap.put(classPrincipalContactsGuid, vertex);

				vertex = createDynamicPrincipalClass(transaction, PropNameSuffix._RELEASE_BUNDLE, accountName,
						principalId, principalGuid, SchemaConstants.LABEL_CLASS, classPrincipalBundleGuid,
						classPrincipalClassGuid, SchemaConstants.LABEL_CLASS, null, PropTypeValue._RELEASE_BUNDLE);
				vertexMap.put(classPrincipalBundleGuid, vertex);

				return null;
			}
		}.execute();

		// link classes
		new TrxHandler<Void>(session) {
			@Override
			public Void block(Transaction transaction) {
				if (parentOrg != null) {
					orgDao.orgHasOrg(transaction,
							getParamMap(BindConstants.SRC, parentOrg.getGuid(), BindConstants.DEST, principalOrgGuid));
					orgDao.orgBelongsOrg(transaction,
							getParamMap(BindConstants.SRC, principalOrgGuid, BindConstants.DEST, parentOrg.getGuid()));
				}
				// P-AD-C-CT<-has-_classes<-is-_data_item
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalDataItemGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalDataItemGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_events
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalEventGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalEventGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_products
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalProductsGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalProductsGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_orgs
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalOrgGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalOrgGuid, BindConstants.DEST, classPrincipalClassGuid));
				// P-AD-C-CT<-has-_classes<-is-_orgs<-is-_company
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalCompanyGuid).getLabel(),
						vertexMap.get(classPrincipalOrgGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalCompanyGuid, BindConstants.DEST, classPrincipalOrgGuid));

				// P-AD-C-CT<-has-_classes<-is-_releaseBundle
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalBundleGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalBundleGuid, BindConstants.DEST, classPrincipalClassGuid));

				// P-AD-C-CT<-has-_classes<-is-_contacts
				classDao.classIsClass(transaction, vertexMap.get(classPrincipalContactsGuid).getLabel(),
						vertexMap.get(classPrincipalClassGuid).getLabel(), getParamMap(BindConstants.SRC,
								classPrincipalContactsGuid, BindConstants.DEST, classPrincipalClassGuid));

				// P-AD-C-CT<-has-_classes<-is-_orgs<-is-_company<-is-ORG
				classDao.classIsClass(transaction, SchemaConstants.LABEL_ORG,
						vertexMap.get(classPrincipalCompanyGuid).getLabel(), getParamMap(BindConstants.SRC,
								principalOrgGuid, BindConstants.DEST, classPrincipalCompanyGuid));

				globalDataItemDao.globalDataItemIsClass(transaction, vertexMap.get(classPrincipalClassGuid).getLabel(),
						getParamMap(BindConstants.SRC, SchemaConstants.GLOBAL_DATAITEM_NODE_ID, BindConstants.DEST,
								classPrincipalClassGuid));
				classDao.classTemplateGlobalOrg(transaction, vertexMap.get(classPrincipalOrgGuid).getLabel(),
						getParamMap(BindConstants.SRC, classPrincipalOrgGuid, BindConstants.DEST,
								SchemaConstants.GLOBAL_ORG_NODE_ID));
				classDao.classTemplateGlobalClasess(transaction, vertexMap.get(classPrincipalClassGuid).getLabel(),
						getParamMap(BindConstants.SRC, classPrincipalClassGuid, BindConstants.DEST,
								SchemaConstants.GLOBAL_CLASSES_NODE_ID));
				classDao.classTemplateGlobalProducts(transaction, vertexMap.get(classPrincipalProductsGuid).getLabel(),
						getParamMap(BindConstants.SRC, classPrincipalProductsGuid, BindConstants.DEST,
								SchemaConstants.GLOBAL_PRODUCT_NODE_ID));
				classDao.classTemplateGlobalClasess(transaction, vertexMap.get(classPrincipalDataItemGuid).getLabel(),
						getParamMap(BindConstants.SRC, classPrincipalDataItemGuid, BindConstants.DEST,
								SchemaConstants.GLOBAL_CLASSES_NODE_ID));
				classDao.classTemplateGlobalClasess(transaction, vertexMap.get(classPrincipalEventGuid).getLabel(),
						getParamMap(BindConstants.SRC, classPrincipalEventGuid, BindConstants.DEST,
								SchemaConstants.GLOBAL_CLASSES_NODE_ID));
				return null;
			}
		}.execute();
		if (vertexMap.isEmpty()) {
			return null;
		}
		return vertexMap;
	}

	protected Vertex createDynamicPrincipalClass(Transaction transaction, Properties.PropNameSuffix propNameSuffix,
			String accountName, String principalId, String principalGuid, String classLabel, String guid,
			String domainClassGuid, String category, String authToken, PropTypeValue type) {
		String name = null;
		String suffix = null;
		suffix = propNameSuffix.getValue();
		if (StringUtils.isNotEmpty(accountName)) {
			name = accountName + suffix;
		} else {
			name = principalId + suffix;
		}

		Map<String, Object> params = null;
		if (propNameSuffix == PropNameSuffix._RELEASE_BUNDLE) {
			params = getParamMap(SchemaConstants.PROP_HDMFID_NAME, guid, SchemaConstants.PROP_NAME, name,
					mandatory(SchemaConstants.PROP_KEY_NAME), DataTypes.STRING.getType(),
					mandatory(SchemaConstants.PROP_BUNDLE_LOCATION), DataTypes.STRING.getType(),
					mandatory(SchemaConstants.PROP_RESOURCE_PROVIDER), DataTypes.STRING.getType(),
					mandatory(SchemaConstants.PROP_BUNDLE_ACCESS_KEY), DataTypes.STRING.getType(),
					mandatory(SchemaConstants.PROP_BUNDLE_PRIVATE_KEY), DataTypes.STRING.getType(),
					SchemaConstants.PROTECTED, true);
		} else {
			params = getParamMap(SchemaConstants.PROP_HDMFID_NAME, guid, SchemaConstants.PROP_NAME, name,
					SchemaConstants.PROP_TYPE, (type != null ? type.getValue() : null), SchemaConstants.PROP_CATEGORY,
					category, SchemaConstants.PROP_AUTH_TOKEN, authToken, SchemaConstants.PROTECTED, true);
		}

		if (domainClassGuid != null && classLabel.equals(SchemaConstants.LABEL_CLASS))
			params.put(SchemaConstants.PROP_DB_UUID, new ClassUID(domainClassGuid, name).get());
		if (principalGuid != null && propNameSuffix == PropNameSuffix._CLASSES)
			params.put(SchemaConstants.PROP_PRINCIPAL_GUID, principalGuid);

		return classDao.saveUpdate(transaction, classLabel, params);
	}

	protected void dsAssignment(Session session, String principalOrgGuid, String classPrincipalClassGuid,
			String classPrincipalAssetGuid, String classPrincipalOrgGuid, String classPrincipalCompanyGuid,
			String classPrincipalProductsGuid, String classPrincipalEventGuid, String classPrincipalBundleGuid,
			String classPrincipalContactsGuid, String dsGuid) throws Exception {

		new TrxHandler<Void>(session) {
			@Override
			public Void block(Transaction transaction) {
				classDao.classHasDataShard(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, classPrincipalOrgGuid, BindConstants.DEST, dsGuid));
				dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, classPrincipalOrgGuid));

				classDao.classHasDataShard(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, classPrincipalClassGuid, BindConstants.DEST, dsGuid));
				dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, classPrincipalClassGuid));

				classDao.classHasDataShard(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, classPrincipalProductsGuid, BindConstants.DEST, dsGuid));
				dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, classPrincipalProductsGuid));

				classDao.classHasDataShard(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, classPrincipalAssetGuid, BindConstants.DEST, dsGuid));
				dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, classPrincipalAssetGuid));

				classDao.classHasDataShard(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, classPrincipalEventGuid, BindConstants.DEST, dsGuid));
				dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, classPrincipalEventGuid));

				classDao.classHasDataShard(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, classPrincipalCompanyGuid, BindConstants.DEST, dsGuid));
				dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, classPrincipalCompanyGuid));

				classDao.classHasDataShard(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, classPrincipalBundleGuid, BindConstants.DEST, dsGuid));
				dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, classPrincipalBundleGuid));

				classDao.classHasDataShard(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, classPrincipalContactsGuid, BindConstants.DEST, dsGuid));
				dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_CLASS,
						getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, classPrincipalContactsGuid));

				orgDao.orgHasDataShard(transaction,
						getParamMap(BindConstants.SRC, principalOrgGuid, BindConstants.DEST, dsGuid));
				dataShardDao.dsBelongsOrg(transaction,
						getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, principalOrgGuid));
				return null;
			}
		}.execute();

	}

	protected JsonArray addClassesToJsonArray(Map<String, Vertex> vertexMap) {
		JsonArray jsonArray = new JsonArray();
		vertexMap.keySet().forEach(guid -> {
			Vertex vertex = vertexMap.get(guid);
			String label = vertex.getLabel();
			Map<String, String> dtMap = new HashMap<>(3);
			dtMap.put(DBToJson.get(SchemaConstants.PROP_HDMFID_NAME), guid);
			dtMap.put(DBToJson.get(SchemaConstants.PROP_NAME), getStr(vertex.getNode().get(SchemaConstants.PROP_NAME)));
			dtMap.put(DBToJson.get(SchemaConstants.LABEL), label);
			jsonArray.add(dtMap);
		});
		return jsonArray;
	}

	protected boolean createEdge(Transaction transaction, String direction, String relType, String srcGuid,
			String srcLbl, String destGuid, String destLbl) {
		if (Direction.IN.name().equalsIgnoreCase(direction)) {
			Edge edge = classDao.createEdge(transaction, srcLbl, SchemaConstants.PROP_HDMFID_NAME, srcGuid, destLbl,
					SchemaConstants.PROP_HDMFID_NAME, destGuid, Relationship.stringToEnum(relType), Direction.IN);
			return (edge != null);
		} else if (Direction.OUT.name().equalsIgnoreCase(direction)) {
			Edge edge = classDao.createEdge(transaction, srcLbl, SchemaConstants.PROP_HDMFID_NAME, srcGuid, destLbl,
					SchemaConstants.PROP_HDMFID_NAME, destGuid, Relationship.stringToEnum(relType), Direction.OUT);
			return (edge != null);
		} else if (Direction.BOTH.name().equalsIgnoreCase(direction)) {
			Edge inEdge = classDao.createEdge(transaction, srcLbl, SchemaConstants.PROP_HDMFID_NAME, srcGuid, destLbl,
					SchemaConstants.PROP_HDMFID_NAME, destGuid, Relationship.stringToEnum(relType), Direction.IN);
			Edge outEdge = classDao.createEdge(transaction, srcLbl, SchemaConstants.PROP_HDMFID_NAME, srcGuid, destLbl,
					SchemaConstants.PROP_HDMFID_NAME, destGuid, Relationship.stringToEnum(relType), Direction.OUT);
			return (inEdge != null && outEdge != null);
		}
		return false;
	}

	protected List<String> getVertexTypes(Session session, Vertex vertex) {
		List<String> list = new ArrayList<>();
		StringBuilder typeString = new StringBuilder();
		if (SchemaConstants.LABEL_CLASS.equals(vertex.getNode().get(SchemaConstants.PROP_CATEGORY))
				|| SchemaConstants.LABEL_ASSET.equals(vertex.getLabel())) {
			List<Vertex> parentVertices = classDao.getInVertex(session, vertex.getLabel(),
					SchemaConstants.PROP_HDMFID_NAME, vertex.getGuid(), Relationship.IS, Optional.empty(), false)
					.getInVertices();
			if (parentVertices != null)
				for (Vertex parentVertex : parentVertices) {
					list.add((String) parentVertex.getNode().get(SchemaConstants.PROP_NAME));
					typeString.append((String) parentVertex.getNode().get(SchemaConstants.PROP_NAME)).append(",");
				}
		} else {
			if (vertex.getNode().get(SchemaConstants.PROP_TYPE) != null)
				list.add(getStr(vertex.getNode().get(SchemaConstants.PROP_TYPE)));
		}
		return list;
	}

	protected Map<String, Vertex> associatePricipalToCT(Session session, String principalGuid, String adminUnitGuid,
			String contractGuid, String contractTypeGuid, String classPrincipalClassGuid, String accountName,
			String principalOrgGuid) throws Exception {

		App app = appDao.getAppByContractType(session, contractTypeGuid);
		Map<String, Vertex> vertexMap = new TrxHandler<Map<String, Vertex>>(session) {
			@Override
			public Map<String, Vertex> block(Transaction transaction) {
				Map<String, Vertex> vertexMap = new LinkedHashMap<>(10);
				String contractName = accountName + SchemaConstants.UNDER_SCORE + app.getName();
				Contract contract = contractDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME,
						contractGuid, SchemaConstants.PROP_NAME, contractName, SchemaConstants.PROP_AUTH_TOKEN, "",
						SchemaConstants.PROP_CONTROL, "", SchemaConstants.PROP_START_DATE, System.currentTimeMillis(),
						SchemaConstants.PROP_END_DATE, (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(90l)),
						SchemaConstants.PROP_VIEW, "", SchemaConstants.PROP_DB_UUID,
						new ContractUID(classPrincipalClassGuid, contractName).get()));
				vertexMap.put(contractGuid, contract.getVertex());
				return vertexMap;
			}
		}.execute();

		// link classes
		new TrxHandler<Void>(session) {
			@Override
			public Void block(Transaction transaction) {
				if (app != null) {
					appDao.appHasContractType(transaction,
							getParamMap(BindConstants.SRC, app.getGuid(), BindConstants.DEST, contractTypeGuid));
				}
				orgDao.orgHasContract(transaction,
						getParamMap(BindConstants.SRC, principalOrgGuid, BindConstants.DEST, contractGuid));
				contractTypeDao.contractTypeHasContract(transaction,
						getParamMap(BindConstants.SRC, contractTypeGuid, BindConstants.DEST, contractGuid));
				contractDao.contractHasAdminUnit(transaction,
						getParamMap(BindConstants.SRC, contractGuid, BindConstants.DEST, adminUnitGuid));

				classDao.classHasClass(transaction, SchemaConstants.LABEL_CLASS, SchemaConstants.LABEL_CONTRACT_TYPE,
						getParamMap(BindConstants.SRC, classPrincipalClassGuid, BindConstants.DEST, contractTypeGuid));

				return null;
			}
		}.execute();

		if (vertexMap.isEmpty()) {
			return null;
		}
		return vertexMap;
	}

	protected RegisterAgentAsset registerAgentAssetFacade(Session session, String requestType,
			RegisterAgentAsset message) {
		RegisterAgentAsset response = null;
		RegisterRequestType type = RegisterRequestType.toEnum(requestType);

		if (type == RegisterRequestType.PRE_REGISTERED) {
			response = preRegisteredAsset(session, message);
		}
		return response;
	}

	private RegisterAgentAsset preRegisteredAsset(Session session, RegisterAgentAsset message) {
		List<org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Asset> assetList = new ArrayList<>();

		message.getAssets().forEach(assetInfo -> {
			Org bootStrapOrg = orgDao.getOrgBootStrapKey(session, assetInfo.getBootStrapKey());
			if (bootStrapOrg == null) {
				LOGGER.debug("bootStrapKey not found for Org:{}..!!", assetInfo.getBootStrapKey());
				return;
			}

			org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.ProductClass product = null;
			Asset assetFound = null;
			if (assetInfo.getProductClass() != null && assetInfo.getProductClass().getName() != null) {
				ProductClass productClass = productClassDao.getByName(session, assetInfo.getProductClass().getName());
				if (productClass == null) {
					LOGGER.info("productClass:{} not found..!!", assetInfo.getProductClass().getName());
					return;
				}
				List<Asset> assets = assetDao.getAssetBySerialNumber(session, assetInfo.getProductClass().getName(),
						assetInfo.getSerialNumber());
				if (assets.isEmpty()) {
					LOGGER.info("asset's serialNumber:{} not found for productClass:{}..!!",
							assetInfo.getSerialNumber(), assetInfo.getProductClass().getName());
					return;
				}
				// must have one unique asset under product-class
				assetFound = assets.get(0);
				product = new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.ProductClass(productClass.getName(),
						productClass.getGuid());
			} else {
				List<Asset> assets = assetDao.getAssetByOrg(session, bootStrapOrg.getGuid(),
						assetInfo.getSerialNumber());
				if (assets.isEmpty()) {
					LOGGER.info("asset's serialNumber:{} not found under Org:{}..!!", assetInfo.getSerialNumber(),
							bootStrapOrg.getGuid());
					return;
				}
				// must have one unique asset under org
				assetFound = assets.get(0);
				ProductClass productClass = assetDao.getProductClass(session, assetFound.getGuid(),
						assetFound.getLabel());
				if (productClass != null)
					product = new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.ProductClass(
							productClass.getName(), productClass.getGuid());
			}
			Org directlyConnectedOrg = orgDao.getImmediateOrgOfAsset(session, bootStrapOrg.getGuid(),
					assetFound.getGuid());
			if (directlyConnectedOrg == null) {
				LOGGER.info("immediate Org:{} Of Asset:{} not found ..!!", bootStrapOrg.getGuid(),
						assetFound.getGuid());
				return;
			}

			// all class's name must be unique
			boolean anyNotFoundClass = false;
			List<org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Classes> classes = new ArrayList<>();
			if (assetInfo.getClasses() != null) {
				for (org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Classes classx : assetInfo.getClasses()) {
					ClassX classX = classDao.getByName(session, SchemaConstants.LABEL_CLASS, classx.getName());
					if (classX == null) {
						LOGGER.info("class not found:{}..!!", classx.getName());
						anyNotFoundClass = true;
						break;
					}
					Edge isEdgeExist = assetDao.assetIsClass(session, assetFound.getGuid(), classX.getGuid());
					if (isEdgeExist == null) {
						LOGGER.info("asset:{} and class:{} relationship not found!!", assetFound.getGuid(),
								classx.getName());
						anyNotFoundClass = true;
						break;
					}
					classes.add(new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Classes(classX.getName(),
							classX.getGuid()));
				}
			}

			if (anyNotFoundClass)
				return;

			List<DataShard> dsList = dataShardDao.getDataShard(session, SchemaConstants.LABEL_ORG,
					directlyConnectedOrg.getGuid(), Direction.OUT);
			if (dsList.isEmpty()) {
				LOGGER.info("dataShard info not found from org:{}..!!", directlyConnectedOrg.getGuid());
				return;
			}
			DataShard ds = dsList.get(0);

			org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Asset asset = new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Asset(
					null, assetFound.getSerialNumber(), assetFound.getGuid(), assetFound.getName(), classes, product,
					new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.DataShard(ds.getName(), ds.getGuid(),
							ds.getIP(), ds.getPort(), ds.getMsgGateway()),
					new org.digi.lg.neo4j.pojo.services.RegisterAgentAsset.Org(directlyConnectedOrg.getName(),
							directlyConnectedOrg.getName()));
			try {
				asset.setAccessKey(KeyGenerator.generateKey(asset.getGuid()));
			} catch (Exception e) {
				LOGGER.error("error occured while generating key,error:{}", ExceptionUtils.getMessage(e));
			}
			assetList.add(asset);
		});
		return new RegisterAgentAsset(assetList);
	}

	public Results<Boolean> orgUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_ORG:
						orgDao.deleteOrgHasOrg(transaction, src.getGuid(), dest.getGuid());
						orgDao.deleteOrgBelongsOrg(transaction, dest.getGuid(), src.getGuid());
						break;
					case SchemaConstants.LABEL_ASSET:
						orgDao.deleteOrgHasAsset(transaction, src.getGuid(), dest.getGuid());
						assetDao.deleteAssetBelongsOrg(transaction, dest.getGuid(), src.getGuid());
						break;
					case SchemaConstants.LABEL_CLASS:
						orgDao.deleteOrgIsClass(transaction, src.getGuid(), dest.getGuid());
						break;
					case SchemaConstants.LABEL_CONTRACT:
						orgDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(), dest.getGuid(),
								Relationship.HAS, Direction.OUT);
						orgDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(), src.getGuid(),
								Relationship.BELONGS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();

		} catch (Exception e) {
			LOGGER.error("error occured in orgUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> classUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_CLASS:
						classDao.deleteClassIsClass(transaction, src.getGuid(), dest.getGuid());
						break;
					case SchemaConstants.LABEL_PRODUCT_CLASS:
						productClassDao.deleteClassIsProductClass(transaction, src.getGuid(), dest.getGuid());
						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in classUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> assetUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_ASSET:
						assetDao.deleteAssetHasAsset(transaction, src.getGuid(), dest.getGuid());
						assetDao.deleteAssetBelongsAsset(transaction, dest.getGuid(), src.getGuid());
						break;
					case SchemaConstants.LABEL_CLASS:
						assetDao.deleteAssetIsClass(transaction, src.getGuid(), dest.getGuid());
						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in assetUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> productClassUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_CLASS:
						productClassDao.deleteProductClassIsClass(transaction, src.getGuid(), dest.getGuid());
						break;
					case SchemaConstants.LABEL_PRODUCT_CLASS:
						productClassDao.deleteProductClassIsProductClass(transaction, src.getGuid(), dest.getGuid());
						break;
					case SchemaConstants.LABEL_DATA_ITEM:
						productClassDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						productClassDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					case SchemaConstants.LABEL_EVENTS:
						productClassDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						productClassDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in classUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> orgLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_ORG:
						if (getDataProviderServiceInstance().isOrgExist(src.getLabel(),
								SchemaConstants.PROP_HDMFID_NAME, src.getGuid(), dest.getLabel(),
								SchemaConstants.PROP_NAME, (String) destNode.getName(), Relationship.HAS,
								Direction.OUT)) {
							LOGGER.error("same name org already exists:  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);

						} else {
							orgDao.orgHasOrg(transaction,
									getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
							orgDao.orgBelongsOrg(transaction,
									getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));
						}
						break;
					case SchemaConstants.LABEL_ASSET:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error("same name org already exists:  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						} else {
							orgDao.orgHasAsset(transaction, src.getGuid(), dest.getGuid());
							assetDao.assetBelongsOrg(transaction, dest.getGuid(), src.getGuid());
						}

						break;
					case SchemaConstants.LABEL_CLASS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.IS, Direction.OUT)) {
							LOGGER.error("same name org already exists:  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						} else {
							orgDao.orgIsClass(transaction, src.getGuid(), dest.getGuid());
						}
						break;
					case SchemaConstants.LABEL_CONTRACT:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error("same name  already exists in parent :  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						} else {
							orgDao.createLink(transaction, src.getLabel(), dest.getLabel(), Relationship.HAS,
									getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
							orgDao.createLink(transaction, dest.getLabel(), src.getLabel(), Relationship.BELONGS,
									getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));
						}

						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();

		} catch (Exception e) {
			LOGGER.error("error occured in orgLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> classLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_CLASS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.IS, Direction.OUT)) {
							LOGGER.error("same name class already exists:  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						classDao.classIsClass(transaction, SchemaConstants.LABEL_CLASS, SchemaConstants.LABEL_CLASS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						break;
					case SchemaConstants.LABEL_PRODUCT_CLASS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.IS, Direction.OUT)) {
							LOGGER.error("same name product class already exists:  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						productClassDao.classIsProductClass(transaction, SchemaConstants.LABEL_CLASS,
								SchemaConstants.LABEL_PRODUCT_CLASS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in classLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> assetLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_ASSET:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error("same name asset already exists:  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						} else {
							assetDao.assetHasAsset(transaction, src.getGuid(), dest.getGuid());
							assetDao.assetBelongsAsset(transaction, dest.getGuid(), src.getGuid());
						}

						break;
					case SchemaConstants.LABEL_CLASS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.IS, Direction.OUT)) {
							LOGGER.error("same name class already exists:  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						} else {
							assetDao.assetIsClass(transaction, src.getGuid(), dest.getGuid());
						}
						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();

		} catch (Exception e) {
			LOGGER.error("error occured in assetLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> productClassLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_CLASS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.IS, Direction.OUT)) {
							LOGGER.error("same name class already exists:  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						productClassDao.productClassIsClass(transaction, SchemaConstants.LABEL_PRODUCT_CLASS,
								SchemaConstants.LABEL_CLASS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						break;
					case SchemaConstants.LABEL_PRODUCT_CLASS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.IS, Direction.OUT)) {
							LOGGER.error("same name product class already exists:  sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						productClassDao.productClassIsProductClass(transaction, SchemaConstants.LABEL_PRODUCT_CLASS,
								SchemaConstants.LABEL_PRODUCT_CLASS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						break;
					case SchemaConstants.LABEL_DATA_ITEM:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						productClassDao.createLink(transaction, SchemaConstants.LABEL_PRODUCT_CLASS,
								SchemaConstants.LABEL_DATA_ITEM, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						productClassDao.createLink(transaction, SchemaConstants.LABEL_DATA_ITEM,
								SchemaConstants.LABEL_PRODUCT_CLASS, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));
						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in productClassLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> contractUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_ADMIN_UNIT:
						contractDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						contractDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in contractUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> contractLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {

					case SchemaConstants.LABEL_ADMIN_UNIT:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						contractDao.createLink(transaction, SchemaConstants.LABEL_CONTRACT,
								SchemaConstants.LABEL_ADMIN_UNIT, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						contractDao.createLink(transaction, SchemaConstants.LABEL_ADMIN_UNIT,
								SchemaConstants.LABEL_CONTRACT, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));
						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in contractLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> principalUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_ADMIN_UNIT:
						principalDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.BELONGS, Direction.OUT);

						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in principalUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> principalLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {

					case SchemaConstants.LABEL_ADMIN_UNIT:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.BELONGS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						principalDao.createLink(transaction, SchemaConstants.LABEL_PRINCIPAL,
								SchemaConstants.LABEL_ADMIN_UNIT, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));

						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in principalLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> adminUnitUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_ADMIN_UNIT:
						adminUnitDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.IS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in adminUnitUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> adminUnitLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {

					case SchemaConstants.LABEL_ADMIN_UNIT:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.IS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						adminUnitDao.createLink(transaction, SchemaConstants.LABEL_ADMIN_UNIT,
								SchemaConstants.LABEL_ADMIN_UNIT, Relationship.IS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));

						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in adminUnitLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> scriptUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_PRODUCT_CLASS:
						scriptDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.APPLIES_TO, Direction.OUT);
						break;
					case SchemaConstants.LABEL_SCRIPT_TEMPLATE:
						scriptDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						scriptDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					case SchemaConstants.LABEL_CONTRACT:
						scriptDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						scriptDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					case SchemaConstants.LABEL_SCRIPT:
						scriptDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.IS, Direction.OUT);
						break;
					case SchemaConstants.LABEL_EVENTS:
						scriptDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						break;
					case SchemaConstants.LABEL_DATA_ITEM:
						scriptDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.TRIGGER, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in scriptUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> scriptLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {

					case SchemaConstants.LABEL_PRODUCT_CLASS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.APPLIES_TO, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						scriptDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT,
								SchemaConstants.LABEL_PRODUCT_CLASS, Relationship.APPLIES_TO,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));

						break;
					case SchemaConstants.LABEL_SCRIPT_TEMPLATE:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						scriptDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT,
								SchemaConstants.LABEL_SCRIPT_TEMPLATE, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						scriptDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT_TEMPLATE,
								SchemaConstants.LABEL_SCRIPT, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					case SchemaConstants.LABEL_CONTRACT:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						scriptDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT, SchemaConstants.LABEL_CONTRACT,
								Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						scriptDao.createLink(transaction, SchemaConstants.LABEL_CONTRACT, SchemaConstants.LABEL_SCRIPT,
								Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					case SchemaConstants.LABEL_SCRIPT:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.IS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						scriptDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT, SchemaConstants.LABEL_SCRIPT,
								Relationship.IS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));

						break;
					case SchemaConstants.LABEL_EVENTS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						scriptDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT, SchemaConstants.LABEL_EVENTS,
								Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						scriptDao.createLink(transaction, SchemaConstants.LABEL_EVENTS, SchemaConstants.LABEL_SCRIPT,
								Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					case SchemaConstants.LABEL_DATA_ITEM:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.TRIGGER, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						scriptDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT, SchemaConstants.LABEL_DATA_ITEM,
								Relationship.TRIGGER,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in scriptLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> scriptTemplateUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_PRODUCT_CLASS:
						scriptTemplateDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.APPLIES_TO, Direction.OUT);
						break;
					case SchemaConstants.LABEL_ADMIN_UNIT:
						scriptTemplateDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						scriptTemplateDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					case SchemaConstants.LABEL_CONFIG_ITEM:
						scriptTemplateDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						scriptTemplateDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in scriptTemplateUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> scriptTemplateLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {

					case SchemaConstants.LABEL_PRODUCT_CLASS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.APPLIES_TO, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						scriptTemplateDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT_TEMPLATE,
								SchemaConstants.LABEL_PRODUCT_CLASS, Relationship.APPLIES_TO,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));

						break;

					case SchemaConstants.LABEL_ADMIN_UNIT:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						scriptTemplateDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT_TEMPLATE,
								SchemaConstants.LABEL_ADMIN_UNIT, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						scriptTemplateDao.createLink(transaction, SchemaConstants.LABEL_ADMIN_UNIT,
								SchemaConstants.LABEL_SCRIPT_TEMPLATE, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					case SchemaConstants.LABEL_CONFIG_ITEM:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						scriptTemplateDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT_TEMPLATE,
								SchemaConstants.LABEL_CONFIG_ITEM, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						scriptTemplateDao.createLink(transaction, SchemaConstants.LABEL_CONFIG_ITEM,
								SchemaConstants.LABEL_SCRIPT_TEMPLATE, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;

					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in scriptTemplateLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termDataUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_DATA_ITEM:
						termDataDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						termDataDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termDataUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termEventTypeUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_EVENT_TYPE:
						termEventTypeDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						termEventTypeDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termEventTypeUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termEventUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_EVENTS:
						termEventDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						termEventDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termEventUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termServiceUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_SCRIPT_TEMPLATE:
						termServiceDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						termServiceDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termServiceUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termActionUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_ACTION:
						termActionDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						termActionDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termActionUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termActionTypeUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_NOTIFICATION:
						termActionTypeDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						termActionTypeDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termActionTypeUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termMashupUnlinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_MASHUP:
						termMashupDao.deleteLink(transaction, src.getLabel(), src.getGuid(), dest.getLabel(),
								dest.getGuid(), Relationship.HAS, Direction.OUT);
						termMashupDao.deleteLink(transaction, dest.getLabel(), dest.getGuid(), src.getLabel(),
								src.getGuid(), Relationship.BELONGS, Direction.OUT);
						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termMashupUnlinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termDataLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_DATA_ITEM:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						termDataDao.createLink(transaction, SchemaConstants.LABEL_TERM_DATA,
								SchemaConstants.LABEL_DATA_ITEM, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						termDataDao.createLink(transaction, SchemaConstants.LABEL_DATA_ITEM,
								SchemaConstants.LABEL_TERM_DATA, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in termDataLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termEventTypeLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_EVENT_TYPE:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						termEventTypeDao.createLink(transaction, SchemaConstants.LABEL_TERM_EVENT_TYPE,
								SchemaConstants.LABEL_EVENT_TYPE, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						termEventTypeDao.createLink(transaction, SchemaConstants.LABEL_EVENT_TYPE,
								SchemaConstants.LABEL_TERM_EVENT_TYPE, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termEventTypeLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termEventLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_EVENTS:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						termEventDao.createLink(transaction, SchemaConstants.LABEL_TERM_EVENT,
								SchemaConstants.LABEL_EVENTS, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						termEventDao.createLink(transaction, SchemaConstants.LABEL_EVENTS,
								SchemaConstants.LABEL_TERM_EVENT, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termEventLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termServiceLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_SCRIPT_TEMPLATE:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						termServiceDao.createLink(transaction, SchemaConstants.LABEL_TERM_SERVICE,
								SchemaConstants.LABEL_SCRIPT_TEMPLATE, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						termServiceDao.createLink(transaction, SchemaConstants.LABEL_SCRIPT_TEMPLATE,
								SchemaConstants.LABEL_TERM_SERVICE, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termServiceLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termActionLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_ACTION:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						termActionDao.createLink(transaction, SchemaConstants.LABEL_TERM_ACTION,
								SchemaConstants.LABEL_ACTION, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						termActionDao.createLink(transaction, SchemaConstants.LABEL_ACTION,
								SchemaConstants.LABEL_TERM_ACTION, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termActionLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termActionTypeLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_NOTIFICATION:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						termActionTypeDao.createLink(transaction, SchemaConstants.LABEL_TERM_ACTION_TYPE,
								SchemaConstants.LABEL_NOTIFICATION, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						termActionTypeDao.createLink(transaction, SchemaConstants.LABEL_NOTIFICATION,
								SchemaConstants.LABEL_TERM_ACTION_TYPE, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termActionTypeLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	public Results<Boolean> termMashupLinkHandler(org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {

					Vertex destNode = getDataProviderServiceInstance().getVertex(Optional.of(dest.getLabel()),
							SchemaConstants.PROP_HDMFID_NAME, dest.getGuid());

					switch (dest.getLabel()) {
					case SchemaConstants.LABEL_MASHUP:
						if (getDataProviderServiceInstance().isExist(src.getLabel(), SchemaConstants.PROP_HDMFID_NAME,
								src.getGuid(), dest.getLabel(), SchemaConstants.PROP_NAME, destNode.getName(),
								Relationship.HAS, Direction.OUT)) {
							LOGGER.error(
									"same name  already exists for parent node:   sourceLBL{},destLBL{},name{}..!!",
									src.getLabel(), dest.getLabel(), destNode.getName());
							return new Results<>(false);
						}
						termMashupDao.createLink(transaction, SchemaConstants.LABEL_TERM_MASHUP,
								SchemaConstants.LABEL_MASHUP, Relationship.HAS,
								getParamMap(BindConstants.SRC, src.getGuid(), BindConstants.DEST, dest.getGuid()));
						termMashupDao.createLink(transaction, SchemaConstants.LABEL_MASHUP,
								SchemaConstants.LABEL_TERM_MASHUP, Relationship.BELONGS,
								getParamMap(BindConstants.SRC, dest.getGuid(), BindConstants.DEST, src.getGuid()));

						break;
					default:
						LOGGER.error(
								"destination label not a valid Combination with source:  sourceLBL{},destLBL{}..!!",
								src.getLabel(), dest.getLabel());
						break;
					}
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in termMashupLinkHandler,srcLabel{},destLabel{},,srcGuid:{},destGuid:{},error:{}",
					src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

}
