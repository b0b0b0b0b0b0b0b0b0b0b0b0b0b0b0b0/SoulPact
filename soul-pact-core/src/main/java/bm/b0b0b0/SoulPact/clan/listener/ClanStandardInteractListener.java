package bm.b0b0b0.SoulPact.clan.listener;

import bm.b0b0b0.SoulPact.clan.service.ClanStandardHubOpenService;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ClanStandardInteractListener implements Listener {

    private final ClanStandardItem clanStandardItem;
    private final ClanStandardHubOpenService hubOpenService;

    public ClanStandardInteractListener(
            ClanStandardItem clanStandardItem,
            ClanStandardHubOpenService hubOpenService
    ) {
        this.clanStandardItem = clanStandardItem;
        this.hubOpenService = hubOpenService;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStandardInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!hubOpenService.enabled()) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null || !block.getType().name().endsWith("_BANNER")) {
            return;
        }
        Long clanId = clanStandardItem.readClanIdFromBlock(block.getState());
        if (clanId == null) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        hubOpenService.openHubFromStandard(player, clanId);
    }
}
