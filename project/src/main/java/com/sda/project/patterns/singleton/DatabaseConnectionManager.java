package com.sda.project.patterns.singleton;

public class DatabaseConnectionManager {
	private static DatabaseConnectionManager instance;

	private DatabaseConnectionManager() {
	}

	public static synchronized DatabaseConnectionManager getInstance() {
		if (instance == null) {
			instance = new DatabaseConnectionManager();
		}
		return instance;
	}

	public String getConnectionStatus() {
		return "Connected to StreamFlixTv MySQL DB";
	}
}
