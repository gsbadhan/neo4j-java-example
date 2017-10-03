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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.digi.lg.neo4j.core.GraphInstanceManager.getAuthorizationServiceInstance;
import static org.digi.lg.neo4j.core.GraphInstanceManager.getDataProviderServiceInstance;
import static org.digi.lg.neo4j.core.ProcedureConstants.DEPTH;
import static org.digi.lg.neo4j.core.ProcedureConstants.DEST_LABEL;
import static org.digi.lg.neo4j.core.ProcedureConstants.DIRECTION;
import static org.digi.lg.neo4j.core.ProcedureConstants.EDGE_LABEL;
import static org.digi.lg.neo4j.core.ProcedureConstants.GUID;
import static org.digi.lg.neo4j.core.ProcedureConstants.SRC_LABEL;
import static org.digi.lg.neo4j.dao.DaoUtil.getMandatoryVertex;
import static org.digi.lg.neo4j.dao.DaoUtil.getParamMap;
import static org.digi.lg.neo4j.dao.DaoUtil.getStr;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.pojo.services.JsonDaoMapper.getDbToJson;
import static org.digi.lg.neo4j.pojo.services.JsonDaoMapper.getJsonFromDataShard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.DBToJson;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.ErrorCodes;
import org.digi.lg.neo4j.core.JsonConstants;
import org.digi.lg.neo4j.core.ProcedureConstants;
import org.digi.lg.neo4j.core.Properties;
import org.digi.lg.neo4j.core.Properties.PropNameSuffix;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.ScriptTemplateScope;
import org.digi.lg.neo4j.core.ValueConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.dao.ExpandNodeProcess;
import org.digi.lg.neo4j.dao.FindAssetByPropertyProcess;
import org.digi.lg.neo4j.dao.FindNodeByPropertyProcess;
import org.digi.lg.neo4j.pojo.model.Asset;
import org.digi.lg.neo4j.pojo.model.ClassX;
import org.digi.lg.neo4j.pojo.model.Contract;
import org.digi.lg.neo4j.pojo.model.ContractType;
import org.digi.lg.neo4j.pojo.model.DataItem;
import org.digi.lg.neo4j.pojo.model.DataShard;
import org.digi.lg.neo4j.pojo.model.Events;
import org.digi.lg.neo4j.pojo.model.Mashup;
import org.digi.lg.neo4j.pojo.model.Org;
import org.digi.lg.neo4j.pojo.model.Principal;
import org.digi.lg.neo4j.pojo.model.ProductClass;
import org.digi.lg.neo4j.pojo.model.Script;
import org.digi.lg.neo4j.pojo.model.ScriptTemplate;
import org.digi.lg.neo4j.pojo.services.AssetPojo;
import org.digi.lg.neo4j.pojo.services.ClassList;
import org.digi.lg.neo4j.pojo.services.ContractVertex;
import org.digi.lg.neo4j.pojo.services.DataShardPojo;
import org.digi.lg.neo4j.pojo.services.JsonDaoMapper;
import org.digi.lg.neo4j.pojo.services.LinkPojo;
import org.digi.lg.neo4j.pojo.services.NeighbourPojo;
import org.digi.lg.neo4j.pojo.services.Node;
import org.digi.lg.neo4j.pojo.services.Results;
import org.digi.lg.neo4j.queries.ProceduresQuery;
import org.digi.lg.neo4j.queries.QueryLoader;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.lg.neo4j.common.ExploreNeighbourTask;
import com.digi.lg.neo4j.perf.PerfConstants;
import com.digi.lg.neo4j.perf.Performance;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DataProviderServiceImpl extends ServiceProvider implements DataProviderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataProviderServiceImpl.class);
	private final AuthorizationService authorizationService;
	private final ProceduresQuery proceduresQuery;

	public DataProviderServiceImpl(final AuthorizationService authorizationService) {
		this.authorizationService = checkNotNull(authorizationService);
		this.proceduresQuery = QueryLoader.proceduresQuery();
	}

	@Override
	public ContractVertex getContractInfo(String principalId, String appId) {
		return personAppContractCache.get(principalId, appId);
	}

	@Override
	public Results<List<Node<Object>>> findNodeByProperty(String appId, String userId, FindProperty property,
			String propertyValue, Optional<Integer> pageOffset) {
		StopWatch watch = Performance.startWatch(PerfConstants.FIND_NODE_DB);
		try {
			int pageSize = pageOffset.orElse(0);
			List<Node<Object>> data = FindNodeByPropertyProcess.get(authorizationService, appId, userId, property,
					propertyValue, pageSize);
			if (data == null || data.isEmpty())
				return new Results<>(ErrorCodes.NOT_FOUND);

			return new Results<>(data, pageSize + data.size());
		} finally {
			Performance.stopWatch(watch);
		}
	}

	@Override
	public Results<List<NeighbourPojo>> expandNodeV2(String appId, String userId, Optional<String> guidLabel,
			String guid, boolean onlyAssets, Optional<Integer> pageOffset, boolean acrossDomain) {
		int pageSize = pageOffset.orElse(0);
		List<NeighbourPojo> data = ExpandNodeProcess.get(this, authorizationService, appId, userId, guidLabel, guid,
				pageSize, acrossDomain);
		if (data == null || data.isEmpty())
			return new Results<>(data, -1);

		return new Results<>(data, pageSize + data.size());
	}

	@Override
	public Map<String, Object> expandNode(String appId, String userId, Optional<String> guidLabel, String guid,
			boolean onlyAssets, final Optional<Integer> pageOffset, boolean acrossDomain) {
		StopWatch watch = Performance.startWatch(PerfConstants.EXPAND_DB);
		Map<String, Node<Object>> nodes = null;
		List<LinkPojo> links = null;
		Map<String, Object> nodesMap = Maps.newConcurrentMap();
		Set<Vertex> authorizeVerticesSet = Sets.newConcurrentHashSet();
		try {
			ContractVertex contractVertex = getContractInfo(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("expand: contractInfo not found for userId{},appId{}..!!", userId, appId);
				return null;
			}
			ClassX classX = guidCache.getClassX(guidLabel.orElse(null), guid);
			if (classX == null) {
				LOGGER.info("expand: data not found for guid{}..!!", guid);
				return null;
			}
			Vertex guidVertex = classX.getVertex();
			List<Vertex> authorizeVertex = authorizationService.isAuthorized(userId, appId, contractVertex, guidVertex,
					acrossDomain);
			if (authorizeVertex.isEmpty())
				return nodesMap;

			authorizeVertex.forEach(authorizeVerticesSet::add);

			nodes = Maps.newConcurrentMap();
			links = Lists.newCopyOnWriteArrayList();

			nodes.put(guid, getNode(guidVertex, true));

			// explore IN vertices
			Vertex vertices = classDao.getInVertex(null,
					(guidLabel.isPresent() ? guidLabel.get() : guidVertex.getLabel()), SchemaConstants.PROP_HDMFID_NAME,
					guid, null, Optional.empty(), false);
			if (vertices.getInVertices() != null) {
				Set<Vertex> guidInVertexSet = Sets.newConcurrentHashSet(vertices.getInVertices());
				exploreVertexNeighbour(appId, userId, contractVertex, nodesMap, authorizeVerticesSet, guidInVertexSet,
						guidVertex, nodes, links, Direction.IN, acrossDomain);
			}
			// explore OUT vertices
			vertices = classDao.getOutVertex(null, (guidLabel.isPresent() ? guidLabel.get() : guidVertex.getLabel()),
					SchemaConstants.PROP_HDMFID_NAME, guid, null, Optional.empty(), false);
			if (vertices.getOutVertices() != null) {
				Set<Vertex> guidOutVertexSet = Sets.newConcurrentHashSet(vertices.getOutVertices());
				exploreVertexNeighbour(appId, userId, contractVertex, nodesMap, authorizeVerticesSet, guidOutVertexSet,
						guidVertex, nodes, links, Direction.OUT, acrossDomain);
			}
		} catch (Exception e) {
			LOGGER.error("error occured expandNode: guidLabel:{},appid:{},userid:{},error:{}", guidLabel, appId, userId,
					e);
		} finally {
			Performance.stopWatch(watch);
		}
		return nodesMap;
	}

	private void exploreVertexNeighbour(String appId, String userId, ContractVertex contractVertex,
			Map<String, Object> nodesMap, Set<Vertex> authorizeVerticesSet, Set<Vertex> guidOutVertexSet,
			Vertex guidVertex, Map<String, Node<Object>> nodes, List<LinkPojo> links, Direction direction,
			boolean acrossDomain) {

		guidOutVertexSet.forEach(neighbour -> {
			ExploreNeighbourTask task = new ExploreNeighbourTask(this, authorizationService, userId, appId,
					contractVertex, neighbour, authorizeVerticesSet, nodesMap, guidVertex, nodes, links, direction,
					acrossDomain);
			try {
				task.call();
			} catch (Exception e) {
				LOGGER.error("error occured in exploreVertexNeighbour for userId:{},guid:{},error:{}", userId, appId,
						e);
			}
		});
	}

	@Override
	public Vertex getVertex(final Optional<String> label, final String propertyName, final String propertyValue) {
		Session session = null;
		try {
			session = graphFactory.readSession();
			if (label.isPresent()) {
				return classDao.getVertex(session, Vertex.class, label.get(), propertyName, propertyValue);
			} else {
				return classDao.getByProperty(session, Vertex.class, propertyName, propertyValue);
			}
		} catch (Exception e) {
			LOGGER.error("error occured in getVertex label:{},propertyName:{},propertyValue:{},error:{}", label,
					propertyName, propertyValue, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return null;
	}

	@Override
	public Node<Object> getNode(Vertex vertex, boolean isTypeRequired) {
		Node<Object> node = new Node<>();
		Session session = null;
		try {
			if (isTypeRequired)
				session = graphFactory.readSession();
			vertex.getNode().forEach((property, value) -> {
				if (property.equals(SchemaConstants.PROP_DB_UUID))
					return;

				if (property.equals(SchemaConstants.PROP_CATEGORY) && !StringUtils.isEmpty(value.toString())) {
					node.put(SchemaConstants.PROP_LABEL, DBToJson.get(value.toString()));
				} else {
					node.put(SchemaConstants.PROP_LABEL, DBToJson.get(vertex.getLabel()));
				}

				if (property.startsWith(SchemaConstants.MANDATORY_PROP_SYMBOL)) {
					node.put(DBToJson.get(property.substring(1)), value);
				} else if (vertex.getLabel().equals(SchemaConstants.LABEL_CONTRACT_TYPE)) {
					if (!property.equals(SchemaConstants.PROP_AUTH_TOKEN)) {
						node.put(DBToJson.get(property), value);
					}
				} else if (vertex.getLabel().equals(SchemaConstants.LABEL_ORG)) {
					if (!property.equals(SchemaConstants.PROP_BOOT_STRAP_KEY)) {
						node.put(DBToJson.get(property), value);
					}
				} else {
					if (!property.equals(SchemaConstants.PROP_CATEGORY)
							&& !property.startsWith(SchemaConstants.MANDATORY_PROP_SYMBOL)) {
						node.put(DBToJson.get(property), value);
					}
				}

			});

			if (session != null)
				node.put(DBToJson.get(SchemaConstants.PROP_TYPE), getVertexTypes(session, vertex));
		} catch (Exception e) {
			LOGGER.error("error occured in getNode", e);
		} finally {
			graphFactory.closeSession(session);
		}
		return node;
	}

	@Override
	public Vertex getAuthorizedVertex(String appId, String userId, String guid, boolean searchInSameDomain) {
		try {
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("Unathorized Access for guid:{}..!!", guid);
				return null;
			}

			ClassX classX = guidCache.getClassX(null, guid);
			if (classX == null) {
				LOGGER.info("vertex not found for guid:{}..!!", guid);
				return null;
			}
			List<Vertex> authorizedVertex = authorizationService.isAuthorized(userId, appId, contractVertex,
					classX.getVertex(), searchInSameDomain);
			if (authorizedVertex.isEmpty()) {
				LOGGER.info("vertex not authorized for guid:{}..!!", guid);
				return null;
			}
			return authorizedVertex.get(0);
		} catch (Exception e) {
			LOGGER.error("error occured in getAuthorizedVertex for guid:{},error:{}", guid, e);
		}
		return null;
	}

	@Override
	public Vertex getAuthorizedVertex(final String appId, final String userId, final String guid,
			final Optional<String> guidLabel) {
		try {
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("Unathorized Access for guid:{}..!!", guid);
				return null;
			}

			ClassX classX = guidCache.getClassX(guidLabel.orElse(null), guid);
			if (classX == null) {
				LOGGER.info("vertex not found for guid:{}..!!", guid);
				return null;
			}
			List<Vertex> authorizedVertex = authorizationService.isAuthorized(userId, appId, contractVertex,
					classX.getVertex(), true);
			if (authorizedVertex.isEmpty()) {
				LOGGER.info("vertex not authorized for guid:{}..!!", guid);
				return null;
			}
			return authorizedVertex.get(0);
		} catch (Exception e) {
			LOGGER.error("error occured in getAuthorizedVertex for guid:{},error:{}", guid, e);
		}
		return null;
	}

	@Override
	public Node<Object> getAuthorizedNode(String appId, String userId, String guid, boolean searchInSameDomain) {
		Vertex authorizedVertex = getAuthorizedVertex(appId, userId, guid, searchInSameDomain);
		return (authorizedVertex != null ? getNode(authorizedVertex, true) : null);
	}

	@Override
	public JsonObject getDataShardInfo(Vertex vertex) {
		return getDataShardInfo(vertex.getLabel(), vertex.getGuid());
	}

	@Override
	public JsonObject getDataShardInfo(String label, String guid) {
		try {
			List<DataShard> dataShards = dataShardDao.getDataShard(null, label, guid, Direction.OUT);
			if (dataShards == null || dataShards.isEmpty())
				return null;
			// taking one,if there is multiple enteries
			return getJsonFromDataShard(dataShards.get(0).getVertex());
		} catch (Exception e) {
			LOGGER.error("error occured in getDataShardInfo guid:{},label:{},error:{}", guid, label, e);
		}
		return null;
	}

	@Override
	public boolean isOrgExist(final String parentLabel, final String parentPropLabel, final String parentPropGuid,
			final String childType, final String childPropLabel, final String childName, final Relationship relType,
			Direction direction) {
		Session session = graphFactory.readSession();
		try {
			return orgDao.isNodeConnected(session, parentLabel, parentPropLabel, parentPropGuid, childType,
					childPropLabel, childName, relType, Direction.OUT);
		} catch (Exception e) {
			LOGGER.error(
					"error occured in isOrgExist parentLabel:{}, parentPropLabel:{},parentPropGuid:{},childType,childPropLabel:{},childName:{},relType:{}, Direction:{}",
					parentLabel, parentPropLabel, parentPropGuid, childType, childPropLabel, childName, relType,
					Direction.OUT, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return false;
	}

	@Override
	public boolean isExist(final String parentLabel, final String parentPropLabel, final String parentPropGuid,
			final String childType, final String childPropLabel, final String childName, final Relationship relType,
			Direction direction) {
		Session session = graphFactory.readSession();
		try {
			return classDao.isNodeConnected(session, parentLabel, parentPropLabel, parentPropGuid, childType,
					childPropLabel, childName, relType, direction);
		} catch (Exception e) {
			LOGGER.error(
					"error occured as name already exists in child chain of the source parentLabel:{}, parentPropLabel:{},parentPropGuid:{},childType,childPropLabel:{},childName:{},relType:{}, Direction:{}",
					parentLabel, parentPropLabel, parentPropGuid, childType, childPropLabel, childName, relType,
					Direction.OUT, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return false;
	}

	@Override
	public boolean isClassExist(String label, String name) {
		Session session = graphFactory.readSession();
		try {
			ClassX classx = classDao.getByName(session, label, name);
			if (classx != null)
				return true;

		} catch (Exception e) {
			LOGGER.error("error occured in isClassExist label:{},name:{},error:{}", label, name, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return false;
	}

	@Override
	public JsonObject getParent(String userId, String appId, Optional<Integer> pageOffset) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_PARENT_DB);
		Session session1 = graphFactory.readSession();
		Session session2 = graphFactory.readSession();
		ExecutorService executorService = null;
		JsonObject parentData = new JsonObject();
		try {
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null)
				return parentData;

			executorService = Executors.newWorkStealingPool();
			List<Callable<Void>> tasks = new ArrayList<>(2);
			Map<Integer, List<Vertex>> tasksMap = Maps.newConcurrentMap();
			tasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					List<Vertex> list = contractTypeDao.getContractTypeInHasClassVertices(session1,
							getParamMap(BindConstants.PROP_HDMFID_NAME, contractVertex.getContractType().getGuid()));
					tasksMap.put(1, list);
					return null;
				}
			});
			tasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					List<Vertex> list = contractDao.getContractInHasOrgVertices(session2,
							getParamMap(BindConstants.PROP_HDMFID_NAME, contractVertex.getContract().getGuid()));
					tasksMap.put(2, list);
					return null;
				}
			});
			executorService.invokeAll(tasks).stream().map(f -> {
				try {
					return f.get();
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.error("error occured in getParent, userId:{},appId:{},error:{}", userId, appId, e);
				}
				return null;
			});

			JsonArray classArray = new JsonArray();
			tasksMap.get(1).forEach(vertx -> {
				Map<String, String> classGuid = new HashMap<>(2);
				classGuid.put(JsonConstants.GUID, vertx.getGuid());
				classGuid.put(JsonConstants.NAME, vertx.getName());
				classArray.add(classGuid);
			});
			parentData.put(JsonConstants.CLASS, classArray);

			JsonArray orgArray = new JsonArray();
			tasksMap.get(2).forEach(vertx -> {
				Map<String, String> orgGuid = new HashMap<>(2);
				orgGuid.put(JsonConstants.GUID, vertx.getGuid());
				orgGuid.put(JsonConstants.NAME, vertx.getName());
				orgArray.add(orgGuid);
			});
			parentData.put(JsonConstants.ORG, orgArray);
		} catch (Exception e) {
			LOGGER.error("error occured in getParent, userId:{},appId:{},error:{}", userId, appId, e);
		} finally {
			graphFactory.closeSession(session1);
			graphFactory.closeSession(session2);
			if (executorService != null)
				executorService.shutdown();
			Performance.stopWatch(watch);
		}
		return parentData;
	}

	@Override
	public Node<Object> getClasses(String userId, String appId, String guid, Optional<String> label,
			Optional<Integer> pageOffset) {
		Session session = null;
		String srcLabel = null;
		Node<Object> results = new Node<>();
		Set<Node<Object>> childs = new HashSet<>();
		StopWatch watch = Performance.startWatch(PerfConstants.GET_CLASSES_DB);
		try {
			session = graphFactory.readSession();
			if (label.isPresent()) {
				srcLabel = label.get();
			} else {
				srcLabel = commonDao.getLabel(session, guid);
			}
			ClassX parentClass = classDao.getByGuid(session, srcLabel, guid);
			if (parentClass == null)
				return results;
			results.put(JsonConstants.PARENT, getNode(parentClass.getVertex(), true));

			List<Vertex> outVertices = null;
			if (parentClass.getName().contains(Properties.PropNameSuffix._CLASSES.getValue())
					|| (parentClass.getType() != null
							&& parentClass.getType().contains(Properties.PropTypeValue._CLASSES.getValue()))) {
				outVertices = classDao.getClasssAndProductClasses(session, srcLabel, SchemaConstants.PROP_HDMFID_NAME,
						guid, Relationship.IS, Direction.IN);
			} else {
				outVertices = classDao.getClasssAndProductClasses(session, srcLabel, SchemaConstants.PROP_HDMFID_NAME,
						guid, Relationship.IS, Direction.OUT);
			}

			outVertices.forEach(vertx -> {
				ContractVertex contractVertex = personAppContractCache.get(userId, appId);
				if (contractVertex == null)
					return;
				List<Vertex> authorizeVertx = null;
				if (SchemaConstants.LABEL_CLASS.equals(vertx.getLabel())) {
					authorizeVertx = authorizationService.authorizeClass(contractVertex.getContractType(), vertx,
							false);
				} else if (SchemaConstants.LABEL_PRODUCT_CLASS.equals(vertx.getLabel())) {
					authorizeVertx = authorizationService.authorizeProductClass(contractVertex.getContractType(), vertx,
							false);
				}
				if (authorizeVertx != null && !authorizeVertx.isEmpty()) {
					childs.add(getNode(vertx, true));
				}
			});
			results.put(JsonConstants.NODES, childs);
		} catch (Exception e) {
			LOGGER.error("error occured in getClasses, userId:{},appId:{},guid:{},label:{},error:{}", userId, appId,
					guid, srcLabel, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return results;
	}

	@Override
	public Results<List<Node>> getAssetByOrg(String userId, String appId, String orgGuid, Optional<String> label,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_ASSET_BY_ORG_DB);
		List<Node> result = new ArrayList<>();

		if (pageOffset.isPresent() && pageOffset.get() < 0) {
			LOGGER.info("invalid pageOffset, userId{},appId{},orgGuid{}..!!", userId, appId, orgGuid);
			return new Results<>(ErrorCodes.INVALID_PARAMETER);
		}
		int pageSize = pageOffset.orElse(0);

		try {
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("contract-info not found,userId{},appId{},orgGuid{}..!!", userId, appId, orgGuid);
				return new Results<>(ErrorCodes.NOT_AUTHORISED);
			}
			Map<String, Object> params = new HashMap<>(7);
			params.put(ProcedureConstants.USER_ID, userId);
			params.put(ProcedureConstants.APP_ID, appId);
			params.put(ProcedureConstants.CONTRACT_TYPE_ID, contractVertex.getContractType().getGuid());
			params.put(ProcedureConstants.CONTRACT_ID, contractVertex.getContract().getGuid());
			params.put(ProcedureConstants.ORG_VERTEX_ID, orgGuid);
			params.put(ProcedureConstants.FILTERS, filters);
			params.put(ProcedureConstants.PAGE_OFFSET, pageSize);
			List<Vertex> assetVertices = procedureDao.getVertices(proceduresQuery.getAssetByOrg(), params);
			assetVertices.forEach(assetVertx -> result.add(getNode(assetVertx, true)));
		} catch (Exception e) {
			LOGGER.error("error occured in getAssetByOrg: userId{},appId{},orgGuid{},error:{}..!!", userId, appId,
					orgGuid, e);
		} finally {
			Performance.stopWatch(watch);
		}
		return !result.isEmpty() ? new Results<>(result, (pageSize + result.size()))
				: new Results<>(ErrorCodes.NOT_FOUND);
	}

	@Override
	public Node<Object> getDataItem(String userId, String appId, String classGuid, Optional<String> label,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_DATA_ITEM_DB);
		Node<Object> result = new Node<>();
		Set<Node<Object>> dataItem = new HashSet<>();

		if (pageOffset.isPresent() && pageOffset.get() < 0) {
			LOGGER.info("invalid pageOffset, userId{},appId{},orgGuid{}..!!", userId, appId, classGuid);
			return result;
		}
		int pageSize = pageOffset.orElse(0);

		try {
			String classLabel = label.orElse(SchemaConstants.LABEL_PRODUCT_CLASS);
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("contract-info not found,userId{},appId{},orgGuid{}..!!", userId, appId, classGuid);
				return result;
			}
			if (pageSize == 0) {
				ClassX parentClass = guidCache.getClassX(classLabel, classGuid);
				if (parentClass == null)
					return result;
				result.put(JsonConstants.PARENT, getNode(parentClass.getVertex(), true));
			}
			Map<String, Object> params = new HashMap<>(6);
			params.put(ProcedureConstants.USER_ID, userId);
			params.put(ProcedureConstants.APP_ID, appId);
			params.put(ProcedureConstants.CLASS_LABEL, classLabel);
			params.put(ProcedureConstants.CLASS_VERTEX_ID, classGuid);
			params.put(ProcedureConstants.FILTERS, filters);
			params.put(ProcedureConstants.PAGE_OFFSET, pageSize);
			List<Vertex> dataItemVertices = procedureDao.getVertices(proceduresQuery.getClassDataItems(), params);
			dataItemVertices.forEach(dataItemVertx -> dataItem.add(getNode(dataItemVertx, true)));
			result.put(JsonConstants.NODES, dataItem);

			// set new page Offset
			result.put(JsonConstants.PAGE_OFFSET, (dataItem.isEmpty() ? -1 : pageSize + dataItem.size()));
		} catch (Exception e) {
			LOGGER.error("error occured in getDataItem: userId{},appId{},orgGuid{},error:{}..!!", userId, appId,
					classGuid, e);
		} finally {
			Performance.stopWatch(watch);
		}
		return result;
	}

	@Override
	public JsonArray getAssets(String userId, String appId, String classGuid, Optional<String> label,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_ALL_ASSETS_DB);
		JsonArray results = new JsonArray();
		Session session = null;
		try {
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("contract-info not found,userId{},appId{},orgGuid{}..!!", userId, appId, classGuid);
				return results;
			}

			Vertex vertexGuid = null;
			if (label.isPresent()) {
				vertexGuid = new Vertex(getParamMap(SchemaConstants.PROP_HDMFID_NAME, classGuid));
				vertexGuid.setLabel(label.get());
			} else {
				session = graphFactory.readSession();
				ClassX classX = classDao.getByGuid(session, classGuid);
				if (classX != null)
					vertexGuid = classX.getVertex();
			}
			if (vertexGuid == null) {
				LOGGER.info("vertex not found..!! userId{},appId{},classGuid{}", userId, appId, classGuid);
				return results;
			}

			List<Vertex> isAuthorisedVertex = authorizationService.isAuthorized(userId, appId, contractVertex,
					vertexGuid, false);
			if (isAuthorisedVertex.isEmpty()) {
				LOGGER.info("classGuid not authorized..!! userId{},appId{},classGuid{}", userId, appId, classGuid);
				return results;
			}

			Map<String, Object> params = new HashMap<>(8);
			params.put(ProcedureConstants.USER_ID, userId);
			params.put(ProcedureConstants.APP_ID, appId);
			params.put(ProcedureConstants.CONTRACT_TYPE_ID, contractVertex.getContractType().getGuid());
			params.put(ProcedureConstants.CONTRACT_ID, contractVertex.getContract().getGuid());
			params.put(ProcedureConstants.CLASS_VERTEX_ID, classGuid);
			params.put(ProcedureConstants.CLASS_LABEL, vertexGuid.getLabel());
			params.put(ProcedureConstants.FILTERS, filters);
			params.put(ProcedureConstants.PAGE_OFFSET, 0);
			List<List<Map<String, Object>>> assets = procedureDao.getListMap(proceduresQuery.getAllAssets(), params);
			if (!assets.isEmpty())
				results = new JsonArray(assets.get(0));
		} catch (Exception e) {
			LOGGER.error("error occured in getAssets: userId{},appId{},classGuid{},error:{}..!!", userId, appId,
					classGuid, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return results;
	}

	@Override
	public Node<Object> getOrgAndAssets(String userId, String appId, String classGuid, Optional<String> label,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_ORG_AND_ASSETS_DB);
		String classLabel = null;
		Node<Object> result = new Node<>();
		Set<Node<Object>> childs = new HashSet<>();

		if (pageOffset.isPresent() && pageOffset.get() < 0) {
			LOGGER.info("invalid pageOffset, userId{},appId{},classGuid{}..!!", userId, appId, classGuid);
			return result;
		}
		int pageSize = pageOffset.orElse(0);

		try {
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("contract-info not found,userId{},appId{},orgGuid{}..!!", userId, appId, classGuid);
				return result;
			}

			if (pageSize == 0) {
				ClassX parentVertex = guidCache.getClassX(label.orElse(null), classGuid);
				if (parentVertex == null)
					return result;
				classLabel = parentVertex.getVertex().getLabel();
				result.put(JsonConstants.PARENT, getNode(parentVertex.getVertex(), true));
			}
			Map<String, Object> params = new HashMap<>(8);
			params.put(ProcedureConstants.USER_ID, userId);
			params.put(ProcedureConstants.APP_ID, appId);
			params.put(ProcedureConstants.CONTRACT_TYPE_ID, contractVertex.getContractType().getGuid());
			params.put(ProcedureConstants.CONTRACT_ID, contractVertex.getContract().getGuid());
			params.put(ProcedureConstants.CLASS_VERTEX_ID, classGuid);
			params.put(ProcedureConstants.CLASS_LABEL, classLabel);
			params.put(ProcedureConstants.FILTERS, filters);
			params.put(ProcedureConstants.PAGE_OFFSET, pageSize);
			List<Vertex> dataItemVertices = procedureDao.getVertices(proceduresQuery.getOrgAndAssets(), params);
			dataItemVertices.forEach(vertex -> childs.add(getNode(vertex, true)));
			result.put(JsonConstants.NODES, childs);

			// set new page Offset
			result.put(JsonConstants.PAGE_OFFSET, (childs.isEmpty() ? -1 : pageSize + childs.size()));
		} catch (Exception e) {
			LOGGER.error("error occured in getOrgAndAssets: userId{},appId{},classGuid{},error:{}..!!", userId, appId,
					classGuid, e);
		} finally {
			Performance.stopWatch(watch);
		}
		return result;
	}

	@Override
	public Collection<Node<String>> getNodeProperties(String userId, String appId, String classGuid,
			Optional<String> classLabel) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_NODE_PROPS_DB);
		Session session = null;
		Map<String, Node<String>> result = new HashMap<>();
		try {
			ClassX classNode = guidCache.getClassX(classLabel.orElse(null), classGuid);
			if (classNode == null) {
				LOGGER.info("guid not found,userId{},appId{},classGuid{}..!!", userId, appId, classGuid);
				return Collections.emptyList();
			}
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("contract-info not found,userId{},appId{},classGuid{}..!!", userId, appId, classGuid);
				return Collections.emptyList();
			}
			Vertex classVertex = classNode.getVertex();
			boolean isNodeConnected = classDao.isNodeConnectedIn(session,
					Optional.of(SchemaConstants.LABEL_CONTRACT_TYPE), Optional.of(classVertex.getLabel()),
					Relationship.HAS, getParamMap(BindConstants.SRC, contractVertex.getContractType().getGuid(),
							BindConstants.DEST, classVertex.getGuid()));
			if (isNodeConnected) {
				getMandatoryVertex(classVertex, result);
				return result.values();
			}
			List<Vertex> parents = classDao.getMandatoryPropertyVertex(session,
					Optional.ofNullable(classVertex.getLabel()), getParamMap(BindConstants.SRC, classVertex.getGuid()));
			parents.forEach(vertx -> getMandatoryVertex(vertx, result));
			return result.values();
		} catch (Exception e) {
			LOGGER.error("error occured in getNodeProperties: userId{},appId{},classGuid{},error:{}..!!", userId, appId,
					classGuid, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return Collections.emptyList();
	}

	@Override
	public Node<Object> getDIClasses(String userId, String appId, Map<String, Map<String, String>> filters,
			Optional<Integer> pageOffset) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_DI_CLASSES_DB);
		Session session = null;
		Node<Object> result = new Node<>();
		if (pageOffset.isPresent() && pageOffset.get() < 0) {
			LOGGER.info("invalid pageOffset, userId{},appId{}..!!", userId, appId);
			return result;
		}
		int pageSize = pageOffset.orElse(0);

		try {
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("contract-info not found,userId{},appId{}..!!", userId, appId);
				return result;
			}
			Map<String, Object> params = new HashMap<>(6);
			params.put(ProcedureConstants.USER_ID, userId);
			params.put(ProcedureConstants.APP_ID, appId);
			params.put(ProcedureConstants.CONTRACT_TYPE_ID, contractVertex.getContractType().getGuid());
			params.put(ProcedureConstants.CONTRACT_ID, contractVertex.getContract().getGuid());
			params.put(ProcedureConstants.FILTERS, filters);
			params.put(ProcedureConstants.PAGE_OFFSET, pageSize);
			List<List<Map<String, Object>>> diClasses = procedureDao.getListMap(proceduresQuery.getDIClasses(), params);
			result.put(JsonConstants.CLASS, diClasses);
			result.put(JsonConstants.PAGE_OFFSET, diClasses.isEmpty() ? -1 : pageSize + diClasses.size());
		} catch (Exception e) {
			LOGGER.error("error occured in getDIClasses: userId{},appId{},error:{}..!!", userId, appId, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return result;
	}

	@Override
	public Results<Map<String, String>> getBootStrapKey(String productName, String serialNumber) {
		Session session = null;
		Map<String, String> orgMap = null;
		Results<Map<String, String>> results = null;
		StopWatch watch = Performance.startWatch(PerfConstants.GET_BOOTSTRAP_KEY_DB);
		try {
			session = graphFactory.readSession();
			Org org = orgDao.getBootStrapKey(session, productName, serialNumber);
			if (org == null)
				return new Results<>(null, ErrorCodes.NOT_FOUND);

			orgMap = new HashMap<>(1);
			orgMap.put(org.getName(), org.getBootStrapKey());
			results = new Results<>(orgMap);
		} catch (Exception e) {
			LOGGER.error("error occured in getBootStrapKey: productName{},assetName{},error:{}..!!", productName,
					serialNumber, e);
			results = new Results<>(null, ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return results;
	}

	@Override
	public Results<Boolean> validatePerson(String principalId) {
		Session session = null;
		Results<Boolean> results = new Results<>(false);
		StopWatch watch = Performance.startWatch(PerfConstants.VALIDATE_PERSON_DB);
		try {
			session = graphFactory.readSession();
			Principal principal = principalDao.getByPrincipalId(session, principalId);
			if (principal != null)
				return new Results<>(true);
		} catch (Exception e) {
			LOGGER.error("error occured in validatePerson: principalId{},error:{}..!!", principalId, e);
		} finally {
			graphFactory.closeSession(session);
			Performance.stopWatch(watch);
		}
		return results;
	}

	@Override
	public Results<JsonArray> getScriptTemplate(String userId, String appId) {
		Session session = null;
		JsonArray tempateArray = new JsonArray();
		try {
			ContractVertex contractInfo = personAppContractCache.get(userId, appId);
			if (contractInfo == null)
				return new Results<>(null, ErrorCodes.NOT_FOUND);

			session = graphFactory.readSession();
			List<Vertex> scriptTemplates = scriptTemplateDao.getScriptTemplateInfoByAduGuid(session,
					contractInfo.getAdminUnit().getGuid());
			scriptTemplates.forEach(vertx -> {
				JsonObject obj = new JsonObject();
				obj.put(JsonConstants.GUID, vertx.getGuid());
				obj.put(JsonConstants.NAME, vertx.getName());
				tempateArray.add(obj);
			});
		} catch (Exception e) {
			LOGGER.error("error occured in getScriptTemplate, userId:{},appId:{},error:{}", userId, appId, e);
			new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(tempateArray);
	}

	@Override
	public Results<JsonArray> getAssetbyProduct(String productClassGuid) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_ASSET_BY_PRODUCT_DB);
		Results<JsonArray> results = null;
		JsonArray data = new JsonArray();
		try {
			ProductClass productClass = guidCache.getProductClass(productClassGuid);
			if (productClass == null) {
				results = new Results<>(data, ErrorCodes.NOT_FOUND);
				return results;
			}
			Map<String, Object> params = new HashMap<>(1);
			params.put(ProcedureConstants.PRODUCT_CLASS_GUID, productClassGuid);
			List<Vertex> assetList = procedureDao.getVertices(proceduresQuery.getAssetByProductClass(), params);
			assetList.forEach(asset -> {
				JsonObject obj = new JsonObject();
				obj.put(JsonConstants.GUID, asset.getGuid());
				obj.put(JsonConstants.NAME, asset.getName());
				obj.put(JsonConstants.SERIAL_NUMBER, getStr(asset.getNode().get(SchemaConstants.PROP_SERIAL_NUMBER)));
				obj.put(JsonConstants.PROP_ACTDATE, getStr(asset.getNode().get(SchemaConstants.PROP_ACTDATE)));
				//
				obj.put(JsonConstants.MODEL_GUID, productClass.getGuid());
				obj.put(JsonConstants.MODEL, productClass.getName());
				data.add(obj);
			});
		} catch (Exception e) {
			LOGGER.error("error occured in getAssetbyProduct, productClassGuid:{},error:{}", productClassGuid, e);
			new Results<>(ErrorCodes.FAILED);
		} finally {
			Performance.stopWatch(watch);
		}
		return new Results<>(data);
	}

	@Override
	public Results<JsonObject> getServiceInfo(String userId, String appId) {
		Session session = null;
		JsonObject data = new JsonObject();
		try {
			ContractVertex contractInfo = personAppContractCache.get(userId, appId);
			if (contractInfo == null)
				return new Results<>(data, ErrorCodes.NOT_FOUND);

			session = graphFactory.readSession();
			JsonArray scriptsArray = new JsonArray();
			List<Vertex> scripts = scriptDao.getScriptsByAduGuid(session, contractInfo.getAdminUnit().getGuid());
			scripts.forEach(vertx -> {
				JsonObject script = new JsonObject();
				script.put(JsonConstants.GUID, vertx.getGuid());
				script.put(JsonConstants.NAME, vertx.getName());
				scriptsArray.add(script);
			});

			JsonArray scriptTemplateArray = new JsonArray();
			List<Vertex> scriptTemplates = scriptTemplateDao.getScriptTemplateInfoByContractType(session,
					contractInfo.getContractType().getGuid());
			scriptTemplates.forEach(vertx -> {
				JsonObject scriptTemplate = new JsonObject();
				scriptTemplate.put(JsonConstants.GUID, vertx.getGuid());
				scriptTemplate.put(JsonConstants.NAME, vertx.getName());
				scriptTemplateArray.add(scriptTemplate);
			});

			data.put(JsonConstants.SUBSCRIBED_SERVICES, scriptsArray);
			data.put(JsonConstants.AVAILABLE_SERVICES, scriptTemplateArray);
		} catch (Exception e) {
			LOGGER.error("error occured in getServiceInfo, userId:{},appId:{},error:{}", userId, appId, e);
			return new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(data);
	}

	@Override
	public Results<JsonObject> getSciptConfigInfo(String scriptGuid) {
		Session session = null;
		JsonObject data = new JsonObject();
		try {
			session = graphFactory.readSession();
			ScriptTemplate scriptTemplate = scriptTemplateDao.getScriptTemplateByScript(session, scriptGuid);
			if (scriptTemplate == null) {
				LOGGER.info("scriptTemplate not found for scriptGuid:{}..!!", scriptGuid);
				return new Results<>(ErrorCodes.NOT_FOUND);
			}
			if (StringUtils.isEmpty(scriptTemplate.getScope())) {
				LOGGER.info("scope not found scriptTemplate:{} not found for scriptGuid:{}..!!",
						scriptTemplate.getGuid(), scriptGuid);
				return new Results<>(ErrorCodes.NOT_FOUND);
			}
			List<Vertex> configItems = scriptDao.getScriptConfigItems(session, scriptGuid);
			JsonArray cfgsArray = new JsonArray();
			configItems.forEach(configItem -> {
				JsonObject cfg = new JsonObject();
				cfg.put(JsonConstants.LABEL, configItem.getLabel());
				cfg.put(JsonConstants.NAME, configItem.getName());
				cfg.put(JsonConstants.GUID, configItem.getGuid());
				cfg.put(JsonConstants.VALUE,
						configItem.getRelation().getProperties().getOrDefault(SchemaConstants.PROP_VALUE, ""));
				cfgsArray.add(cfg);
			});

			JsonArray assetsArray = new JsonArray();
			List<Vertex> assets = scriptDao.getScriptAssets(session, scriptGuid,
					ScriptTemplateScope.stringToEnum(scriptTemplate.getScope()));
			assets.forEach(asset -> {
				JsonObject ast = new JsonObject();
				ast.put(JsonConstants.LABEL, asset.getLabel());
				ast.put(JsonConstants.NAME, asset.getName());
				ast.put(JsonConstants.GUID, asset.getGuid());
				ast.put(JsonConstants.SERIAL_NUMBER, getStr(asset.getNode().get(SchemaConstants.PROP_SERIAL_NUMBER)));
				assetsArray.add(ast);
			});

			data.put(JsonConstants.CONFIG, cfgsArray);
			data.put(JsonConstants.ASSET, assetsArray);
		} catch (Exception e) {
			LOGGER.error("error occured in getSciptConfigInfo, scriptGuid:{},error:{}", scriptGuid, e);
			return new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(data);
	}

	@Override
	public Results<JsonArray> findCompatible(String userId, String appId, List<String> assets) {
		Session session = null;
		JsonArray result = new JsonArray();
		try {
			session = graphFactory.readSession();
			for (String assetGuid : assets) {
				Object data = assetDao.getCompatible(session, assetGuid);
				if (data == null)
					continue;
				result.add(data);
			}
		} catch (Exception e) {
			LOGGER.error("error occured in findCompatible, userId:{},appId:{},error:{}", userId, appId, e);
			return new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}

		return result.isEmpty() ? new Results<>(ErrorCodes.NOT_FOUND) : new Results<>(result);
	}

	@Override
	public Results<JsonArray> getScriptDetails(String scriptGuid, JsonObject assetsJson) {
		final Session session = graphFactory.readSession();
		JsonArray output = new JsonArray();
		try {
			Map<String, Object> assets = assetsJson.getMap();
			if (assets.isEmpty())
				return new Results<>(ErrorCodes.MISSING_PARAMETER);
			Script script = scriptDao.getByGuid(session, scriptGuid);
			if (script == null)
				return new Results<>(ErrorCodes.NOT_FOUND);
			ScriptTemplate scriptTemplate = scriptTemplateDao.getScriptTemplateByScript(session, script.getGuid());
			if (scriptTemplate == null)
				return new Results<>(ErrorCodes.NOT_FOUND);

			assets.forEach((astGuid, astLabel) -> {
				JsonObject assetJson = new JsonObject();
				JsonObject scriptInfos = new JsonObject();
				ProductClass productClass = assetDao.getProductClass(session, astGuid, astLabel.toString());
				Asset asset = assetDao.getByGuid(session, astGuid, astLabel.toString());
				assetJson.put(JsonConstants.GUID, asset.getGuid());
				assetJson.put(JsonConstants.NAME, asset.getName());
				assetJson.put(JsonConstants.SERIAL_NUMBER, asset.getSerialNumber());
				assetJson.put(JsonConstants.MODEL, productClass.getName());
				assetJson.put(JsonConstants.MODEL_GUID, productClass.getGuid());
				assetJson.put(JsonConstants.SCRIPT_TEMPLATE_NAME, scriptTemplate.getName());

				long scriptTemplateCount = scriptDao.countScriptTemplateInstance(session, scriptTemplate.getGuid(),
						astGuid, astLabel.toString());
				assetJson.put(JsonConstants.SCRIPT_INTANCE, scriptTemplateCount);

				Map<String, Object> data = new HashMap<>(2);
				List<Vertex> configs = scriptDao.getScriptConfigItems(session, scriptGuid);
				configs.forEach(config -> {

					data.put(config.getName(),
							config.getRelation().getProperties().getOrDefault(SchemaConstants.PROP_VALUE, ""));

				});
				scriptInfos.put(JsonConstants.CONFIG, data);

				JsonArray diJsar = new JsonArray();
				List<Vertex> dataItems = scriptDao.getScriptDataItems(session, scriptGuid);
				dataItems.forEach(di -> diJsar.add(getNode(di, false)));
				scriptInfos.put(JsonConstants.DI, diJsar);

				JsonArray evtsJsar = new JsonArray();
				List<Vertex> events = scriptDao.getScriptEvents(session, scriptGuid);
				events.forEach(evts -> evtsJsar.add(getNode(evts, false)));
				scriptInfos.put(JsonConstants.EVENTS, evtsJsar);

				scriptInfos.put(JsonConstants.NAME, script.getName());
				scriptInfos.put(JsonConstants.SCRIPT_GUID, script.getGuid());
				JsonArray scriptInfo = new JsonArray().add(scriptInfos);
				assetJson.put(JsonConstants.SCRIPT_INFO, scriptInfo);

				output.add(assetJson);
			});
		} catch (Exception e) {
			LOGGER.error("error occured in getScriptDetails, scriptGuid:{},error:{}", scriptGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(output);
	}

	@Override
	public Results<JsonObject> getEventsClasses(String userId, String appId) {
		Session session = null;
		JsonObject output = null;
		try {
			session = graphFactory.readSession();
			ContractVertex contractInfo = personAppContractCache.get(userId, appId);
			if (contractInfo == null) {
				LOGGER.debug("contract-info not found userId:{},appId:{}", userId, appId);
				return new Results<>(ErrorCodes.NOT_FOUND);
			}
			List<Record> eventsClasses = eventsDao.getEventsClasses(session, contractInfo.getContractType().getGuid());
			if (eventsClasses.isEmpty())
				return new Results<>(ErrorCodes.NOT_FOUND);

			output = new JsonObject(eventsClasses.get(0).asMap());
		} catch (Exception e) {
			LOGGER.error("error in getEventClasses,userId:{},appId:{},error:{}", userId, appId, e);
			return new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return output != null ? new Results<>(output) : new Results<>(ErrorCodes.NOT_FOUND);
	}

	@Override
	public Results<JsonArray> getCIAndScopeByScript(String scriptTemplateName) {
		final Session session = graphFactory.readSession();
		JsonArray output = new JsonArray();
		try {
			if (scriptTemplateName == null)
				return new Results<>(ErrorCodes.NOT_FOUND);

			String scriptTemplateGuid = scriptTemplateDao.getByName(session, scriptTemplateName).getGuid();
			String scope = scriptTemplateDao.getByName(session, scriptTemplateName).getVertex().getNode()
					.get(JsonConstants.SCOPE).toString();

			List<Vertex> scripts = scriptDao.getScriptsByScriptTemplate(session, scriptTemplateGuid);

			Map<String, Object> assetsMap = new HashMap<>();

			scripts.forEach(scriptVertex -> {

				List<Vertex> assets = scriptDao.getScriptAssets(session, scriptVertex.getGuid(),
						ScriptTemplateScope.stringToEnum(scope));

				assets.forEach(asset -> assetsMap.put(asset.getGuid(), asset.getLabel()));

			});

			assetsMap.forEach((astOrPcGuid, astOrPcLabel) -> {
				JsonObject assetJson = new JsonObject();

				Vertex productClasOrAsset = null;
				if (astOrPcLabel.equals(SchemaConstants.LABEL_ASSET)) {
					ProductClass productClass = assetDao.getProductClass(session, astOrPcGuid, astOrPcLabel.toString());
					Asset asset = assetDao.getByGuid(session, astOrPcGuid, astOrPcLabel.toString());
					assetJson.put(JsonConstants.GUID, asset.getGuid());
					assetJson.put(JsonConstants.NAME, asset.getName());
					assetJson.put(JsonConstants.SERIAL_NUMBER, asset.getSerialNumber());
					assetJson.put(JsonConstants.MODEL, productClass.getName());
					assetJson.put(JsonConstants.MODEL_GUID, productClass.getGuid());
					assetJson.put(JsonConstants.SCRIPT_TEMPLATE_NAME, scriptTemplateName);
					productClasOrAsset = asset.getVertex();
				} else if (astOrPcLabel.equals(SchemaConstants.LABEL_PRODUCT_CLASS)) {
					ProductClass productClass = productClassDao.getByGuid(session, astOrPcGuid);
					assetJson.put(JsonConstants.MODEL, productClass.getName());
					assetJson.put(JsonConstants.MODEL_GUID, productClass.getGuid());
					assetJson.put(JsonConstants.SCRIPT_TEMPLATE_NAME, scriptTemplateName);
					productClasOrAsset = productClass.getVertex();
				} else {
					LOGGER.debug("label not handled");
					return;
				}

				long scriptTemplateCount = scriptDao.countScriptTemplateInstance(session, scriptTemplateGuid,
						astOrPcGuid, astOrPcLabel.toString());
				assetJson.put(JsonConstants.SCRIPT_INTANCE, scriptTemplateCount);

				List<Vertex> assetScripts = scriptDao.getScriptTemplateInstance(session, scriptTemplateGuid,
						productClasOrAsset.getGuid(), astOrPcLabel.toString());
				JsonArray scriptInfoList = new JsonArray();
				assetScripts.forEach(sc -> {
					JsonObject scriptInfos = new JsonObject();

					List<Vertex> configs = scriptDao.getScriptConfigItems(session, sc.getGuid());
					Map<String, Object> data = new HashMap<>();
					configs.forEach(config -> {

						data.put(config.getName(),
								config.getRelation().getProperties().getOrDefault(SchemaConstants.PROP_VALUE, ""));
					});
					scriptInfos.put(JsonConstants.CONFIG, data);

					JsonArray diJsar = new JsonArray();
					List<Vertex> dataItems = scriptDao.getScriptDataItems(session, sc.getGuid());
					dataItems.forEach(di -> {

						JsonObject dataI = new JsonObject();
						dataI.put(JsonConstants.NAME, di.getName());
						dataI.put(JsonConstants.ALIAS, di.getNode().get(JsonConstants.ALIAS));
						diJsar.add(dataI);

					});
					scriptInfos.put(JsonConstants.DI, diJsar);

					JsonArray evtsJsar = new JsonArray();
					List<Vertex> events = scriptDao.getScriptEvents(session, sc.getGuid());
					events.forEach(evts -> {
						JsonObject ev = new JsonObject();
						ev.put(JsonConstants.NAME, evts.getName());
						evtsJsar.add(ev);
					});
					scriptInfos.put(JsonConstants.EVENTS, evtsJsar);

					scriptInfos.put(JsonConstants.NAME, sc.getName());
					scriptInfos.put(JsonConstants.SCRIPT_GUID, sc.getGuid());
					scriptInfoList.add(scriptInfos);

				});
				assetJson.put(JsonConstants.SCRIPT_INFO, scriptInfoList);
				output.add(assetJson);
			});
		} catch (Exception e) {
			LOGGER.error("error occured in getCIAndScopeByScript, scriptGuid:{},error:{}", scriptTemplateName, e);
			return new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return !output.isEmpty() ? new Results<>(output) : new Results<>(ErrorCodes.NOT_FOUND);
	}

	@Override
	public List<DataItem> getDataItemsfromProductClass(String productClassId) {

		Session session = null;
		List<DataItem> dataItemList = null;
		try {
			session = graphFactory.readSession();
			dataItemList = dataItemDao.getDataItemByProductClass(session, productClassId);

		} catch (Exception e) {
			LOGGER.error("error occured in getDataItemsfromModel, productClassId:{},error:{}", productClassId, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return dataItemList;

	}

	@Override
	public Results<Map<String, String>> getBootStrapKeyByOrgAndSerialNum(String orgName, String serialNumber) {
		Session session = null;
		Map<String, String> data = null;
		try {
			session = graphFactory.readSession();
			String bootStrapKey = orgDao.getBootStrapKeyByOrgAndSrn(session, orgName, serialNumber);
			if (StringUtils.isBlank(bootStrapKey)) {
				LOGGER.debug("getBootStrapKey not found, orgName:{},serialNumber:{} ..!!", orgName, serialNumber);
				return new Results<>(ErrorCodes.NOT_FOUND);
			}
			data = new HashMap<>(1);
			data.put(JsonConstants.BOOT_STRAP_KEY, bootStrapKey);
		} catch (Exception e) {
			LOGGER.error("error occured in getBootStrapKeyByOrgAndSerialNum, orgName:{},serialNumber:{},error:{}",
					orgName, serialNumber, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return data != null ? new Results<>(data) : new Results<>(ErrorCodes.NOT_FOUND);
	}

	@Override
	public List<DataItem> getDataItemsFromProductClass(String productClassId, String dataItemName) {
		Session session = null;
		List<DataItem> dataItemList = null;
		try {
			session = graphFactory.readSession();
			dataItemList = dataItemDao.getDataItemByProductClass(session, productClassId, dataItemName);
		} catch (Exception e) {
			LOGGER.error("error occured in getDataItemsFromProductClass, productClassId:{},dataItemName:{},error:{}",
					productClassId, dataItemName, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return dataItemList;
	}

	@Override
	public List<AssetPojo> getDsAggData(String userId, String appId, JsonObject query) {
		Session session = null;

		List<DataItem> dataItemresult = new ArrayList<>();
		List<AssetPojo> finallist = new ArrayList<>();
		Map<Object, DataItem> dataItemMap = new HashMap<>();
		JsonArray labelarray = query.getJsonArray(JsonConstants.LABEL_GUIDS);
		JsonArray diarray = query.getJsonArray(JsonConstants.DI_IDS);
		Vertex outVertex = null;
		try {
			session = graphFactory.readSession();
			ContractVertex contractVertex = getContractInfo(userId, appId);
			ContractType contracttype = contractVertex.getContractType();
			Set<String> childs = new HashSet<>();
			List<Vertex> assetresult = new ArrayList<>();
			for (Object jo : labelarray) {
				Vertex assetVertex = getDataProviderServiceInstance().getVertex(Optional.empty(),
						SchemaConstants.PROP_HDMFID_NAME, jo.toString());
				if (query.getString("label").equalsIgnoreCase(SchemaConstants.LABEL_PRODUCT_CLASS)) {
					assetresult = assetDao.getAssetByProductClass(session, contractVertex.getContract().getGuid(),
							contracttype.getGuid(), assetVertex.getGuid());
				} else {
					List<Vertex> assetVertexList = getAuthorizationServiceInstance().authorizeAssets(contracttype,
							contractVertex.getContract(), assetVertex, true);
					if (!(assetVertexList == null || assetVertexList.isEmpty()))
						assetresult.add(assetVertex);
				}
			}
			for (Vertex asset : assetresult) {
				DataShardPojo dsobj = new DataShardPojo();
				AssetPojo assetobj = new AssetPojo();
				JsonObject dsinfo = getDataShardInfo(asset);
				dsobj.setGuid(dsinfo.getString(JsonConstants.SOURCE_GUID));
				dsobj.setApiGatewayUrl(dsinfo.getString(JsonConstants.API_GATEWAY));
				dsobj.setHashKey("");
				assetobj.setDs(dsobj);
				assetobj.setDs(dsobj);
				assetobj.setGuid(asset.getGuid());
				outVertex = classDao.getOutVertex(session, SchemaConstants.LABEL_ASSET,
						SchemaConstants.PROP_HDMFID_NAME, asset.getGuid(), Relationship.IS,
						Optional.of(SchemaConstants.LABEL_PRODUCT_CLASS), false);
				List<Vertex> source = (outVertex != null ? outVertex.getOutVertices() : null);
				List<ClassList> cllist = new ArrayList<>();
				for (Vertex classVertex : source) {
					boolean dataitemcheck = false;
					ClassList innerclass = new ClassList();
					List<DataItem> dataItemList = new ArrayList<>();
					List<Vertex> classV = getAuthorizationServiceInstance().authorizeProductClass(contracttype,
							classVertex, true);
					if (classV != null && (!classV.isEmpty())
							&& classVertex.getRelation().getProperties().get(JsonConstants.IS_DERIVED) != null) {
						dataItemList = dataItemDao.getByContractType(session, contracttype.getGuid());
						dataitemcheck = true;
						for (DataItem divt : dataItemList) {
							dataItemMap.put(divt.getGuid(), divt);
						}
						dataItemresult.addAll(dataItemList);
					}
					if (!dataitemcheck) {
						List<DataItem> dataItem = dataItemDao.getDataItemByProductClass(session, classVertex.getGuid());
						for (DataItem di : dataItem) {
							List<Vertex> diList = getAuthorizationServiceInstance().isAuthorized(userId, appId,
									contractVertex, di.getVertex(), false);
							if (!(diList == null || diList.isEmpty())) {
								dataItemList.addAll(dataItem);
								dataItemMap.put(di.getGuid(), di);
							}
							dataItemresult.addAll(dataItemList);
						}
						childs.add(classVertex.getGuid());
						innerclass.setGuid(classVertex.getGuid());
						cllist.add(innerclass);
					}
					if (diarray != null && diarray.size() > 0) {
						for (Object diObject : diarray) {
							if (dataItemMap.get(diObject) != null) {
								List<Edge> edges = dataItemDao.getEdgesByProductClass(null,
										dataItemMap.get(diObject).getGuid(), innerclass.getGuid());
								for (Edge v : edges) {
									if (v.getProperties().get(SchemaConstants.PROP_AVG) != null) {
										String val = (String) v.getProperties().get(SchemaConstants.PROP_AVG);
										Map<String, Object> propertyMap = new HashMap<>();
										propertyMap.put(SchemaConstants.PROP_AVG, val);
										propertyMap.putAll(dataItemMap.get(diObject).getVertex().getNode());

										innerclass.addDi(DataItem.rowMapper(propertyMap));
									}
								}
							}
						}
					}
					assetobj.setModels(cllist);
					finallist.add(assetobj);
				}
			}
		} catch (Exception e) {
			LOGGER.error("error occured in getDsAggData userId:{},appId:{},input json:{},error:{}", userId, appId,
					query, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return finallist;
	}

	@Override
	public Results<List<AssetPojo>> getDataItemById(String userId, String appId, JsonObject query) {
		Session session = null;
		List<AssetPojo> finallist = new ArrayList<>();
		StopWatch watch = Performance.startWatch(PerfConstants.GET_DI_BY_ID_DB);

		Vertex outVertex = null;
		JsonArray labelarray = query.getJsonArray(JsonConstants.LABEL_GUIDS);
		JsonArray diarray = query.getJsonArray(JsonConstants.DI_IDS);
		JsonArray eventarray = query.getJsonArray(JsonConstants.EVENTS_IDS);
		String passedLabel = null;
		try {
			session = graphFactory.readSession();

			ContractVertex contractVertex = getDataProviderServiceInstance().getContractInfo(userId, appId);
			if (contractVertex == null) {
				LOGGER.error("error occured in getDataItemByIduserId:{},Error:{},userId:{},appId:{}",
						"Contract not Found", userId, appId);
				return new Results<>(ErrorCodes.NOT_FOUND);
			}
			ContractType contracttype = contractVertex.getContractType();
			if (contracttype == null) {
				LOGGER.error("error occured in getDataItemByIduserId:{},Error:{},userId:{},appId:{}",
						"Contract not Found", userId, appId);
				return new Results<>(ErrorCodes.NOT_FOUND);
			}
			Set<String> childs = new HashSet<>();
			List<Vertex> assetresult = new ArrayList<>();
			for (Object jo : labelarray) {
				if (query.getString(SchemaConstants.PROP_LABEL).equalsIgnoreCase(SchemaConstants.LABEL_CLASS))
					passedLabel = SchemaConstants.LABEL_PRODUCT_CLASS;
				else
					passedLabel = SchemaConstants.LABEL_ASSET;

				Vertex assetVertex = getDataProviderServiceInstance().getVertex(Optional.of(passedLabel),
						SchemaConstants.PROP_HDMFID_NAME, jo.toString());
				if (assetVertex != null) {
					if (query.getString(SchemaConstants.PROP_LABEL).equalsIgnoreCase(SchemaConstants.LABEL_CLASS)) {
						assetresult = assetDao.getAssetByProductClass(null, contractVertex.getContract().getGuid(),
								contracttype.getGuid(), assetVertex.getGuid());
					} else {

						Node<Object> assetVertexList = getDataProviderServiceInstance().getAuthorizedNode(appId, userId,
								assetVertex.getGuid(), false);
						if (!(assetVertexList == null || assetVertexList.isEmpty()))
							assetresult.add(assetVertex);
					}
				}
			}
			for (Vertex asset : assetresult) {
				DataShardPojo dsobj = new DataShardPojo();
				AssetPojo assetobj = new AssetPojo();
				JsonObject dsinfo = getDataProviderServiceInstance().getDataShardInfo(asset);
				dsobj.setGuid(dsinfo.getString(JsonConstants.SOURCE_GUID));
				dsobj.setApiGatewayUrl(dsinfo.getString(JsonConstants.API_GATEWAY));
				dsobj.setHashKey("");
				assetobj.setDs(dsobj);
				assetobj.setDs(dsobj);
				assetobj.setGuid(asset.getGuid());
				outVertex = classDao.getOutVertex(null, SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME,
						asset.getGuid(), Relationship.IS, Optional.of(SchemaConstants.LABEL_PRODUCT_CLASS), false);
				List<Vertex> source = (outVertex != null ? outVertex.getOutVertices() : null);
				List<ClassList> cllist = new ArrayList<>();

				for (Vertex classVertex : source) {
					boolean dataitemcheck = false;
					List<Events> eventresult = new ArrayList<>();
					List<DataItem> dataItemresult = new ArrayList<>();

					ClassList innerclass = new ClassList();
					List<DataItem> dataItemList = new ArrayList<>();
					List<Events> eventsList = new ArrayList<>();
					Map<Object, DataItem> dataItemMap = new HashMap<>();
					Map<Object, Events> eventsMap = new HashMap<>();

					List<Vertex> classV = getAuthorizationServiceInstance().isAuthorized(userId, appId, contractVertex,
							classVertex, false);
					Vertex oVertex = classDao.getInVertex(session, SchemaConstants.LABEL_CONTRACT_TYPE,
							SchemaConstants.PROP_HDMFID_NAME, contracttype.getGuid(), Relationship.IS_DERIVED,
							Optional.of(SchemaConstants.LABEL_PRODUCT_CLASS), false);

					if (classV != null && !classV.isEmpty() && oVertex.getInVertices() != null) {
						dataItemList = dataItemDao.getByContractType(null, contracttype.getGuid());
						dataitemcheck = true;
						for (DataItem divt : dataItemList) {
							dataItemMap.put(divt.getGuid(), divt);
							dataItemresult.add(divt);
						}
						eventsList = eventsDao.getByContractType(null, contracttype.getGuid());
						for (Events et : eventsList) {
							eventsMap.put(et.getGuid(), et);
							eventresult.add(et);
						}
					}
					if (!dataitemcheck) {
						List<DataItem> dataItem = dataItemDao.getDataItemByProductClass(null, classVertex.getGuid());
						for (DataItem di : dataItem) {
							dataItemList.add(di);
							dataItemMap.put(di.getGuid(), di);
							dataItemresult.addAll(dataItemList);
						}
						outVertex = classDao.getOutVertex(null, SchemaConstants.LABEL_PRODUCT_CLASS,
								SchemaConstants.PROP_HDMFID_NAME, classVertex.getGuid(), Relationship.HAS,
								Optional.of(SchemaConstants.LABEL_EVENTS), false);
						List<Vertex> events = (outVertex != null ? outVertex.getOutVertices() : null);
						if (events != null && !events.isEmpty()) {
							for (Vertex et : events) {
								Events event = Events.rowMapper(et.getNode());
								eventsMap.put(et.getGuid(), event);
								eventresult.add(event);
							}
						}
					}
					ClassX rootEventClass = classDao.getDomainChildClass(session,
							contractVertex.getDomainClass().getGuid(), PropNameSuffix._EVENTS.getValue());
					if (rootEventClass != null) {
						List<Events> scriptEvents = eventsDao.getEventsByScriptByAssetId(session, asset.getGuid(),
								rootEventClass);
						if (scriptEvents != null && !scriptEvents.isEmpty()) {
							for (Events sc : scriptEvents) {
								eventsMap.put(sc.getGuid(), sc);
							}
							eventresult.addAll(scriptEvents);
						}
					}

					childs.add(classVertex.getGuid());
					innerclass.setGuid(classVertex.getGuid());
					cllist.add(innerclass);
					if (diarray != null && diarray.size() > 0) {
						for (Object diObject : diarray) {
							if (dataItemMap.get(diObject) != null) {
								Vertex tempV = dataItemMap.get(diObject).getVertex();
								DataItem di = new DataItem(tempV.getGuid(), tempV.getName(),
										(tempV.getNode().get(SchemaConstants.PROP_TYPE) != null)
												? tempV.getNode().get(SchemaConstants.PROP_TYPE).toString() : null,
										(tempV.getNode().get(SchemaConstants.PROP_SOURCE) != null)
												? tempV.getNode().get(SchemaConstants.PROP_SOURCE).toString() : null);

								dataItemMap.remove(diObject);
								if (query.getString(SchemaConstants.REQUEST).equalsIgnoreCase(ValueConstants.DATAITEMS)
										|| query.getString(SchemaConstants.REQUEST)
												.equalsIgnoreCase(SchemaConstants.STATUS))
									innerclass.addDi(di);
							}
						}
					} else {
						for (DataItem diObject : dataItemList) {
							if (dataItemMap.get(diObject.getGuid()) != null) {
								Vertex tempV = dataItemMap.get(diObject.getGuid()).getVertex();
								DataItem di = new DataItem(tempV.getGuid(), tempV.getName(),
										(tempV.getNode().get(SchemaConstants.PROP_TYPE) != null)
												? tempV.getNode().get(SchemaConstants.PROP_TYPE).toString() : null,
										(tempV.getNode().get(SchemaConstants.PROP_SOURCE) != null)
												? tempV.getNode().get(SchemaConstants.PROP_SOURCE).toString() : null);
								if (query.getString(SchemaConstants.REQUEST).equalsIgnoreCase(ValueConstants.DATAITEMS)
										|| query.getString(SchemaConstants.REQUEST)
												.equalsIgnoreCase(SchemaConstants.STATUS))
									innerclass.addDi(di);
								dataItemMap.remove(diObject.getGuid());
							}

						}
					}
					if (eventarray != null && eventarray.size() > 0) {
						for (Object et : eventarray) {
							if (eventsMap.get(et) != null) {
								Vertex eventV = eventsMap.get(et).getVertex();
								Events events = new Events(eventV.getGuid(), eventV.getName(),
										(eventV.getNode().get(SchemaConstants.PROP_CATEGORY) != null)
												? new Integer(
														eventV.getNode().get(SchemaConstants.PROP_CATEGORY).toString())
												: null);
								eventsMap.remove(et);
								if (query.getString(SchemaConstants.REQUEST)
										.equalsIgnoreCase(SchemaConstants.LABEL_EVENTS)
										|| query.getString(SchemaConstants.REQUEST)
												.equalsIgnoreCase(SchemaConstants.STATUS))
									innerclass.addEvent(events);
							}
						}
					} else {
						for (Events etVertex : eventresult) {
							if (eventsMap.get(etVertex.getGuid()) != null) {
								Vertex eventV = eventsMap.get(etVertex.getGuid()).getVertex();
								Events events = new Events(eventV.getGuid(), eventV.getName(),
										(eventV.getNode().get(SchemaConstants.PROP_CATEGORY) != null)
												? new Integer(
														eventV.getNode().get(SchemaConstants.PROP_CATEGORY).toString())
												: null);
								if (query.getString(SchemaConstants.REQUEST)
										.equalsIgnoreCase(SchemaConstants.LABEL_EVENTS)
										|| query.getString(SchemaConstants.REQUEST)
												.equalsIgnoreCase(SchemaConstants.STATUS))
									innerclass.addEvent(events);
								eventsMap.remove(etVertex.getGuid());

							}
						}
					}
					if (classVertex.getLabel().equals(SchemaConstants.LABEL_PRODUCT_CLASS)) {
						childs.add(classVertex.getGuid());
						innerclass.setGuid(classVertex.getGuid());
					}
					cllist.add(innerclass);
				}
				assetobj.setModels(cllist);
				finallist.add(assetobj);

			}
		} catch (

		Exception e) {
			LOGGER.error("error occured in getDataItemByIduserId:{},appId:{},input json:{},error:{}", userId, appId,
					query, e);
			return new Results<>(ErrorCodes.FAILED);

		} finally {
			Performance.stopWatch(watch);
			graphFactory.closeSession(session);
		}
		return new Results<>(finallist);
	}

	@Override
	public Results<List<Vertex>> getAllProducClasses() {
		Session session = null;
		List<Vertex> productList = new ArrayList<>();
		try {
			session = graphFactory.readSession();
			productList = productClassDao.getAllProductClasses(session);
		} catch (Exception e) {
			LOGGER.error("error occured in getAllModelClasses error:{}", e);
			new Results<>(null, ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		if (productList.isEmpty()) {
			return new Results<>(null, ErrorCodes.NOT_FOUND);
		}

		return new Results<>(productList);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Results<List<Node<Object>>> getOrgsByUser(String userId, String appId,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset) {
		Session session = null;
		try {
			ContractVertex contractInfo = personAppContractCache.get(userId, appId);
			if (contractInfo == null)
				return new Results(ErrorCodes.NOT_FOUND);
			session = graphFactory.readSession();
			List<Vertex> orgsVertices = orgDao.getOrgsByUser(session, contractInfo.getContract().getGuid());
			if (orgsVertices.isEmpty())
				return new Results(ErrorCodes.NOT_FOUND);
			List<Node<Object>> orgs = new LinkedList<>();
			orgsVertices.forEach(orgVrtx -> orgs.add(getNode(orgVrtx, false)));
			return new Results<>(orgs);
		} catch (Exception e) {
			LOGGER.error("error occured in getOrgsByUser userId:{},appId:{}, error:{}", userId, appId, e);
			new Results(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results(ErrorCodes.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Results<List<Node<Object>>> getDataItems(String userId, String appId,
			Map<String, Map<String, String>> filters, Optional<Integer> pageOffset) {
		Session session = null;
		try {
			ContractVertex contractInfo = personAppContractCache.get(userId, appId);
			if (contractInfo == null)
				return new Results(ErrorCodes.NOT_FOUND);
			session = graphFactory.readSession();
			List<Vertex> diVertices = dataItemDao.getDataItems(session, contractInfo.getContractType().getGuid());
			if (diVertices.isEmpty())
				return new Results(ErrorCodes.NOT_FOUND);
			List<Node<Object>> dataItems = new LinkedList<>();
			diVertices.forEach(di -> dataItems.add(getNode(di, false)));
			return new Results<>(dataItems);
		} catch (Exception e) {
			LOGGER.error("error occured in getDataItems userId:{},appId:{}, error:{}", userId, appId, e);
			new Results(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results(ErrorCodes.NOT_FOUND);
	}

	@Override
	public Results<List<Node<Object>>> findAssetByProperty(String appId, String userId, FindProperty property,
			String propertyValue, Optional<Integer> pageOffset) {
		StopWatch watch = Performance.startWatch(PerfConstants.FIND_ASSET_ONLY_DB);
		try {
			int pageSize = pageOffset.orElse(0);
			List<Node<Object>> data = FindAssetByPropertyProcess.get(authorizationService, appId, userId, property,
					propertyValue, pageSize);
			if (data == null || data.isEmpty())
				return new Results<>(ErrorCodes.NOT_FOUND);

			return new Results<>(data, pageSize + data.size());
		} finally {
			Performance.stopWatch(watch);
		}
	}

	@Override
	public Results<String> getMashupInfo(String userId, String appId, String mashupath) {
		Session session = null;
		String output = null;
		JsonArray tempateArray = new JsonArray();

		try {
			ContractVertex contractInfo = personAppContractCache.get(userId, appId);

			if (contractInfo == null)
				return new Results<>(null, ErrorCodes.NOT_FOUND);
			session = graphFactory.readSession();
			Mashup scriptcheck = mashUpDao.getByPath(session, mashupath);
			LOGGER.info("scriptRepoVertex!!" + scriptcheck.getGuid());
			Mashup scriptRepoCheck = mashUpDao.getScriptRepobyPath(session, mashupath);

			if (scriptcheck == null)
				return new Results<>(null, ErrorCodes.NOT_FOUND);
			Vertex guidVertex = scriptcheck.getVertex();
			List<Vertex> authorizeVertex = authorizationService.isAuthorized(userId, appId, contractInfo, guidVertex,
					false);
			if (authorizeVertex.isEmpty()) {
				LOGGER.info("vertex not authorized for guid:{}..!!");
				return null;
			}
			Vertex ScriptRepoVertex = scriptRepoCheck.getVertex();

			List<Vertex> scriptTemplates = mashUpDao.getMashupRepoItems(session, mashupath);
			scriptTemplates.forEach(vertx -> {
				JsonObject obj = new JsonObject();
				obj.put(JsonConstants.GUID, vertx.getGuid());
				obj.put(JsonConstants.LABEL, vertx.getLabel());
				obj.put(JsonConstants.NAME, vertx.getName());
				tempateArray.add(obj);
			});
			LOGGER.info("scriptRepoVertex!!" + ScriptRepoVertex.getGuid());
			output = ScriptRepoVertex.getGuid() + "/" + tempateArray.getJsonObject(0).getString(JsonConstants.NAME);

		} catch (Exception e) {
			LOGGER.error("error occured in getMashupInfo, userId:{},appId:{},error:{}", userId, appId, e);
			new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(output);
	}

	@Override
	public Results<JsonArray> getEventsAndDIByUser(String userId, String appId, JsonObject query) {
		Session session = null;
		StopWatch watch = Performance.startWatch(PerfConstants.GET_EVENTS_BY_USER);
		List<String> finalAssetList = new ArrayList<>();
		boolean isOem = false;
		try {
			session = graphFactory.readSession();
			List<ContractType> contrats = new ArrayList<>();
			List<Contract> contract = new ArrayList<>();

			List<Record> records = contractDao.getContract(session, userId, appId);
			if (records != null && !records.isEmpty()) {
				records.forEach(record -> contrats.add(ContractType.rowMapper(parseNode(record, "ctt"))));
				records.forEach(record -> contract.add(Contract.rowMapper(parseNode(record, "ct"))));
			}
			if (contrats.isEmpty()) {
				LOGGER.error("error occured in getDataItemByIduserId:{},Error:{},userId:{},appId:{}",
						"Contract not Found", userId, appId);
				return new Results<>(ErrorCodes.NOT_FOUND);
			}
			JsonObject dataShard = new JsonObject();
			JsonArray finalRequest = new JsonArray();
			int count = 0;

			for (ContractType contractType : contrats) {
				if (contractType != null) {
					boolean isDervied = false;
					Vertex outVertex = null;

					List<Vertex> source = null;
					List<Vertex> productClasses = contractTypeDao.getProductClassByContractType(session,
							contractType.getGuid());
					source = productClasses;
					if (productClasses == null) {
						outVertex = classDao.getInVertex(session, SchemaConstants.LABEL_CONTRACT_TYPE,
								SchemaConstants.PROP_HDMFID_NAME, contractType.getGuid(), Relationship.IS_DERIVED,
								Optional.of(SchemaConstants.LABEL_PRODUCT_CLASS), false);
						source = (outVertex != null ? outVertex.getInVertices() : null);
						isDervied = true;
					}

					if (source != null) {
						for (Vertex classVertex : source) {
							List<ClassList> cllist = new ArrayList<>();
							boolean dataitemcheck = false;
							List<Events> eventresult = new ArrayList<>();
							ClassList innerclass = new ClassList();
							List<Events> eventsList = new ArrayList<>();
							Map<Object, Events> eventsMap = new HashMap<>();
							List<String> assets = new ArrayList<>();
							List<Vertex> classV = getAuthorizationServiceInstance().authorizeProductClass(contractType,
									classVertex, false);

							if (classV != null && !classV.isEmpty() && isDervied) {
								dataitemcheck = true;
								eventsList = eventsDao.getByContractType(null, contractType.getGuid());

								for (Events et : eventsList) {

									eventsMap.put(et.getGuid(), et);
									eventresult.add(et);
								}
								Map<String, Object> params = new HashMap<>(1);
								params.put(ProcedureConstants.PRODUCT_CLASS_GUID, classVertex.getGuid());
								List<Vertex> assetList = procedureDao
										.getVertices(proceduresQuery.getAssetByProductClass(), params);
								for (Vertex asset : assetList) {
									assets.add(asset.getGuid());
								}
							}
							LOGGER.debug("classV:{},isDervied:{},classV.isEmpty():{}", classV, isDervied,
									classV.isEmpty());

							if (classV != null && !classV.isEmpty() && !dataitemcheck) {
								isOem = true;
								outVertex = classDao.getOutVertex(null, SchemaConstants.LABEL_PRODUCT_CLASS,
										SchemaConstants.PROP_HDMFID_NAME, classVertex.getGuid(), Relationship.HAS,
										Optional.of(SchemaConstants.LABEL_EVENTS), false);
								List<Vertex> events = (outVertex != null ? outVertex.getOutVertices() : null);
								if (events != null && !events.isEmpty()) {
									for (Vertex et : events) {
										Events event = Events.rowMapper(et.getNode());
										eventsList.add(event);
										eventsMap.put(et.getGuid(), event);
										eventresult.addAll(eventsList);
									}
								}
							}
							ContractVertex contractInfo = personAppContractCache.get(userId, appId);
							ClassX rootEventClass = classDao.getDomainChildClass(session,
									contractInfo.getDomainClass().getGuid(), PropNameSuffix._EVENTS.getValue());
							if (rootEventClass != null) {

								List<Events> scriptEvents = eventsDao.getEventsByScriptByProductClassId(session,
										classVertex.getGuid(), rootEventClass);
								if (scriptEvents != null && !scriptEvents.isEmpty()) {
									for (Events sc : scriptEvents) {
										eventsMap.put(sc.getGuid(), sc);
									}
									eventresult.addAll(scriptEvents);
								}
							}
							innerclass.setGuid(classVertex.getGuid());

							innerclass.addDi(new DataItem());
							for (Events etVertex : eventresult) {
								if (eventsMap.get(etVertex.getGuid()) != null) {
									Vertex eventV = eventsMap.get(etVertex.getGuid()).getVertex();
									Events events = new Events(eventV.getGuid(), eventV.getName(),
											(eventV.getNode().get(SchemaConstants.PROP_CATEGORY) != null) ? new Integer(
													eventV.getNode().get(SchemaConstants.PROP_CATEGORY).toString())
													: null);
									innerclass.addEvent(events);
									eventsMap.remove(etVertex.getGuid());

								}
							}

							innerclass.setGuid(classVertex.getGuid());
							cllist.add(innerclass);
							finalAssetList.addAll(assets);
							JsonObject ds = getDataShardInfo(classVertex);
							if (ds != null) {
								JsonObject dsinfo = new JsonObject();
								dsinfo.put(JsonConstants.GUID, ds.getString(JsonConstants.SOURCE_GUID));
								dsinfo.put(JsonConstants.API_GATEWAY_URL, ds.getString(JsonConstants.API_GATEWAY));
								dsinfo.put(JsonConstants.HASH_KEY, "");

								if (ds != null && ds.getString(JsonConstants.SOURCE_GUID) != null) {
									JsonObject newdsOb;
									JsonObject sourceClass = new JsonArray(new Gson().toJson(cllist)).getJsonObject(0);
									if (dataShard.containsKey(ds.getString(JsonConstants.SOURCE_GUID))) {
										newdsOb = dataShard.getJsonObject(ds.getString(JsonConstants.SOURCE_GUID));
										if (newdsOb.containsKey(JsonConstants.CLASS_ + classVertex.getGuid())) {

											if (!assets.isEmpty())
												newdsOb.getJsonObject(JsonConstants.CLASS_ + classVertex.getGuid())
														.getJsonArray(JsonConstants.ASSETS)
														.addAll(new JsonArray(new Gson().toJson(assets)));

											else if (isOem && assets.isEmpty())
												newdsOb.getJsonObject(JsonConstants.CLASS_ + classVertex.getGuid())
														.getJsonArray(JsonConstants.ASSETS)
														.addAll(new JsonArray(new Gson().toJson(assets)));
											;

										} else {

											if (!assets.isEmpty()) {
												sourceClass.put(JsonConstants.ASSETS,
														new JsonArray(new Gson().toJson(assets)));
												newdsOb.put(JsonConstants.CLASS_ + classVertex.getGuid(), sourceClass);
											} else if (isOem && assets.isEmpty()) {
												sourceClass.put(JsonConstants.ASSETS,
														new JsonArray(new Gson().toJson(assets)));
												newdsOb.put(JsonConstants.CLASS_ + classVertex.getGuid(), sourceClass);
											}
										}
									} else {
										newdsOb = dsinfo;
										if (!assets.isEmpty()) {
											sourceClass.put(JsonConstants.ASSETS,
													new JsonArray(new Gson().toJson(assets)));
											newdsOb.put(JsonConstants.CLASS_ + classVertex.getGuid(), sourceClass);
										} else if (isOem && assets.isEmpty()) {
											sourceClass.put(JsonConstants.ASSETS,
													new JsonArray(new Gson().toJson(assets)));

											newdsOb.put(JsonConstants.CLASS_ + classVertex.getGuid(), sourceClass);
										}
									}
									dataShard.put(ds.getString(JsonConstants.SOURCE_GUID), newdsOb);

								}
							}
						}

					}
				}
			}

			dataShard.forEach(entry -> {
				JsonObject dataShardObj = (JsonObject) entry.getValue();

				JsonArray jsonArray = new JsonArray();
				Iterator<Entry<String, Object>> en = dataShardObj.iterator();
				while (en.hasNext()) {
					Entry<String, Object> test = en.next();
					if (test.getKey().startsWith(JsonConstants.CLASS_)) {
						jsonArray.add((JsonObject) test.getValue());
						en.remove();
					}
				}
				dataShardObj.put(JsonConstants.MODELS, jsonArray);
				finalRequest.add(dataShardObj);

			});
			if (finalAssetList.isEmpty() && !isOem)
				return new Results<JsonArray>(new JsonArray());

			return new Results<JsonArray>(finalRequest);
		} catch (

		Exception e) {
			LOGGER.error("error occured in getOrgsByUser userId:{},appId:{}, error:{}", userId, appId, e);
		} finally {
			Performance.stopWatch(watch);
			graphFactory.closeSession(session);
		}
		return new Results(ErrorCodes.FAILED);
	}

	@Override
	public Results<JsonArray> getMashupRepoDetails() {
		Session session = null;
		JsonArray finalArray = new JsonArray();

		try {
			session = graphFactory.readSession();
			List<Vertex> scriptRepo = scriptRepoDao.getScriptRepo(session);
			scriptRepo.forEach(scriptRepoDao -> {
				JsonObject obj = new JsonObject();
				obj.put(JsonConstants.GUID, scriptRepoDao.getGuid());
				obj.put(JsonConstants.NAME, scriptRepoDao.getName());
				obj.put(JsonConstants.GIT_LOCATION, getStr(scriptRepoDao.getNode().get(SchemaConstants.PROP_GIT_URL)));
				obj.put(JsonConstants.GIT_USER, getStr(scriptRepoDao.getNode().get(SchemaConstants.PROP_GIT_USER)));
				obj.put(JsonConstants.GIT_PWD, getStr(scriptRepoDao.getNode().get(SchemaConstants.PROP_GIT_PWD)));
				finalArray.add(obj);
			});
		} catch (Exception e) {
			LOGGER.error("error occured in getMashupRepoDetails, error:{}", e);
			return new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(finalArray);
	}

	@Override
	public Results<JsonArray> getMashup(String userId, String appId) {
		Session session = null;
		JsonArray tempateArray = new JsonArray();
		try {
			ContractVertex contractInfo = personAppContractCache.get(userId, appId);
			if (contractInfo == null)
				return new Results<>(null, ErrorCodes.NOT_FOUND);

			session = graphFactory.readSession();
			List<Vertex> scriptTemplates = mashUpDao.getMashupInfoByAduGuid(session,
					contractInfo.getAdminUnit().getGuid());
			scriptTemplates.forEach(vertx -> {
				JsonObject obj = new JsonObject();
				obj.put(JsonConstants.GUID, vertx.getGuid());
				obj.put(JsonConstants.NAME, vertx.getName());
				tempateArray.add(obj);
			});
		} catch (Exception e) {
			LOGGER.error("error occured in getScriptTemplate, userId:{},appId:{},error:{}", userId, appId, e);
			return new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(tempateArray);
	}

	@Override
	public Results<JsonArray> termAssociation(JsonArray jarray, Vertex termvertex) {
		Session session = null;
		JsonArray obj = new JsonArray();
		List<String> outputGuids = new ArrayList<>();
		try {
			session = graphFactory.readSession();

			for (Object jsonObject : jarray) {
				if (jsonObject != null) {
					Vertex inputvertex = getDataProviderServiceInstance().getVertex(Optional.empty(),
							SchemaConstants.PROP_HDMFID_NAME, jsonObject.toString());
					if (inputvertex != null && termvertex != null) {
						Edge edge = termDataDao.termHasVertex(null, termvertex.getLabel(), termvertex.getGuid(),
								inputvertex.getLabel(), inputvertex.getGuid(), Relationship.HAS, Direction.OUT);
						if (edge != null) {
							outputGuids.add(inputvertex.getGuid().toString());
						}
					}
				}
			}
			obj = new JsonArray(new Gson().toJson(outputGuids));
		} catch (Exception e) {
			LOGGER.error("error occured in termAssociation, jarray:{},termvertex:{},error:{}", jarray, termvertex, e);
			new Results<JsonArray>(ErrorCodes.INVALID_OPERATION);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(obj);

	}

	@Override
	public Results<JsonArray> getResponseForUpdateConfigDetails(String scriptGuid, JsonObject assetsJson) {
		final Session session = graphFactory.readSession();
		JsonArray output = new JsonArray();
		try {
			Map<String, Object> assets = assetsJson.getMap();
			if (assets.isEmpty())
				return new Results<>(ErrorCodes.MISSING_PARAMETER);
			Script script = scriptDao.getByGuid(session, scriptGuid);
			if (script == null)
				return new Results<>(ErrorCodes.NOT_FOUND);
			ScriptTemplate scriptTemplate = scriptTemplateDao.getScriptTemplateByScript(session, script.getGuid());
			if (scriptTemplate == null)
				return new Results<>(ErrorCodes.NOT_FOUND);

			assets.forEach((astOrPcGuid, astOrPcLabel) -> {
				JsonObject assetJson = new JsonObject();
				Vertex productClasOrAsset = null;
				if (astOrPcLabel.equals(SchemaConstants.LABEL_ASSET)) {
					ProductClass productClass = assetDao.getProductClass(session, astOrPcGuid, astOrPcLabel.toString());
					Asset asset = assetDao.getByGuid(session, astOrPcGuid, astOrPcLabel.toString());
					assetJson.put(JsonConstants.GUID, asset.getGuid());
					assetJson.put(JsonConstants.NAME, asset.getName());
					assetJson.put(JsonConstants.SERIAL_NUMBER, asset.getSerialNumber());
					assetJson.put(JsonConstants.MODEL, productClass.getName());
					assetJson.put(JsonConstants.MODEL_GUID, productClass.getGuid());
					assetJson.put(JsonConstants.SCRIPT_TEMPLATE_NAME, scriptTemplate.getName());
					productClasOrAsset = asset.getVertex();
				} else if (astOrPcLabel.equals(SchemaConstants.LABEL_PRODUCT_CLASS)) {
					ProductClass productClass = productClassDao.getByGuid(session, astOrPcGuid);
					assetJson.put(JsonConstants.MODEL, productClass.getName());
					assetJson.put(JsonConstants.MODEL_GUID, productClass.getGuid());
					assetJson.put(JsonConstants.SCRIPT_TEMPLATE_NAME, scriptTemplate.getName());
					productClasOrAsset = productClass.getVertex();
				} else {
					LOGGER.debug("label not handled");
					return;
				}

				long scriptTemplateCount = scriptDao.countScriptTemplateInstance(session, scriptTemplate.getGuid(),
						astOrPcGuid, astOrPcLabel.toString());
				assetJson.put(JsonConstants.SCRIPT_INTANCE, scriptTemplateCount);

				List<Vertex> scripts = scriptDao.getScriptTemplateInstance(session, scriptTemplate.getGuid(),
						productClasOrAsset.getGuid(), astOrPcLabel.toString());
				JsonArray scriptInfo = new JsonArray();
				scripts.forEach(sc -> {
					JsonObject scriptInfos = new JsonObject();
					Map<String, Object> data = new HashMap<>(2);
					List<Vertex> configs = scriptDao.getScriptConfigItems(session, sc.getGuid());
					configs.forEach(config -> {

						data.put(config.getName(),
								config.getRelation().getProperties().getOrDefault(SchemaConstants.PROP_VALUE, ""));

					});
					scriptInfos.put(JsonConstants.CONFIG, data);

					JsonArray diJsar = new JsonArray();
					List<Vertex> dataItems = scriptDao.getScriptDataItems(session, sc.getGuid());
					dataItems.forEach(di -> diJsar.add(getNode(di, false)));
					scriptInfos.put(JsonConstants.DI, diJsar);

					JsonArray evtsJsar = new JsonArray();
					List<Vertex> events = scriptDao.getScriptEvents(session, sc.getGuid());
					events.forEach(evts -> evtsJsar.add(getNode(evts, false)));
					scriptInfos.put(JsonConstants.EVENTS, evtsJsar);

					scriptInfos.put(JsonConstants.NAME, script.getName());
					scriptInfos.put(JsonConstants.SCRIPT_GUID, sc.getGuid());
					scriptInfo.add(scriptInfos);
				});
				assetJson.put(JsonConstants.SCRIPT_INFO, scriptInfo);

				output.add(assetJson);
			});
		} catch (Exception e) {
			LOGGER.error("error occured in getScriptDetails, scriptGuid:{},error:{}", scriptGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return new Results<>(output);
	}

	@Override
	public Results<JsonArray> getAssetbyClass(String classGuid, String classLabel) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_ASSET_BYCLASS);
		Session session = null;
		List<Vertex> assetList = new ArrayList<>();
		Results<JsonArray> results = null;
		JsonArray data = new JsonArray();
		try {
			session = graphFactory.readSession();

			if (classLabel == null) {
				classLabel = commonDao.getLabel(session, classGuid);
			}
			Map<String, Object> params = new HashMap<>(1);

			if (classLabel.equals(SchemaConstants.LABEL_PRODUCT_CLASS)) {
				ProductClass productClass = guidCache.getProductClass(classGuid);
				if (productClass == null) {
					results = new Results<>(data, ErrorCodes.NOT_FOUND);
					return results;
				}
				params.put(ProcedureConstants.PRODUCT_CLASS_GUID, classGuid);
				assetList = procedureDao.getVertices(proceduresQuery.getAssetByProductClass(), params);
			} else {
				ClassX classes = guidCache.getClassX(classLabel, classGuid);
				if (classes == null) {
					results = new Results<>(data, ErrorCodes.NOT_FOUND);
					return results;
				}
				params.put(ProcedureConstants.CLASS_GUID, classGuid);
				assetList = procedureDao.getVertices(proceduresQuery.getAssetByClass(), params);

			}
			assetList.forEach(asset -> {
				JsonObject obj = new JsonObject();
				asset.getNode().forEach((k, v) -> {
					obj.put(getDbToJson(k), v);
					obj.put(SchemaConstants.LABEL, getDbToJson(asset.getLabel()));
				});
				data.add(obj);
			});
		} catch (Exception e) {
			LOGGER.error("error occured in getAssetbyClass, classGuid:{},classLabel:{},error:{}", classGuid, classLabel,
					e);
			return new Results<>(ErrorCodes.FAILED);
		} finally {
			Performance.stopWatch(watch);
		}
		return new Results<>(data);
	}

	@Override
	public Results<List<Node<Object>>> getOrgsByType(String userId, String appId, String assetGuid, String type) {
		Session session = null;
		List<Node<Object>> data = new LinkedList<>();
		try {
			ContractVertex contractInfo = personAppContractCache.get(userId, appId);
			if (contractInfo == null)
				return new Results<>(ErrorCodes.NOT_AUTHORISED);

			session = graphFactory.readSession();
			List<Vertex> vertexs = orgDao.getOrgsByType(session, contractInfo.getContract().getGuid(), assetGuid, type);
			vertexs.forEach(vertx -> data.add(JsonDaoMapper.getNodeFromVertex(vertx)));
		} catch (Exception e) {
			LOGGER.error("error occurred in getOrgsByType, userId:{},appId:{},assetGuid:{},type:{},error:{}", userId,
					appId, assetGuid, type, e);
			new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return data.isEmpty() ? new Results<>(ErrorCodes.NOT_FOUND) : new Results<>(data);
	}

	@Override
	public Results<JsonArray> getContractTypesForUser(String userId, String appId) {

		Session session = null;
		Results<JsonArray> results = null;
		JsonArray data = new JsonArray();
		try {
			ContractVertex contractInfo = personAppContractCache.get(userId, appId);
			if (contractInfo == null)
				return new Results<>(ErrorCodes.NOT_AUTHORISED);

			session = graphFactory.readSession();

			List<Vertex> vertexs = contractTypeDao.getContractTypeInIsContractType(session,
					contractInfo.getContractType().getGuid());
			if (vertexs == null || vertexs.isEmpty()) {
				results = new Results<>(data, ErrorCodes.NOT_FOUND);
				return results;
			}

			vertexs.forEach(vertx -> {
				vertx.getNode().get(JsonConstants.NAME);
				JsonObject output = new JsonObject();
				Map<String, Object> map = vertx.getNode();
				output.put(JsonConstants.NAME, map.get(SchemaConstants.PROP_NAME).toString());
				output.put(JsonConstants.AUTH_TOKEN, map.get(SchemaConstants.PROP_AUTH_TOKEN).toString()
						.replace(SchemaConstants._GLOBAL_CONTRACT, ""));
				output.put(JsonConstants.GUID, map.get(SchemaConstants.PROP_HDMFID_NAME).toString());

				data.add(output);
			});

		} catch (Exception e) {
			LOGGER.error("error occurred in getContractTypesForUser, userId:{},appId:{},error:{}", userId, appId, e);
			new Results<>(ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		return data.isEmpty() ? new Results<>(ErrorCodes.NOT_FOUND) : new Results<>(data);

	}

	@Override
	public boolean isExist(String label, String property, String value) {
		Session session = graphFactory.readSession();
		try {
			return classDao.isClassExist(session, label, property, value);
		} catch (Exception e) {
			LOGGER.error("error occured in isExist label:{},name:{},value:{},error:{}", label, property, value, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return false;
	}

	@Override
	public List<Map<String, Object>> haveChildren(String guid, String srcLabel, String destLabel, String direction,
			String edgeLabel, Long depth) {
		Map<String, Object> params = new HashMap<>(5);
		params.put(GUID, guid);
		params.put(SRC_LABEL, srcLabel);
		params.put(DEST_LABEL, destLabel);
		params.put(DIRECTION, direction);
		params.put(EDGE_LABEL, edgeLabel);
		params.put(DEPTH, depth);
		return procedureDao.getMap(proceduresQuery.haveChildren(), params);
	}

	@Override
	public List<Asset> getAssetByProductClass(String productClass, String assetId) {
		Session session = null;
		List<Asset> assetList = null;
		try {
			session = graphFactory.readSession();
			assetList = assetDao.getAssetBySerialNumber(session, productClass, assetId);

		} catch (Exception e) {
			LOGGER.error("error occured in getAssetByProductClass, productClass:{},error:{}", assetId, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return assetList;
	}

	@Override
	public List<Asset> getAssetByOrg(String orgid, String assetId) {
		Session session = null;
		List<Asset> assetList = null;
		try {
			session = graphFactory.readSession();
			assetList = assetDao.getAssetByOrg(session, orgid, assetId);

		} catch (Exception e) {
			LOGGER.error("error occured in getAssetByProductClass, productClass:{},error:{}", assetId, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return assetList;
	}

	@Override
	public Org getOrgBootStrapKey(String bootStrapKey) {
		Session session = null;
		Org orgNode = null;
		try {
			session = graphFactory.readSession();
			orgNode = orgDao.getOrgBootStrapKey(session, bootStrapKey);

		} catch (Exception e) {
			LOGGER.error("error occured in getAssetByProductClass, productClass:{},error:{}", bootStrapKey, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return orgNode;

	}

	@Override
	public Asset getLicenceAsset(String orgGuid) {
		Session session = null;
		Asset assetNode = null;
		try {
			session = graphFactory.readSession();
			assetNode = assetDao.getLicenceAssetBybootStrapOrg(session, orgGuid);

		} catch (Exception e) {
			LOGGER.error("error occured in getAssetByProductClass, productClass:{},error:{}", orgGuid, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return assetNode;

	}
	@Override
	public List<Asset> getAllLicenceAsset() {
		Session session = null;
		List<Asset> productList = new ArrayList<>();
		try {
			session = graphFactory.readSession();
			productList = assetDao.getAllLicenceAssets(session);
		} catch (Exception e) {
			LOGGER.error("error occured in getAllLicenceAsset error:{}", e);
			new Results<>(null, ErrorCodes.FAILED);
		} finally {
			graphFactory.closeSession(session);
		}
		

		return productList;
	}


	
	@Override
	public Asset saveUpdate(Map<String, Object> params) {
		Session session = null;
		Asset assetNode = null;
		try {
			session = graphFactory.readSession();
			assetNode = assetDao.saveUpdate(session, params);

		} catch (Exception e) {
			LOGGER.error("error occured in saveUpdate, error:{}", params, e);
		} finally {
			graphFactory.closeSession(session);
		}
		return assetNode;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Results<List<Node>> getAssetsByUser(String userId, String appId, Map<String, Map<String, String>> filters,
			Optional<Integer> pageOffset) {
		StopWatch watch = Performance.startWatch(PerfConstants.GET_ASSETS_BY_USER);
		List<Node> result = new ArrayList<>();
		int pageSize = pageOffset.orElse(0);
		try {
			ContractVertex contractVertex = personAppContractCache.get(userId, appId);
			if (contractVertex == null) {
				LOGGER.info("contract-info not found,userId{},appId{}..!!", userId, appId);
				return new Results<>(ErrorCodes.NOT_AUTHORISED);
			}
			Map<String, Object> params = new HashMap<>(6);
			params.put(ProcedureConstants.USER_ID, userId);
			params.put(ProcedureConstants.APP_ID, appId);
			params.put(ProcedureConstants.CONTRACT_TYPE_ID, contractVertex.getContractType().getGuid());
			params.put(ProcedureConstants.CONTRACT_ID, contractVertex.getContract().getGuid());
			params.put(ProcedureConstants.FILTERS, filters);
			params.put(ProcedureConstants.PAGE_OFFSET, pageSize);
			List<Vertex> assetVertices = procedureDao.getVertices(proceduresQuery.getAssetsByUser(), params);
			assetVertices.forEach(assetVertx -> result.add(getNode(assetVertx, false)));
		} catch (Exception e) {
			LOGGER.error("error occured in getAssetByUser: userId{},appId{},error:{}..!!", userId, appId, e);
		} finally {
			Performance.stopWatch(watch);
		}
		return !result.isEmpty() ? new Results<>(result, -1) : new Results<>(ErrorCodes.NOT_FOUND);
	}

}
