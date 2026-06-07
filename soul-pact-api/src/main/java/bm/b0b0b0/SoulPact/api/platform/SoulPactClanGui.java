package bm.b0b0b0.SoulPact.api.platform;

import org.bukkit.entity.Player;

public interface SoulPactClanGui {

    void openHub(Player player);

    void openList(Player player, int page);

    void openInfo(Player player, long clanId, int listPage);
}
