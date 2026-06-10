package bm.b0b0b0.SoulPact.clanholo.service;

import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class HologramPlaceholderBuilder {

    private final DataSource dataSource;

    public HologramPlaceholderBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, String> build(ClanSnapshot clan) {
        if (clan == null) {
            return Map.of();
        }
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("tag", clan.tag());
        placeholders.put("name", clan.name());
        placeholders.put("description", clan.description() == null ? "" : clan.description());
        placeholders.put("leader", resolveName(clan.leaderId()));
        placeholders.put("points", String.valueOf(clan.points()));
        placeholders.put("verified", clan.verified() ? "да" : "нет");
        placeholders.put("ff", clan.friendlyFire() ? "да" : "нет");
        placeholders.put("slots", String.valueOf(clan.maxSlots()));
        return placeholders;
    }

    public Optional<ClanSnapshot> loadClan(long clanId) {
        String sql = """
                SELECT id, tag, name, description, leader_uuid, points, max_slots, verified, friendly_fire, created_at
                FROM clans WHERE id = ? LIMIT 1
                """;
        try (Connection connection = dataSource.getConnection();
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
                        resultSet.getInt("verified") == 1,
                        resultSet.getInt("friendly_fire") == 1,
                        resultSet.getLong("created_at")
                ));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to load clan snapshot " + clanId, exception);
        }
    }

    private static String resolveName(UUID playerId) {
        if (playerId == null) {
            return "";
        }
        OfflinePlayer offline = Bukkit.getOfflinePlayer(playerId);
        return offline.getName() == null ? playerId.toString().substring(0, 8) : offline.getName();
    }
}
