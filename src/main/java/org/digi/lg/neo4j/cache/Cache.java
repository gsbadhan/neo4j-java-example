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

public interface Cache<K, V> {
	V get(K key);

	void put(K key, V value);

	void put(K key, V value, long ttl);

	void remove(K key);
}
