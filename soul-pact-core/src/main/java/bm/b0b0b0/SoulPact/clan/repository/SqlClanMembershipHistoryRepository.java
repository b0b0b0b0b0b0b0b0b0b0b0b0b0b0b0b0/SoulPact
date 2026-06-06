package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanMembershipHistoryEntry;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SqlClanMembershipHistoryRepository implements ClanMembershipHistoryRepository {

    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public SqlClanMembershipHistoryRepository(
            DataSourceProvider dataSourceProvider,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    @Override
    public CompletableFuture<Void> record(
            UUID playerId,
            long clanId,
            String clanTag,
            String clanName,
            String role,
            long joinedAt,
            long leftAt,
            String reason
    ) {
        return asyncDatabaseExecutor.run(() -> insertRecord(
                playerId,
                clanId,
                clanTag,
                clanName,
                role,
                joinedAt,
                leftAt,
                reason
        ));
    }

    @Override
    public CompletableFuture<List<ClanMembershipHistoryEntry>> findByPlayerId(UUID playerId, int limit) {
        return asyncDatabaseExecutor.supply(() -> queryByPlayerId(playerId, limit));
    }

    private void insertRecord(
            UUID playerId,
            long clanId,
            String clanTag,
            String clanName,
            String role,
            long joinedAt,
            long leftAt,
            String reason
    ) {
        String sql = """
                INSERT INTO clan_membership_history
                (player_uuid, clan_id, clan_tag, clan_name, role, joined_at, left_at, reason)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            statement.setLong(2, clanId);
            statement.setString(3, clanTag);
            statement.setString(4, clanName);
            statement.setString(5, role);
            statement.setLong(6, joinedAt);
            statement.setLong(7, leftAt);
            statement.setString(8, reason);
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to record membership history", exception);
        }
    }

    private List<ClanMembershipHistoryEntry> queryByPlayerId(UUID playerId, int limit) {
        String sql = """
                SELECT id, player_uuid, clan_id, clan_tag, clan_name, role, joined_at, left_at, reason
                FROM clan_membership_history
                WHERE player_uuid = ?
                ORDER BY left_at DESC
                LIMIT ?
                """;
        List<ClanMembershipHistoryEntry> entries = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            statement.setInt(2, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entries.add(mapRow(resultSet));
                }
            }
            return entries;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query membership history", exception);
        }
    }

    private static ClanMembershipHistoryEntry mapRow(ResultSet resultSet) throws Exception {
        return new ClanMembershipHistoryEntry(
                resultSet.getLong("id"),
                UUID.fromString(resultSet.getString("player_uuid")),
                resultSet.getLong("clan_id"),
                resultSet.getString("clan_tag"),
                resultSet.getString("clan_name"),
                resultSet.getString("role"),
                resultSet.getLong("joined_at"),
                resultSet.getLong("left_at"),
                resultSet.getString("reason")
        );
    }
}
