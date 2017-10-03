
package com.neo4j.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;


public class GraphContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphContext.class);
	private static final String CONFIG_FILE = "graph-config.json";
	private static Properties properties;

	private GraphContext() {
	}

	static {
		InputStream graphConfigFile = null;
		try {
			// load graph-config.json properties
			String currentFilePath = new File(".").getCanonicalPath() + File.separator + CONFIG_FILE;
			if (new File(currentFilePath).exists()) {
				graphConfigFile = new FileInputStream(currentFilePath);
			} else {
				LOGGER.info("graph-config not found at path:{}, so loading from jar's resources..", currentFilePath);
				graphConfigFile = GraphContext.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
			}
			Gson graphConfigGson = new Gson();
			properties = graphConfigGson.fromJson(new InputStreamReader(graphConfigFile), Properties.class);
			LOGGER.info("graph configurations:{}", properties);
			graphConfigFile.close();
		} catch (Exception e) {
			LOGGER.error("error occured while loading graphcontext..!!", e);
			throw new RuntimeException(e);
		} finally {
			if (graphConfigFile != null)
				try {
					graphConfigFile.close();
				} catch (IOException e) {
					LOGGER.error("error occured while loading graphcontext..!!", e);
				}
		}

	}

	public static Properties getConfig() {
		return properties;
	}
}
