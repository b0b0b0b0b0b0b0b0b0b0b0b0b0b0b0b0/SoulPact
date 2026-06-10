package bm.b0b0b0.SoulPact.core.placeholder;

import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ClanPlaceholderDataLoader {

    private final DataSourceProvider dataSourceProvider;

    public ClanPlaceholderDataLoader(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public ClanPlaceholderMembershipRow loadMembership(UUID playerId) {
        String sql = "SELECT clan_id, role, kills, deaths FROM clan_members WHERE player_uuid = ? LIMIT 1";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return ClanPlaceholderMembershipRow.empty();
                }
                return new ClanPlaceholderMembershipRow(
                        resultSet.getLong("clan_id"),
                        resultSet.getString("role"),
                        resultSet.getInt("kills"),
                        resultSet.getInt("deaths")
                );
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load clan membership for " + playerId, exception);
        }
    }

    public ClanPlaceholderClanBundle loadClanBundle(long clanId) {
        try (Connection connection = dataSourceProvider.dataSource().getConnection()) {
            ClanRow clanRow = queryClan(connection, clanId);
            if (clanRow == null) {
                return null;
            }
            List<ClanPlaceholderMemberRow> members = queryMembers(connection, clanId);
            Map<String, Integer> historyStats = queryHistoryStats(connection, clanId);
            List<String> allyTags = queryAllyTags(connection, clanId);
            double bankBalance = queryBankBalance(connection, clanId);
            return new ClanPlaceholderClanBundle(
                    clanRow.id(),
                    clanRow.tag(),
                    clanRow.name(),
                    clanRow.description(),
                    clanRow.leaderId(),
                    PlaceholderTextUtil.resolvePlayerName(clanRow.leaderId()),
                    clanRow.points(),
                    clanRow.warsWon(),
                    0,
                    clanRow.maxSlots(),
                    clanRow.verified(),
                    clanRow.friendlyFire(),
                    clanRow.joinOpen(),
                    clanRow.createdAt(),
                    clanRow.bannerData(),
                    bankBalance,
                    List.copyOf(members),
                    Map.copyOf(historyStats),
                    List.copyOf(allyTags)
            );
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load clan placeholder bundle " + clanId, exception);
        }
    }

    private static ClanRow queryClan(Connection connection, long clanId) throws SQLException {
        String sql = """
                SELECT id, tag, name, description, leader_uuid, points, wars_won, max_slots,
                       verified, friendly_fire, join_requests_open, created_at, banner_data
                FROM clans WHERE id = ? LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                int warsWon = 0;
                try {
                    warsWon = resultSet.getInt("wars_won");
                } catch (SQLException ignored) {
                }
                String bannerData = resultSet.getString("banner_data");
                return new ClanRow(
                        resultSet.getLong("id"),
                        resultSet.getString("tag"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        UUID.fromString(resultSet.getString("leader_uuid")),
                        resultSet.getInt("points"),
                        warsWon,
                        resultSet.getInt("max_slots"),
                        resultSet.getInt("verified") == 1,
                        resultSet.getInt("friendly_fire") == 1,
                        resultSet.getInt("join_requests_open") == 1,
                        resultSet.getLong("created_at"),
                        bannerData == null ? "" : bannerData
                );
            }
        }
    }

    private static List<ClanPlaceholderMemberRow> queryMembers(Connection connection, long clanId) throws SQLException {
        String sql = "SELECT player_uuid, role, kills, deaths FROM clan_members WHERE clan_id = ?";
        List<ClanPlaceholderMemberRow> members = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    members.add(new ClanPlaceholderMemberRow(
                            UUID.fromString(resultSet.getString("player_uuid")),
                            resultSet.getString("role"),
                            resultSet.getInt("kills"),
                            resultSet.getInt("deaths")
                    ));
                }
            }
        }
        return members;
    }

    private static Map<String, Integer> queryHistoryStats(Connection connection, long clanId) throws SQLException {
        String sql = "SELECT reason, COUNT(*) AS total FROM clan_membership_history WHERE clan_id = ? GROUP BY reason";
        Map<String, Integer> stats = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    stats.put(resultSet.getString("reason"), resultSet.getInt("total"));
                }
            }
        }
        return stats;
    }

    private static List<String> queryAllyTags(Connection connection, long clanId) throws SQLException {
        String coalitionSql = "SELECT coalition_id FROM coalition_members WHERE clan_id = ? LIMIT 1";
        List<String> tags = new ArrayList<>();
        Long coalitionId = null;
        try (PreparedStatement statement = connection.prepareStatement(coalitionSql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    coalitionId = resultSet.getLong("coalition_id");
                }
            }
        }
        if (coalitionId == null) {
            return tags;
        }
        String alliesSql = """
                SELECT c.tag
                FROM coalition_members cm
                JOIN clans c ON c.id = cm.clan_id
                WHERE cm.coalition_id = ? AND cm.clan_id <> ?
                ORDER BY c.tag
                """;
        try (PreparedStatement statement = connection.prepareStatement(alliesSql)) {
            statement.setLong(1, coalitionId);
            statement.setLong(2, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tags.add(resultSet.getString("tag"));
                }
            }
        }
        return tags;
    }

    private static double queryBankBalance(Connection connection, long clanId) throws SQLException {
        String sql = "SELECT balance FROM clan_treasury WHERE clan_id = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0.0D;
                }
                return resultSet.getDouble("balance");
            }
        }
    }

    private record ClanRow(
            long id,
            String tag,
            String name,
            String description,
            UUID leaderId,
            int points,
            int warsWon,
            int maxSlots,
            boolean verified,
            boolean friendlyFire,
            boolean joinOpen,
            long createdAt,
            String bannerData
    ) {
    }
}
