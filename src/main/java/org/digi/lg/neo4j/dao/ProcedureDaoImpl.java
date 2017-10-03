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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.Vertex;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;

public class ProcedureDaoImpl implements ProcedureDao {

	private final GraphFactory graphFactoryRW;
	private final GraphFactory graphFactoryRD;
	private final BaseDao baseDao;

	protected ProcedureDaoImpl(final GraphFactory graphFactoryRW, final GraphFactory graphFactoryRD,
			final BaseDao baseDao) {
		this.graphFactoryRW = checkNotNull(graphFactoryRW);
		this.graphFactoryRD = checkNotNull(graphFactoryRD);
		this.baseDao = checkNotNull(baseDao);
	}

	@Override
	public List<Vertex> getVertices(String query, Map<String, Object> params) {
		Session session = null;
		try {
			session = graphFactoryRD.readSession();
			List<Record> records = baseDao.executeQuery(session, query, params);

			if (records == null || records.isEmpty())
				return Collections.emptyList();

			List<Vertex> vertexs = new LinkedList<>();
			records.forEach(x -> vertexs.add(
					new Vertex(x.asMap().get("label").toString(), ((InternalNode) x.asMap().get("value")).asMap())));
			return vertexs;
		} catch (Exception e) {
			throw e;
		} finally {
			graphFactoryRW.closeSession(session);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getMap(String query, Map<String, Object> params) {
		Session session = null;
		try {
			session = graphFactoryRD.readSession();
			List<Record> records = baseDao.executeQuery(session, query, params);

			if (records == null || records.isEmpty())
				return Collections.emptyList();

			List<Map<String, Object>> list = new LinkedList<>();
			records.forEach(x -> list.add((Map<String, Object>) x.asMap().get("value")));
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			graphFactoryRW.closeSession(session);
		}
	}

	@Override
	public List<List<Map<String, Object>>> getListMap(String query, Map<String, Object> params) {
		Session session = null;
		try {
			session = graphFactoryRD.readSession();
			List<Record> records = baseDao.executeQuery(session, query, params);

			if (records == null || records.isEmpty())
				return Collections.emptyList();

			List<List<Map<String, Object>>> list = new LinkedList<>();
			records.forEach(x -> list.add((List<Map<String, Object>>) x.asMap().get("value")));
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			graphFactoryRW.closeSession(session);
		}
	}

}
