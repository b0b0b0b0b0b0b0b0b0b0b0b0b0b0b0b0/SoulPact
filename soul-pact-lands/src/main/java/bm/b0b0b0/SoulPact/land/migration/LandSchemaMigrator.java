package bm.b0b0b0.SoulPact.land.migration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.bukkit.plugin.java.JavaPlugin;

public final class LandSchemaMigrator {

    private final JavaPlugin plugin;
    private final DataSource dataSource;

    public LandSchemaMigrator(JavaPlugin plugin, DataSource dataSource) {
        this.plugin = plugin;
        this.dataSource = dataSource;
    }

    public void migrate() {
        try {
            runScript("database/001_clan_bases.sql");
            try (Connection connection = dataSource.getConnection()) {
                ensureColumn(connection, "clan_base_border_blocks", "original_material", "VARCHAR(64)");
                ensureColumn(connection, "clan_bases", "border_material", "VARCHAR(64)");
                ensureColumn(connection, "clan_bases", "extent_x_pos", "INTEGER");
                ensureColumn(connection, "clan_bases", "extent_x_neg", "INTEGER");
                ensureColumn(connection, "clan_bases", "extent_z_pos", "INTEGER");
                ensureColumn(connection, "clan_bases", "extent_z_neg", "INTEGER");
                ensureColumn(connection, "clan_bases", "standard_uid", "VARCHAR(36)");
            }
        } catch (SQLException | IOException exception) {
            throw new IllegalStateException("Failed to migrate clan lands schema", exception);
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
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(null, null, table, column)) {
            return resultSet.next();
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
