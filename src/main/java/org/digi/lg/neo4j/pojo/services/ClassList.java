
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

import org.digi.lg.neo4j.pojo.model.DataItem;
import org.digi.lg.neo4j.pojo.model.Events;

public class ClassList {

	private String guid;
	private List<String> assets = null;
	private List<DataItem> dis = null;
	private List<Events> events = null;

	public String getGuid() {
		return guid;
	}

	public void addAsset(String assetGuid) {
		if (assets == null) {
			assets = new ArrayList<String>();
		}
		assets.add(assetGuid);
	}

	public void addEvent(Events event) {
		if (events == null) {
			events = new ArrayList<Events>();
		}
		events.add(event);
	}

	public void addDi(DataItem dataItem) {
		if (dis == null) {
			dis = new ArrayList<DataItem>();
		}
		dis.add(dataItem);
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public List<String> getAssets() {
		return assets;
	}

	public void setAssets(List<String> assets) {
		this.assets = assets;
	}

	public List<DataItem> getDis() {
		return dis;
	}

	public void setDis(List<DataItem> dis) {
		this.dis = dis;
	}

	public List<Events> getEvents() {
		return events;
	}

	public void setEvents(List<Events> events) {
		this.events = events;
	}

}
