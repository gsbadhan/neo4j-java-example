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

import static com.google.common.base.Preconditions.checkNotNull;

import org.digi.lg.neo4j.core.GraphFactory;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.dao.ProductClassDao;
import org.digi.lg.neo4j.pojo.model.ProductClass;
import org.neo4j.driver.v1.Session;

public class NameCache extends AbstractCache<String, Object> {
	private final GraphFactory graphFactory;
	private final ProductClassDao productClassDao;

	protected NameCache(final GraphFactory graphFactory, final ProductClassDao productClassDao) {
		super(CacheArea.NAME.name());
		this.graphFactory = checkNotNull(graphFactory);
		this.productClassDao = checkNotNull(productClassDao);
	}

	public ProductClass getProductClass(String name) {
		Vertex vertex = (Vertex) super.get(name);
		if (vertex != null) {
			return new ProductClass(vertex);
		}
		Session session = graphFactory.readSession();
		ProductClass prdctClass = null;
		prdctClass = productClassDao.getByName(session, name);
		if (prdctClass != null)
			super.put(name, prdctClass.getVertex());
		graphFactory.closeSession(session);
		return prdctClass;
	}

}
