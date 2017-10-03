package org.digi.lg.neo4j.core;

public enum ScriptTemplateScope {
	PUBLIC("public"), PRIVATE("private");

	private String scope;

	private ScriptTemplateScope(String scope) {
		this.scope = scope;
	}

	public String getType() {
		return this.scope;
	}

	public static ScriptTemplateScope stringToEnum(String value) {
		if (PUBLIC.name().equalsIgnoreCase(value)) {
			return PUBLIC;
		} else if (PRIVATE.name().equalsIgnoreCase(value)) {
			return PRIVATE;
		} else {
			throw new RuntimeException("enum not defined:" + value);
		}
	}
}
