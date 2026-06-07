package bm.b0b0b0.SoulPact.war.listener;

import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class WarPlayerCacheListener implements Listener {

    private final ClanWarService warService;

    public WarPlayerCacheListener(ClanWarService warService) {
        this.warService = warService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        warService.trackPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        warService.untrackPlayer(event.getPlayer().getUniqueId());
    }
}
