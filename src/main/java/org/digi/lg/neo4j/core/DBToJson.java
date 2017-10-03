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

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class DBToJson {
	private DBToJson() {
	}

	public static String get(String key) {
		return toJson(key).orElse(key);
	}

	private static Optional<String> toJson(String input) {
		String[] words = input.trim().split(SchemaConstants.UNDER_SCORE);
		if (words == null)
			return Optional.empty();

		StringBuilder dbColumnBuilder = new StringBuilder(words[0].toLowerCase());
		for (int i = 1; i < words.length; i++)
			dbColumnBuilder.append(StringUtils.capitalize(words[i].toLowerCase()));

		return Optional.of(dbColumnBuilder.toString());
	}

}
