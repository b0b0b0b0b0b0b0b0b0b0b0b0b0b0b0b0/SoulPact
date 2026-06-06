package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanMembershipNotification;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SqlClanMembershipNotificationRepository implements ClanMembershipNotificationRepository {

    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public SqlClanMembershipNotificationRepository(
            DataSourceProvider dataSourceProvider,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    @Override
    public CompletableFuture<Void> create(
            UUID playerId,
            String kind,
            long clanId,
            String clanTag,
            String clanName,
            long createdAt
    ) {
        return asyncDatabaseExecutor.run(() -> insertNotification(playerId, kind, clanId, clanTag, clanName, createdAt));
    }

    @Override
    public CompletableFuture<List<ClanMembershipNotification>> findByPlayerId(UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> queryByPlayerId(playerId));
    }

    @Override
    public CompletableFuture<Integer> deleteByPlayerId(UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> deleteAllByPlayerId(playerId));
    }

    private void insertNotification(
            UUID playerId,
            String kind,
            long clanId,
            String clanTag,
            String clanName,
            long createdAt
    ) {
        String sql = """
                INSERT INTO clan_membership_notifications
                (player_uuid, kind, clan_id, clan_tag, clan_name, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            statement.setString(2, kind);
            statement.setLong(3, clanId);
            statement.setString(4, clanTag);
            statement.setString(5, clanName);
            statement.setLong(6, createdAt);
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create membership notification", exception);
        }
    }

    private List<ClanMembershipNotification> queryByPlayerId(UUID playerId) {
        String sql = """
                SELECT id, player_uuid, kind, clan_id, clan_tag, clan_name, created_at
                FROM clan_membership_notifications
                WHERE player_uuid = ?
                ORDER BY created_at ASC
                """;
        List<ClanMembershipNotification> notifications = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    notifications.add(mapRow(resultSet));
                }
            }
            return notifications;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query membership notifications", exception);
        }
    }

    private int deleteAllByPlayerId(UUID playerId) {
        String sql = "DELETE FROM clan_membership_notifications WHERE player_uuid = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            return statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to delete membership notifications", exception);
        }
    }

    private static ClanMembershipNotification mapRow(ResultSet resultSet) throws Exception {
        return new ClanMembershipNotification(
                resultSet.getLong("id"),
                UUID.fromString(resultSet.getString("player_uuid")),
                resultSet.getString("kind"),
                resultSet.getLong("clan_id"),
                resultSet.getString("clan_tag"),
                resultSet.getString("clan_name"),
                resultSet.getLong("created_at")
        );
    }
}
