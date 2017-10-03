
package com.neo4j.dao;


import org.neo4j.driver.v1.Record;

public interface FriendDao {

	<TRX> Record save(TRX trx, String name, Integer age);

	<TRX> Record update(TRX trx, String name, Integer newAge);

	<TRX> Record delete(TRX trx, String name);
	
	<TRX> Record friendConnectToFriend(TRX trx, String nameA, String nameB);

}
