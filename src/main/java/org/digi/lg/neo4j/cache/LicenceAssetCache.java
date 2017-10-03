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

import com.hazelcast.com.eclipsesource.json.JsonObject;

public class LicenceAssetCache extends AbstractCache<String, Object> {

	public LicenceAssetCache() {
		super(CacheArea.LICENCE.name());
	}

	public void put(String key,JsonObject asset) {
		
		super.put(key, asset);
	}

	public String getKey(String key) {
		return new StringBuilder().toString();
	}

}
