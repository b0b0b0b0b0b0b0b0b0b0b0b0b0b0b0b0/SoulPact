package bm.b0b0b0.SoulPact.clanholo.repository;

import bm.b0b0b0.SoulPact.clanholo.model.ClanHologram;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.sql.DataSource;

public final class SqlHologramRepository implements HologramRepository {

    private final DataSource dataSource;

    public SqlHologramRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CompletableFuture<Optional<ClanHologram>> findById(long id) {
        return supply(() -> queryById(id));
    }

    @Override
    public CompletableFuture<Optional<ClanHologram>> findByName(long clanId, String name) {
        return supply(() -> queryByName(clanId, name));
    }

    @Override
    public CompletableFuture<List<ClanHologram>> findByClanId(long clanId) {
        return supply(() -> queryByClanId(clanId));
    }

    @Override
    public CompletableFuture<Integer> countByClanId(long clanId) {
        return supply(() -> queryCount(clanId));
    }

    @Override
    public CompletableFuture<ClanHologram> create(ClanHologram hologram) {
        return supply(() -> insert(hologram));
    }

    @Override
    public CompletableFuture<Boolean> delete(long id) {
        return supply(() -> deleteById(id));
    }

    @Override
    public CompletableFuture<Boolean> deleteByClanId(long clanId) {
        return supply(() -> deleteAllForClan(clanId));
    }

    @Override
    public CompletableFuture<Boolean> replaceLines(long hologramId, List<String> lines) {
        return supply(() -> updateLines(hologramId, lines));
    }

    @Override
    public CompletableFuture<List<ClanHologram>> findAll() {
        return supply(this::queryAll);
    }

    private <T> CompletableFuture<T> supply(SqlSupplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (SQLException exception) {
                throw new IllegalStateException("Clan hologram database error", exception);
            }
        });
    }

    private Optional<ClanHologram> queryById(long id) throws SQLException {
        String sql = """
                SELECT id, clan_id, name, world, x, y, z, creator_uuid, creator_name, template, created_at
                FROM ch_holograms WHERE id = ? LIMIT 1
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(resultSet, queryLines(connection, id)));
            }
        }
    }

    private Optional<ClanHologram> queryByName(long clanId, String name) throws SQLException {
        String sql = """
                SELECT id, clan_id, name, world, x, y, z, creator_uuid, creator_name, template, created_at
                FROM ch_holograms WHERE clan_id = ? AND LOWER(name) = LOWER(?) LIMIT 1
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                long id = resultSet.getLong("id");
                return Optional.of(mapRow(resultSet, queryLines(connection, id)));
            }
        }
    }

    private List<ClanHologram> queryByClanId(long clanId) throws SQLException {
        String sql = """
                SELECT id, clan_id, name, world, x, y, z, creator_uuid, creator_name, template, created_at
                FROM ch_holograms WHERE clan_id = ? ORDER BY name
                """;
        List<ClanHologram> holograms = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    holograms.add(mapRow(resultSet, queryLines(connection, id)));
                }
            }
        }
        return holograms;
    }

    private List<ClanHologram> queryAll() throws SQLException {
        String sql = """
                SELECT id, clan_id, name, world, x, y, z, creator_uuid, creator_name, template, created_at
                FROM ch_holograms ORDER BY clan_id, name
                """;
        List<ClanHologram> holograms = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                holograms.add(mapRow(resultSet, queryLines(connection, id)));
            }
        }
        return holograms;
    }

    private int queryCount(long clanId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM ch_holograms WHERE clan_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("total") : 0;
            }
        }
    }

    private ClanHologram insert(ClanHologram hologram) throws SQLException {
        String sql = """
                INSERT INTO ch_holograms (clan_id, name, world, x, y, z, creator_uuid, creator_name, template, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, hologram.clanId());
            statement.setString(2, hologram.name());
            statement.setString(3, hologram.world());
            statement.setDouble(4, hologram.x());
            statement.setDouble(5, hologram.y());
            statement.setDouble(6, hologram.z());
            statement.setString(7, hologram.creatorId().toString());
            statement.setString(8, hologram.creatorName());
            statement.setString(9, hologram.template() == null ? "" : hologram.template());
            statement.setLong(10, hologram.createdAt());
            statement.executeUpdate();
            long id;
            try (ResultSet keys = statement.getGeneratedKeys()) {
                id = keys.next() ? keys.getLong(1) : 0L;
            }
            updateLines(connection, id, hologram.lines());
            return new ClanHologram(
                    id,
                    hologram.clanId(),
                    hologram.name(),
                    hologram.world(),
                    hologram.x(),
                    hologram.y(),
                    hologram.z(),
                    hologram.creatorId(),
                    hologram.creatorName(),
                    hologram.template(),
                    hologram.createdAt(),
                    List.copyOf(hologram.lines())
            );
        }
    }

    private boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM ch_holograms WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private boolean deleteAllForClan(long clanId) throws SQLException {
        String sql = "DELETE FROM ch_holograms WHERE clan_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.executeUpdate();
            return true;
        }
    }

    private boolean updateLines(long hologramId, List<String> lines) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            updateLines(connection, hologramId, lines);
            return true;
        }
    }

    private static void updateLines(Connection connection, long hologramId, List<String> lines) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM ch_hologram_lines WHERE hologram_id = ?")) {
            delete.setLong(1, hologramId);
            delete.executeUpdate();
        }
        String insertSql = "INSERT INTO ch_hologram_lines (hologram_id, line_index, content) VALUES (?, ?, ?)";
        try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
            for (int index = 0; index < lines.size(); index++) {
                insert.setLong(1, hologramId);
                insert.setInt(2, index);
                insert.setString(3, lines.get(index));
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private static List<String> queryLines(Connection connection, long hologramId) throws SQLException {
        String sql = "SELECT content FROM ch_hologram_lines WHERE hologram_id = ? ORDER BY line_index";
        List<String> lines = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, hologramId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    lines.add(resultSet.getString("content"));
                }
            }
        }
        return lines;
    }

    private static ClanHologram mapRow(ResultSet resultSet, List<String> lines) throws SQLException {
        return new ClanHologram(
                resultSet.getLong("id"),
                resultSet.getLong("clan_id"),
                resultSet.getString("name"),
                resultSet.getString("world"),
                resultSet.getDouble("x"),
                resultSet.getDouble("y"),
                resultSet.getDouble("z"),
                UUID.fromString(resultSet.getString("creator_uuid")),
                resultSet.getString("creator_name"),
                resultSet.getString("template"),
                resultSet.getLong("created_at"),
                List.copyOf(lines)
        );
    }

    @FunctionalInterface
    private interface SqlSupplier<T> {
        T get() throws SQLException;
    }
}
