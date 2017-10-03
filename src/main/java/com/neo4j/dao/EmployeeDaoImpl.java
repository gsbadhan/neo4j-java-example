
package com.neo4j.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Record;

import com.neo4j.core.BaseDao;

public class EmployeeDaoImpl implements EmployeeDao {
	private BaseDao baseDao;

	public EmployeeDaoImpl(final BaseDao baseDao) {
		this.baseDao = checkNotNull(baseDao);
	}

	@Override
	public <TRX> Record save(TRX trx, Map<String, Object> params) {
		String query="";
		List<Record> records=baseDao.executeQuery(trx, query, params);
		if(records.isEmpty())
			return null;
		return records.get(0);
	}

	@Override
	public <TRX> Record update(TRX trx, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <TRX> Record delete(TRX trx, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}


}
