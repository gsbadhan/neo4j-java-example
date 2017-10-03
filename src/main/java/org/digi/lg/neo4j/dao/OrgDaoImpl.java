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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.digi.lg.neo4j.dao.DaoUtil.createEdgeQueryBuilder;
import static org.digi.lg.neo4j.dao.DaoUtil.getParamMap;
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
import static org.digi.lg.neo4j.dao.DaoUtil.parseLabel;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Org;
import org.digi.lg.neo4j.queries.OrgQuery;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Path;

public class OrgDaoImpl extends CRUD implements OrgDao {
	private final BaseDao baseDao;
	private final OrgQuery orgQuery;

	protected OrgDaoImpl(final BaseDao baseDao, final CommonDao commonDao, final OrgQuery orgQuery) {
		super(baseDao, commonDao);
		this.baseDao = checkNotNull(baseDao);
		this.orgQuery = checkNotNull(orgQuery);
	}

	@Override
	public <TRX> Org addUpdate(TRX trx, Map<String, Object> params) {
		return new Org(saveUpdate(trx, SchemaConstants.LABEL_ORG, params));
	}

	@Override
	public <TRX> Org getOrgByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getOrgByGuid(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return Org.rowMapper(parseNode(records, "or"));
	}

	@Override
	public <TRX> Edge orgHasOrg(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getOrgHasOrg(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge orgBelongsOrg(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getOrgBelongsOrg(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge orgHasDataShard(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getOrgHasDataShard(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge orgIsContract(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getOrgIsContract(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> boolean isNodeConnected(TRX trx, String srcLabel, String srcPropLabel, String srcPropValue,
			String dstLabel, String dstPropName, String dstPropValue, Relationship relationship, Direction direction) {

		return super.isExist(trx, srcLabel, srcPropLabel, srcPropValue, dstLabel, dstPropName, dstPropValue,
				relationship, direction);

	}

	@Override
	public <TRX> Edge orgHasContract(TRX trx, Map<String, Object> params) {
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getOrgHasContract(), params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> Edge deleteOrgHasAsset(TRX trx, String orgGuid, String assetGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, orgGuid,
				SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetGuid, Relationship.HAS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge deleteOrgHasOrg(TRX trx, String srcOrgGuid, String destOrgGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, srcOrgGuid,
				SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, destOrgGuid, Relationship.HAS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge deleteOrgBelongsOrg(TRX trx, String destOrgGuid, String srcOrgGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, destOrgGuid,
				SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, srcOrgGuid, Relationship.BELONGS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge orgHasAsset(TRX trx, String orgGuid, String assetGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, orgGuid,
				SchemaConstants.LABEL_ASSET, SchemaConstants.PROP_HDMFID_NAME, assetGuid, Relationship.HAS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge deleteOrgIsClass(TRX trx, String orgGuid, String classGuid) {
		return super.deleteEdge(trx, SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, orgGuid,
				SchemaConstants.LABEL_CLASS, SchemaConstants.PROP_HDMFID_NAME, classGuid, Relationship.IS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge orgIsClass(TRX trx, String orgGuid, String classGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_ORG, SchemaConstants.PROP_HDMFID_NAME, orgGuid,
				SchemaConstants.LABEL_CLASS, SchemaConstants.PROP_HDMFID_NAME, classGuid, Relationship.IS,
				Direction.OUT);
	}

	@Override
	public <TRX> Org getBootStrapKey(TRX trx, String productName, String serialNumber) {
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getBootStrapKey(),
				getParamMap(BindConstants.PRODUCT_NAME, productName, BindConstants.PROP_SERIAL_NUMBER, serialNumber));
		if (records == null || records.isEmpty())
			return null;
		return new Org(new Vertex(parseLabel(records.get(0)), parseNode(records, "or")));
	}

	@Override
	public <TRX> Org getOrgBootStrapKey(TRX trx, String bootStrapKey) {
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getBootStrapKeyV2(),
				getParamMap(BindConstants.PROP_BOOT_STRAP_KEY, bootStrapKey));
		if (records == null || records.isEmpty())
			return null;
		return new Org(new Vertex(parseLabel(records.get(0)), parseNode(records, "or")));
	}

	@Override
	public <TRX> Org getImmediateOrgOfAsset(TRX trx, String srcOrgGuid, String assetGuid) {
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getImmediateOrgOfAsset(),
				getParamMap(BindConstants.SRC, srcOrgGuid, BindConstants.DEST, assetGuid));
		if (records == null || records.isEmpty())
			return null;
		Path path = ((Path) records.get(0).asMap().get("paths"));
		LinkedList<Map<String, Object>> orgList = new LinkedList<>();
		path.nodes().forEach(node -> {
			if (node.hasLabel(SchemaConstants.LABEL_ORG))
				orgList.add(node.asMap());
		});
		return orgList.isEmpty() ? null : new Org(new Vertex(orgList.getLast()));
	}

	@Override
	public <TRX> String getBootStrapKeyByOrgAndSrn(TRX trx, String orgName, String serialNumber) {
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getBootStrapKeyByOrgAndSrn(),
				getParamMap(BindConstants.SRC, serialNumber, BindConstants.DEST, orgName));
		if (records == null || records.isEmpty())
			return null;
		return records.get(0).get("btskey").asString();
	}

	@Override
	public <TRX> List<Vertex> getOrgsByUser(TRX trx, String contractGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, contractGuid);
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getOrgsByUser(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		List<Vertex> orgs = new LinkedList<>();
		orgs.add(new Vertex(parseLabel(records.get(0)), parseNode(records.get(0), "orA")));
		records.forEach(record -> orgs.add(new Vertex(parseLabel(record), parseNode(record, "orn"))));
		return orgs;
	}

	@Override
	public <TRX> List<Vertex> getOrgsByType(TRX trx, String contractGuid, String assetGuid, String type) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_CONTRACT_GUID, contractGuid);
		bindParams.put(BindConstants.PROP_ASSET_GUID, assetGuid);
		bindParams.put(BindConstants.PROP_TYPE, type);
		List<Record> records = baseDao.executeQuery(trx, orgQuery.getOrgsByType(), bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		List<Vertex> orgs = new LinkedList<>();
		records.forEach(record -> orgs.add(new Vertex(parseLabel(record), parseNode(record, "or"))));
		return orgs;
	}

	@Override
	public <TRX> Boolean deleteNode(TRX trx, String nodeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, nodeGuid);
		baseDao.executeQuery(trx, orgQuery.getDetachDeleteNode(), bindParams);
		return true;
	}

	@Override
	public <TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction) {
		return super.deleteEdge(trx, srcLabel, SchemaConstants.PROP_HDMFID_NAME, srcGuid, destLabel,
				SchemaConstants.PROP_HDMFID_NAME, destGuid, relType, direction);
	}

	@Override
	public <TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType,
			Map<String, Object> params) {
		String query = createEdgeQueryBuilder(orgQuery.getCreateLink(), handleHyphen(srcLabel), handleHyphen(destLabel),
				relType);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

}
