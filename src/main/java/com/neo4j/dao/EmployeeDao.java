
package com.neo4j.dao;

import java.util.Map;

import org.neo4j.driver.v1.Record;

public interface EmployeeDao {

	<TRX> Record save(TRX trx, Map<String, Object> params);

	<TRX> Record update(TRX trx, Map<String, Object> params);

	<TRX> Record delete(TRX trx, Map<String, Object> params);

}
