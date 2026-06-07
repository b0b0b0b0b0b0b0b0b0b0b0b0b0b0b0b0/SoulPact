package bm.b0b0b0.SoulPact.war.listener;

import bm.b0b0b0.SoulPact.api.clan.SoulPactClanStandard;
import bm.b0b0b0.SoulPact.api.war.WarFlagBreakGate;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public final class WarFlagBreakBukkitListener implements Listener {

    private final SoulPactClanStandard clanStandard;
    private final WarFlagBreakGate flagBreakGate;

    public WarFlagBreakBukkitListener(SoulPactClanStandard clanStandard, WarFlagBreakGate flagBreakGate) {
        this.clanStandard = clanStandard;
        this.flagBreakGate = flagBreakGate;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void allowFlagBreakHigh(BlockBreakEvent event) {
        allowFlagBreak(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void allowFlagBreakMonitor(BlockBreakEvent event) {
        allowFlagBreak(event);
    }

    private void allowFlagBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        if (!block.getType().name().endsWith("_BANNER")) {
            return;
        }
        Long clanId = clanStandard.readClanIdFromBlock(block.getState());
        if (clanId == null) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (flagBreakGate.allowsEnemyStandardBreak(player.getUniqueId(), clanId)) {
            event.setCancelled(false);
        }
    }
}
