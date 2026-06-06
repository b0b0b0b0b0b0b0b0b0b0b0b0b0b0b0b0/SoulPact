package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.DatabasePoolSettings;

public final class DatabasePoolConfig {

    private final int maximumPoolSize;
    private final int minimumIdle;
    private final long connectionTimeoutMs;
    private final long idleTimeoutMs;
    private final long maxLifetimeMs;

    public DatabasePoolConfig(DatabasePoolSettings settings) {
        this.maximumPoolSize = settings.maximumPoolSize;
        this.minimumIdle = settings.minimumIdle;
        this.connectionTimeoutMs = settings.connectionTimeoutMs;
        this.idleTimeoutMs = settings.idleTimeoutMs;
        this.maxLifetimeMs = settings.maxLifetimeMs;
    }

    public int maximumPoolSize() {
        return maximumPoolSize;
    }

    public int minimumIdle() {
        return minimumIdle;
    }

    public long connectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public long idleTimeoutMs() {
        return idleTimeoutMs;
    }

    public long maxLifetimeMs() {
        return maxLifetimeMs;
    }
}
