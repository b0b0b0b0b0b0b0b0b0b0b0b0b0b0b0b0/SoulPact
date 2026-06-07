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
