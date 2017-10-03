/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.pojo.model;

import static org.digi.lg.neo4j.dao.DaoUtil.getStr;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.dao.ScriptRepoDao;
@SuppressWarnings("serial")

public class ScriptRepo  implements Serializable {
	private Vertex vertex;

	public ScriptRepo() {
	}

	public ScriptRepo(Vertex vertex) {
		super();
		this.vertex = vertex;
	}

	public String getGuid() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_HDMFID_NAME));
	}

	public String getName() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_NAME));
	}

	public String getgitLocation() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_GIT_LOCATION));
	}
	public String getgitUser() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_GIT_USER));
	}
	public String getgitPwd() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_GIT_PWD));
	}
	public String getgitUrl() {
		return getStr(vertex.getNode().get(SchemaConstants.PROP_GIT_URL));
	}
	public String getLabel() {
		return SchemaConstants.LABEL_SCRIPT_REPO;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public static ScriptRepo rowMapper(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;

		return new ScriptRepo(new Vertex(SchemaConstants.LABEL_SCRIPT_REPO, map));
	}
}
