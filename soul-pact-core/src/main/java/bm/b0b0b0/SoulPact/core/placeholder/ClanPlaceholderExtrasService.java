package bm.b0b0b0.SoulPact.core.placeholder;

import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClanPlaceholderExtrasService implements ClanPlaceholderInvalidator {

    private final DataSourceProvider dataSourceProvider;
    private final int cacheMillis;
    private final Map<Long, CachedExtras> clanCache = new ConcurrentHashMap<>();
    private final Map<UUID, CachedUnread> unreadCache = new ConcurrentHashMap<>();

    public ClanPlaceholderExtrasService(DataSourceProvider dataSourceProvider, int cacheMillis) {
        this.dataSourceProvider = dataSourceProvider;
        this.cacheMillis = cacheMillis;
    }

    public ClanPlaceholderExtras load(long clanId) {
        long now = System.currentTimeMillis();
        if (cacheMillis > 0) {
            CachedExtras cached = clanCache.get(clanId);
            if (cached != null && cached.expiresAt() > now) {
                return cached.extras();
            }
        }
        ClanPlaceholderExtras extras = query(clanId);
        if (cacheMillis > 0) {
            clanCache.put(clanId, new CachedExtras(extras, now + cacheMillis));
        }
        return extras;
    }

    public int unreadMail(long clanId, UUID playerId) {
        long now = System.currentTimeMillis();
        if (cacheMillis > 0) {
            CachedUnread cached = unreadCache.get(playerId);
            if (cached != null && cached.clanId() == clanId && cached.expiresAt() > now) {
                return cached.count();
            }
        }
        int count = queryUnread(clanId, playerId);
        if (cacheMillis > 0) {
            unreadCache.put(playerId, new CachedUnread(clanId, count, now + cacheMillis));
        }
        return count;
    }

    @Override
    public void invalidatePlayer(UUID playerId) {
        unreadCache.remove(playerId);
    }

    @Override
    public void invalidateClan(long clanId) {
        clanCache.remove(clanId);
        Iterator<Map.Entry<UUID, CachedUnread>> iterator = unreadCache.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().clanId() == clanId) {
                iterator.remove();
            }
        }
    }

    @Override
    public void invalidateAll() {
        clanCache.clear();
        unreadCache.clear();
    }

    private ClanPlaceholderExtras query(long clanId) {
        try (Connection connection = dataSourceProvider.dataSource().getConnection()) {
            List<String> bannedNames = queryBannedNames(connection, clanId);
            MailSummary mail = queryMailSummary(connection, clanId);
            List<String> homeNames = queryHomeNames(connection, clanId);
            return new ClanPlaceholderExtras(
                    bannedNames.size(),
                    bannedNames,
                    mail.total(),
                    mail.lastSender(),
                    mail.lastMessage(),
                    homeNames
            );
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load clan placeholder extras " + clanId, exception);
        }
    }

    private int queryUnread(long clanId, UUID playerId) {
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
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load unread mail count", exception);
        }
    }

    private static List<String> queryBannedNames(Connection connection, long clanId) throws SQLException {
        String sql = "SELECT player_uuid FROM clan_join_blocks WHERE clan_id = ? ORDER BY blocked_at";
        List<String> names = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    names.add(PlaceholderTextUtil.resolvePlayerName(
                            UUID.fromString(resultSet.getString("player_uuid"))
                    ));
                }
            }
        }
        return names;
    }

    private static MailSummary queryMailSummary(Connection connection, long clanId) throws SQLException {
        int total = 0;
        String countSql = "SELECT COUNT(*) AS total FROM clan_mail WHERE clan_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(countSql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    total = resultSet.getInt("total");
                }
            }
        }
        String lastSql = """
                SELECT sender_name, message
                FROM clan_mail
                WHERE clan_id = ?
                ORDER BY created_at DESC, id DESC
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(lastSql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new MailSummary(
                            total,
                            resultSet.getString("sender_name"),
                            resultSet.getString("message")
                    );
                }
            }
        }
        return new MailSummary(total, "", "");
    }

    private static List<String> queryHomeNames(Connection connection, long clanId) throws SQLException {
        String sql = "SELECT name FROM clan_homes WHERE clan_id = ? ORDER BY name";
        List<String> names = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    names.add(resultSet.getString("name"));
                }
            }
        }
        return names;
    }

    private record CachedExtras(ClanPlaceholderExtras extras, long expiresAt) {
    }

    private record CachedUnread(long clanId, int count, long expiresAt) {
    }

    private record MailSummary(int total, String lastSender, String lastMessage) {
    }
}
