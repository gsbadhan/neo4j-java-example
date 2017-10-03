
package com.neo4j.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Record;

import com.neo4j.core.BaseDao;

public class FriendDaoImpl implements FriendDao {
	private BaseDao baseDao;

	public FriendDaoImpl(final BaseDao baseDao) {
		this.baseDao = checkNotNull(baseDao);
	}

	@Override
	public <TRX> Record save(TRX trx, String name, Integer age) {
		String query = "create (f:friends{name:{name},age:{age}}) return f";
		Map<String, Object> params = new HashMap<>(2);
		params.put("name", name);
		params.put("age", age);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records.isEmpty())
			return null;
		return records.get(0);
	}

	@Override
	public <TRX> Record update(TRX trx, String name, Integer newAge) {
		String query = "match(f:friends{name:{name}}) set f.age={age} return f";
		Map<String, Object> params = new HashMap<>(2);
		params.put("name", name);
		params.put("age", newAge);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records.isEmpty())
			return null;
		return records.get(0);
	}

	@Override
	public <TRX> Record delete(TRX trx, String name) {
		String query = "match(f:friends{name:{name}}) detach delete f return f";
		Map<String, Object> params = new HashMap<>(2);
		params.put("name", name);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records.isEmpty())
			return null;
		return records.get(0);

	}

	@Override
	public <TRX> Record friendConnectToFriend(TRX trx, String nameA, String nameB) {
		String query = "match(a:friends{name:{nameA}}),(b:friends{name:{nameB}}) create (a)-[:connect]->(b) return a,b";
		Map<String, Object> params = new HashMap<>(2);
		params.put("nameA", nameA);
		params.put("nameB", nameB);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records.isEmpty())
			return null;
		return records.get(0);
	}

	@Override
	public <TRX> Record merge(TRX trx, String name, Integer age) {
		String query = "merge (f:friends{name:{name},age:{age}}) return f";
		Map<String, Object> params = new HashMap<>(2);
		params.put("name", name);
		params.put("age", age);
		List<Record> records = baseDao.executeQuery(trx, query, params);
		if (records.isEmpty())
			return null;
		return records.get(0);
	
	}

}
