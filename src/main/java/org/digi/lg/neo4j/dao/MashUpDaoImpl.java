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
import static org.digi.lg.neo4j.dao.DaoUtil.handleHyphen;
import static org.digi.lg.neo4j.dao.DaoUtil.parseLabel;
import static org.digi.lg.neo4j.dao.DaoUtil.parseNode;
import static org.digi.lg.neo4j.dao.DaoUtil.parseRelationship;
import static org.digi.lg.neo4j.dao.DaoUtil.srcLabelQueryBuilder;

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
import org.digi.lg.neo4j.pojo.model.Mashup;
import org.digi.lg.neo4j.queries.MashUpQuery;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;

public class MashUpDaoImpl extends CRUD implements MashUpDao {
	private final BaseDao baseDao;
	private final MashUpQuery mashupQuery;

	protected MashUpDaoImpl(final BaseDao baseDao, final MashUpQuery mashupQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.mashupQuery = checkNotNull(mashupQuery);
	}

	@Override
	public <TRX> Mashup add(TRX trx, Map<String, Object> params) {
		return new Mashup(saveUpdate(trx, SchemaConstants.LABEL_MASHUP, params));
	}

	@Override
	public <TRX> Mashup getByGuid(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, mashupQuery.getByPath(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return Mashup.rowMapper(DaoUtil.parseNode(records, "mashup"));
	}
	
	
	@Override
	public <TRX> Mashup getByPath(TRX trx, String path) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_PATH, path);
		List<Record> records = baseDao.executeQuery(trx, mashupQuery.getByPath(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return Mashup.rowMapper(DaoUtil.parseNode(records, "mashup"));
	}
	@Override
	public <TRX> Mashup getScriptRepobyPath(TRX trx, String path) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_PATH, path);
		List<Record> records = baseDao.executeQuery(trx, mashupQuery.getScriptRepobyPath(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return Mashup.rowMapper(DaoUtil.parseNode(records, "sp"));
	}

	@Override
	public <TRX> Edge mashupHasAdu(TRX trx, String mashupGuid, String aduGuid) {
		return super.createEdge(trx, SchemaConstants.LABEL_MASHUP, SchemaConstants.PROP_HDMFID_NAME,
				mashupGuid, SchemaConstants.LABEL_ADMIN_UNIT, SchemaConstants.PROP_HDMFID_NAME, aduGuid,
				Relationship.HAS, Direction.OUT);
	}

	@Override
	public <TRX> List<Vertex> getMashupInfoByAduGuid(TRX trx, String aduGuid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, aduGuid);
		List<Record> records = baseDao.executeQuery(trx, mashupQuery.getMashUpInfoByAduGuid(),
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
	public <TRX> Edge mashupHasScriptRepo(TRX trx, String anyClassLabel, Map<String, Object> params) {
		String query = srcLabelQueryBuilder(mashupQuery.getmashupHasScriptRepo(), handleHyphen(anyClassLabel));
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records == null || records.isEmpty())
			return null;
		return new Edge(parseRelationship(records, "rid"));
	}

	@Override
	public <TRX> List<Vertex> getMashupRepoItems(TRX trx, String mashupath) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_PATH, mashupath);

		List<Record> records = baseDao.executeQuery(trx, mashupQuery.getScriptrepro(), bindParams);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> scripts = new LinkedList<>();
		records.forEach(record -> scripts.add(new Vertex(record.asMap().get("lbl").toString(), ((InternalNode) record.asMap().get("sc")).asMap())));

		return scripts;
	}

	
	

	
}
