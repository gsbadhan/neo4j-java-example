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
import static org.digi.lg.neo4j.dao.DaoUtil.destLabelQueryBuilder;
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
import org.digi.lg.neo4j.core.ScriptTemplateScope;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.core.Vertex.Relation;
import org.digi.lg.neo4j.pojo.model.Script;
import org.digi.lg.neo4j.queries.ScriptQuery;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;

public class ScriptDaoImpl extends CRUD implements ScriptDao {
	private final BaseDao baseDao;
	private final ScriptQuery scriptQuery;

	protected ScriptDaoImpl(final BaseDao baseDao, final ScriptQuery scriptQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.scriptQuery = checkNotNull(scriptQuery);
	}

	@Override
	public <TRX> Script save(TRX trx, Map<String, Object> params) {
		return new Script(save(trx, SchemaConstants.LABEL_SCRIPT, params));
	}

	@Override
	public <TRX> Script getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getByGuid(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return Script.rowMapper(DaoUtil.parseNode(records, "sp"));
	}

	@Override
	public <TRX> Script getByName(TRX trx, String name) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_NAME, name);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getByName(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return Script.rowMapper(DaoUtil.parseNode(records, "sp"));
	}

	@Override
	public <TRX> Edge scriptToScriptTemplate(TRX trx, String scriptGuid, String scriptTemplateGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid,
				SchemaConstants.LABEL_SCRIPT_TEMPLATE, SchemaConstants.PROP_HDMFID_NAME, scriptTemplateGuid,
				Relationship.HAS, Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptHasEvents(TRX trx, String scriptGuid, String eventsGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid,
				SchemaConstants.LABEL_EVENTS, SchemaConstants.PROP_HDMFID_NAME, eventsGuid, Relationship.HAS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptHasConfigItem(TRX trx, String scriptGuid, String configItemGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid,
				SchemaConstants.LABEL_CONFIG_ITEM, SchemaConstants.PROP_HDMFID_NAME, configItemGuid, Relationship.HAS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptHasContract(TRX trx, String scriptGuid, String contractGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid,
				SchemaConstants.LABEL_CONTRACT, SchemaConstants.PROP_HDMFID_NAME, contractGuid, Relationship.HAS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptBelongsAdu(TRX trx, String scriptGuid, String aduGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid,
				SchemaConstants.LABEL_ADMIN_UNIT, SchemaConstants.PROP_HDMFID_NAME, aduGuid, Relationship.BELONGS,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptTriggerDataItem(TRX trx, String scriptGuid, String dataItemGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid,
				SchemaConstants.LABEL_DATA_ITEM, SchemaConstants.PROP_HDMFID_NAME, dataItemGuid, Relationship.TRIGGER,
				Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptHasDS(TRX trx, String scriptGuid, String dsGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid,
				SchemaConstants.LABEL_DS, SchemaConstants.PROP_HDMFID_NAME, dsGuid, Relationship.HAS, Direction.OUT);
	}

	@Override
	public <TRX> List<Vertex> getScriptsByAduGuid(TRX trx, String aduGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, aduGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getScriptsByAduGuid(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> scripts = new LinkedList<>();
		records.forEach(record -> scripts.add(
				new Vertex(record.asMap().get("lbl").toString(), ((InternalNode) record.asMap().get("sp")).asMap())));
		return scripts;
	}

	@Override
	public <TRX> List<Vertex> getScriptConfigItems(TRX trx, String scriptGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getScriptConfigItems(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> scripts = new LinkedList<>();
		records.forEach(record -> scripts.add(new Vertex(parseLabel(record), parseNode(record, "cf"),
				new Relation(null, null, parseRelationship(record, "r")))));
		return scripts;
	}

	@Override
	public <TRX> List<Vertex> getScriptAssets(TRX trx, String scriptGuid, ScriptTemplateScope scope) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptGuid);
		List<Record> records = null;
		if (scope == ScriptTemplateScope.PUBLIC) {
			records = baseDao.executeQuery(trx, scriptQuery.getScriptPublicAssets(), bindParams);
		} else if (scope == ScriptTemplateScope.PRIVATE) {
			records = baseDao.executeQuery(trx, scriptQuery.getScriptAppliesToAssets(), bindParams);
		}
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		List<Vertex> scripts = new LinkedList<>();
		records.forEach(record -> scripts.add(new Vertex(parseLabel(record), parseNode(record, "ast"))));
		return scripts;
	}

	@Override
	public <TRX> List<Vertex> getScriptAppliesToAssets(TRX trx, String scriptGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getScriptAppliesToAssets(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> scripts = new LinkedList<>();
		records.forEach(record -> scripts.add(new Vertex(parseLabel(record), parseNode(record, "ast"))));
		return scripts;
	}

	@Override
	public <TRX> Edge scriptHasCongigItem(TRX trx, String scriptGuid, String configItemLabel, String configItemGuid,
			String configValue) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid,
				configItemLabel, SchemaConstants.PROP_HDMFID_NAME, configItemGuid, SchemaConstants.PROP_VALUE,
				configValue, Relationship.HAS, Direction.OUT);
	}

	@Override
	public <TRX> Edge scriptAppliestoAsset(TRX trx, String scriptGuid, String assetOrClassLabel,
			String assetOrClassGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_SCRIPT, SchemaConstants.PROP_HDMFID_NAME, scriptGuid,
				assetOrClassLabel, SchemaConstants.PROP_HDMFID_NAME, assetOrClassGuid, Relationship.APPLIES_TO,
				Direction.OUT);
	}

	@Override
	public <TRX> List<Vertex> getScriptDataItems(TRX trx, String scriptGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getScriptDataItems(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> dataItems = new LinkedList<>();
		records.forEach(record -> dataItems.add(new Vertex(parseLabel(record), parseNode(record, "di"),
				new Relation(null, null, parseRelationship(record, "r")))));
		return dataItems;
	}

	@Override
	public <TRX> List<Vertex> getScriptEvents(TRX trx, String scriptGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getScriptEvents(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> events = new LinkedList<>();
		records.forEach(record -> events.add(new Vertex(parseLabel(record), parseNode(record, "evts"),
				new Relation(null, null, parseRelationship(record, "r")))));
		return events;
	}

	@Override
	public <TRX> long countScriptTemplateInstance(TRX trx, String scriptTemplateGuid, String assetGuid,
			String astLabel) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.SRC, scriptTemplateGuid);
		bindParams.put(BindConstants.DEST, assetGuid);
		List<Record> records = baseDao.executeQuery(trx,
				destLabelQueryBuilder(scriptQuery.countScriptTemplateInstance(), astLabel), bindParams);
		return records.get(0).get("cnt", 0L);
	}

	@Override
	public <TRX> List<Vertex> getScriptAdminUnits(TRX trx, String scriptGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getScriptAdminUnits(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> adminUnits = new LinkedList<>();
		records.forEach(record -> adminUnits.add(new Vertex(parseLabel(record), parseNode(record, "adu"),
				new Relation(null, null, parseRelationship(record, "r")))));
		return adminUnits;
	}

	@Override
	public <TRX> List<Vertex> getOtherScripts(TRX trx, String scriptGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getOtherScripts(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> scripts = new LinkedList<>();
		records.forEach(record -> scripts.add(new Vertex(parseLabel(record), parseNode(record, "sp"),
				new Relation(null, null, parseRelationship(record, "r")))));
		return scripts;
	}

	@Override
	public <TRX> List<Vertex> getScriptsByScriptTemplate(TRX trx, String scriptTemplateGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, scriptTemplateGuid);
		List<Record> records = baseDao.executeQuery(trx, scriptQuery.getScriptByScriptTemplate(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> scripts = new LinkedList<>();
		records.forEach(record -> scripts.add(new Vertex(parseLabel(record), parseNode(record, "sc"),
				new Relation(null, null, parseRelationship(record, "r")))));
		return scripts;
	}

	public <TRX> List<Vertex> getScriptTemplateInstance(TRX trx, String scriptTemplateGuid, String assetGuid,
			String astLabel) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.SRC, scriptTemplateGuid);
		bindParams.put(BindConstants.DEST, assetGuid);
		List<Record> records = baseDao.executeQuery(trx,
				destLabelQueryBuilder(scriptQuery.getScriptTemplateInstance(), astLabel), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> scripts = new LinkedList<>();
		records.forEach(record -> scripts.add(new Vertex(parseLabel(record), parseNode(record, "st"))));
		return scripts;
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
		String query = createEdgeQueryBuilder(scriptQuery.getCreateLink(), handleHyphen(srcLabel),
				handleHyphen(destLabel), relType);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

}
