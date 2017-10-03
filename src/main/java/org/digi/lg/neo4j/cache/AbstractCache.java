/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.cache;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public abstract class AbstractCache<K, V> implements Cache<K, V> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCache.class);
	private static HazelcastInstance hazelcastInstance = null;
	private IMap<K, V> cacheMap = null;
	// default eviction time in minutes
	private long EVICTION_TIME = 15;
	static {
		LOGGER.info("FYI:already running instance found {},taking default HZC instance..",
				Hazelcast.getAllHazelcastInstances());
		if (!Hazelcast.getAllHazelcastInstances().isEmpty()) {
			hazelcastInstance = Hazelcast.getAllHazelcastInstances().iterator().next();
		} else {
			Config config = new Config();
			config.getNetworkConfig().setPortAutoIncrement(true);
			LOGGER.info("graph-hazelcast cache configs:{}", config);
			hazelcastInstance = Hazelcast.newHazelcastInstance(config);
		}
		LOGGER.info("graph haszelcast instance loaded.. {}", hazelcastInstance);
	}

	public AbstractCache(final String cacheArea) {
		cacheMap = hazelcastInstance.getMap(cacheArea);
	}

	@Override
	public V get(K key) {
		return cacheMap.get(key);
	}

	@Override
	public void put(K key, V value) {
		if (value != null)
			put(key, value, EVICTION_TIME);
	}

	@Override
	public void put(K key, V value, long ttl) {
		cacheMap.put(key, value, ttl, TimeUnit.MINUTES);
	}

	@Override
	public void remove(K key) {
		cacheMap.remove(key);
	}
}
