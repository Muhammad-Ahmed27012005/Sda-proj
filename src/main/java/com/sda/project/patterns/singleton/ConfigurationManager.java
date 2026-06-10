package com.sda.project.patterns.singleton;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {
	private static ConfigurationManager instance;
	private final Map<String, String> config = new HashMap<>();

	private ConfigurationManager() {
		config.put("app.name", "StreamFlixTv");
		config.put("max.stream.quality", "4K");
	}

	public static synchronized ConfigurationManager getInstance() {
		if (instance == null) {
			instance = new ConfigurationManager();
		}
		return instance;
	}

	public String get(String key) {
		return config.get(key);
	}
}
