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
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.digi.lg.neo4j.exception.DataException;

public class JsonToDB {
	private static final Pattern DB_COLUMN_REGEX = Pattern.compile("[^A-Za-z0-9\\s_\\-]");

	private JsonToDB() {
	}

	public static String get(String key) {
		return toDbColumn(key).orElse(key);
	}

	private static Optional<String> toDbColumn(String input) {
		if (StringUtils.isEmpty(input))
			return Optional.empty();

		String trimInput = null;
		boolean isMandatoryColumn = false;
		if (input.charAt(0) == '!') {
			trimInput = input.substring(1, input.length()).trim();
			isMandatoryColumn = true;
		} else {
			trimInput = input.trim();
		}

		if (DB_COLUMN_REGEX.matcher(trimInput).find()) {
			throw new DataException("invalid DB column,input:{}..!!", input);
		}
		String[] newStings = StringUtils.splitByCharacterTypeCamelCase(trimInput);
		if (ArrayUtils.isEmpty(newStings))
			return Optional.empty();
		StringBuilder columnBuilder = new StringBuilder(isMandatoryColumn ? SchemaConstants.MANDATORY_PROP_SYMBOL : "");
		for (int i = 0; i < newStings.length; i++) {
			String str = newStings[i].trim();
			if (SchemaConstants.UNDER_SCORE.equals(str) || StringUtils.isEmpty(str))
				continue;
			if (i > 0)
				columnBuilder.append(SchemaConstants.UNDER_SCORE);
			columnBuilder.append(str.toLowerCase());
		}
		return Optional.of(columnBuilder.toString());
	}

}
