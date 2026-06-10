package bm.b0b0b0.SoulPact.core.placeholder;

import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleTheme;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanPlaceholderSnapshotLoader {

    private final DataSourceProvider dataSourceProvider;
    private final RoleThemeService roleThemeService;
    private final int cacheMillis;
    private final Map<UUID, CachedSnapshot> cache = new ConcurrentHashMap<>();

    public ClanPlaceholderSnapshotLoader(
            DataSourceProvider dataSourceProvider,
            RoleThemeService roleThemeService,
            int cacheMillis
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.roleThemeService = roleThemeService;
        this.cacheMillis = cacheMillis;
    }

    public void invalidate(UUID playerId) {
        cache.remove(playerId);
    }

    public void invalidateAll() {
        cache.clear();
    }

    public ClanPlaceholderSnapshot load(Player player) {
        if (cacheMillis <= 0) {
            return loadFresh(player);
        }
        UUID playerId = player.getUniqueId();
        CachedSnapshot cached = cache.get(playerId);
        long now = System.currentTimeMillis();
        if (cached != null && cached.expiresAt() > now) {
            return cached.snapshot();
        }
        ClanPlaceholderSnapshot snapshot = loadFresh(player);
        cache.put(playerId, new CachedSnapshot(snapshot, now + cacheMillis));
        return snapshot;
    }

    private ClanPlaceholderSnapshot loadFresh(Player player) {
        UUID playerId = player.getUniqueId();
        OptionalMemberClan membership = queryMembership(playerId);
        if (membership.isEmpty()) {
            return ClanPlaceholderSnapshot.empty();
        }
        OptionalClanRow clanRow = queryClan(membership.clanId());
        if (clanRow.isEmpty()) {
            return ClanPlaceholderSnapshot.empty();
        }
        ClanRow clan = clanRow.get();
        List<MemberRow> members = queryMembers(clan.id());
        MemberRow self = findMember(members, playerId);
        int clanKills = 0;
        int clanDeaths = 0;
        List<String> memberNames = new ArrayList<>();
        Set<UUID> memberIds = new HashSet<>();
        for (MemberRow member : members) {
            clanKills += member.kills();
            clanDeaths += member.deaths();
            memberNames.add(PlaceholderTextUtil.resolvePlayerName(member.playerId()));
            memberIds.add(member.playerId());
        }
        List<String> onlineNames = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (memberIds.contains(online.getUniqueId())) {
                onlineNames.add(online.getName());
            }
        }
        Map<String, Integer> historyStats = queryHistoryStats(clan.id());
        List<String> allies = queryAllyTags(clan.id());
        double bankBalance = queryBankBalance(clan.id());
        String bannerData = queryBannerData(clan.id());
        RoleTheme theme = roleThemeService.theme();
        int roleRank = roleRank(theme, self == null ? "member" : self.role());
        return new ClanPlaceholderSnapshot(
                true,
                clan.id(),
                clan.tag(),
                clan.name(),
                clan.description(),
                clan.leaderId(),
                PlaceholderTextUtil.resolvePlayerName(clan.leaderId()),
                clan.points(),
                clan.warsWon(),
                clan.warsLost(),
                clan.maxSlots(),
                members.size(),
                clan.verified(),
                clan.friendlyFire(),
                clan.joinOpen(),
                clan.createdAt(),
                self == null ? "member" : self.role(),
                self == null ? 0 : self.kills(),
                self == null ? 0 : self.deaths(),
                clanKills,
                clanDeaths,
                onlineNames.size(),
                memberNames,
                onlineNames,
                allies,
                bankBalance,
                historyStats.getOrDefault("join", members.size()),
                historyStats.getOrDefault("leave", 0),
                historyStats.getOrDefault("kick", 0),
                bannerData,
                roleRank
        );
    }

    private static MemberRow findMember(List<MemberRow> members, UUID playerId) {
        for (MemberRow member : members) {
            if (member.playerId().equals(playerId)) {
                return member;
            }
        }
        return null;
    }

    private static int roleRank(RoleTheme theme, String roleKey) {
        List<String> order = theme.order();
        for (int index = 0; index < order.size(); index++) {
            if (order.get(index).equals(roleKey)) {
                return index + 1;
            }
        }
        return order.size();
    }

    public String patentName(String roleKey) {
        RoleDefinition definition = roleThemeService.theme().definition(roleKey);
        if (definition == null) {
            return roleKey;
        }
        return PlaceholderTextUtil.stripColors(definition.title());
    }

    public String patentFormatted(String roleKey) {
        RoleDefinition definition = roleThemeService.theme().definition(roleKey);
        if (definition == null) {
            return roleKey;
        }
        return definition.title(        );
    }

    private record CachedSnapshot(ClanPlaceholderSnapshot snapshot, long expiresAt) {
    }

    private OptionalMemberClan queryMembership(UUID playerId) {
        String sql = "SELECT clan_id, role, kills, deaths FROM clan_members WHERE player_uuid = ? LIMIT 1";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return OptionalMemberClan.empty();
                }
                return OptionalMemberClan.of(
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

    private OptionalClanRow queryClan(long clanId) {
        String sql = """
                SELECT id, tag, name, description, leader_uuid, points, wars_won, max_slots,
                       verified, friendly_fire, join_requests_open, created_at
                FROM clans WHERE id = ? LIMIT 1
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return OptionalClanRow.empty();
                }
                return OptionalClanRow.of(mapClanRow(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load clan " + clanId, exception);
        }
    }

    private List<MemberRow> queryMembers(long clanId) {
        String sql = "SELECT player_uuid, role, kills, deaths FROM clan_members WHERE clan_id = ?";
        List<MemberRow> members = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    members.add(new MemberRow(
                            UUID.fromString(resultSet.getString("player_uuid")),
                            resultSet.getString("role"),
                            resultSet.getInt("kills"),
                            resultSet.getInt("deaths")
                    ));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load clan members " + clanId, exception);
        }
        return members;
    }

    private Map<String, Integer> queryHistoryStats(long clanId) {
        String sql = "SELECT reason, COUNT(*) AS total FROM clan_membership_history WHERE clan_id = ? GROUP BY reason";
        Map<String, Integer> stats = new HashMap<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    stats.put(resultSet.getString("reason"), resultSet.getInt("total"));
                }
            }
        } catch (SQLException exception) {
            return stats;
        }
        return stats;
    }

    private List<String> queryAllyTags(long clanId) {
        String coalitionSql = "SELECT coalition_id FROM coalition_members WHERE clan_id = ? LIMIT 1";
        List<String> tags = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection()) {
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
        } catch (SQLException exception) {
            return tags;
        }
        return tags;
    }

    private double queryBankBalance(long clanId) {
        String sql = "SELECT balance FROM clan_treasury WHERE clan_id = ? LIMIT 1";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0.0D;
                }
                return resultSet.getDouble("balance");
            }
        } catch (SQLException exception) {
            return 0.0D;
        }
    }

    private String queryBannerData(long clanId) {
        String sql = "SELECT banner_data FROM clans WHERE id = ? LIMIT 1";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return "";
                }
                String value = resultSet.getString("banner_data");
                return value == null ? "" : value;
            }
        } catch (SQLException exception) {
            return "";
        }
    }

    private static ClanRow mapClanRow(ResultSet resultSet) throws SQLException {
        int warsWon = 0;
        try {
            warsWon = resultSet.getInt("wars_won");
        } catch (SQLException ignored) {
        }
        return new ClanRow(
                resultSet.getLong("id"),
                resultSet.getString("tag"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                UUID.fromString(resultSet.getString("leader_uuid")),
                resultSet.getInt("points"),
                warsWon,
                0,
                resultSet.getInt("max_slots"),
                resultSet.getInt("verified") == 1,
                resultSet.getInt("friendly_fire") == 1,
                resultSet.getInt("join_requests_open") == 1,
                resultSet.getLong("created_at")
        );
    }

    private record MemberRow(UUID playerId, String role, int kills, int deaths) {
    }

    private record ClanRow(
            long id,
            String tag,
            String name,
            String description,
            UUID leaderId,
            int points,
            int warsWon,
            int warsLost,
            int maxSlots,
            boolean verified,
            boolean friendlyFire,
            boolean joinOpen,
            long createdAt
    ) {
    }

    private record OptionalMemberClan(long clanId, String role, int kills, int deaths) {
        static OptionalMemberClan empty() {
            return new OptionalMemberClan(0L, null, 0, 0);
        }

        static OptionalMemberClan of(long clanId, String role, int kills, int deaths) {
            return new OptionalMemberClan(clanId, role, kills, deaths);
        }

        boolean isEmpty() {
            return clanId == 0L;
        }
    }

    private record OptionalClanRow(ClanRow row) {
        static OptionalClanRow empty() {
            return new OptionalClanRow(null);
        }

        static OptionalClanRow of(ClanRow row) {
            return new OptionalClanRow(row);
        }

        boolean isEmpty() {
            return row == null;
        }

        ClanRow get() {
            return row;
        }
    }
}
