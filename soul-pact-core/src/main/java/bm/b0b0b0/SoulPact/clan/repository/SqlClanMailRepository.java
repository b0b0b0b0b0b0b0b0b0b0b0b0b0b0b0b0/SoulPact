package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanMail;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SqlClanMailRepository implements ClanMailRepository {

    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public SqlClanMailRepository(
            DataSourceProvider dataSourceProvider,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    @Override
    public CompletableFuture<ClanMail> send(long clanId, UUID senderId, String senderName, String message, long createdAt) {
        return asyncDatabaseExecutor.supply(() -> insertMail(clanId, senderId, senderName, message, createdAt));
    }

    @Override
    public CompletableFuture<List<ClanMail>> findPage(long clanId, int offset, int limit) {
        return asyncDatabaseExecutor.supply(() -> queryPage(clanId, offset, limit));
    }

    @Override
    public CompletableFuture<Integer> countByClanId(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryCount(clanId));
    }

    @Override
    public CompletableFuture<Integer> countUnread(long clanId, UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> queryUnreadCount(clanId, playerId));
    }

    @Override
    public CompletableFuture<Void> markRead(long clanId, UUID playerId, long readAt) {
        return asyncDatabaseExecutor.supply(() -> {
            upsertReadMark(clanId, playerId, readAt);
            return null;
        });
    }

    @Override
    public CompletableFuture<Integer> clear(long clanId) {
        return asyncDatabaseExecutor.supply(() -> deleteAll(clanId));
    }

    @Override
    public CompletableFuture<Void> trimToLimit(long clanId, int maxStored) {
        return asyncDatabaseExecutor.supply(() -> {
            deleteOverflow(clanId, maxStored);
            return null;
        });
    }

    private ClanMail insertMail(long clanId, UUID senderId, String senderName, String message, long createdAt) {
        String sql = """
                INSERT INTO clan_mail (clan_id, sender_uuid, sender_name, message, created_at)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, clanId);
            statement.setString(2, senderId.toString());
            statement.setString(3, senderName);
            statement.setString(4, message);
            statement.setLong(5, createdAt);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                long id = keys.next() ? keys.getLong(1) : 0L;
                return new ClanMail(id, clanId, senderId, senderName, message, createdAt);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to insert clan mail", exception);
        }
    }

    private List<ClanMail> queryPage(long clanId, int offset, int limit) {
        String sql = """
                SELECT id, clan_id, sender_uuid, sender_name, message, created_at
                FROM clan_mail
                WHERE clan_id = ?
                ORDER BY created_at DESC, id DESC
                LIMIT ? OFFSET ?
                """;
        List<ClanMail> mails = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    mails.add(mapMailRow(resultSet));
                }
            }
            return mails;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query clan mail page", exception);
        }
    }

    private int queryCount(long clanId) {
        String sql = "SELECT COUNT(*) AS total FROM clan_mail WHERE clan_id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("total") : 0;
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to count clan mail", exception);
        }
    }

    private int queryUnreadCount(long clanId, UUID playerId) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM clan_mail m
                WHERE m.clan_id = ?
                  AND m.created_at > COALESCE(
                      (SELECT r.last_read_at FROM clan_mail_reads r WHERE r.clan_id = m.clan_id AND r.player_uuid = ?),
                      0
                  )
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("total") : 0;
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to count unread clan mail", exception);
        }
    }

    private void upsertReadMark(long clanId, UUID playerId, long readAt) {
        String sql = """
                INSERT INTO clan_mail_reads (clan_id, player_uuid, last_read_at)
                VALUES (?, ?, ?)
                ON CONFLICT(clan_id, player_uuid) DO UPDATE SET last_read_at = excluded.last_read_at
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            statement.setLong(3, readAt);
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to mark clan mail read", exception);
        }
    }

    private int deleteAll(long clanId) {
        String sql = "DELETE FROM clan_mail WHERE clan_id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            return statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to clear clan mail", exception);
        }
    }

    private void deleteOverflow(long clanId, int maxStored) {
        String sql = """
                DELETE FROM clan_mail
                WHERE clan_id = ?
                  AND id NOT IN (
                      SELECT id FROM clan_mail
                      WHERE clan_id = ?
                      ORDER BY created_at DESC, id DESC
                      LIMIT ?
                  )
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setLong(2, clanId);
            statement.setInt(3, Math.max(1, maxStored));
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to trim clan mail", exception);
        }
    }

    private static ClanMail mapMailRow(ResultSet resultSet) throws Exception {
        return new ClanMail(
                resultSet.getLong("id"),
                resultSet.getLong("clan_id"),
                UUID.fromString(resultSet.getString("sender_uuid")),
                resultSet.getString("sender_name"),
                resultSet.getString("message"),
                resultSet.getLong("created_at")
        );
    }
}
