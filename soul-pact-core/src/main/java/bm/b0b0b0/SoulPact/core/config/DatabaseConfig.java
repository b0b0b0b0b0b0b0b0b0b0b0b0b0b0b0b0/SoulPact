package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.DatabaseSettings;

public final class DatabaseConfig {

    public enum StorageType {
        SQLITE,
        MYSQL
    }

    private final StorageType storageType;
    private final boolean failOnConnect;
    private final String storageDirectory;
    private final String sqliteFile;
    private final String mysqlHost;
    private final int mysqlPort;
    private final String mysqlDatabase;
    private final String mysqlUsername;
    private final String mysqlPassword;
    private final DatabasePoolConfig poolConfig;

    public DatabaseConfig(DatabaseSettings settings) {
        this.storageType = parseType(settings.type);
        this.failOnConnect = settings.failOnConnect;
        this.storageDirectory = settings.storageDirectory;
        this.sqliteFile = settings.sqlite.file;
        this.mysqlHost = settings.mysql.host;
        this.mysqlPort = settings.mysql.port;
        this.mysqlDatabase = settings.mysql.database;
        this.mysqlUsername = settings.mysql.username;
        this.mysqlPassword = settings.mysql.password;
        this.poolConfig = new DatabasePoolConfig(settings.pool);
    }

    public StorageType storageType() {
        return storageType;
    }

    public boolean failOnConnect() {
        return failOnConnect;
    }

    public String storageDirectory() {
        return storageDirectory;
    }

    public String sqliteFile() {
        return sqliteFile;
    }

    public String mysqlHost() {
        return mysqlHost;
    }

    public int mysqlPort() {
        return mysqlPort;
    }

    public String mysqlDatabase() {
        return mysqlDatabase;
    }

    public String mysqlUsername() {
        return mysqlUsername;
    }

    public String mysqlPassword() {
        return mysqlPassword;
    }

    public DatabasePoolConfig poolConfig() {
        return poolConfig;
    }

    private static StorageType parseType(String rawValue) {
        if (rawValue == null) {
            return StorageType.SQLITE;
        }
        return switch (rawValue.toLowerCase()) {
            case "mysql" -> StorageType.MYSQL;
            default -> StorageType.SQLITE;
        };
    }
}
