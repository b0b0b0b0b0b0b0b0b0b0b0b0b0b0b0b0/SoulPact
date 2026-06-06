package bm.b0b0b0.SoulPact.core.database;

import bm.b0b0b0.SoulPact.core.config.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public final class DatabaseBootstrap {

    private final JavaPlugin plugin;
    private final DatabaseConfig databaseConfig;
    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public DatabaseBootstrap(
            JavaPlugin plugin,
            DatabaseConfig databaseConfig,
            DataSourceProvider dataSourceProvider,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.plugin = plugin;
        this.databaseConfig = databaseConfig;
        this.dataSourceProvider = dataSourceProvider;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public CompletableFuture<Boolean> start() {
        return asyncDatabaseExecutor.supply(this::initialize);
    }

    public void shutdown() {
        dataSourceProvider.shutdown();
    }

    private boolean initialize() {
        try {
            HikariDataSource dataSource = HikariFactory.create(plugin, databaseConfig);
            SchemaMigrator migrator = new SchemaMigrator(plugin, dataSource);
            migrator.migrate();
            dataSourceProvider.assign(dataSource);
            return true;
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Database initialization failed", exception);
            return !databaseConfig.failOnConnect();
        }
    }
}
