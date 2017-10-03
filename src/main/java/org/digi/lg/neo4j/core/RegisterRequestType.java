package org.digi.lg.neo4j.core;

import org.digi.lg.neo4j.exception.DataException;

public enum RegisterRequestType {
	PRE_REGISTERED("preRegistered");

	private String type;

	private RegisterRequestType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static RegisterRequestType toEnum(String type) {
		if (type.equalsIgnoreCase(PRE_REGISTERED.getType())) {
			return PRE_REGISTERED;
		} else {
			new DataException("Invalid type {} ..!!", ErrorCodes.INVALID_PARAMETER, type);
		}
		return null;
	}

}
