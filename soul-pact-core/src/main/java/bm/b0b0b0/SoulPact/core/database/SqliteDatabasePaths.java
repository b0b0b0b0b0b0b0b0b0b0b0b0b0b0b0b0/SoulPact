package bm.b0b0b0.SoulPact.core.database;

import bm.b0b0b0.SoulPact.core.config.DatabaseConfig;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public final class SqliteDatabasePaths {

    private SqliteDatabasePaths() {
    }

    public static File resolveFile(JavaPlugin plugin, DatabaseConfig databaseConfig) {
        File directory = new File(plugin.getDataFolder(), databaseConfig.storageDirectory());
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Failed to create storage directory: " + directory.getAbsolutePath());
        }
        return new File(directory, databaseConfig.sqliteFile());
    }
}
