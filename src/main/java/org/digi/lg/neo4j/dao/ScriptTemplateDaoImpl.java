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
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
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
import org.digi.lg.neo4j.pojo.model.Script;
import org.digi.lg.neo4j.pojo.model.ScriptTemplate;
import org.digi.lg.neo4j.queries.ScriptTemplateQuery;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;

public class ScriptTemplateDaoImpl extends CRUD implements ScriptTemplateDao {
	private final BaseDao baseDao;
	private final ScriptTemplateQuery scriptTemplateQuery;

	protected ScriptTemplateDaoImpl(final BaseDao baseDao, final ScriptTemplateQuery scriptTemplateQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.scriptTemplateQuery = checkNotNull(scriptTemplateQuery);
	}

	@Override
	public <TRX> ScriptTemplate save(TRX trx, Map<String, Object> params) {
		checkConstraints(SchemaConstants.LABEL_SCRIPT_TEMPLATE, params);
		return new ScriptTemplate(save(trx, SchemaConstants.LABEL_SCRIPT_TEMPLATE, params));
	}

	@Override
	public <TRX> ScriptTemplate getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, scriptTemplateQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return ScriptTemplate.rowMapper(DaoUtil.parseNode(records, "spt"));
	}

	@Override
	public <TRX> List<Vertex> getConfigItem(TRX trx, String scriptTemplateGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptTemplateGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptTemplateQuery.getConfigItem(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> list = new LinkedList<>();
		records.forEach(record -> list.add(
				new Vertex(record.asMap().get("lbl").toString(), ((InternalNode) record.asMap().get("ci")).asMap())));
		return list;
	}

	@Override
	public <TRX> List<Vertex> getEventType(TRX trx, String scriptTemplateGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptTemplateGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptTemplateQuery.getEventType(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> list = new LinkedList<>();
		records.forEach(record -> list.add(
				new Vertex(record.asMap().get("lbl").toString(), ((InternalNode) record.asMap().get("et")).asMap())));
		return list;
	}

	@Override
	public <TRX> List<Vertex> getTriggerDataItem(TRX trx, String scriptTemplateGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptTemplateGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptTemplateQuery.getTriggerDataItem(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> list = new LinkedList<>();
		records.forEach(record -> list.add(
				new Vertex(record.asMap().get("lbl").toString(), ((InternalNode) record.asMap().get("di")).asMap())));
		return list;
	}

	@Override
	public <TRX> Edge scriptTemplateHasConfigItem(TRX trx, String scriptTemplateGuid, String configItemGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT_TEMPLATE, SchemaConstants.PROP_HDMFID_NAME,
				scriptTemplateGuid, SchemaConstants.LABEL_CONFIG_ITEM, SchemaConstants.PROP_HDMFID_NAME, configItemGuid,
				Relationship.HAS, Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptTemplateGeneratesEventType(TRX trx, String scriptTemplateGuid, String eventTypeGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT_TEMPLATE, SchemaConstants.PROP_HDMFID_NAME,
				scriptTemplateGuid, SchemaConstants.LABEL_EVENT_TYPE, SchemaConstants.PROP_HDMFID_NAME, eventTypeGuid,
				Relationship.GENERATES, Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptTemplateTriggerDataItem(TRX trx, String scriptTemplateGuid, String dataItemGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT_TEMPLATE, SchemaConstants.PROP_HDMFID_NAME,
				scriptTemplateGuid, SchemaConstants.LABEL_DATA_ITEM, SchemaConstants.PROP_HDMFID_NAME, dataItemGuid,
				Relationship.TRIGGER, Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptTemplateAppliesProductClass(TRX trx, String scriptTemplateGuid, String prdtClassGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT_TEMPLATE, SchemaConstants.PROP_HDMFID_NAME,
				scriptTemplateGuid, SchemaConstants.LABEL_PRODUCT_CLASS, SchemaConstants.PROP_HDMFID_NAME,
				prdtClassGuid, Relationship.APPLIES_TO, Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptTemplateHasAdu(TRX trx, String scriptTemplateGuid, String aduGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT_TEMPLATE, SchemaConstants.PROP_HDMFID_NAME,
				scriptTemplateGuid, SchemaConstants.LABEL_ADMIN_UNIT, SchemaConstants.PROP_HDMFID_NAME, aduGuid,
				Relationship.HAS, Direction.OUT);
	}

	@Override
	public <TRX> List<Vertex> getScriptTemplateInfoByAduGuid(TRX trx, String aduGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, aduGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptTemplateQuery.getScriptTemplateInfoByAduGuid(),
				bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> list = new LinkedList<>();
		records.forEach(record -> list.add(
				new Vertex(record.asMap().get("lbl").toString(), ((InternalNode) record.asMap().get("spt")).asMap())));
		return list;
	}

	@Override
	public <TRX> List<Vertex> getScriptTemplateInfoByContractType(TRX trx, String contractTypeGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, contractTypeGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptTemplateQuery.getScriptTemplateInfoByContractType(),
				bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> list = new LinkedList<>();
		records.forEach(record -> list.add(
				new Vertex(record.asMap().get("lbl").toString(), ((InternalNode) record.asMap().get("spt")).asMap())));
		return list;
	}

	@Override
	public <TRX> ScriptTemplate getScriptTemplateByScript(TRX trx, String scriptGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptTemplateQuery.getScriptTemplateByScript(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return ScriptTemplate.rowMapper(DaoUtil.parseNode(records, "spt"));
	}

	@Override
	public <TRX> Script getByName(TRX trx, String name) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_NAME, name);
		List<Record> records = baseDao.executeQuery(trx, scriptTemplateQuery.getByName(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return Script.rowMapper(DaoUtil.parseNode(records, "spt"));
	}

	@Override
	public <TRX> Edge scriptTemplateHasDS(TRX trx, String scriptTemplateGuid, String dsGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT_TEMPLATE, SchemaConstants.PROP_HDMFID_NAME,
				scriptTemplateGuid, SchemaConstants.LABEL_DS, SchemaConstants.PROP_HDMFID_NAME, dsGuid,
				Relationship.HAS, Direction.OUT);
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
		String query = createEdgeQueryBuilder(scriptTemplateQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), relType);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

}
