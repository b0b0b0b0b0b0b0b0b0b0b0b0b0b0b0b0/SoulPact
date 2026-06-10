package bm.b0b0b0.SoulPact.core.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.bukkit.plugin.java.JavaPlugin;

public final class SchemaMigrator {

    private final JavaPlugin plugin;
    private final DataSource dataSource;

    public SchemaMigrator(JavaPlugin plugin, DataSource dataSource) {
        this.plugin = plugin;
        this.dataSource = dataSource;
    }

    public void migrate() throws SQLException, IOException {
        runScript("database/001_initial.sql");
        runScript("database/002_membership_pending.sql");
        runScript("database/003_membership_extras.sql");
        runScript("database/004_role_permissions.sql");
        runScript("database/005_mail_homes.sql");
        try (Connection connection = dataSource.getConnection()) {
            ensureColumn(connection, "clans", "join_requests_open", "INTEGER NOT NULL DEFAULT 1");
            ensureColumn(connection, "clans", "banner_data", "TEXT");
            ensureColumn(connection, "clans", "standard_issued", "INTEGER NOT NULL DEFAULT 0");
            ensureColumn(connection, "clans", "wars_won", "INTEGER NOT NULL DEFAULT 0");
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("INSERT INTO schema_version(version) SELECT 1 WHERE NOT EXISTS (SELECT 1 FROM schema_version)");
            }
        }
    }

    private void ensureColumn(Connection connection, String table, String column, String definition) throws SQLException {
        if (columnExists(connection, table, column)) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        }
    }

    private boolean columnExists(Connection connection, String table, String column) throws SQLException {
        try (Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (resultSet.next()) {
                if (column.equalsIgnoreCase(resultSet.getString("name"))) {
                    return true;
                }
            }
            return false;
        }
    }

    private void runScript(String resourcePath) throws SQLException, IOException {
        List<String> statements = readStatements(resourcePath);
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.executeUpdate(sql);
            }
        }
    }

    private List<String> readStatements(String resourcePath) throws IOException {
        InputStream stream = plugin.getResource(resourcePath);
        if (stream == null) {
            throw new IOException("Missing migration resource: " + resourcePath);
        }
        List<String> statements = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                builder.append(line).append('\n');
                if (trimmed.endsWith(";")) {
                    statements.add(builder.toString().trim());
                    builder.setLength(0);
                }
            }
        }
        return statements;
    }
}
