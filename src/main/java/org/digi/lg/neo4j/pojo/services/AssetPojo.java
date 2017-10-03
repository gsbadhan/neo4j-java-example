
/*******************************************************************************
* Copyright (c) 2017  Wipro Digital. All rights reserved.
*
* Contributors:
*     Wipro Digital - Looking Glass Team.
*     
*      
*     Apr 26, 2017  
 *******************************************************************************/
package org.digi.lg.neo4j.pojo.services;

import java.util.ArrayList;
import java.util.List;

public class AssetPojo {

	private String guid;
	private DataShardPojo ds;
	private List<ClassList> models = null;

	public String getGuid() {
		return guid;
	}

	public void addClassList(ClassList classesList) {
		if (models == null) {
			models = new ArrayList<>();
		}
		models.add(classesList);
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	
	public List<ClassList> getModels() {
		return models;
	}

	public void setModels(List<ClassList> models) {
		this.models = models;
	}

	public DataShardPojo getDs() {
		return ds;
	}

	public void setDs(DataShardPojo ds) {
		this.ds = ds;
	}

}
