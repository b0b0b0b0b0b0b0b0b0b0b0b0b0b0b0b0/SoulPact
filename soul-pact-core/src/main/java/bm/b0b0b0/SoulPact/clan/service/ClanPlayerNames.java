package bm.b0b0b0.SoulPact.clan.service;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class ClanPlayerNames {

    private ClanPlayerNames() {
    }

    public static String displayName(java.util.UUID playerId) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
        String name = offlinePlayer.getName();
        if (name == null || name.isBlank()) {
            return playerId.toString();
        }
        return name;
    }
}
