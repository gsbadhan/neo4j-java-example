/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.digi.lg.neo4j.core.BindConstants;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.SchemaConstants;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.services.Node;
import org.digi.lg.security.utils.KeyGenerator;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class DaoUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DaoUtil.class);
	private static final String CLASS_PREFIX = "Class_";

	private DaoUtil() {
	}

	public static String parseLabel(Record record) {
		return record.asMap().get("lbl").toString();
	}

	public static String parseLabel(List<Record> records) {
		return records.get(0).asMap().get("lbl").toString();
	}

	public static Map<String, Object> parseNode(List<Record> records, String returnVarName) {
		return ((InternalNode) records.get(0).asMap().get(returnVarName)).asMap();
	}

	public static Map<String, Object> parseNode(Record record, String returnVarName) {
		return ((InternalNode) record.asMap().get(returnVarName)).asMap();
	}

	public static Map<String, Object> parseRelationship(List<Record> records, String returnVarName) {
		return ((org.neo4j.driver.v1.types.Relationship) records.get(0).asMap().get(returnVarName)).asMap();
	}

	public static Map<String, Object> parseRelationship(Record record, String returnVarName) {
		return ((org.neo4j.driver.v1.types.Relationship) record.asMap().get(returnVarName)).asMap();
	}

	public static String getClassName(String name, boolean hyphenContained) {
		if (hyphenContained)
			return new StringBuilder().append("`").append(CLASS_PREFIX).append(name).append("`").toString();

		return CLASS_PREFIX.concat(name);
	}

	public static Map<String, Object> getClassMetaParams(Vertex vertex) {
		Map<String, Object> params = new HashMap<>(5);
		params.put(BindConstants.PROP_HDMFID_NAME, vertex.getGuid());
		params.put(BindConstants.PROP_NAME, vertex.getNode().get(SchemaConstants.PROP_NAME));
		params.put(BindConstants.PROP_SERIAL_NUMBER, vertex.getNode().get(SchemaConstants.PROP_SERIAL_NUMBER));
		return params;
	}

	public static Map<String, Object> getParamMap(Object... param) {
		Map<String, Object> map = new HashMap<>(param.length);
		int index = 0;
		while (param.length > index) {
			map.put(param[index].toString(), param[index += 1]);
			++index;
		}
		return map;
	}

	public static String createEdgeQueryBuilder(String rawQuery, String srcLabel, String destLabel, Relationship rel) {
		return rawQuery.replaceFirst(BindConstants.SRC_LBL, handleHyphen(srcLabel))
				.replaceFirst(BindConstants.DEST_LBL, handleHyphen(destLabel))
				.replaceFirst(BindConstants.REL, rel.getType());
	}

	public static String srcLabelQueryBuilder(String rawQuery, String srcClassLabel) {
		if (srcClassLabel == null)
			return rawQuery.replaceFirst(BindConstants.REPLACE_SRC_LBL, "");

		return rawQuery.replaceFirst(BindConstants.SRC_LBL, handleHyphen(srcClassLabel));
	}

	public static String destLabelQueryBuilder(String rawQuery, String destClassLabel) {
		if (destClassLabel == null)
			return rawQuery.replaceFirst(BindConstants.REPLACE_SRC_LBL, "");

		return rawQuery.replaceFirst(BindConstants.DEST_LBL, handleHyphen(destClassLabel));
	}

	public static String relationQueryBuilder(String rawQuery, String rel) {
		if (rel == null)
			return rawQuery.replaceFirst(BindConstants.REPLACE_REL, "");

		return rawQuery.replaceFirst(BindConstants.REL, rel);
	}

	public static String getBooostrapKey(String name) {
		String bootStrapKey = "";
		try {
			bootStrapKey = KeyGenerator.generateKey(name);
		} catch (Exception e) {
			LOGGER.error("error occured in getBooostrapKey..!!", e);
		}
		return bootStrapKey;
	}

	public static String handleHyphen(String classLabel) {
		if (classLabel.contains("`"))
			return classLabel;

		if (classLabel.contains("-")) {
			return "`" + classLabel + "`";
		} else {
			return classLabel;
		}
	}

	public static String handleExclamation(String property) {
		if (property.contains("`"))
			return property;

		if (property.contains("!")) {
			return "`" + property + "`";
		} else {
			return property;
		}
	}

	public static String getStr(Object value) {
		if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}

	public static Long getLong(Object value) {
		if (value != null) {
			return new Long(value.toString());
		} else {
			return null;
		}
	}

	public static Integer getInteger(Object value) {
		if (value != null) {
			return new Integer(value.toString());
		} else {
			return null;
		}
	}

	public static void getMandatoryVertex(Vertex sourceVertx, Map<String, Node<String>> output) {
		sourceVertx.getNode().keySet().forEach(propKey -> {
			if (SchemaConstants.PROP_HDMFID_NAME.equals(propKey) || SchemaConstants.PROP_NAME.equals(propKey)
					|| SchemaConstants.PROP_DB_UUID.equals(propKey) || SchemaConstants.PROP_PRINCIPAL_GUID.equals(propKey))
				return;
			Node<String> node = new Node<>();
			node.put(SchemaConstants.PROP_DATATYPE, sourceVertx.getNode().get(propKey).getClass().getSimpleName());
			if (propKey.startsWith(SchemaConstants.MANDATORY_PROP_SYMBOL)) {
				node.put(SchemaConstants.PROP_MANDATORY, String.valueOf(true));
				propKey = propKey.substring(1);
			} else {
				node.put(SchemaConstants.PROP_MANDATORY, String.valueOf(false));
			}
			node.put(SchemaConstants.PROP_TAG, propKey);
			output.put(propKey, node);
		});
	}

	public static String generateToken() {
		return UUID.randomUUID().toString();
	}

	/**
	 * generate UUID for DB
	 * 
	 * @return
	 */
	public static String generateHdmfUUID() {
		return UUID.randomUUID().toString();
	}

	public static String onEmptyorNull(String actual, String optional) {
		return Strings.isNullOrEmpty(actual) ? optional : actual;
	}

	public static String mandatory(String property) {
		return SchemaConstants.MANDATORY_PROP_SYMBOL + property;
	}

}
