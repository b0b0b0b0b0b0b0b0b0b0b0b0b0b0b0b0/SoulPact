package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class WarClanLookup {

    private final SoulPactApi api;

    public WarClanLookup(SoulPactApi api) {
        this.api = api;
    }

    public CompletableFuture<Optional<ClanSnapshot>> findClan(long clanId) {
        return api.scheduler().supplyAsync(() -> queryClan(clanId));
    }

    public Optional<Long> findClanIdByPlayerSync(UUID playerId) {
        String sql = "SELECT clan_id FROM clan_members WHERE player_uuid = ? LIMIT 1";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(resultSet.getLong("clan_id"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query player clan " + playerId, exception);
        }
    }

    public Optional<String> findClanTagSync(long clanId) {
        String sql = "SELECT tag FROM clans WHERE id = ? LIMIT 1";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.ofNullable(resultSet.getString("tag"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan tag " + clanId, exception);
        }
    }

    public Optional<String> findClanNameSync(long clanId) {
        String sql = "SELECT name FROM clans WHERE id = ? LIMIT 1";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.ofNullable(resultSet.getString("name"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan name " + clanId, exception);
        }
    }

    public void incrementWarsWon(long clanId) {
        String sql = "UPDATE clans SET wars_won = wars_won + 1 WHERE id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to increment wars won for clan " + clanId, exception);
        }
    }

    private Optional<ClanSnapshot> queryClan(long clanId) {
        String sql = """
                SELECT id, tag, name, description, leader_uuid, points, max_slots, verified, friendly_fire, created_at
                FROM clans
                WHERE id = ?
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(new ClanSnapshot(
                        resultSet.getLong("id"),
                        resultSet.getString("tag"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        UUID.fromString(resultSet.getString("leader_uuid")),
                        resultSet.getInt("points"),
                        resultSet.getInt("max_slots"),
                        resultSet.getBoolean("verified"),
                        resultSet.getBoolean("friendly_fire"),
                        resultSet.getLong("created_at")
                ));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan " + clanId, exception);
        }
    }
}
