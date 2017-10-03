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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Mashup;
import org.digi.lg.neo4j.pojo.model.ScriptRepo;
import org.digi.lg.neo4j.queries.ScriptRepoQuery;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;

public class ScriptRepoDaoImpl extends CRUD implements ScriptRepoDao {
	private final BaseDao baseDao;
	private ScriptRepoQuery ScriptRepoQuery;

	protected ScriptRepoDaoImpl(final BaseDao baseDao, final ScriptRepoQuery ScriptRepoQuery) {
		super(baseDao);
		this.baseDao = checkNotNull(baseDao);
		this.ScriptRepoQuery = checkNotNull(ScriptRepoQuery);
	}

	
	@Override
	public <TRX> List<ScriptRepoDao> getScriptRepoInfo(TRX trx, String fromlabel, String fromGuid, Direction direction) {
		/*String query = null;
		switch (direction) {
		case IN:
			query = dataShardQuery.getInDsFromGuid();
			break;
		case OUT:
			query = dataShardQuery.getOutDsFromGuid();
			break;
		default:
			throw new DataException("invalid direction param:" + direction, ErrorCodes.INVALID_PARAMETER);
		}
		query = relationQueryBuilder(srcLabelQueryBuilder(query, fromlabel),
				org.digi.lg.neo4j.core.Relationship.HAS.getType());
		List<Record> records = baseDao.executeQuery(trx, query, DaoUtil.getParamMap(BindConstants.SRC, fromGuid));

		if (records == null || records.isEmpty())
			return Collections.emptyList();
*/
		List<ScriptRepoDao> dsList = new LinkedList<>();
	/*	records.forEach(record -> dsList.add(DataShard.rowMapper(((InternalNode) record.asMap().get("ds")).asMap())));
		return dsList;*/
		return dsList;
	}


	@Override
	public <TRX> ScriptRepo add(TRX trx, Map<String, Object> params) {
		return new ScriptRepo(saveUpdate(trx, SchemaConstants.LABEL_SCRIPT_REPO, params));

	}

	@Override
	public <TRX> List<Vertex> getScriptRepo(TRX trx) {
		
		List<Record> records = baseDao.executeQuery(trx, ScriptRepoQuery.getScriptRepoInfo(),null);
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}
		List<Vertex> list = new LinkedList<>();
		records.forEach(record -> list.add(
				new Vertex(record.asMap().get("lbl").toString(), ((InternalNode) record.asMap().get("spt")).asMap())));
		return list;
	}


	@Override
	public <TRX> ScriptRepo getByUrl(TRX trx, String url) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_GIT_URL, url);
		List<Record> records = baseDao.executeQuery(trx, ScriptRepoQuery.getByUrl(), bindParams);
		if (records == null || records.isEmpty()) {
			return null;
		}
		return ScriptRepo.rowMapper(DaoUtil.parseNode(records, "spt"));
	}


	


	
}
