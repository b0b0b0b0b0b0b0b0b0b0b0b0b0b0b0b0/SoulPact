package bm.b0b0b0.SoulPact.clan.service;

import java.util.Map;
import org.bukkit.inventory.ItemStack;

public record ClanHubSnapshot(
        Map<String, String> placeholders,
        boolean inClan,
        boolean clanLeader,
        ItemStack bannerItem
) {
}
