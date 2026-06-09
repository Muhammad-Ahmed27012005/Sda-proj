package com.sda.project.patterns.singelton;

public class DatabaseConnectionManager {
    private static final DatabaseConnectionManager INSTANCE = new DatabaseConnectionManager();
    private boolean connected;

    private DatabaseConnectionManager() {
    }

    public static DatabaseConnectionManager getInstance() {
        return INSTANCE;
    }

    public void connect() {
        connected = true;
    }

    public void disconnect() {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}
