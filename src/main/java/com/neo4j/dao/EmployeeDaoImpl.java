
package com.neo4j.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;

import com.neo4j.core.BaseDao;


public class EmployeeDaoImpl implements EmployeeDao {

	public EmployeeDaoImpl(final BaseDao baseDao) {
	}

	@Override
	public <TRX> Record save(TRX trx, String label, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}




}
