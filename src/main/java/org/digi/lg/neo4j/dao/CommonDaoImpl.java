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
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.pojo.model.ClassMeta;
import org.digi.lg.neo4j.queries.CommonQuery;
import org.neo4j.driver.v1.Record;

public class CommonDaoImpl implements CommonDao {
	private static final String EMPTY = "";
	private static final String COMMA = ",";
	private final BaseDao baseDao;
	private final CommonQuery commonQuery;

	public CommonDaoImpl(final BaseDao baseDao, final CommonQuery commonQuery) {
		this.baseDao = checkNotNull(baseDao);
		this.commonQuery = checkNotNull(commonQuery);
	}

	@Override
	public <TRX> List<Record> addUpdateClassMeta(TRX trx, String classLabel, Map<String, Object> params) {
		params.put(BindConstants.PROP_LABEL, classLabel);
		params.put(BindConstants.PROP_TXT_SEARCH, new StringBuilder().append(classLabel.replaceAll("`", EMPTY))
				.append(COMMA).append(defaultValue(params.get(BindConstants.PROP_HDMFID_NAME))).append(COMMA)
				.append(defaultValue(params.get(BindConstants.PROP_NAME))).append(COMMA)
				.append(defaultValue(params.get(BindConstants.PROP_SERIAL_NUMBER))).toString().trim().toLowerCase());
		return baseDao.executeQuery(trx, commonQuery.getAddUpdateClassMeta(), params);
	}

	@Override
	public <TRX> List<ClassMeta> searchFromClassMeta(TRX trx, String text) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_TXT_SEARCH, text);
		List<Record> records = baseDao.executeQuery(trx, commonQuery.getSearchFromText(), bindParams);

		if (records == null || records.isEmpty())
			return Collections.emptyList();

		List<ClassMeta> list = new LinkedList<>();
		records.forEach(rcrd -> list.add(ClassMeta.rowMapper(rcrd.asMap())));
		return list;
	}

	@Override
	public <TRX> List<ClassMeta> searchFromGraph(TRX trx, String property, String text) {

		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROPERTY_X, text);
		String query = commonQuery.getSearchFromGraph().replaceFirst(SchemaConstants.PROPERTY_X, property);
		List<Record> records = baseDao.executeQuery(trx, query, bindParams);
		if (records == null || records.isEmpty())
			return Collections.emptyList();
		List<ClassMeta> list = new LinkedList<>();
		records.forEach(rcrd -> list.add(ClassMeta.rowMapper(rcrd.asMap())));
		return list;
	}

	private String defaultValue(Object obj) {
		return (obj == null ? EMPTY : obj.toString());
	}

	@Override
	public <TRX> String getLabel(TRX trx, String guid) {
		Map<String, Object> bindParams = new HashMap<>(1);
		bindParams.put(BindConstants.PROP_HDMFID_NAME, guid);
		List<Record> records = baseDao.executeQuery(trx, commonQuery.getGetLabelName(), bindParams);
		if (records == null || records.isEmpty())
			return null;
		return records.get(0).asMap().get("lbl").toString();
	}

}
