package org.digi.lg.neo4j.pojo.services;

public enum LinkIdentifier {

	classclass("classclass"), orgasset("orgasset"), assetasset("assetasset"), orgorg("orgorg"), assetclass(
			"assetclass"), orgclass(
					"orgclass"), classproductclass("classproductclass"), productclassclass("productclassclass");

	private String literal;

	LinkIdentifier(String literal) {
		this.literal = literal;
	}

	public String literal() {
		return literal;
	}

}
