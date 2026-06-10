package com.sda.project.patterns.singelton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationManager {
    private static final ConfigurationManager INSTANCE = new ConfigurationManager();
    private final Map<String, String> values = new ConcurrentHashMap<>();

    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        return INSTANCE;
    }

    public String get(String key) {
        return values.get(key);
    }

    public void set(String key, String value) {
        values.put(key, value);
    }
}
