package com.neo4j.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

import com.neo4j.core.BaseDao;
import com.neo4j.core.BaseDaoImpl;
import com.neo4j.core.GraphContext;
import com.neo4j.core.GraphFactory;
import com.neo4j.core.GraphFactoryImpl;
import com.neo4j.core.TrxHandler;

public class FriendDaoImplTestIT {
	private FriendDao friendsDao;
	private BaseDao baseDao;
	private GraphFactory graphFactory;

	@Before
	public void setUp() {
		String url = GraphContext.getConfig().getProperty("db.url");
		String user = GraphContext.getConfig().getProperty("db.user");
		String password = GraphContext.getConfig().getProperty("db.password");

		graphFactory = new GraphFactoryImpl(url, user, password);
		graphFactory.init();

		baseDao = new BaseDaoImpl(graphFactory);
		friendsDao = new FriendDaoImpl(baseDao);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSave() throws Exception {
		Session session = graphFactory.writeSession();
		Record record = new TrxHandler<Record>(session) {
			@Override
			public Record block(Transaction transaction) {
				Record record = friendsDao.save(transaction, "john", 20);
				return record;
			}
		}.execute();

		System.out.println("friend:" + record);

		graphFactory.closeSession(session);
	}
	
	
	@Test
	public void testUpdate() throws Exception {
		Session session = graphFactory.writeSession();
		Record record = new TrxHandler<Record>(session) {
			@Override
			public Record block(Transaction transaction) {
				Record record = friendsDao.friendConnectToFriend(transaction, "john", "tom");
				return record;
			}
		}.execute();

		System.out.println("friend:" + record);

		graphFactory.closeSession(session);
	}

	
	@Test
	public void testMerge() throws Exception {
		Session session = graphFactory.writeSession();
		Record record = new TrxHandler<Record>(session) {
			@Override
			public Record block(Transaction transaction) {
				Record record = friendsDao.merge(transaction, "tom", 23);
				return record;
			}
		}.execute();

		System.out.println("friend:" + record);

		graphFactory.closeSession(session);
	}
	
	
	@Test
	public void testFriendConnectToFriend() throws Exception {
		Session session = graphFactory.writeSession();
		Record record = new TrxHandler<Record>(session) {
			@Override
			public Record block(Transaction transaction) {
				Record record = friendsDao.friendConnectToFriend(transaction, "john", "tom");
				return record;
			}
		}.execute();

		System.out.println("friend:" + record);

		graphFactory.closeSession(session);
	}
	
	@Test
	public void testDelete() throws Exception {
		Session session = graphFactory.writeSession();
		Record record = new TrxHandler<Record>(session) {
			@Override
			public Record block(Transaction transaction) {
				Record record = friendsDao.delete(transaction, "john");
				return record;
			}
		}.execute();

		System.out.println("friend:" + record);

		graphFactory.closeSession(session);
	}
}
