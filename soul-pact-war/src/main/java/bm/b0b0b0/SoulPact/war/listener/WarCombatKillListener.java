package bm.b0b0b0.SoulPact.war.listener;

import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class WarCombatKillListener implements Listener {

    private final ClanWarService warService;

    public WarCombatKillListener(ClanWarService warService) {
        this.warService = warService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null || killer.equals(victim)) {
            return;
        }
        warService.recordCombatKill(killer, victim);
    }
}
