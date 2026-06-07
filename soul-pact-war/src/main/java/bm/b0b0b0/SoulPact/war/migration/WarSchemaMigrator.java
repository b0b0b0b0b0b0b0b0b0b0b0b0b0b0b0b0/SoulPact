package bm.b0b0b0.SoulPact.war.migration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.bukkit.plugin.java.JavaPlugin;

public final class WarSchemaMigrator {

    private final JavaPlugin plugin;
    private final DataSource dataSource;

    public WarSchemaMigrator(JavaPlugin plugin, DataSource dataSource) {
        this.plugin = plugin;
        this.dataSource = dataSource;
    }

    public void migrate() {
        try {
            runScript("database/001_war.sql");
            addColumnIfMissing("clan_wars", "attacker_flag_world", "VARCHAR(64)");
            addColumnIfMissing("clan_wars", "attacker_flag_x", "INTEGER");
            addColumnIfMissing("clan_wars", "attacker_flag_y", "INTEGER");
            addColumnIfMissing("clan_wars", "attacker_flag_z", "INTEGER");
            addColumnIfMissing("clan_wars", "defender_flag_world", "VARCHAR(64)");
            addColumnIfMissing("clan_wars", "defender_flag_x", "INTEGER");
            addColumnIfMissing("clan_wars", "defender_flag_y", "INTEGER");
            addColumnIfMissing("clan_wars", "defender_flag_z", "INTEGER");
        } catch (SQLException | IOException exception) {
            throw new IllegalStateException("Failed to migrate clan war schema", exception);
        }
    }

    private void addColumnIfMissing(String tableName, String columnName, String definition) throws SQLException {
        if (columnExists(tableName, columnName)) {
            return;
        }
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (resultSet.next()) {
                if (columnName.equalsIgnoreCase(resultSet.getString("name"))) {
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
