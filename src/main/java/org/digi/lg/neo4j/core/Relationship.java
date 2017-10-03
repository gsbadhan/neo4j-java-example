/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.core;

import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_APPLIESTO;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_BELONGS;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_COMPATIBLE;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_CONNECTEDTO;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_CONTAINS;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_GENERATES;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_HASA;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_IS;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_ISDERIVED;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_TEMPLATE;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_TOKEN;
import static org.digi.lg.neo4j.core.SchemaConstants.REL_LABEL_TRIGGER;

public enum Relationship {
	IS(REL_LABEL_IS), HAS(REL_LABEL_HASA), BELONGS(REL_LABEL_BELONGS), TOKEN(REL_LABEL_TOKEN), TEMPLATE(
			REL_LABEL_TEMPLATE), CONNECTED_TO(REL_LABEL_CONNECTEDTO), GENERATES(REL_LABEL_GENERATES), TRIGGER(
					REL_LABEL_TRIGGER), APPLIES_TO(REL_LABEL_APPLIESTO), IS_DERIVED(
							REL_LABEL_ISDERIVED), COMPATIBLE(REL_LABEL_COMPATIBLE), CONTAINS(REL_LABEL_CONTAINS);

	private String type;

	private Relationship(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public static Relationship stringToEnum(String value) {
		if (IS.name().equalsIgnoreCase(value)) {
			return IS;
		} else if (HAS.name().equalsIgnoreCase(value)) {
			return HAS;
		} else if (BELONGS.name().equalsIgnoreCase(value)) {
			return BELONGS;
		} else if (TEMPLATE.name().equalsIgnoreCase(value)) {
			return TEMPLATE;
		} else if (CONNECTED_TO.name().equalsIgnoreCase(value)) {
			return CONNECTED_TO;
		} else if (IS_DERIVED.name().equalsIgnoreCase(value)) {
			return IS_DERIVED;
		} else if (COMPATIBLE.name().equalsIgnoreCase(value)) {
			return COMPATIBLE;
		} else if (APPLIES_TO.name().equalsIgnoreCase(value)) {
			return APPLIES_TO;
		} else if (GENERATES.name().equalsIgnoreCase(value)) {
			return GENERATES;
		} else if (TRIGGER.name().equalsIgnoreCase(value)) {
			return TRIGGER;
		} else if (CONTAINS.name().equalsIgnoreCase(value)) {
			return CONTAINS;
		} else {
			throw new RuntimeException("enum not defined:" + value);
		}
	}
}
