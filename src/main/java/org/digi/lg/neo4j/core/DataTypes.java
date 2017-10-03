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

public enum DataTypes {
	STRING("String"), INT("int"), DOUBLE("Double"), BOOLEAN("boolean");
	private String type;

	private DataTypes(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
