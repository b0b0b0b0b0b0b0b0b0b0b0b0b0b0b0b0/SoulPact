package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanHome;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class SqlClanHomeRepository implements ClanHomeRepository {

    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public SqlClanHomeRepository(
            DataSourceProvider dataSourceProvider,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    @Override
    public CompletableFuture<Optional<ClanHome>> create(ClanHome home) {
        return asyncDatabaseExecutor.supply(() -> insertHome(home));
    }

    @Override
    public CompletableFuture<Boolean> delete(long clanId, String name) {
        return asyncDatabaseExecutor.supply(() -> deleteHome(clanId, name));
    }

    @Override
    public CompletableFuture<Optional<ClanHome>> findByName(long clanId, String name) {
        return asyncDatabaseExecutor.supply(() -> queryByName(clanId, name));
    }

    @Override
    public CompletableFuture<List<ClanHome>> findByClanId(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryByClanId(clanId));
    }

    @Override
    public CompletableFuture<Integer> countByClanId(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryCount(clanId));
    }

    private Optional<ClanHome> insertHome(ClanHome home) {
        String sql = """
                INSERT INTO clan_homes (clan_id, name, world, x, y, z, yaw, pitch, password_hash, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, home.clanId());
            statement.setString(2, home.name());
            statement.setString(3, home.world());
            statement.setDouble(4, home.x());
            statement.setDouble(5, home.y());
            statement.setDouble(6, home.z());
            statement.setFloat(7, home.yaw());
            statement.setFloat(8, home.pitch());
            statement.setString(9, home.passwordHash() == null ? "" : home.passwordHash());
            statement.setLong(10, home.createdAt());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                long id = keys.next() ? keys.getLong(1) : 0L;
                return Optional.of(new ClanHome(
                        id,
                        home.clanId(),
                        home.name(),
                        home.world(),
                        home.x(),
                        home.y(),
                        home.z(),
                        home.yaw(),
                        home.pitch(),
                        home.passwordHash(),
                        home.createdAt()
                ));
            }
        } catch (SQLException exception) {
            if (isUniqueViolation(exception)) {
                return Optional.empty();
            }
            throw new IllegalStateException("Failed to insert clan home", exception);
        }
    }

    private boolean deleteHome(long clanId, String name) {
        String sql = "DELETE FROM clan_homes WHERE clan_id = ? AND LOWER(name) = LOWER(?)";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, name);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to delete clan home", exception);
        }
    }

    private Optional<ClanHome> queryByName(long clanId, String name) {
        String sql = """
                SELECT id, clan_id, name, world, x, y, z, yaw, pitch, password_hash, created_at
                FROM clan_homes
                WHERE clan_id = ? AND LOWER(name) = LOWER(?)
                LIMIT 1
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapHomeRow(resultSet));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query clan home", exception);
        }
    }

    private List<ClanHome> queryByClanId(long clanId) {
        String sql = """
                SELECT id, clan_id, name, world, x, y, z, yaw, pitch, password_hash, created_at
                FROM clan_homes
                WHERE clan_id = ?
                ORDER BY name
                """;
        List<ClanHome> homes = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    homes.add(mapHomeRow(resultSet));
                }
            }
            return homes;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query clan homes", exception);
        }
    }

    private int queryCount(long clanId) {
        String sql = "SELECT COUNT(*) AS total FROM clan_homes WHERE clan_id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("total") : 0;
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to count clan homes", exception);
        }
    }

    private static boolean isUniqueViolation(SQLException exception) {
        String message = exception.getMessage();
        return message != null && message.toUpperCase().contains("UNIQUE");
    }

    private static ClanHome mapHomeRow(ResultSet resultSet) throws Exception {
        return new ClanHome(
                resultSet.getLong("id"),
                resultSet.getLong("clan_id"),
                resultSet.getString("name"),
                resultSet.getString("world"),
                resultSet.getDouble("x"),
                resultSet.getDouble("y"),
                resultSet.getDouble("z"),
                resultSet.getFloat("yaw"),
                resultSet.getFloat("pitch"),
                resultSet.getString("password_hash"),
                resultSet.getLong("created_at")
        );
    }
}
