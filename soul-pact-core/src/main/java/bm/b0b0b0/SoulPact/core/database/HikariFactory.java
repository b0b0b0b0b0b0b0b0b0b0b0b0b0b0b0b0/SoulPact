package bm.b0b0b0.SoulPact.core.database;

import bm.b0b0b0.SoulPact.core.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public final class HikariFactory {

    private HikariFactory() {
    }

    public static HikariDataSource create(JavaPlugin plugin, DatabaseConfig databaseConfig) {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(databaseConfig.poolConfig().maximumPoolSize());
        config.setMinimumIdle(databaseConfig.poolConfig().minimumIdle());
        config.setConnectionTimeout(databaseConfig.poolConfig().connectionTimeoutMs());
        config.setIdleTimeout(databaseConfig.poolConfig().idleTimeoutMs());
        config.setMaxLifetime(databaseConfig.poolConfig().maxLifetimeMs());
        applyStorageSettings(plugin, databaseConfig, config);
        return new HikariDataSource(config);
    }

    private static void applyStorageSettings(JavaPlugin plugin, DatabaseConfig databaseConfig, HikariConfig config) {
        if (databaseConfig.storageType() == DatabaseConfig.StorageType.MYSQL) {
            config.setJdbcUrl(buildMysqlUrl(databaseConfig));
            config.setUsername(databaseConfig.mysqlUsername());
            config.setPassword(databaseConfig.mysqlPassword());
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            return;
        }
        File databaseFile = SqliteDatabasePaths.resolveFile(plugin, databaseConfig);
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");
    }

    private static String buildMysqlUrl(DatabaseConfig databaseConfig) {
        return "jdbc:mysql://"
                + databaseConfig.mysqlHost()
                + ":"
                + databaseConfig.mysqlPort()
                + "/"
                + databaseConfig.mysqlDatabase()
                + "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8";
    }
}
