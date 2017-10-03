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
import static org.digi.lg.neo4j.pojo.services.JsonDaoMapper.getParameterMapFromNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.DBRowUUID.AdminUnitUID;
import org.digi.lg.neo4j.core.DBRowUUID.AssetUID;
import org.digi.lg.neo4j.core.DBRowUUID.ClassUID;
import org.digi.lg.neo4j.core.DBRowUUID.ConfigItemUID;
import org.digi.lg.neo4j.core.DBRowUUID.ContractTypeUID;
import org.digi.lg.neo4j.core.DBRowUUID.ContractUID;
import org.digi.lg.neo4j.core.DBRowUUID.DataItemUID;
import org.digi.lg.neo4j.core.DBRowUUID.EventTypeUID;
import org.digi.lg.neo4j.core.DBRowUUID.EventsUID;
import org.digi.lg.neo4j.core.DBRowUUID.ProductClassUID;
import org.digi.lg.neo4j.core.DBRowUUID.ScriptTemplateUID;
import org.digi.lg.neo4j.core.DBRowUUID.TermActionTypeUID;
import org.digi.lg.neo4j.core.DBRowUUID.TermActionUID;
import org.digi.lg.neo4j.core.DBRowUUID.TermDataUID;
import org.digi.lg.neo4j.core.DBRowUUID.TermEventTypeUID;
import org.digi.lg.neo4j.core.DBRowUUID.TermEventUID;
import org.digi.lg.neo4j.core.DBRowUUID.TermMashupUID;
import org.digi.lg.neo4j.core.DBRowUUID.TermServiceUID;
import org.digi.lg.neo4j.core.DataTypes;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.ErrorCodes;
import org.digi.lg.neo4j.core.JsonConstants;
import org.digi.lg.neo4j.core.JsonToDB;
import org.digi.lg.neo4j.core.Properties.PropNameSuffix;
import org.digi.lg.neo4j.core.Properties.TermsPrefix;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.TrxHandler;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.dao.DaoUtil;
import org.digi.lg.neo4j.exception.DataException;
import org.digi.lg.neo4j.pojo.model.AdminUnit;
import org.digi.lg.neo4j.pojo.model.Asset;
import org.digi.lg.neo4j.pojo.model.AuthToken;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.model.ConfigItem;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.pojo.model.DataItem;
import org.digi.lg.neo4j.pojo.model.DataShard;
import org.digi.lg.neo4j.pojo.model.EventType;
import org.digi.lg.neo4j.pojo.model.Events;
import org.digi.lg.neo4j.pojo.model.Mashup;
import org.digi.lg.neo4j.pojo.model.Org;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.pojo.model.ProductClass;
import org.digi.lg.neo4j.pojo.model.Script;
import org.digi.lg.neo4j.pojo.model.ScriptRepo;
import org.digi.lg.neo4j.pojo.model.ScriptTemplate;
import org.digi.lg.neo4j.pojo.services.ConnectionLinks;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.pojo.services.GraphNode;
import org.digi.lg.neo4j.pojo.services.LinkIdentifier;
import org.digi.lg.neo4j.pojo.services.LinkPojo;
import org.digi.lg.neo4j.pojo.services.LinkSet;
import org.digi.lg.neo4j.pojo.services.Node;
import org.digi.lg.neo4j.pojo.services.OrgNode;
import org.digi.lg.neo4j.pojo.services.RegisterAgentAsset;
import org.digi.lg.neo4j.pojo.services.RegisterAgentMetricAsset;
import org.digi.lg.neo4j.pojo.services.Results;
import org.digi.lg.neo4j.pojo.services.UpdateAgentAsset;
import org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.Classes;
import org.digi.lg.security.utils.KeyGenerator;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.lg.neo4j.perf.PerfConstants;
import com.digi.lg.neo4j.perf.Performance;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ModelCRUDServiceImpl extends ServiceProvider implements ModelCRUDService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelCRUDServiceImpl.class);

	private final DataProviderService dataProviderService;

	public ModelCRUDServiceImpl(final DataProviderService dataProviderService) {
		this.dataProviderService = dataProviderService;
	}

	@Override
	public Map<String, Vertex> addPrincipal(String personGuid, String adminUnitGuid, String contractGuid,
			String contractTypeGuid, String classPersonAssetGuid, String classPersonClassGuid, String personOrgGuid,
			String classPersonOrgGuid, String classPersonCompanyGuid, String classPersonProductsGuid,
			String classPersonEventGuid, String classPrincipalBundleGuid, String classPrincipalContactsGuid,
			String principalId, String accountName, String bootStrapKey, boolean isDataSyncFlag, Integer isAdminFlag) {

		Session session = null;
		StopWatch watch = Performance.startWatch(PerfConstants.ADD_ACCOUNT_DB);
		Map<String, Vertex> vertexMap = null;
		try {
			session = graphFactory.writeSession();
			vertexMap = addPrincipalClasses(session, personGuid, adminUnitGuid, contractGuid, contractTypeGuid,
					classPersonAssetGuid, classPersonClassGuid, personOrgGuid, classPersonOrgGuid,
					classPersonCompanyGuid, classPersonProductsGuid, classPersonEventGuid, classPrincipalBundleGuid,
					classPrincipalContactsGuid, principalId, accountName, bootStrapKey, isAdminFlag);
			if (!isDataSyncFlag) {
				DataShard ds = dataShardDao.incrementCount(session);
				dsAssignment(session, personOrgGuid, classPersonClassGuid, classPersonAssetGuid, classPersonOrgGuid,
						classPersonCompanyGuid, classPersonProductsGuid, classPersonEventGuid, classPrincipalBundleGuid,
						classPrincipalContactsGuid, ds.getGuid());
			}
		} catch (Exception e) {
			LOGGER.error("error occured in addPerson", e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return vertexMap;
	}

	@Override
	public Map<String, Vertex> addPrincipalToDbSync(String principalGuid, String classPersonAssetGuid,
			String classPersonClassGuid, String personOrgGuid, String classPersonOrgGuid, String classPersonCompanyGuid,
			String classPersonProductsGuid, String classPersonEventGuid, String classPrincipalBundleGuid,
			String classPrincipalContactsGuid, String principalId, String accountName, String bootStrapKey) {

		Session session = null;
		StopWatch watch = Performance.startWatch(PerfConstants.ADD_ACCOUNT_DB);
		Map<String, Vertex> vertexMap = null;
		try {
			session = graphFactory.writeSession();
			vertexMap = addPrincipalClassesToDBSync(session, principalGuid, classPersonAssetGuid, classPersonClassGuid,
					personOrgGuid, classPersonOrgGuid, classPersonCompanyGuid, classPersonProductsGuid,
					classPersonEventGuid, classPrincipalBundleGuid, classPrincipalContactsGuid, principalId,
					accountName, bootStrapKey);

		} catch (Exception e) {
			LOGGER.error("error occured in addPerson at DataShard", e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return vertexMap;
	}

	@Override
	public Results<Boolean> addContractType(String contractTypeGuid, ContractTypeUID contractTypeUID,
			Vertex ctDerivedClass, String appGuid, String parentContractTypeGuid, String termDataGuid,
			String termEventGuid, String termEventTypeGuid, String termActionGuid, String termActionTypeGuid,
			String termServiceGuid, String termMashupGuid, Map<String, Object> propertyValueMap, String name,
			String contractGuid) {
		Session session = graphFactory.writeSession();
		Results<Boolean> status = null;
		try {
			status = new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					propertyValueMap.put(SchemaConstants.PROP_DB_UUID, contractTypeUID.get());
					ContractType contractType = contractTypeDao.save(transaction, propertyValueMap);

					// TD
					String tName = TermsPrefix.TERM_DATA.getValue() + name;
					TermDataUID termDataUID = new TermDataUID(contractTypeGuid, tName);
					if (classDao.isNodeConnected(transaction, SchemaConstants.LABEL_CONTRACT_TYPE,
							SchemaConstants.PROP_HDMFID_NAME, contractTypeGuid, SchemaConstants.LABEL_TERM_DATA,
							SchemaConstants.PROP_DB_UUID, termDataUID.get(), Relationship.HAS, Direction.OUT))
						return new Results<>(false, ErrorCodes.ALREADY_EXIST);
					termDataDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME, termDataGuid,
							SchemaConstants.PROP_NAME, tName, SchemaConstants.PROP_DB_UUID, termDataUID.get()));

					// TE
					tName = TermsPrefix.TERM_EVENT.getValue() + name;
					TermEventUID termEventUID = new TermEventUID(contractTypeGuid, tName);
					if (classDao.isNodeConnected(transaction, SchemaConstants.LABEL_CONTRACT_TYPE,
							SchemaConstants.PROP_HDMFID_NAME, contractTypeGuid, SchemaConstants.LABEL_TERM_EVENT,
							SchemaConstants.PROP_DB_UUID, termEventUID.get(), Relationship.HAS, Direction.OUT))
						return new Results<>(false, ErrorCodes.ALREADY_EXIST);
					termEventDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME, termEventGuid,
							SchemaConstants.PROP_NAME, tName, SchemaConstants.PROP_DB_UUID, termEventUID.get()));

					// TET
					tName = TermsPrefix.TERM_EVENT_TYPE.getValue() + name;
					TermEventTypeUID termEventTypeUID = new TermEventTypeUID(contractTypeGuid, tName);
					if (classDao.isNodeConnected(transaction, SchemaConstants.LABEL_CONTRACT_TYPE,
							SchemaConstants.PROP_HDMFID_NAME, contractTypeGuid, SchemaConstants.LABEL_TERM_EVENT_TYPE,
							SchemaConstants.PROP_DB_UUID, termEventTypeUID.get(), Relationship.HAS, Direction.OUT))
						return new Results<>(false, ErrorCodes.ALREADY_EXIST);
					termEventTypeDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME, termEventTypeGuid,
							SchemaConstants.PROP_NAME, tName, SchemaConstants.PROP_DB_UUID, termEventTypeUID.get()));

					// TA
					tName = TermsPrefix.TERM_ACTION.getValue() + name;
					TermActionUID termActionUID = new TermActionUID(contractTypeGuid, tName);
					if (classDao.isNodeConnected(transaction, SchemaConstants.LABEL_CONTRACT_TYPE,
							SchemaConstants.PROP_HDMFID_NAME, contractTypeGuid, SchemaConstants.LABEL_TERM_ACTION,
							SchemaConstants.PROP_DB_UUID, termActionUID.get(), Relationship.HAS, Direction.OUT))
						return new Results<>(false, ErrorCodes.ALREADY_EXIST);
					termActionDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME, termActionGuid,
							SchemaConstants.PROP_NAME, tName, SchemaConstants.PROP_DB_UUID, termActionUID.get()));

					// TAT
					tName = TermsPrefix.TERM_ACTION_TYPE.getValue() + name;
					TermActionTypeUID termActionTypeUID = new TermActionTypeUID(contractTypeGuid, tName);
					if (classDao.isNodeConnected(transaction, SchemaConstants.LABEL_CONTRACT_TYPE,
							SchemaConstants.PROP_HDMFID_NAME, contractTypeGuid, SchemaConstants.LABEL_TERM_ACTION_TYPE,
							SchemaConstants.PROP_DB_UUID, termActionTypeUID.get(), Relationship.HAS, Direction.OUT))
						return new Results<>(false, ErrorCodes.ALREADY_EXIST);
					termActionTypeDao.save(transaction,
							getParamMap(SchemaConstants.PROP_HDMFID_NAME, termActionTypeGuid, SchemaConstants.PROP_NAME,
									tName, SchemaConstants.PROP_DB_UUID, termActionTypeUID.get()));

					// TS
					tName = TermsPrefix.TERM_SERVICE.getValue() + name;
					TermServiceUID termServiceUID = new TermServiceUID(contractTypeGuid, tName);
					if (classDao.isNodeConnected(transaction, SchemaConstants.LABEL_CONTRACT_TYPE,
							SchemaConstants.PROP_HDMFID_NAME, contractTypeGuid, SchemaConstants.LABEL_TERM_SERVICE,
							SchemaConstants.PROP_DB_UUID, termServiceUID.get(), Relationship.HAS, Direction.OUT))
						return new Results<>(false, ErrorCodes.ALREADY_EXIST);
					termServiceDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME, termServiceGuid,
							SchemaConstants.PROP_NAME, tName, SchemaConstants.PROP_DB_UUID, termServiceUID.get()));

					// TMP
					tName = TermsPrefix.TERM_MASHUP.getValue() + name;
					TermMashupUID termMashupUID = new TermMashupUID(contractTypeGuid, tName);
					if (classDao.isNodeConnected(transaction, SchemaConstants.LABEL_CONTRACT_TYPE,
							SchemaConstants.PROP_HDMFID_NAME, contractTypeGuid, SchemaConstants.LABEL_TERM_MASHUP,
							SchemaConstants.PROP_DB_UUID, termMashupUID.get(), Relationship.HAS, Direction.OUT))
						return new Results<>(false, ErrorCodes.ALREADY_EXIST);
					termMashupDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME, termMashupGuid,
							SchemaConstants.PROP_NAME, tName, SchemaConstants.PROP_DB_UUID, termMashupUID.get()));

					contractTypeDao.contractTypeIsDerivedClass(transaction, ctDerivedClass.getLabel(), getParamMap(
							BindConstants.SRC, ctDerivedClass.getGuid(), BindConstants.DEST, contractTypeGuid));

					appDao.appHasContractType(transaction,
							getParamMap(BindConstants.SRC, appGuid, BindConstants.DEST, contractTypeGuid));
					contractTypeDao.contractTypeIsContractType(transaction, getParamMap(BindConstants.SRC,
							contractTypeGuid, BindConstants.DEST, parentContractTypeGuid));

					contractTypeDao.contractTypeHasTermData(transaction,
							getParamMap(BindConstants.SRC, contractTypeGuid, BindConstants.DEST, termDataGuid));
					contractTypeDao.contractTypeHasTermEvent(transaction,
							getParamMap(BindConstants.SRC, contractTypeGuid, BindConstants.DEST, termEventGuid));
					contractTypeDao.contractTypeHasTermEventType(transaction,
							getParamMap(BindConstants.SRC, contractTypeGuid, BindConstants.DEST, termEventTypeGuid));
					contractTypeDao.contractTypeHasTermAction(transaction,
							getParamMap(BindConstants.SRC, contractTypeGuid, BindConstants.DEST, termActionGuid));
					contractTypeDao.contractTypeHasTermActionType(transaction,
							getParamMap(BindConstants.SRC, contractTypeGuid, BindConstants.DEST, termActionTypeGuid));
					contractTypeDao.contractTypeHasTermService(transaction,
							getParamMap(BindConstants.SRC, contractTypeGuid, BindConstants.DEST, termServiceGuid));
					contractTypeDao.contractTypeHasTermMashUP(transaction,
							getParamMap(BindConstants.SRC, contractTypeGuid, BindConstants.DEST, termMashupGuid));

					Org org = contractDao.getOrgByContract(transaction, contractGuid);
					JsonObject dsInfo = dataProviderService.getDataShardInfo(org.getVertex());
					String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID)
							: SchemaConstants.ROOT_DS);
					classDao.classHasDataShard(transaction, SchemaConstants.LABEL_CONTRACT_TYPE,
							getParamMap(BindConstants.SRC, contractType.getGuid(), BindConstants.DEST, dsGuid));
					dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_CONTRACT_TYPE,
							getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, contractType.getGuid()));

					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in error occured in addContractType with guid:{},appid:{},contractid:{},parentContractTypeid:{},termDataguid:{},termEventGuid:{},termEventTypeGuid:{},"
							+ "termActionGuid:{},termActionTypeGuid:{}, termServiceGuid:{},error:{},termMashupGuid:{},error:{}",
					contractTypeGuid, appGuid, contractGuid, parentContractTypeGuid, termDataGuid, termEventGuid,
					termEventTypeGuid, termActionGuid, termActionTypeGuid, termServiceGuid, termMashupGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return status;
	}

	@Override
	public Vertex saveUpdateNode(String label, Map<String, Object> properties) {
		Session session = graphFactory.writeSession();
		try {
			return new TrxHandler<Vertex>(session) {
				@Override
				public Vertex block(Transaction transaction) {
					return classDao.saveUpdate(transaction, label, properties);
				}
			}.execute();

		} catch (Exception e) {
			LOGGER.error("error occured in saveUpdateNode for label:{},params:{},error:{}", label, properties, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return null;
	}

	@Override
	public List<Vertex> addAdminUnit(String principalGuid, String aduGuid, String contractGuid, String name,
			String principalId, ContractVertex contractInfo, String parentAduGuid, String parentOrgGuid) {
		Session session = graphFactory.writeSession();
		List<Vertex> vertexs = null;
		try {
			vertexs = new TrxHandler<List<Vertex>>(session) {
				@Override
				public List<Vertex> block(Transaction transaction) {
					List<Vertex> vertexs = new LinkedList<>();
					Map<String, Object> paramMap = null;

					paramMap = getParamMap(SchemaConstants.PROP_HDMFID_NAME, principalGuid,
							SchemaConstants.PROP_PRINCIPAL_ID, principalId, SchemaConstants.PROP_NAME, name);
					vertexs.add(principalDao.save(transaction, paramMap).getVertex());

					paramMap = getParamMap(SchemaConstants.PROP_HDMFID_NAME, aduGuid, SchemaConstants.PROP_NAME, name,
							SchemaConstants.PROP_DB_UUID,
							new AdminUnitUID(contractInfo.getDomainClass().getGuid(), name).get());
					vertexs.add(adminUnitDao.save(transaction, paramMap).getVertex());

					String contractName = name + SchemaConstants.UNDER_SCORE + contractInfo.getApp().getName();
					paramMap = getParamMap(SchemaConstants.PROP_HDMFID_NAME, contractGuid, SchemaConstants.PROP_NAME,
							contractName, SchemaConstants.PROP_START_DATE, System.currentTimeMillis(),
							SchemaConstants.PROP_END_DATE, (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(90l)),
							SchemaConstants.PROP_DB_UUID,
							new ContractUID(contractInfo.getDomainClass().getGuid(), contractName).get());
					vertexs.add(contractDao.save(transaction, paramMap).getVertex());

					paramMap = getParamMap(BindConstants.SRC, principalGuid, BindConstants.DEST, aduGuid,
							BindConstants.IS_ADMIN, SchemaConstants.IS_ADMIN_YES);
					principalDao.principalBelongsAdminUnit(transaction, paramMap);

					paramMap = getParamMap(BindConstants.SRC, contractGuid, BindConstants.DEST, aduGuid);
					contractDao.contractHasAdminUnit(transaction, paramMap);

					paramMap = getParamMap(BindConstants.SRC, contractInfo.getContractType().getGuid(),
							BindConstants.DEST, contractGuid);
					contractTypeDao.contractTypeHasContract(transaction, paramMap);

					paramMap = getParamMap(BindConstants.SRC, aduGuid, BindConstants.DEST, parentAduGuid);
					adminUnitDao.adminUnitIsAdminUnit(transaction, paramMap);

					paramMap = getParamMap(BindConstants.SRC, parentOrgGuid, BindConstants.DEST, contractGuid);
					orgDao.orgHasContract(transaction, paramMap);
					return vertexs;
				}
			}.execute();

		} catch (Exception e) {
			LOGGER.error("error occured in addAdminUnit principalGuid{},error:{}", principalGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return vertexs;
	}

	@Override
	public Vertex addNode(String newGuid, OrgNode orgNode, List<Vertex> relationVertices, Vertex belongsVertx,
			String label, boolean DbSyncFlag) {
		Session session = graphFactory.writeSession();

		try {

			// type tag is overridden with name of the class
			if (!relationVertices.isEmpty()) {
				for (Vertex v : relationVertices) {
					orgNode.setType(getStr(v.getNode().get(SchemaConstants.PROP_NAME)));
				}
			}

			Map<String, Object> newVertexParams = getParameterMapFromNode(orgNode);
			newVertexParams.put(SchemaConstants.PROP_HDMFID_NAME, newGuid);
			if (label.equalsIgnoreCase(SchemaConstants.LABEL_ORG) && orgNode.getBootStrapKey() == null) {
				newVertexParams.put(SchemaConstants.PROP_BOOT_STRAP_KEY, getBooostrapKey(orgNode.getName().toString()));
			}

			Vertex newVertex = new TrxHandler<Vertex>(session) {
				@Override
				public Vertex block(Transaction transaction) {
					return classDao.saveUpdate(transaction, label, newVertexParams);
				}
			}.execute();
			if (newVertex == null) {
				LOGGER.info("not able to save newNode newGuid:{},orgnode:{} ..!!", newGuid, orgNode);
				return null;
			}

			boolean relVertxLinkToNewVertx = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					relationVertices.forEach(
							relVert -> classDao.classIsClass(transaction, label, relVert.getLabel(), getParamMap(
									BindConstants.SRC, newVertex.getGuid(), BindConstants.DEST, relVert.getGuid())));
					return true;
				}
			}.execute();
			if (!relVertxLinkToNewVertx) {
				LOGGER.info("not able to create relations b/w newGuid:{},relationVertices:{} ..!!", newGuid,
						relationVertices);
				return null;
			}
			JsonObject dsInfo = dataProviderService.getDataShardInfo(belongsVertx);
			String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID) : SchemaConstants.ROOT_DS);
			boolean saveStatus = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					Edge classHasDs = null;
					Edge dsBelongsClass = null;
					if (!DbSyncFlag) {

						classHasDs = dataShardDao.classHasDs(transaction, newVertex.getLabel(),
								getParamMap(BindConstants.SRC, newVertex.getGuid(), BindConstants.DEST, dsGuid));
						dsBelongsClass = dataShardDao.dsBelongsClass(transaction, newVertex.getLabel(),
								getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, newVertex.getGuid()));
						//
					}
					Edge classBelongsClass = classDao.classBelongsClass(transaction, newVertex.getLabel(),
							belongsVertx.getLabel(), getParamMap(BindConstants.SRC, newVertex.getGuid(),
									BindConstants.DEST, belongsVertx.getGuid()));
					Edge classHasClass = classDao.classHasClass(transaction, belongsVertx.getLabel(),
							newVertex.getLabel(), getParamMap(BindConstants.SRC, belongsVertx.getGuid(),
									BindConstants.DEST, newVertex.getGuid()));
					if (DbSyncFlag)
						return (classBelongsClass != null && classHasClass != null);
					return (classHasDs != null && dsBelongsClass != null && classBelongsClass != null
							&& classHasClass != null);
				}
			}.execute();
			if (!saveStatus) {
				LOGGER.info("not able to create links completely newVertex:{},dsGuid:{},belongsVertx:{}..!!",
						newVertex.getGuid(), dsGuid, belongsVertx.getGuid());
				return null;
			}

			return newVertex;
		} catch (

		Exception e) {
			LOGGER.error("error occured in addNode newGuid{},error:{}", newGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return null;
	}

	@Override
	public Vertex addClassNode(String classNewGuid, ClassUID classUID, Node<Object> classNode, Vertex relationVertex,
			boolean model, boolean isDataSync) {
		Session session = null;
		try {
			session = graphFactory.writeSession();
			Map<String, Object> newVertexParams = getParameterMapFromNode(classNode);
			newVertexParams.put(SchemaConstants.PROP_CATEGORY, SchemaConstants.LABEL_CLASS);
			newVertexParams.put(SchemaConstants.PROP_DB_UUID, classUID.get());
			Vertex newVertex = new TrxHandler<Vertex>(session, SchemaConstants.LABEL_CLASS) {
				@Override
				public Vertex block(Transaction transaction) {
					return classDao.save(transaction, this.getOptionalArgs()[0].toString(), newVertexParams);
				}
			}.execute();

			if (newVertex == null) {
				LOGGER.info("not able to create class:{},existingClass{}", classNewGuid, classNode);
				return null;
			}

			JsonObject dsInfo = dataProviderService.getDataShardInfo(relationVertex);

			boolean saveStatus = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					Edge edge = null;
					if (relationVertex != null && !isDataSync) {
						edge = classDao.classIsClass(transaction, newVertex.getLabel(), relationVertex.getLabel(),
								getParamMap(BindConstants.SRC, newVertex.getGuid(), BindConstants.DEST,
										relationVertex.getGuid()));
					}
					if (!isDataSync) {
						String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID)
								: SchemaConstants.ROOT_DS);
						Edge classHasDs = dataShardDao.classHasDs(transaction, newVertex.getLabel(),
								getParamMap(BindConstants.SRC, newVertex.getGuid(), BindConstants.DEST, dsGuid));
						Edge dsBelongsClass = dataShardDao.dsBelongsClass(transaction, newVertex.getLabel(),
								getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, newVertex.getGuid()));
						return (classHasDs != null && dsBelongsClass != null);
					}
					if (relationVertex != null && isDataSync
							&& !relationVertex.getLabel().equals(SchemaConstants.LABEL_CONTRACT_TYPE)) {
						edge = classDao.classIsClass(transaction, newVertex.getLabel(), relationVertex.getLabel(),
								getParamMap(BindConstants.SRC, newVertex.getGuid(), BindConstants.DEST,
										relationVertex.getGuid()));
					}

					return (edge != null || (relationVertex != null && isDataSync
							&& relationVertex.getLabel().equals(SchemaConstants.LABEL_CONTRACT_TYPE)));

				}
			}.execute();

			return (saveStatus ? newVertex : null);
		} catch (Exception e) {
			LOGGER.error("error in addClassNode classNewGuid:{}", classNewGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return null;
	}

	@Override
	public DataItem addDataItem(String newDataItemGuid, DataItemUID dataItemUID, GraphNode orgNode,
			List<Vertex> classRelationVertices, Vertex belongs, String type, boolean isDataSync) {
		Session session = graphFactory.writeSession();
		try {
			if (!(orgNode.get(JsonConstants.DI_TYPE).equals(DataTypes.STRING.getType())
					|| orgNode.get(JsonConstants.DI_TYPE).equals(DataTypes.INT.getType())
					|| orgNode.get(JsonConstants.DI_TYPE).equals(DataTypes.DOUBLE.getType())
					|| orgNode.get(JsonConstants.DI_TYPE).equals(DataTypes.BOOLEAN.getType()))) {
				LOGGER.info("ditype field not matched for guid:{},orgnode:{}..!!", newDataItemGuid, orgNode);
				return null;
			}

			DataItem newDataItem = new TrxHandler<DataItem>(session) {
				@Override
				public DataItem block(Transaction transaction) {
					Map<String, Object> params = getParamMap(SchemaConstants.PROP_NAME, orgNode.get(JsonConstants.NAME),
							SchemaConstants.PROP_HDMFID_NAME, newDataItemGuid, SchemaConstants.PROP_UOM,
							orgNode.get(JsonConstants.UNIT_OF_MEASUREMENT), SchemaConstants.PROP_UOF,
							orgNode.get(JsonConstants.FREQUENCY_OF_MEASUREMENT), SchemaConstants.PROP_MAX_VALUE,
							orgNode.get(JsonConstants.MAXIMUM_VALUE), SchemaConstants.PROP_MIN_VALUE,
							orgNode.get(JsonConstants.MINIMUM_VALUE), SchemaConstants.PROP_TYPE,
							orgNode.get(JsonConstants.DI_TYPE), SchemaConstants.PROP_ALIAS_NAME,
							orgNode.get(JsonConstants.ALIAS));
					params.put(SchemaConstants.PROP_DB_UUID, dataItemUID.get());
					return dataItemDao.save(transaction, params);
				}
			}.execute();

			if (newDataItem == null) {
				LOGGER.info("not able to create DataItem newDataItemGuid:{}..!!", newDataItemGuid);
				return null;
			}

			// create edges
			new TrxHandler<Void>(session) {
				@Override
				public Void block(Transaction transaction) {
					classRelationVertices.forEach(v -> {
						classDao.classHasClass(transaction, v.getLabel(), newDataItem.getLabel(),
								getParamMap(BindConstants.SRC, v.getGuid(), BindConstants.DEST, newDataItemGuid));
						if (!isDataSync) {
							JsonObject dsInfo = dataProviderService.getDataShardInfo(v);
							String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID)
									: SchemaConstants.ROOT_DS);
							dataShardDao.classHasDs(transaction, newDataItem.getLabel(),
									getParamMap(BindConstants.SRC, newDataItemGuid, BindConstants.DEST, dsGuid));
							dataShardDao.dsBelongsClass(transaction, newDataItem.getLabel(),
									getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, newDataItemGuid));

						}
					});
					if (belongs != null)
						classDao.classHasClass(transaction, belongs.getLabel(), newDataItem.getLabel(),
								getParamMap(BindConstants.SRC, belongs.getGuid(), BindConstants.DEST, newDataItemGuid));

					if (type != null) {
						ClassX classx = guidCache.getClassX(null, type);
						if (classx != null)
							classDao.classIsClass(transaction, newDataItem.getLabel(), classx.getVertex().getLabel(),
									getParamMap(BindConstants.SRC, newDataItemGuid, BindConstants.DEST,
											classx.getVertex().getGuid()));
					}

					return null;
				}
			}.execute();

			return newDataItem;
		} catch (Exception e) {
			LOGGER.error("eror occured in addDataItem addDataItem:{},type:{},error:{}", newDataItemGuid, type, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return null;
	}

	@Override
	public Events addEvent(GraphNode orgNode, EventsUID EventsUID, List<Vertex> classRelationVertices, Vertex belongs,
			String type, boolean isDataSync) {
		Session session = graphFactory.writeSession();
		String newEventGuid = orgNode.getGuid().toString();
		try {
			Events newEvent = new TrxHandler<Events>(session) {
				@Override
				public Events block(Transaction transaction) {
					Map<String, Object> params = getParameterMapFromNode(orgNode);
					params.put(SchemaConstants.PROP_DB_UUID, EventsUID.get());
					return eventsDao.save(transaction, params);
				}
			}.execute();

			if (newEvent == null) {
				LOGGER.info("failed to create eventVertex newEventGuid:{}..!!", newEventGuid);
				return null;
			}

			// create edges
			new TrxHandler<Void>(session) {
				@Override
				public Void block(Transaction transaction) {
					classRelationVertices.forEach(v -> {
						classDao.classHasClass(transaction, v.getLabel(), newEvent.getLabel(),
								getParamMap(BindConstants.SRC, v.getGuid(), BindConstants.DEST, newEventGuid));
						JsonObject dsInfo = dataProviderService.getDataShardInfo(v);
						if (!isDataSync) {
							String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID)
									: SchemaConstants.ROOT_DS);
							dataShardDao.classHasDs(transaction, newEvent.getLabel(),
									getParamMap(BindConstants.SRC, newEventGuid, BindConstants.DEST, dsGuid));
							dataShardDao.dsBelongsClass(transaction, newEvent.getLabel(),
									getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, newEventGuid));
						}
					});
					if (belongs != null)
						classDao.classHasClass(transaction, belongs.getLabel(), newEvent.getLabel(),
								getParamMap(BindConstants.SRC, belongs.getGuid(), BindConstants.DEST, newEventGuid));

					if (type != null) {
						ClassX classx = guidCache.getClassX(null, type);
						if (classx != null)
							classDao.classIsClass(transaction, newEvent.getLabel(), classx.getVertex().getLabel(),
									getParamMap(BindConstants.SRC, newEventGuid, BindConstants.DEST,
											classx.getVertex().getGuid()));
					}

					return null;
				}
			}.execute();
			return newEvent;
		} catch (Exception e) {
			LOGGER.error("eror occured in addEvent newEventGuid:{},type:{},error:{}", newEventGuid, type, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return null;
	}

	@Override
	public Vertex addProductClassNode(String classNewGuid, ProductClassUID productClassUID, Node<Object> classNode,
			Vertex relationVertex, boolean isDataSync) {
		Session session = graphFactory.writeSession();
		try {
			Map<String, Object> params = getParameterMapFromNode(classNode);
			params.put(SchemaConstants.PROP_DB_UUID, productClassUID.get());

			Vertex newVertex = new TrxHandler<Vertex>(session) {
				@Override
				public Vertex block(Transaction transaction) {
					return productClassDao.save(transaction, params);
				}
			}.execute();

			if (newVertex == null) {
				LOGGER.info("not able to create productClass:{},existingClass{} ..!!", classNewGuid, classNode);
				return null;
			}

			JsonObject dsInfo = dataProviderService.getDataShardInfo(relationVertex);
			boolean saveStatus = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					Edge edge = null;
					if (relationVertex != null && isDataSync) {
						edge = classDao.classIsClass(transaction, newVertex.getLabel(), relationVertex.getLabel(),
								getParamMap(BindConstants.SRC, newVertex.getGuid(), BindConstants.DEST,
										relationVertex.getGuid()));
					}
					if (isDataSync) {
						String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID)
								: SchemaConstants.ROOT_DS);
						Edge classHasDs = dataShardDao.classHasDs(transaction, newVertex.getLabel(),
								getParamMap(BindConstants.SRC, newVertex.getGuid(), BindConstants.DEST, dsGuid));
						Edge dsBelongsClass = dataShardDao.dsBelongsClass(transaction, newVertex.getLabel(),
								getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, newVertex.getGuid()));
						return (classHasDs != null && dsBelongsClass != null);
					}
					if (relationVertex != null && !isDataSync
							&& !relationVertex.getLabel().equals(SchemaConstants.LABEL_CONTRACT_TYPE)) {
						edge = classDao.classIsClass(transaction, newVertex.getLabel(), relationVertex.getLabel(),
								getParamMap(BindConstants.SRC, newVertex.getGuid(), BindConstants.DEST,
										relationVertex.getGuid()));
					}
					return (edge != null || (relationVertex != null && !isDataSync
							&& relationVertex.getLabel().equals(SchemaConstants.LABEL_CONTRACT_TYPE)));
				}
			}.execute();
			return (saveStatus ? newVertex : null);
		} catch (Exception e) {
			LOGGER.error("error in addProductClassNode classNewGuid:{} ..!!", classNewGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return null;
	}

	@Override
	public Vertex addOrgNode(String newGuid, OrgNode orgNode, List<Vertex> relationVertices, Vertex belongsVertx,
			String label, boolean dbSyncFlag) {
		return addNode(newGuid, orgNode, relationVertices, belongsVertx, label, dbSyncFlag);
	}

	@Override
	public Vertex addAssetNode(String newGuid, AssetUID assetUID, OrgNode orgNode, List<Vertex> relationVertices,
			Vertex belongsVertx, boolean isDataSync) {
		Session session = null;
		try {
			session = graphFactory.writeSession();
			if (!relationVertices.isEmpty()) {
				for (Vertex v : relationVertices) {
					orgNode.setType(getStr(v.getNode().get(SchemaConstants.PROP_NAME)));
				}
			}

			Map<String, Object> assetParams = getParameterMapFromNode(orgNode);
			assetParams.put(SchemaConstants.PROP_HDMFID_NAME, newGuid);
			assetParams.put(SchemaConstants.PROP_DB_UUID, assetUID.get());
			Asset newAsset = new TrxHandler<Asset>(session) {
				@Override
				public Asset block(Transaction transaction) {
					return assetDao.save(transaction, assetParams);
				}
			}.execute();

			if (newAsset == null) {
				LOGGER.info("not able to save asset newGuid:{},orgnode:{} ..!!", newGuid, orgNode);
				return null;
			}

			boolean relVertxLinkToNewVertx = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					relationVertices.forEach(relVert -> classDao.classIsClass(transaction, SchemaConstants.LABEL_ASSET,
							relVert.getLabel(),
							getParamMap(BindConstants.SRC, newAsset.getGuid(), BindConstants.DEST, relVert.getGuid())));
					return true;
				}
			}.execute();
			if (!relVertxLinkToNewVertx) {
				LOGGER.info("not able to create relations b/w newGuid:{},relationVertices:{} ..!!", newGuid,
						relationVertices);
				return null;
			}
			JsonObject dsInfo = null;
			if (belongsVertx != null)
				dsInfo = dataProviderService.getDataShardInfo(belongsVertx);
			String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID) : SchemaConstants.ROOT_DS);
			boolean saveStatus = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					Edge classHasDs = null;
					Edge dsBelongsClass = null;
					Edge classBelongsClass = null;
					Edge classHasClass = null;
					if (!isDataSync) {
						classHasDs = dataShardDao.classHasDs(transaction, newAsset.getLabel(),
								getParamMap(BindConstants.SRC, newAsset.getGuid(), BindConstants.DEST, dsGuid));
						dsBelongsClass = dataShardDao.dsBelongsClass(transaction, newAsset.getLabel(),
								getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, newAsset.getGuid()));
					}
					if (belongsVertx != null) {
						classBelongsClass = classDao.classBelongsClass(transaction, newAsset.getLabel(),
								belongsVertx.getLabel(), getParamMap(BindConstants.SRC, newAsset.getGuid(),
										BindConstants.DEST, belongsVertx.getGuid()));
						classHasClass = classDao.classHasClass(transaction, belongsVertx.getLabel(),
								newAsset.getLabel(), getParamMap(BindConstants.SRC, belongsVertx.getGuid(),
										BindConstants.DEST, newAsset.getGuid()));
					}
					if (isDataSync && belongsVertx == null)
						return true;
					return (classHasDs != null && dsBelongsClass != null && classBelongsClass != null
							&& classHasClass != null);
				}
			}.execute();
			if (!saveStatus) {
				LOGGER.info("not able to create links completely newVertex:{},dsGuid:{},belongsVertx:{}..!!",
						newAsset.getGuid(), dsGuid, belongsVertx.getGuid());
				return null;
			}
			return newAsset.getVertex();
		} catch (Exception e) {
			LOGGER.error("error occured in addAssetNode newGuid{},error:{}", newGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return null;
	}

	@Override
	public Vertex addLicenceAssetNode(String newGuid, AssetUID assetUID, OrgNode orgNode, List<Vertex> relationVertices,
			Vertex belongsVertx, boolean isDataSync) {
		Session session = null;
		try {
			session = graphFactory.writeSession();
			if (!relationVertices.isEmpty()) {
				for (Vertex v : relationVertices) {
					orgNode.setType(getStr(v.getNode().get(SchemaConstants.PROP_NAME)));
				}
			}

			Map<String, Object> assetParams = getParameterMapFromNode(orgNode);
			assetParams.put(SchemaConstants.PROP_HDMFID_NAME, newGuid);
			assetParams.put(SchemaConstants.PROP_DB_UUID, assetUID.get());
			assetParams.put(SchemaConstants.CURRENT_ASSET_COUNT, "0");
			assetParams.put(SchemaConstants.PROP_API_KEY, DaoUtil.generateToken());

			Asset newAsset = new TrxHandler<Asset>(session) {
				@Override
				public Asset block(Transaction transaction) {
					return assetDao.save(transaction, assetParams);
				}
			}.execute();

			if (newAsset == null) {
				LOGGER.info("not able to save asset newGuid:{},orgnode:{} ..!!", newGuid, orgNode);
				return null;
			}

			boolean relVertxLinkToNewVertx = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					relationVertices.forEach(relVert -> classDao.classIsClass(transaction, SchemaConstants.LABEL_ASSET,
							relVert.getLabel(),
							getParamMap(BindConstants.SRC, newAsset.getGuid(), BindConstants.DEST, relVert.getGuid())));
					return true;
				}
			}.execute();
			if (!relVertxLinkToNewVertx) {
				LOGGER.info("not able to create relations b/w newGuid:{},relationVertices:{} ..!!", newGuid,
						relationVertices);
				return null;
			}
			JsonObject dsInfo = null;
			if (belongsVertx != null)
				dsInfo = dataProviderService.getDataShardInfo(belongsVertx);
			String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID) : SchemaConstants.ROOT_DS);
			boolean saveStatus = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					Edge classHasDs = null;
					Edge dsBelongsClass = null;
					Edge classBelongsClass = null;
					Edge classHasClass = null;
					if (!isDataSync) {
						classHasDs = dataShardDao.classHasDs(transaction, newAsset.getLabel(),
								getParamMap(BindConstants.SRC, newAsset.getGuid(), BindConstants.DEST, dsGuid));
						dsBelongsClass = dataShardDao.dsBelongsClass(transaction, newAsset.getLabel(),
								getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, newAsset.getGuid()));
					}
					if (belongsVertx != null) {
						classBelongsClass = classDao.classBelongsClass(transaction, newAsset.getLabel(),
								belongsVertx.getLabel(), getParamMap(BindConstants.SRC, newAsset.getGuid(),
										BindConstants.DEST, belongsVertx.getGuid()));
						classHasClass = classDao.classHasClass(transaction, belongsVertx.getLabel(),
								newAsset.getLabel(), getParamMap(BindConstants.SRC, belongsVertx.getGuid(),
										BindConstants.DEST, newAsset.getGuid()));
					}
					if (isDataSync && belongsVertx == null)
						return true;
					return (classHasDs != null && dsBelongsClass != null && classBelongsClass != null
							&& classHasClass != null);
				}
			}.execute();
			if (!saveStatus) {
				LOGGER.info("not able to create links completely newVertex:{},dsGuid:{},belongsVertx:{}..!!",
						newAsset.getGuid(), dsGuid, belongsVertx.getGuid());
				return null;
			}
			return newAsset.getVertex();
		} catch (Exception e) {
			LOGGER.error("error occured in addAssetNode newGuid{},error:{}", newGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return null;
	}

	@Override
	public Map<String, Vertex> associateDataItems(String srcClassGuid, Optional<String> srcClasLabel,
			List<String> dataItemGuids) {
		StopWatch watch = Performance.startWatch(PerfConstants.ASSOCIATE_DATA_ITEMS_DB);
		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Map<String, Vertex>>(session) {
				@Override
				public Map<String, Vertex> block(Transaction transaction) {
					String classLabel = srcClasLabel.orElse(SchemaConstants.LABEL_PRODUCT_CLASS);
					ClassX classVertx = classDao.getByGuid(transaction, classLabel, srcClassGuid);
					if (classVertx == null)
						return Collections.emptyMap();

					Vertex vertx = classVertx.getVertex();
					Map<String, Vertex> returnMap = new HashMap<>(1);
					returnMap.put(srcClassGuid, vertx);
					dataItemGuids.forEach(dataItemGuid -> {
						if (SchemaConstants.LABEL_CLASS.equals(vertx.getLabel())) {
							classDao.classHasDataItem(transaction, classLabel,
									getParamMap(BindConstants.SRC, srcClassGuid, BindConstants.DEST, dataItemGuid));
						} else if (SchemaConstants.LABEL_PRODUCT_CLASS.equals(vertx.getLabel())) {
							productClassDao.productClassHasDataItem(transaction,
									getParamMap(BindConstants.SRC, srcClassGuid, BindConstants.DEST, dataItemGuid));
						}
					});
					return returnMap;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in associateDataItems srcClassGuid:{},srcClasLabel:{},error{}", srcClassGuid,
					srcClasLabel, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Vertex> associateEvents(String srcClassGuid, Optional<String> srcClasLabel,
			List<String> eventsGuids) {
		StopWatch watch = Performance.startWatch(PerfConstants.ASSOCIATE_EVENTS_DB);
		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Map<String, Vertex>>(session) {
				@Override
				public Map<String, Vertex> block(Transaction transaction) {
					String classLabel = srcClasLabel.orElse(SchemaConstants.LABEL_PRODUCT_CLASS);
					ClassX classVertx = classDao.getByGuid(transaction, classLabel, srcClassGuid);
					if (classVertx == null)
						return Collections.emptyMap();

					Vertex vertx = classVertx.getVertex();
					Map<String, Vertex> returnMap = new HashMap<>(1);
					returnMap.put(srcClassGuid, vertx);
					eventsGuids.forEach(dataItemGuid -> {
						if (SchemaConstants.LABEL_CLASS.equals(vertx.getLabel())) {
							classDao.classHasEvents(transaction, classLabel,
									getParamMap(BindConstants.SRC, srcClassGuid, BindConstants.DEST, dataItemGuid));
						} else if (SchemaConstants.LABEL_PRODUCT_CLASS.equals(vertx.getLabel())) {
							productClassDao.productClassHasEvents(transaction,
									getParamMap(BindConstants.SRC, srcClassGuid, BindConstants.DEST, dataItemGuid));
						}
					});
					return returnMap;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in associateEvents srcClassGuid:{},srcClasLabel:{},error{}", srcClassGuid,
					srcClasLabel, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Vertex> associateAggDataItems(String srcClassGuid, Optional<String> srcClasLabel,
			Map<String, String> dataItemGuids) {
		StopWatch watch = Performance.startWatch(PerfConstants.ASSOCIATE_AGG_DATA_ITEMS_DB);
		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Map<String, Vertex>>(session) {
				@Override
				public Map<String, Vertex> block(Transaction transaction) {
					String classLabel = srcClasLabel.orElse(SchemaConstants.LABEL_PRODUCT_CLASS);
					ClassX classVertx = classDao.getByGuid(transaction, classLabel, srcClassGuid);
					if (classVertx == null)
						return Collections.emptyMap();

					Vertex vertx = classVertx.getVertex();
					Map<String, Vertex> returnMap = new HashMap<>(1);
					returnMap.put(srcClassGuid, vertx);
					dataItemGuids.forEach((guid, avg) -> {
						if (SchemaConstants.LABEL_CLASS.equals(vertx.getLabel())) {
							classDao.classHasAggDataItem(transaction, classLabel, getParamMap(BindConstants.SRC,
									srcClassGuid, BindConstants.DEST, guid, BindConstants.PROP_AVG, avg));
						} else if (SchemaConstants.LABEL_PRODUCT_CLASS.equals(vertx.getLabel())) {
							productClassDao.productClassHasAggDataItem(transaction, getParamMap(BindConstants.SRC,
									srcClassGuid, BindConstants.DEST, guid, BindConstants.PROP_AVG, avg));
						}
					});
					return returnMap;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in associateAggDataItems srcClassGuid:{},srcClasLabel:{},error{}", srcClassGuid,
					srcClasLabel, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return Collections.emptyMap();
	}

	@Override
	public Vertex associatePrinicpal(Principal loginPrincipal, String appId, AdminUnit adminUnit, Principal principal)
			throws Exception {
		StopWatch watch = Performance.startWatch(PerfConstants.ASSOCIATE_PRINCIPAL_DB);
		Session session = null;
		Principal updatedPrincipal = null;
		try {
			session = graphFactory.writeSession();
			updatedPrincipal = new TrxHandler<Principal>(session) {
				@Override
				public Principal block(Transaction transaction) {
					Edge edge = principalDao.principalBelongsAdminUnit(transaction, getParamMap(BindConstants.SRC,
							principal.getGuid(), BindConstants.DEST, adminUnit.getGuid()));
					if (edge == null)
						return null;
					return principalDao.saveUpdate(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME,
							principal.getGuid(), SchemaConstants.PROP_AUTH_TOKEN, DaoUtil.generateHdmfUUID()));
				}
			}.execute();

		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return (updatedPrincipal != null ? updatedPrincipal.getVertex() : null);
	}

	@Override
	public boolean linkAsset(List<ConnectionLinks> links) {
		return linkAsset(null, null, links);
	}

	@Override
	public boolean linkAsset(String userId, String appId, List<ConnectionLinks> links) {
		StopWatch watch = Performance.startWatch(PerfConstants.LINK_ASSET_DB);
		Session session = null;
		boolean status = false;
		try {
			session = graphFactory.writeSession();
			status = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					links.forEach(link -> {
						String srcLabel = link.getSourceLabel();
						String destLabel = link.getDestinationLabel();
						if (StringUtils.isEmpty(srcLabel)) {
							srcLabel = commonDao.getLabel(transaction, link.getSource());
						}
						if (StringUtils.isEmpty(destLabel)) {
							destLabel = commonDao.getLabel(transaction, link.getDestination());
						}
						if (StringUtils.isEmpty(srcLabel) || StringUtils.isEmpty(destLabel)) {
							throw new DataException("label not found srcLabel:{},destLabel:{}..!!", ErrorCodes.FAILED,
									srcLabel, destLabel);
						}

						Edge edge = classDao.classConnnectedToClass(transaction, srcLabel, destLabel, getParamMap(
								BindConstants.SRC, link.getSource(), BindConstants.DEST, link.getDestination()));
						if (edge == null)
							throw new DataException("Not able to link,try again..!!," + link, ErrorCodes.FAILED);
					});
					return true;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in linkAsset, links:{},error:{}", links, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		if (!status)
			LOGGER.info("linkAsset failed..!!, {}", links);

		return status;
	}

	@Override
	public boolean unLinkAsset(List<ConnectionLinks> links) {
		return unLinkAsset(null, null, links);
	}

	@Override
	public boolean unLinkAsset(String userId, String appId, List<ConnectionLinks> links) {
		StopWatch watch = Performance.startWatch(PerfConstants.UNLINK_ASSET_DB);
		Session session = null;
		boolean status = false;
		try {
			session = graphFactory.writeSession();
			status = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					links.forEach(link -> {
						String srcLabel = link.getSourceLabel();
						String destLabel = link.getDestinationLabel();
						if (StringUtils.isEmpty(srcLabel)) {
							srcLabel = commonDao.getLabel(transaction, link.getSource());
						}
						if (StringUtils.isEmpty(destLabel)) {
							destLabel = commonDao.getLabel(transaction, link.getDestination());
						}
						if (StringUtils.isEmpty(srcLabel) || StringUtils.isEmpty(destLabel)) {
							throw new DataException("label not found srcLabel:{},destLabel:{}..!!", ErrorCodes.FAILED,
									srcLabel, destLabel);
						}
						Edge edge = classDao.unLinkClassConnnectedToClass(transaction, srcLabel, destLabel, getParamMap(
								BindConstants.SRC, link.getSource(), BindConstants.DEST, link.getDestination()));
						if (edge == null)
							throw new DataException("Not able to do UnLinking,try again..!!," + link,
									ErrorCodes.FAILED);
					});
					return true;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in UnLinkAsset, links:{},error:{}", links, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		if (!status)
			LOGGER.info("UnLinkAsset failed..!!, {}", links);
		return status;
	}

	@Override
	public boolean unLinkAll(String gatewayGuid, Optional<String> gatewayLabel) {
		return unLinkAll(null, null, gatewayGuid, gatewayLabel);
	}

	@Override
	public boolean unLinkAll(String userId, String appId, String gatewayGuid, Optional<String> gatewayLabel) {
		StopWatch watch = Performance.startWatch(PerfConstants.UNLINK_ALL_GW_DB);
		Session session = null;
		boolean status = false;
		try {
			session = graphFactory.writeSession();
			status = new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					classDao.unlinkAllClassesConnnectedToGateway(transaction,
							gatewayLabel.orElse(SchemaConstants.LABEL_ASSET),
							getParamMap(BindConstants.SRC, gatewayGuid));
					return true;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in UnLinkAll, gatewayGuid:{},error:{}", gatewayGuid, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return status;
	}

	@Override
	public Results<RegisterAgentAsset> registerAgentAsset(String userId, String appId, RegisterAgentAsset message) {
		Session session = null;
		StopWatch watch = Performance.startWatch(PerfConstants.REGISTER_AGENT_ASSET_DB);
		RegisterAgentAsset response = null;
		try {
			session = graphFactory.writeSession();
			response = registerAgentAssetFacade(session, "preRegistered", message);
		} catch (Exception e) {
			LOGGER.error("error occured in registerAgentAsset", e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return response != null ? new Results<>(response) : new Results<>(ErrorCodes.FAILED);
	}

	@Override
	public Results<UpdateAgentAsset> updateAgentAsset(UpdateAgentAsset message) {
		return updateAgentAsset(null, null, message);
	}

	@Override
	public Results<UpdateAgentAsset> updateAgentAsset(String userId, String appId, UpdateAgentAsset message) {
		Session session = null;
		StopWatch watch = Performance.startWatch(PerfConstants.UPDATE_AGENT_ASSET_DB);
		UpdateAgentAsset updateAgentAsset = null;
		try {
			if (message.getAsset() == null) {
				return new Results<>(ErrorCodes.MISSING_PARAMETER);
			}

			session = graphFactory.writeSession();
			updateAgentAsset = new TrxHandler<UpdateAgentAsset>(session) {
				@Override
				public UpdateAgentAsset block(Transaction transaction) {
					UpdateAgentAsset updateAgentAsset = new UpdateAgentAsset();
					List<org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.Asset> assetsJs = new ArrayList<>();
					message.getAsset().forEach(asset -> {
						List<Classes> classes = asset.getClasses();
						ProductClass productClass = productClassDao.getByName(transaction,
								asset.getProductClass().getName());
						if (productClass == null) {
							LOGGER.debug("productClass:{} not found.!!", asset.getProductClass().getName());
							return;
						}
						List<Asset> dbAssets = assetDao.getAssetBySerialNumber(transaction,
								asset.getProductClass().getName(), asset.getSerialNumber());
						if (dbAssets.isEmpty()) {
							LOGGER.debug("no asset:{}->productClass:{} relation found...!!",
									asset.getProductClass().getName(), asset.getSerialNumber());
							return;
						}
						if (dbAssets.size() > 1) {
							LOGGER.debug(
									"asset:{}->productClass:{} relation must be unique, found duplicate count:{}...!!",
									asset.getProductClass().getName(), asset.getSerialNumber(), dbAssets.size());
							return;
						}
						Asset dbAsset = dbAssets.get(0);

						assetDao.deleteAssetIsClassAll(transaction, dbAsset.getGuid());
						List<org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.Classes> classesJs = new ArrayList<>();
						classes.forEach(newClass -> {
							ClassX dbNewClass = classDao.getByName(transaction, SchemaConstants.LABEL_CLASS,
									newClass.getName());
							if (dbNewClass == null) {
								LOGGER.debug("class:{} not found, not linking to asset:{}..!!", newClass.getGuid(),
										dbAsset.getGuid());
								return;
							}
							Edge isEdge = assetDao.assetIsClass(transaction, dbAsset.getGuid(), dbNewClass.getGuid());
							if (isEdge != null)
								classesJs.add(new org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.Classes(
										dbNewClass.getName(), dbNewClass.getGuid()));
						});
						// Json Response
						org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.Asset assetJs = new org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.Asset(
								dbAsset.getGuid(), dbAsset.getSerialNumber(), classesJs,
								new org.digi.lg.neo4j.pojo.services.UpdateAgentAsset.ProductClass(
										productClass.getName(), productClass.getGuid()));
						assetsJs.add(assetJs);
					});

					updateAgentAsset.setAsset(assetsJs);
					return updateAgentAsset.getAsset().isEmpty() ? null : updateAgentAsset;
				}
			}.execute();

		} catch (Exception e) {
			LOGGER.error("error occured in updateAgentAsset, userId:{},appId:{},error:{}", userId, appId, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return updateAgentAsset == null ? new Results<>(ErrorCodes.INVALID_OPERATION) : new Results<>(updateAgentAsset);
	}

	@Override
	public List<List<LinkPojo>> createLink(String userId, String appId, LinkSet linkSet) {
		StopWatch watch = Performance.startWatch(PerfConstants.CREATE_LINK_DB);
		Session session = null;
		List<List<LinkPojo>> linkList = null;
		try {
			session = graphFactory.writeSession();
			linkList = new TrxHandler<List<List<LinkPojo>>>(session) {
				@Override
				public List<List<LinkPojo>> block(Transaction transaction) {
					List<List<LinkPojo>> linkList = new ArrayList<>();
					linkSet.getLinks().forEach(linkSource -> {
						Vertex srcVertex = dataProviderService.getAuthorizedVertex(appId, userId,
								linkSource.getNodeId(), Optional.ofNullable(linkSource.getNodeLabel()));
						if (srcVertex == null)
							return;

						List<LinkPojo> links = new ArrayList<>();
						linkSource.getTargets().forEach(linkDest -> {
							Vertex destVertex = dataProviderService.getAuthorizedVertex(appId, userId,
									linkDest.getNodeId(), Optional.ofNullable(linkDest.getNodeLabel()));
							if (destVertex == null)
								return;

							boolean isEdgeCreated = createEdge(transaction, linkDest.getDirection(),
									linkDest.getLinkType(), srcVertex.getGuid(), srcVertex.getLabel(),
									destVertex.getGuid(), destVertex.getLabel());
							if (isEdgeCreated)
								links.add(new LinkPojo().setRelation(linkDest.getLinkType())
										.setSource(srcVertex.getGuid()).setTarget(destVertex.getGuid())
										.setDirection(linkDest.getDirection()).setRelation(linkDest.getLinkType()));

						});
						if (!links.isEmpty())
							linkList.add(links);
					});
					return linkList;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in createLink, linkSet:{},error:{}", linkSet, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return linkList;
	}

	@Override
	public List<List<LinkPojo>> createLinkForDBSync(LinkSet linkSet) {
		StopWatch watch = Performance.startWatch(PerfConstants.CREATE_LINK_DB);
		Session session = null;
		List<List<LinkPojo>> linkList = null;
		try {
			session = graphFactory.writeSession();
			linkList = new TrxHandler<List<List<LinkPojo>>>(session) {
				@Override
				public List<List<LinkPojo>> block(Transaction transaction) {
					List<List<LinkPojo>> linkList = new ArrayList<>();
					linkSet.getLinks().forEach(linkSource -> {
						Vertex srcVertex = dataProviderService.getVertex(Optional.of(linkSource.getNodeLabel()),
								SchemaConstants.PROP_HDMFID_NAME, linkSource.getNodeId());
						if (srcVertex == null)
							return;

						List<LinkPojo> links = new ArrayList<>();
						linkSource.getTargets().forEach(linkDest -> {
							Vertex destVertex = dataProviderService.getVertex(Optional.of(linkDest.getNodeLabel()),
									SchemaConstants.PROP_HDMFID_NAME, linkDest.getNodeId());
							if (destVertex == null)
								return;

							boolean isEdgeCreated = createEdge(transaction, linkDest.getDirection(),
									linkDest.getLinkType(), srcVertex.getGuid(), srcVertex.getLabel(),
									destVertex.getGuid(), destVertex.getLabel());
							if (isEdgeCreated)
								links.add(new LinkPojo().setRelation(linkDest.getLinkType())
										.setSource(srcVertex.getGuid()).setTarget(destVertex.getGuid())
										.setDirection(linkDest.getDirection()).setRelation(linkDest.getLinkType()));

						});
						if (!links.isEmpty())
							linkList.add(links);
					});
					return linkList;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in createLink, linkSet:{},error:{}", linkSet, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return linkList;
	}

	@Override
	public boolean modifyLink(String userId, String appId, String sourceLabel, String destLabel, String oldSourceGuid,
			String newSourceGuid, String oldDestGuid, String newDestGuid) {
		StopWatch watch = Performance.startWatch(PerfConstants.MODIFY_ORG_ASSET_LINK_DB);
		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {

					switch (LinkIdentifier.valueOf(sourceLabel + destLabel)) {
					case classclass:
						modifyClassIsClass(transaction, oldSourceGuid, newSourceGuid, oldDestGuid, newDestGuid);
						break;
					case orgasset:
						modifyOrgHasAsset(transaction, oldSourceGuid, newSourceGuid, oldDestGuid, newDestGuid);
						break;
					case assetasset:
						modifyAssetHasAsset(transaction, oldSourceGuid, newSourceGuid, oldDestGuid, newDestGuid);
						break;
					case orgorg:
						modifyOrgHasOrg(transaction, oldSourceGuid, newSourceGuid, oldDestGuid, newDestGuid);
						break;
					case assetclass:
						modifyAssetIsClass(transaction, oldSourceGuid, newSourceGuid, oldDestGuid, newDestGuid);
						break;
					case orgclass:
						modifyOrgIsClass(transaction, oldSourceGuid, newSourceGuid, oldDestGuid, newDestGuid);
						break;
					case classproductclass:
						modifyProductClassIsClass(transaction, oldSourceGuid, newSourceGuid, oldDestGuid, newDestGuid);
						break;
					case productclassclass:
						modifyClassIsProductClass(transaction, oldSourceGuid, newSourceGuid, oldDestGuid, newDestGuid);
						break;
					}
					return true;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in modifyOrgAssetLink sourceLabel:{},destLabel:{},oldSourceGuid:{},newSourceGuid:{},oldDestGuid:{},newDestGuid:{} error:{}",
					sourceLabel, destLabel, oldSourceGuid, newSourceGuid, oldDestGuid, newDestGuid, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return false;
	}

	@Override
	public Vertex createAuthToken(String tokenGuid, String encryptedToken, String adminUnitGuid, long expiryDate,
			String principalId) {
		Session session = null;
		Vertex authTokenVertex = null;
		try {
			session = graphFactory.writeSession();
			authTokenVertex = new TrxHandler<Vertex>(session) {
				@Override
				public Vertex block(Transaction transaction) {
					AuthToken authToken = authTokenDao.add(transaction,
							getParamMap(SchemaConstants.PROP_HDMFID_NAME, tokenGuid, SchemaConstants.PROP_PRINCIPAL_ID,
									principalId, SchemaConstants.PROP_VALIDITY_DATE, expiryDate,
									SchemaConstants.PROP_AUTH_TOKEN, encryptedToken));
					if (authToken == null) {
						LOGGER.info("not able to create authToken,tokenGuid:{},adminUnitGuid:{} ..!!", tokenGuid,
								adminUnitGuid);
						return null;
					}
					Edge tokenEdge = authTokenDao.adminUnitHasToken(transaction, adminUnitGuid, tokenGuid);
					if (tokenEdge == null)
						throw new DataException(
								"not able to create authToken edge b/w tokenGuid:{},adminUnitGuid:{}..!!", tokenGuid,
								adminUnitGuid);

					return authToken.getVertex();
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in create token,tokenGuid:{},adminUnitGuid:{},error:{} ..!!", tokenGuid,
					adminUnitGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return authTokenVertex;
	}

	@Override
	public Results<JsonObject> addScriptInstance(String userId, String appId, String scriptTemplateGuid,
			String scriptGuid, String eventsGuid, boolean dbSync) {
		Session session = null;
		Results<JsonObject> result = null;
		Long timestamp = System.currentTimeMillis();
		try {
			session = graphFactory.writeSession();
			result = new TrxHandler<Results<JsonObject>>(session) {
				@Override
				public Results<JsonObject> block(Transaction transaction) {
					JsonObject data = new JsonObject();
					ContractVertex contractInfo = personAppContractCache.get(userId, appId);
					if (contractInfo == null) {
						LOGGER.info("contract-info not found userId:{},appId{} ..!!", userId, appId);
						return new Results<>(data, ErrorCodes.NOT_FOUND);
					}

					ClassX rootEventClass = classDao.getDomainChildClass(transaction,
							contractInfo.getDomainClass().getGuid(), PropNameSuffix._EVENTS.getValue());
					if (rootEventClass == null) {
						LOGGER.info("root-event class not found userId:{},appId{} ..!!", userId, appId);
						return new Results<>(data, ErrorCodes.NOT_FOUND);
					}

					ScriptTemplate scriptTemplate = scriptTemplateDao.getByGuid(transaction, scriptTemplateGuid);
					if (scriptTemplate == null)
						return new Results<>(data, ErrorCodes.NOT_FOUND);

					data.put(JsonConstants.SCRIPT_TEMPLATE_GUID, scriptTemplateGuid);
					data.put(JsonConstants.SCRIPT_TEMPLATE_NAME, scriptTemplate.getName());

					List<Vertex> configItems = scriptTemplateDao.getConfigItem(transaction, scriptTemplateGuid);
					List<Vertex> eventTypes = scriptTemplateDao.getEventType(transaction, scriptTemplateGuid);
					List<Vertex> dataItems = scriptTemplateDao.getTriggerDataItem(transaction, scriptTemplateGuid);

					String scriptName = new StringBuilder(scriptTemplate.getName()).append(SchemaConstants.UNDER_SCORE)
							.append(contractInfo.getPrincipal().getName()).append(SchemaConstants.UNDER_SCORE)
							.append(timestamp).toString();

					Script script = scriptDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME,
							scriptGuid, SchemaConstants.PROP_NAME, scriptName));
					scriptDao.scriptToScriptTemplate(transaction, script.getGuid(), scriptTemplate.getGuid());
					data.put(JsonConstants.SCRIPT_GUID, script.getGuid());
					data.put(JsonConstants.SCRIPT_NAME, script.getName());
					JsonArray configItemArray = new JsonArray();
					configItems.forEach(configItem -> {
						JsonObject configobj = new JsonObject();
						scriptDao.scriptHasConfigItem(transaction, script.getGuid(), configItem.getGuid());
						configobj.put(JsonConstants.CONFIG_ITEM_GUID, configItem.getGuid());
						configItemArray.add(configobj);
					});
					data.put(JsonConstants.CONFIG_ITEM_ARRAY, configItemArray);

					String eventsName = new StringBuilder(scriptTemplate.getName()).append(SchemaConstants.UNDER_SCORE)
							.append(contractInfo.getPrincipal().getName()).append(SchemaConstants.UNDER_SCORE)
							.append(timestamp).toString();
					Events events = eventsDao.save(transaction,
							getParamMap(SchemaConstants.PROP_HDMFID_NAME, eventsGuid, SchemaConstants.PROP_NAME,
									eventsName, SchemaConstants.PROP_DB_UUID,
									new EventsUID(contractInfo.getDomainClass().getGuid(), eventsName).get()));
					classDao.classHasEvents(transaction, rootEventClass.getVertex().getLabel(), getParamMap(
							BindConstants.SRC, rootEventClass.getGuid(), BindConstants.DEST, events.getGuid()));
					scriptDao.scriptHasEvents(transaction, script.getGuid(), events.getGuid());
					JsonArray eventTypeArray = new JsonArray();
					data.put(JsonConstants.EVENT_GUID, events.getGuid());
					data.put(JsonConstants.EVENT_NAME, events.getName());
					eventTypes.forEach(eventType -> {
						JsonObject eobj = new JsonObject();
						eventsDao.eventsHasEventype(transaction, events.getGuid(), eventType.getGuid());
						eobj.put(JsonConstants.EVENT_TYPE, eventType.getGuid());
						eventTypeArray.add(eobj);
					});
					data.put(JsonConstants.EVENT_TYPE_ARRAY, eventTypeArray);

					scriptDao.scriptHasContract(transaction, script.getGuid(), contractInfo.getContract().getGuid());
					scriptDao.scriptBelongsAdu(transaction, script.getGuid(), contractInfo.getAdminUnit().getGuid());
					JsonArray dataItemArray = new JsonArray();
					dataItems.forEach(dataItem -> {
						JsonObject dataItemObj = new JsonObject();
						scriptDao.scriptTriggerDataItem(transaction, script.getGuid(), dataItem.getGuid());
						dataItemObj.put(JsonConstants.DATA_ITEM_GUID, dataItem.getGuid());
						dataItemArray.add(dataItemObj);
					});
					data.put(JsonConstants.DATA_ITEM_ARRAY, dataItemArray);

					if (!dbSync) {
						JsonObject dsInfo = dataProviderService.getDataShardInfo(SchemaConstants.LABEL_SCRIPT_TEMPLATE,
								scriptTemplateGuid);

						String dsGuid = dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID)
								: SchemaConstants.ROOT_DS;
						scriptDao.scriptHasDS(transaction, script.getGuid(), dsGuid);
						dataShardDao.dsBelongsScript(transaction, dsGuid, script.getGuid());
						data.put(JsonConstants.DS_GUID, dsGuid);
					}
					return new Results<>(data);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in addScriptInstance,userId:{},scriptTemplateGuid:{},scriptGuid:{},eventsGuid:{},error:{} ..!!",
					userId, scriptTemplateGuid, scriptGuid, eventsGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return result;
	}

	@Override
	public void addScriptInstanceDS(String scriptTemplateGuid, String scriptGuid, String scriptName, String eventsGuid,
			String eventsName, String domainClassGuid) {
		Session session = null;
		try {
			session = graphFactory.writeSession();
			new TrxHandler<Void>(session) {
				@Override
				public Void block(Transaction transaction) {
					ScriptTemplate scriptTemplate = scriptTemplateDao.getByGuid(transaction, scriptTemplateGuid);
					if (scriptTemplate == null) {
						LOGGER.info("not found scriptTemplateGuid:{} ..!!", scriptTemplateGuid);
						return null;
					}

					ClassX rootEventClass = classDao.getDomainChildClass(transaction, domainClassGuid,
							PropNameSuffix._EVENTS.getValue());
					if (rootEventClass == null) {
						LOGGER.info("root-event class not found for domainClassGuid:{} ..!!", domainClassGuid);
						return null;
					}

					List<Vertex> configItems = scriptTemplateDao.getConfigItem(transaction, scriptTemplateGuid);
					List<Vertex> eventTypes = scriptTemplateDao.getEventType(transaction, scriptTemplateGuid);
					List<Vertex> dataItems = scriptTemplateDao.getTriggerDataItem(transaction, scriptTemplateGuid);

					Script script = scriptDao.save(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME,
							scriptGuid, SchemaConstants.PROP_NAME, scriptName));
					scriptDao.scriptToScriptTemplate(transaction, script.getGuid(), scriptTemplate.getGuid());
					configItems.forEach(configItem -> scriptDao.scriptHasConfigItem(transaction, script.getGuid(),
							configItem.getGuid()));

					Events events = eventsDao.save(transaction,
							getParamMap(SchemaConstants.PROP_HDMFID_NAME, eventsGuid, SchemaConstants.PROP_NAME,
									eventsName, SchemaConstants.PROP_DB_UUID,
									new EventsUID(domainClassGuid, eventsName).get()));
					classDao.classHasEvents(transaction, rootEventClass.getVertex().getLabel(), getParamMap(
							BindConstants.SRC, rootEventClass.getGuid(), BindConstants.DEST, events.getGuid()));
					scriptDao.scriptHasEvents(transaction, script.getGuid(), events.getGuid());
					eventTypes.forEach(eventType -> eventsDao.eventsHasEventype(transaction, events.getGuid(),
							eventType.getGuid()));

					dataItems.forEach(dataItem -> scriptDao.scriptTriggerDataItem(transaction, script.getGuid(),
							dataItem.getGuid()));
					return null;
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in addScriptInstanceDS,userId:{},scriptTemplateGuid:{},scriptGuid:{},eventsGuid:{},error:{} ..!!",
					scriptTemplateGuid, scriptGuid, eventsGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
	}

	@Override
	public Results<JsonObject> addScriptTemplate(String userId, String appId, ScriptTemplateUID scriptTemplateUID,
			String scriptTemplateId, String scriptTemplateName, String scriptTemplateScope, JsonArray configItems,
			JsonArray eventTypes, JsonArray dataItems, JsonArray productClasses, boolean dbSync) {
		Session session = null;
		Results<JsonObject> finalResult = null;

		try {
			session = graphFactory.writeSession();
			finalResult = new TrxHandler<Results<JsonObject>>(session) {
				@Override
				public Results<JsonObject> block(Transaction transaction) {
					JsonObject data = new JsonObject();
					Map<String, Object> params = getParamMap(SchemaConstants.PROP_HDMFID_NAME, scriptTemplateId,
							SchemaConstants.PROP_NAME, scriptTemplateName, SchemaConstants.PROP_SCOPE,
							scriptTemplateScope, SchemaConstants.PROP_TYPE, "none");
					params.put(SchemaConstants.PROP_DB_UUID, scriptTemplateUID.get());
					ScriptTemplate scriptTemplate = scriptTemplateDao.save(transaction, params);

					configItems.forEach(configItem -> {
						JsonObject object = (JsonObject) configItem;
						ConfigItemUID configItemUID = new ConfigItemUID(
								ScriptTemplateUID.getDomainGuid(scriptTemplateUID.get()),
								object.getString(JsonConstants.NAME));
						ConfigItem configItemDb = configItemDao.save(transaction,
								getParamMap(SchemaConstants.PROP_HDMFID_NAME, DaoUtil.generateHdmfUUID(),
										SchemaConstants.PROP_NAME, object.getString(JsonConstants.NAME),
										SchemaConstants.PROP_DB_UUID, configItemUID.get()));
						scriptTemplateDao.scriptTemplateHasConfigItem(transaction, scriptTemplate.getGuid(),
								configItemDb.getGuid());
					});

					eventTypes.forEach(eventType -> {
						JsonObject object = (JsonObject) eventType;
						EventTypeUID eventTypeUID = new EventTypeUID(
								ScriptTemplateUID.getDomainGuid(scriptTemplateUID.get()),
								object.getString(JsonConstants.NAME));
						EventType eventTypeDb = eventTypeDao.save(transaction,
								getParamMap(SchemaConstants.PROP_HDMFID_NAME, DaoUtil.generateHdmfUUID(),
										SchemaConstants.PROP_NAME, object.getString(JsonConstants.NAME),
										SchemaConstants.PROP_SCOPE, object.getString(JsonConstants.SCOPE),
										SchemaConstants.PROP_DB_UUID, eventTypeUID.get()));
						scriptTemplateDao.scriptTemplateGeneratesEventType(transaction, scriptTemplate.getGuid(),
								eventTypeDb.getGuid());
					});

					dataItems.forEach(dataItem -> {
						JsonObject object = (JsonObject) dataItem;
						DataItem dataItemDb = dataItemDao.getByGuid(transaction,
								object.getString(SchemaConstants.PROP_HDMFID_NAME));
						scriptTemplateDao.scriptTemplateTriggerDataItem(transaction, scriptTemplate.getGuid(),
								dataItemDb.getGuid());
					});

					productClasses.forEach(prdtClass -> {
						JsonObject object = (JsonObject) prdtClass;
						ProductClass prdtClassDb = productClassDao.getByGuid(transaction,
								object.getString(SchemaConstants.PROP_HDMFID_NAME));
						scriptTemplateDao.scriptTemplateAppliesProductClass(transaction, scriptTemplate.getGuid(),
								prdtClassDb.getGuid());
					});

					if (!dbSync) {
						ContractVertex contractInfo = personAppContractCache.get(userId, appId);
						if (contractInfo == null) {
							LOGGER.error("contractInfo not found..!! userId:{},appId:{}", userId, appId);
							return null;
						}
						scriptTemplateDao.scriptTemplateHasAdu(transaction, scriptTemplate.getGuid(),
								contractInfo.getAdminUnit().getGuid());

						JsonObject dsInfo = dataProviderService.getDataShardInfo(SchemaConstants.LABEL_PRODUCT_CLASS,
								productClasses.getJsonObject(0).getString(SchemaConstants.PROP_HDMFID_NAME));

						String dsGuid = dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID)
								: SchemaConstants.ROOT_DS;
						scriptTemplateDao.scriptTemplateHasDS(transaction, scriptTemplate.getGuid(), dsGuid);
						dataShardDao.dsBelongsScriptTemplate(transaction, dsGuid, scriptTemplate.getGuid());
						data.put(JsonConstants.DS_GUID, dsGuid);
					}
					return new Results<>(data);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error(
					"error occured in addScriptTemplate,userId:{},scriptTemplateName:{},scriptTemplateScope:{},error:{} ..!!",
					userId, scriptTemplateId, scriptTemplateName, scriptTemplateScope, e);
		} finally {
			graphFactory.closeSession(session);
		}

		return finalResult;
	}

	@Override
	public Map<String, Vertex> associatePricipalToCT(String principalGuid, String adminUnitGuid, String contractGuid,
			String contractTypeGuid, String classPrincipalClassGuid, String accountName, String principalOrgGuid) {
		Session session = null;
		Map<String, Vertex> vertexMap = null;
		StopWatch watch = Performance.startWatch(PerfConstants.ASSOCIATE_PRINCIAPLTO_CONTRACT);
		try {
			session = graphFactory.writeSession();
			vertexMap = associatePricipalToCT(session, principalGuid, adminUnitGuid, contractGuid, contractTypeGuid,
					classPrincipalClassGuid, accountName, principalOrgGuid);
		} catch (

		Exception e) {
			LOGGER.error("error occured in addPerson", e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return vertexMap;
	}

	@Override
	public boolean deleteAuthToken(String tokenGuid) {
		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					return authTokenDao.deleteAuthToken(transaction, tokenGuid);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in deleteAuthToken,tokenGuid:{},error:{}", tokenGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return false;
	}

	@Override
	public Map<String, Vertex> associatePricipalToContractType(String principalId, String appId, String contractTypeId,
			String accountName, String contractId) {
		Map<String, Vertex> vertexMap = new HashMap<>();
		Session session = null;
		try {
			session = graphFactory.writeSession();
			ContractVertex contractVertex = personAppContractCache.get(principalId, appId);
			Org org = contractDao.getOrgByContract(session, contractVertex.getContract().getGuid());
			Principal principal = contractVertex.getPrincipal();
			vertexMap.put(org.getGuid(), org.getVertex());
			vertexMap.put(contractVertex.getAdminUnit().getGuid(), contractVertex.getAdminUnit().getVertex());
			vertexMap.put(principal.getGuid(), principal.getVertex());

			ClassX principalDomainClass = contractVertex.getDomainClass();
			vertexMap.put(principalDomainClass.getGuid(), principalDomainClass.getVertex());
			vertexMap = associatePricipalToCT(session, principal.getGuid(), contractVertex.getAdminUnit().getGuid(),
					contractId, contractTypeId, principalDomainClass.getGuid(), accountName, org.getGuid());
		} catch (Exception e) {
			LOGGER.error("error occured in associatePincipalToContract", e);
		} finally {
			graphFactory.closeSession(session);

		}
		return vertexMap;
	}

	/**
	 * 
	 */
	@Override
	public Results<Boolean> associateAssetToAccount(List<String> assetGuids, String location, String accountName,
			Vertex parentVertex, boolean isDBSync) {
		Session session = null;
		try {
			session = graphFactory.writeSession();
			List<Asset> assets = new ArrayList<>(assetGuids.size());
			for (String assetGuid : assetGuids) {
				Asset asset = assetDao.getByGuid(session, assetGuid);
				if (asset == null)
					continue;
				boolean isUnique = dataProviderService.isExist(parentVertex.getLabel(),
						SchemaConstants.PROP_HDMFID_NAME, parentVertex.getGuid(), asset.getLabel(),
						SchemaConstants.PROP_NAME, asset.getName(), Relationship.HAS, Direction.OUT);
				if (!isUnique) {
					assets.add(asset);
				}
			}
			if (assets.size() != assetGuids.size()) {
				return new Results<>(false, ErrorCodes.ALREADY_EXIST);
			}

			return new TrxHandler<Results<Boolean>>(session) {
				@Override
				public Results<Boolean> block(Transaction transaction) {
					assets.forEach(asset -> {
						Map<String, Object> params = getParamMap(SchemaConstants.PROP_HDMFID_NAME, asset.getGuid(),
								SchemaConstants.PROP_DB_UUID, asset.getDBUUID(), SchemaConstants.PROP_ACTDATE,
								System.currentTimeMillis(), SchemaConstants.PROP_LOCATION, location);

						assetDao.saveUpdate(transaction, params);

						if (parentVertex.getLabel().equals(SchemaConstants.LABEL_ORG)) {
							orgDao.orgHasAsset(transaction, parentVertex.getGuid(), asset.getGuid());
							assetDao.assetBelongsOrg(transaction, asset.getGuid(), parentVertex.getGuid());
						} else if (parentVertex.getLabel().equals(SchemaConstants.LABEL_ASSET)) {
							assetDao.assetHasAsset(transaction, parentVertex.getGuid(), asset.getGuid());
							assetDao.assetBelongsAsset(transaction, asset.getGuid(), parentVertex.getGuid());
						}
						if (!isDBSync) {
							JsonObject dsInfo = dataProviderService.getDataShardInfo(parentVertex);
							String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID)
									: SchemaConstants.ROOT_DS);
							classDao.classHasDataShard(transaction, SchemaConstants.LABEL_ASSET,
									getParamMap(BindConstants.SRC, asset.getGuid(), BindConstants.DEST, dsGuid));
							dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_ASSET,
									getParamMap(BindConstants.SRC, dsGuid, BindConstants.DEST, asset.getGuid()));
						}
					});
					return new Results<>(true);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in AssociateAssetToContract,assetGuids:{},location:{},accountName:{},error:{}",
					assetGuids, location, accountName, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(false);
	}

	@Override
	public Results<JsonObject> updateConfigInfo(String userId, String appId, String scriptGuid, JsonArray configItems,
			JsonArray assets) {
		Session session = null;
		JsonObject output = null;
		Map<String, Object> assetsMap = new HashMap<>();
		JsonObject ds = null;
		try {
			session = graphFactory.writeSession();
			Script script = scriptDao.getByGuid(session, scriptGuid);
			if (script == null)
				return new Results<>(ErrorCodes.NOT_FOUND);
			output = new TrxHandler<JsonObject>(session) {
				@Override
				public JsonObject block(Transaction transaction) {
					JsonObject output = new JsonObject();

					// link script->config_item
					configItems.forEach(configItem -> {
						JsonObject cfg = (JsonObject) configItem;
						String guid = cfg.getString(JsonConstants.GUID);
						String label = cfg.getString(JsonConstants.LABEL);
						String value = cfg.getString(JsonConstants.VALUE);
						scriptDao.scriptHasCongigItem(transaction, script.getGuid(), label, guid, value);
					});
					// link script->asset
					assets.forEach(asset -> {
						JsonObject anyClass = (JsonObject) asset;
						String guid = anyClass.getString(JsonConstants.GUID);
						String label = anyClass.getString(JsonConstants.LABEL);
						scriptDao.scriptAppliestoAsset(transaction, script.getGuid(), label, guid);
						assetsMap.put(guid, label);
					});

					/// DS linking
					JsonObject ds = dataProviderService.getDataShardInfo(script.getLabel(), script.getGuid());
					if (ds == null) {
						classDao.classHasDataShard(transaction, SchemaConstants.LABEL_SCRIPT, getParamMap(
								BindConstants.SRC, script.getGuid(), BindConstants.DEST, SchemaConstants.ROOT_DS));
						dataShardDao.dsBelongsClass(transaction, SchemaConstants.LABEL_SCRIPT, getParamMap(
								BindConstants.SRC, SchemaConstants.ROOT_DS, BindConstants.DEST, script.getGuid()));

					}
					output.put(JsonConstants.ASSETS, new JsonObject(assetsMap));

					return output;
				}
			}.execute();

			ds = dataProviderService.getDataShardInfo(script.getLabel(), script.getGuid());
			output.put(JsonConstants.DS, ds);
		} catch (Exception e) {
			LOGGER.error("error occured in updateConfigInfo,scriptGuid:{},error:{}", scriptGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return output != null ? new Results<>(output) : new Results<>(ErrorCodes.FAILED);
	}

	@Override

	public Results<JsonObject> addMashupScript(String userId, String appId, String mashupGuid, String mashupName,
			String mashupPath, String scriptRepoGuid, String scriptRepoName, String scriptRepoUrl,
			String scriptRepouser, String scriptRepopwd, String scriptRepolocation) {

		Session session = null;
		JsonObject data = new JsonObject();
		try {
			session = graphFactory.writeSession();
			new TrxHandler<Results<JsonObject>>(session) {
				@Override
				public Results<JsonObject> block(Transaction transaction) {
					ContractVertex contractInfo = personAppContractCache.get(userId, appId);
					if (contractInfo == null) {
						LOGGER.error("contractInfo not found..!! userId:{},appId:{}", userId, appId);
						return null;
					}

					Mashup scriptcheck = mashUpDao.getByPath(transaction, mashupPath);
					if (scriptcheck != null) {
						LOGGER.info("not able to create Mashup:{},existingClass{}", mashupPath);
						return null;
					}

					Mashup mashup = mashUpDao.add(transaction, getParamMap(SchemaConstants.PROP_HDMFID_NAME, mashupGuid,
							SchemaConstants.PROP_NAME, mashupName, SchemaConstants.PROP_PATH, mashupPath));
					mashUpDao.mashupHasAdu(transaction, mashup.getGuid(), contractInfo.getAdminUnit().getGuid());

					if (!scriptRepoUrl.isEmpty()) {
						ScriptRepo scriptrepo1 = scriptRepoDao.getByUrl(transaction, scriptRepoUrl);
						if (scriptrepo1 != null) {
							mashUpDao.mashupHasScriptRepo(transaction, SchemaConstants.LABEL_MASHUP, getParamMap(
									BindConstants.SRC, mashup.getGuid(), BindConstants.DEST, scriptrepo1.getGuid()));
						} else {
							scriptRepoDao.add(transaction,
									getParamMap(SchemaConstants.PROP_HDMFID_NAME, scriptRepoGuid,
											SchemaConstants.PROP_NAME, scriptRepoName, SchemaConstants.PROP_GIT_PWD,
											scriptRepopwd, SchemaConstants.PROP_GIT_USER, scriptRepouser,
											SchemaConstants.PROP_GIT_LOCATION, scriptRepolocation,
											SchemaConstants.PROP_GIT_URL, scriptRepoUrl));

							mashUpDao.mashupHasScriptRepo(transaction, SchemaConstants.LABEL_MASHUP, getParamMap(
									BindConstants.SRC, mashup.getGuid(), BindConstants.DEST, scriptRepoGuid));
						}
					} else {
						mashUpDao.mashupHasScriptRepo(transaction, SchemaConstants.LABEL_MASHUP,
								getParamMap(BindConstants.SRC, mashup.getGuid(), BindConstants.DEST,
										SchemaConstants.ROOT_SCRIPT_REPO));
					}
					data.put(JsonConstants.MASHUP_GUID, mashup.getGuid());
					data.put(JsonConstants.MASHUP_NAME, mashup.getName());
					data.put(JsonConstants.MASHUP_PATH, mashup.getPath());
					return new Results<>(data);
				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in addMashup Script,userId:{},mashup:{},mashuppath:{},error:{} ..!!", userId,
					mashupGuid, mashupName, mashupPath, e);
		} finally {
			graphFactory.closeSession(session);
		}

		return new Results<>(data);

	}

	@Override
	public <TRX> boolean modifyClassIsClass(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid) {

		if (oldSourceGuid != null && newSourceGuid != null && oldDestGuid != null && newDestGuid == null) {
			Edge delClassIsClass = classDao.deleteClassIsClass(trx, oldSourceGuid, oldDestGuid);
			Edge createClassIsClass = classDao.classIsClass(trx, SchemaConstants.LABEL_CLASS,
					SchemaConstants.LABEL_CLASS,
					getParamMap(BindConstants.SRC, newSourceGuid, BindConstants.DEST, oldDestGuid));
			return (delClassIsClass != null && createClassIsClass != null);
		} else if (oldSourceGuid != null && newSourceGuid == null && oldDestGuid != null && newDestGuid != null) {
			Edge delClassIsClass = classDao.deleteClassIsClass(trx, oldSourceGuid, oldDestGuid);
			Edge createClassIsClass = classDao.classIsClass(trx, SchemaConstants.LABEL_CLASS,
					SchemaConstants.LABEL_CLASS,
					getParamMap(BindConstants.SRC, oldSourceGuid, BindConstants.DEST, newDestGuid));

			return (delClassIsClass != null && createClassIsClass != null);
		}
		return false;

	}

	@Override
	public <TRX> boolean modifyProductClassIsClass(TRX trx, String oldSourceGuid, String newSourceGuid,
			String oldDestGuid, String newDestGuid) {

		if (oldSourceGuid != null && newSourceGuid != null && oldDestGuid != null && newDestGuid == null) {
			Edge delProductClassIsClass = productClassDao.deleteProductClassIsClass(trx, oldSourceGuid, oldDestGuid);
			Edge createProductClassIsClass = productClassDao.productClassIsClass(trx,
					SchemaConstants.LABEL_PRODUCT_CLASS, SchemaConstants.LABEL_CLASS,
					getParamMap(BindConstants.SRC, newSourceGuid, BindConstants.DEST, oldDestGuid));
			return (delProductClassIsClass != null && createProductClassIsClass != null);
		} else if (oldSourceGuid != null && newSourceGuid == null && oldDestGuid != null && newDestGuid != null) {
			Edge delProductClassIsClass = productClassDao.deleteProductClassIsClass(trx, oldSourceGuid, oldDestGuid);
			Edge createProductClassIsClass = productClassDao.productClassIsClass(trx,
					SchemaConstants.LABEL_PRODUCT_CLASS, SchemaConstants.LABEL_CLASS,
					getParamMap(BindConstants.SRC, oldSourceGuid, BindConstants.DEST, newDestGuid));

			return (delProductClassIsClass != null && createProductClassIsClass != null);
		}
		return false;

	}

	@Override
	public <TRX> boolean modifyClassIsProductClass(TRX trx, String oldSourceGuid, String newSourceGuid,
			String oldDestGuid, String newDestGuid) {

		if (oldSourceGuid != null && newSourceGuid != null && oldDestGuid != null && newDestGuid == null) {
			Edge delClassIsProductClass = productClassDao.deleteClassIsProductClass(trx, oldSourceGuid, oldDestGuid);
			Edge createClassIsProductClass = productClassDao.classIsProductClass(trx, SchemaConstants.LABEL_CLASS,
					SchemaConstants.LABEL_PRODUCT_CLASS,
					getParamMap(BindConstants.SRC, newSourceGuid, BindConstants.DEST, oldDestGuid));
			return (delClassIsProductClass != null && createClassIsProductClass != null);
		} else if (oldSourceGuid != null && newSourceGuid == null && oldDestGuid != null && newDestGuid != null) {
			Edge delClassIsProductClass = productClassDao.deleteClassIsProductClass(trx, oldSourceGuid, oldDestGuid);
			Edge createClassIsProductClass = productClassDao.classIsProductClass(trx, SchemaConstants.LABEL_CLASS,
					SchemaConstants.LABEL_PRODUCT_CLASS,
					getParamMap(BindConstants.SRC, oldSourceGuid, BindConstants.DEST, newDestGuid));

			return (delClassIsProductClass != null && createClassIsProductClass != null);
		}
		return false;

	}

	@Override
	public <TRX> boolean modifyOrgHasAsset(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid) {

		if (oldSourceGuid != null && newSourceGuid != null && oldDestGuid != null && newDestGuid == null) {
			Edge delOrgHasAsset = orgDao.deleteOrgHasAsset(trx, oldSourceGuid, oldDestGuid);
			Edge delAssetBelongsOrg = assetDao.deleteAssetBelongsOrg(trx, oldDestGuid, oldSourceGuid);
			Edge createOrgHasAsset = orgDao.orgHasAsset(trx, newSourceGuid, oldDestGuid);
			Edge createAssetBelongsOrg = assetDao.assetBelongsOrg(trx, oldDestGuid, newSourceGuid);

			return (delOrgHasAsset != null && delAssetBelongsOrg != null && createOrgHasAsset != null
					&& createAssetBelongsOrg != null);
		} else if (oldSourceGuid != null && newSourceGuid == null && oldDestGuid != null && newDestGuid != null) {
			Edge delOrgHasAsset = orgDao.deleteOrgHasAsset(trx, oldSourceGuid, oldDestGuid);
			Edge delAssetBelongsOrg = assetDao.deleteAssetBelongsOrg(trx, oldDestGuid, oldSourceGuid);
			Edge createOrgHasAsset = orgDao.orgHasAsset(trx, oldSourceGuid, newDestGuid);
			Edge createAssetBelongsOrg = assetDao.assetBelongsOrg(trx, newDestGuid, oldSourceGuid);

			return (delOrgHasAsset != null && delAssetBelongsOrg != null && createOrgHasAsset != null
					&& createAssetBelongsOrg != null);
		}
		return false;

	}

	@Override
	public <TRX> boolean modifyOrgHasOrg(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid) {

		if (oldSourceGuid != null && newSourceGuid != null && oldDestGuid != null && newDestGuid == null) {
			Edge delOrgHasOrg = orgDao.deleteOrgHasOrg(trx, oldSourceGuid, oldDestGuid);
			Edge delOrgBelongsOrg = orgDao.deleteOrgBelongsOrg(trx, oldDestGuid, oldSourceGuid);
			Edge createOrgHasOrg = orgDao.orgHasOrg(trx,
					getParamMap(BindConstants.SRC, newSourceGuid, BindConstants.DEST, oldDestGuid));
			Edge createOrgBelongsOrg = orgDao.orgBelongsOrg(trx,
					getParamMap(BindConstants.SRC, oldDestGuid, BindConstants.DEST, newSourceGuid));

			return (delOrgHasOrg != null && delOrgBelongsOrg != null && createOrgHasOrg != null
					&& createOrgBelongsOrg != null);
		} else if (oldSourceGuid != null && newSourceGuid == null && oldDestGuid != null && newDestGuid != null) {
			Edge delOrgHasOrg = orgDao.deleteOrgHasOrg(trx, oldSourceGuid, oldDestGuid);
			Edge delOrgBelongsOrg = orgDao.deleteOrgBelongsOrg(trx, oldDestGuid, oldSourceGuid);
			Edge createOrgHasOrg = orgDao.orgHasOrg(trx,
					getParamMap(BindConstants.SRC, oldSourceGuid, BindConstants.DEST, newDestGuid));
			Edge createOrgBelongsOrg = orgDao.orgBelongsOrg(trx,
					getParamMap(BindConstants.SRC, newDestGuid, BindConstants.DEST, oldSourceGuid));

			return (delOrgHasOrg != null && delOrgBelongsOrg != null && createOrgHasOrg != null
					&& createOrgBelongsOrg != null);
		}
		return false;

	}

	@Override
	public <TRX> boolean modifyAssetHasAsset(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid) {

		if (oldSourceGuid != null && newSourceGuid != null && oldDestGuid != null && newDestGuid == null) {
			Edge delAssetHasAsset = assetDao.deleteAssetHasAsset(trx, oldSourceGuid, oldDestGuid);
			Edge delAssetBelongsAsset = assetDao.deleteAssetBelongsAsset(trx, oldDestGuid, oldSourceGuid);
			Edge createAssetHasAsset = assetDao.assetHasAsset(trx, newSourceGuid, oldDestGuid);
			Edge createAssetBelongsAsset = assetDao.assetBelongsAsset(trx, oldDestGuid, newSourceGuid);

			return (delAssetHasAsset != null && delAssetBelongsAsset != null && createAssetHasAsset != null
					&& createAssetBelongsAsset != null);
		} else if (oldSourceGuid != null && newSourceGuid == null && oldDestGuid != null && newDestGuid != null) {
			Edge delAssetHasAsset = assetDao.deleteAssetHasAsset(trx, oldSourceGuid, oldDestGuid);
			Edge delAssetBelongsAsset = assetDao.deleteAssetBelongsAsset(trx, oldDestGuid, oldSourceGuid);
			Edge createAssetHasAsset = assetDao.assetHasAsset(trx, oldSourceGuid, newDestGuid);
			Edge createAssetBelongsAsset = assetDao.assetBelongsAsset(trx, newDestGuid, oldSourceGuid);

			return (delAssetHasAsset != null && delAssetBelongsAsset != null && createAssetHasAsset != null
					&& createAssetBelongsAsset != null);
		}
		return false;

	}

	@Override
	public <TRX> boolean modifyAssetIsClass(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid) {

		if (oldSourceGuid != null && newSourceGuid != null && oldDestGuid != null && newDestGuid == null) {
			Edge delAssetIsClass = assetDao.deleteAssetIsClass(trx, oldSourceGuid, oldDestGuid);
			Edge createAssetIsClass = assetDao.assetIsClass(trx, newSourceGuid, oldDestGuid);
			return (delAssetIsClass != null && createAssetIsClass != null);
		} else if (oldSourceGuid != null && newSourceGuid == null && oldDestGuid != null && newDestGuid != null) {
			Edge delAssetIsClass = assetDao.deleteAssetIsClass(trx, oldSourceGuid, oldDestGuid);
			Edge createAssetIsClass = assetDao.assetIsClass(trx, oldSourceGuid, newDestGuid);
			return (delAssetIsClass != null && createAssetIsClass != null);
		}
		return false;

	}

	@Override
	public <TRX> boolean modifyOrgIsClass(TRX trx, String oldSourceGuid, String newSourceGuid, String oldDestGuid,
			String newDestGuid) {

		if (oldSourceGuid != null && newSourceGuid != null && oldDestGuid != null && newDestGuid == null) {
			Edge delOrgIsClass = orgDao.deleteOrgIsClass(trx, oldSourceGuid, oldDestGuid);
			Edge createOrgIsClass = orgDao.orgIsClass(trx, newSourceGuid, oldDestGuid);
			return (delOrgIsClass != null && createOrgIsClass != null);
		} else if (oldSourceGuid != null && newSourceGuid == null && oldDestGuid != null && newDestGuid != null) {
			Edge delOrgIsClass = orgDao.deleteOrgIsClass(trx, oldSourceGuid, oldDestGuid);
			Edge createOrgIsClass = orgDao.orgIsClass(trx, oldSourceGuid, newDestGuid);
			return (delOrgIsClass != null && createOrgIsClass != null);
		}
		return false;

	}

	/**
	 * 
	 */
	@Override
	public <TRX> boolean deleteNode(TRX trx, String guid, String label) {
		Session session = null;
		try {
			session = graphFactory.writeSession();
			return new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {

					switch (label) {
					case SchemaConstants.LABEL_ORG:
						if (dataProviderService
								.haveChildren(guid, label, SchemaConstants.LABEL_ORG, "OUT",
										SchemaConstants.REL_LABEL_HASA, 1L)
								.isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_ASSET, "OUT",
										SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_CONTRACT, "OUT",
										SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							orgDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_CLASS:
						if (dataProviderService
								.haveChildren(guid, label, SchemaConstants.LABEL_CLASS, "IN",
										SchemaConstants.REL_LABEL_IS, 1L)
								.isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_ASSET, "IN",
										SchemaConstants.REL_LABEL_IS, 1L).isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_DATA_ITEM, "OUT",
										SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_EVENTS, "OUT",
										SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_PRODUCT_CLASS,
										"IN", SchemaConstants.REL_LABEL_IS, 1L).isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_ORG, "IN",
										SchemaConstants.REL_LABEL_IS, 1L).isEmpty()) {
							classDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_ASSET:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_ASSET, "OUT",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							assetDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_PRODUCT_CLASS:
						if (dataProviderService
								.haveChildren(guid, label, SchemaConstants.LABEL_CLASS, "IN",
										SchemaConstants.REL_LABEL_IS, 1L)
								.isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_ASSET, "IN",
										SchemaConstants.REL_LABEL_IS, 1L).isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_DATA_ITEM, "OUT",
										SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_EVENTS, "OUT",
										SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_PRODUCT_CLASS,
										"IN", SchemaConstants.REL_LABEL_IS, 1L).isEmpty()
								&& dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_CONTRACT_TYPE,
										"IN", SchemaConstants.REL_LABEL_IS, 1L).isEmpty()) {
							orgDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_DATA_ITEM:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_TERM_DATA, "IN",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							dataItemDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_EVENTS:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_TERM_EVENT, "IN",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							eventsDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_TERM_DATA:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_DATA_ITEM, "OUT",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							termDataDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_TERM_EVENT:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_EVENTS, "OUT",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							termEventDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_TERM_EVENT_TYPE:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_EVENT_TYPE, "OUT",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							termEventTypeDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_TERM_MASHUP:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_MASHUP, "OUT",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							termMashupDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_TERM_SERVICE:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_SCRIPT_TEMPLATE, "OUT",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							termServiceDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_TERM_ACTION:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_ACTION, "OUT",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							termActionDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_TERM_ACTION_TYPE:
						if (dataProviderService.haveChildren(guid, label, SchemaConstants.LABEL_NOTIFICATION, "OUT",
								SchemaConstants.REL_LABEL_HASA, 1L).isEmpty()) {
							termActionTypeDao.deleteNode(trx, guid);
							return true;
						}
						break;
					case SchemaConstants.LABEL_CONFIG_ITEM:
						configItemDao.deleteNode(trx, guid);
						return true;

					default:
						LOGGER.info("provide appropriate label(org/class/asset) for guid:{},label:{}", guid, label);
						break;
					}

					return false;

				}
			}.execute();
		} catch (Exception e) {
			LOGGER.error("error occured in deleteNode,guid:{},label:{},error:{}", guid, label, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return false;

	}

	@Override
	public Boolean addLicenceTerm(String contractTypeGuid, Vertex ctDerivedClass, String appGuid,
			String parentContractTypeGuid, Map<String, Object> propertyValueMap, String name, String contractGuid) {
		Session session = graphFactory.writeSession();
		try {
			return new TrxHandler<Boolean>(session) {
				@Override
				public Boolean block(Transaction transaction) {
					Vertex contractType = saveUpdateNode(SchemaConstants.LABEL_CONTRACT_TYPE, propertyValueMap);

					contractTypeDao.contractTypeIsDerivedClass(transaction, ctDerivedClass.getLabel(), getParamMap(
							BindConstants.SRC, ctDerivedClass.getGuid(), BindConstants.DEST, contractTypeGuid));

					appDao.appHasContractType(transaction,
							getParamMap(BindConstants.SRC, appGuid, BindConstants.DEST, contractTypeGuid));
					contractTypeDao.contractTypeIsContractType(transaction, getParamMap(BindConstants.SRC,
							contractTypeGuid, BindConstants.DEST, parentContractTypeGuid));
					return true;
				}
			}.execute();

		} catch (Exception e) {
			LOGGER.error(
					"error occured in error occured in addContractType with guid:{},appid:{},contractid:{},parentContractTypeid:{}",
					contractTypeGuid, appGuid, contractGuid, parentContractTypeGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return false;
	}

	@Override
	public Results<JsonObject> removeNode(String nodeGuid, String label) {
		Session session = null;
		JsonObject output = null;
		try {
			session = graphFactory.writeSession();

			output = new TrxHandler<JsonObject>(session) {
				@Override
				public JsonObject block(Transaction transaction) {
					JsonObject output = new JsonObject();
					JsonObject dsInfo = dataProviderService.getDataShardInfo(label, nodeGuid);
					JsonObject dsObject = new JsonObject();

					String dsGuid = (dsInfo != null ? dsInfo.getString(JsonConstants.SOURCE_GUID)
							: SchemaConstants.ROOT_DS);

					Vertex ds = dataProviderService.getVertex(Optional.of(SchemaConstants.LABEL_DATA_SHARD),
							SchemaConstants.PROP_HDMFID_NAME, dsGuid);
					dsObject.put(SchemaConstants.PROP_IP, (String) ds.getNode().get(SchemaConstants.PROP_IP));
					dsObject.put(SchemaConstants.PROP_PORT,
							Long.valueOf((String) ds.getNode().get(SchemaConstants.PROP_PORT)));
					output.put(JsonConstants.DS, dsObject);

					boolean deleted = deleteNode(transaction, nodeGuid, label);
					if (deleted) {
						output.put(JsonConstants.SUCCESS, true);
					} else {
						output.put(JsonConstants.SUCCESS, false);
					}
					return output;
				}
			}.execute();

		} catch (Exception e) {
			LOGGER.error("error occured in removeNode,nodeGuid:{},,label:{}error:{}", nodeGuid, label, e);
			return new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return (output != null && output.getBoolean(JsonConstants.SUCCESS).equals(true)) ? new Results<>(output)
				: new Results<>(ErrorCodes.FAILED);
	}

	@Override
	public Results<Boolean> deleteLinks(String userId, String appId, org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {
		try {

			src.setLabel(JsonToDB.get(src.getLabel()));
			dest.setLabel(JsonToDB.get(dest.getLabel()));

			Vertex srcVertx = dataProviderService.getAuthorizedVertex(appId, userId, src.getGuid(),
					Optional.of(src.getLabel()));
			if (srcVertx == null || srcVertx.getNode().get(SchemaConstants.PROTECTED) != null) {
				return new Results<>(false, ErrorCodes.NOT_AUTHORISED);
			}
			Vertex destVertx = dataProviderService.getAuthorizedVertex(appId, userId, dest.getGuid(),
					Optional.of(dest.getLabel()));
			if (destVertx == null || destVertx.getNode().get(SchemaConstants.PROTECTED) != null) {
				return new Results<>(false, ErrorCodes.NOT_AUTHORISED);
			}

			switch (src.getLabel()) {
			case SchemaConstants.LABEL_ORG:
				super.orgUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_CLASS:
				super.classUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_ASSET:
				super.assetUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_PRODUCT_CLASS:
				super.productClassUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_CONTRACT:
				super.contractUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_PRINCIPAL:
				super.principalUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_ADMIN_UNIT:
				super.adminUnitUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_SCRIPT:
				super.scriptUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_SCRIPT_TEMPLATE:
				super.scriptTemplateUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_DATA:
				super.termDataUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_EVENT_TYPE:
				super.termEventTypeUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_SERVICE:
				super.termServiceUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_EVENT:
				super.termEventUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_ACTION:
				super.termActionUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_ACTION_TYPE:
				super.termActionTypeUnlinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_MASHUP:
				super.termMashupUnlinkHandler(src, dest);
				break;
			default:
				LOGGER.error("source label not valid:{}..!!", dest.getLabel());
				break;
			}

		} catch (Exception e) {
			LOGGER.error(
					"error occured in deletelinks,userID:{},appID:{},srcLabel:{},destLabel:{},srcGuid{},destGuid:{},error:{}",
					userId, appId, src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		}
		return new Results<>(false);

	}

	@Override
	public Results<Boolean> createLinks(String userId, String appId, org.digi.lg.neo4j.core.ModifyNode.Node src,
			org.digi.lg.neo4j.core.ModifyNode.Node dest) {

		try {

			src.setLabel(JsonToDB.get(src.getLabel()));
			dest.setLabel(JsonToDB.get(dest.getLabel()));

			Vertex srcVertx = dataProviderService.getAuthorizedVertex(appId, userId, src.getGuid(),
					Optional.of(src.getLabel()));
			if (srcVertx == null || srcVertx.getNode().get(SchemaConstants.PROTECTED) != null) {
				return new Results<>(false, ErrorCodes.NOT_AUTHORISED);
			}
			Vertex destVertx = dataProviderService.getAuthorizedVertex(appId, userId, dest.getGuid(),
					Optional.of(dest.getLabel()));
			if (destVertx == null || destVertx.getNode().get(SchemaConstants.PROTECTED) != null) {
				return new Results<>(false, ErrorCodes.NOT_AUTHORISED);
			}

			switch (src.getLabel()) {
			case SchemaConstants.LABEL_ORG:
				super.orgLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_CLASS:
				super.classLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_ASSET:
				super.assetLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_PRODUCT_CLASS:
				super.productClassLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_CONTRACT:
				super.contractLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_ADMIN_UNIT:
				super.adminUnitLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_SCRIPT:
				super.scriptLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_SCRIPT_TEMPLATE:
				super.scriptTemplateLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_DATA:
				super.termDataLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_EVENT_TYPE:
				super.termEventTypeLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_SERVICE:
				super.termServiceLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_EVENT:
				super.termEventLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_ACTION:
				super.termActionLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_ACTION_TYPE:
				super.termActionTypeLinkHandler(src, dest);
				break;
			case SchemaConstants.LABEL_TERM_MASHUP:
				super.termMashupLinkHandler(src, dest);
				break;
			default:
				LOGGER.error("source label not valid:{}..!!", dest.getLabel());
				break;
			}

		} catch (Exception e) {
			LOGGER.error(
					"error occured in deletelinks,userID:{},appID:{},srcLabel:{},destLabel:{},srcGuid{},destGuid:{},error:{}",
					userId, appId, src.getLabel(), dest.getLabel(), src.getGuid(), dest.getGuid(), e);
		}
		return new Results<>(false);

	}

	@Override
	public Results<RegisterAgentMetricAsset> registerMetricGateway(RegisterAgentMetricAsset message) {
		Session session = null;
		StopWatch watch = Performance.startWatch(PerfConstants.REGISTER_AGENT_ASSET_DB);
		RegisterAgentMetricAsset response = null;
		try {
			session = graphFactory.writeSession();
			response = registerMetricAssetFacade(session, message);
		} catch (Exception e) {
			LOGGER.error("error occured in registerMetricAssetFacade", e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return response != null ? new Results<>(response) : new Results<>(ErrorCodes.FAILED);
	}

	protected RegisterAgentMetricAsset registerMetricAssetFacade(Session session, RegisterAgentMetricAsset message) {
		RegisterAgentMetricAsset response = null;

		response = preRegisteredMetricAsset(session, message);

		return response;
	}

	private RegisterAgentMetricAsset preRegisteredMetricAsset(Session session, RegisterAgentMetricAsset message) {
		List<org.digi.lg.neo4j.pojo.services.RegisterAgentMetricAsset.MetricAsset> assetList = new ArrayList<>();
		List<Object> ItemArray = new ArrayList<>();
		message.getAssets().forEach(assetInfo -> {
			LOGGER.debug("assetInfo.getBootStrapKey().!!", assetInfo.getBootStrapKey());

			Org bootStrapOrg = orgDao.getOrgBootStrapKey(session, assetInfo.getBootStrapKey());
			if (bootStrapOrg == null) {
				LOGGER.debug("bootStrapKey not found for Org:{}..!!" + assetInfo.getBootStrapKey());
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

				List<Vertex> outVertices = classDao.getClasssAndProductClasses(session,
						SchemaConstants.LABEL_PRODUCT_CLASS, SchemaConstants.PROP_HDMFID_NAME, productClass.getGuid(),
						Relationship.IS, Direction.BOTH);

				outVertices.forEach(vertex -> {
					Map<String, Object> productClassObj = new HashMap<>(3);
					Map<String, String> productMap = new HashMap<>(1);
					productMap.put(JsonConstants.NAME, vertex.getName());
					productMap.put(JsonConstants.GUID, vertex.getGuid());

					List<DataItem> dataItems = getDataProviderServiceInstance()
							.getDataItemsfromProductClass(vertex.getGuid());
					List<Asset> assetResult = assetDao.getAssetByProductClassGuid(session, vertex.getGuid());
					List<Map<String, String>> assetResultList = new ArrayList<>();

					List<Map<String, String>> dataItemsList = new ArrayList<>();
					dataItems.forEach(divertex -> {
						Map<String, String> dataItemMap = new HashMap<>(5);
						dataItemMap.put(JsonConstants.GUID, divertex.getGuid());
						dataItemMap.put(JsonConstants.NAME, divertex.getName());
						dataItemMap.put(JsonConstants.ALIAS, divertex.getAlias());
						dataItemMap.put(JsonConstants.TYPE, divertex.getType());
						dataItemMap.put(JsonConstants.UNIT_OF_MEASUREMENT, divertex.getUnitOfMeasurement());
						dataItemMap.put(JsonConstants.MAXIMUM_VALUE, divertex.getMaximunValue());

						dataItemsList.add(dataItemMap);
					});
					assetResult.forEach(assetvertex -> {
						Map<String, String> assetMap = new HashMap<>(8);
						assetMap.put(JsonConstants.GUID, assetvertex.getGuid());
						assetMap.put(JsonConstants.NAME, assetvertex.getName());
						assetMap.put(JsonConstants.SERIAL_NUMBER, assetvertex.getSerialNumber());
						assetMap.put(JsonConstants.MAX_ASSET_COUNT, assetvertex.getMaxAssetCount());
						assetMap.put(JsonConstants.CURRENT_ASSET_COUNT, assetvertex.getCurrentAssetCount());

						assetResultList.add(assetMap);
					});
					productClassObj.put(JsonConstants.PRODUCT_CLASS, productMap);
					productClassObj.put(JsonConstants.DATA_ITEM, dataItemsList);
					productClassObj.put(JsonConstants.ASSET, assetResultList);

					ItemArray.add(productClassObj);
				});

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

			org.digi.lg.neo4j.pojo.services.RegisterAgentMetricAsset.MetricAsset asset = new org.digi.lg.neo4j.pojo.services.RegisterAgentMetricAsset.MetricAsset(
					null, assetFound.getSerialNumber(), assetFound.getGuid(), assetFound.getName(), classes, product,
					new org.digi.lg.neo4j.pojo.services.RegisterAgentMetricAsset.DataShard(ds.getName(), ds.getGuid(),
							ds.getIP(), ds.getPort(), ds.getMsgGateway()),
					new org.digi.lg.neo4j.pojo.services.RegisterAgentMetricAsset.Org(directlyConnectedOrg.getName(),
							directlyConnectedOrg.getName()),
					ItemArray);

			try {
				asset.setAccessKey(KeyGenerator.generateKey(asset.getGuid()));

			} catch (Exception e) {
				LOGGER.error("error occured while generating key,error:{}", ExceptionUtils.getMessage(e));
			}
			assetList.add(asset);

		});

		return new RegisterAgentMetricAsset(assetList);
	}

}
