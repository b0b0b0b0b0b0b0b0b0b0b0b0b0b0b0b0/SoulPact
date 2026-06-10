package bm.b0b0b0.SoulPact.gladiator.listener;

import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class GladiatorQuitListener implements Listener {

    private final GladiatorEventService eventService;

    public GladiatorQuitListener(GladiatorEventService eventService) {
        this.eventService = eventService;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!eventService.hasEvents()) {
            return;
        }
        eventService.handleQuit(event.getPlayer());
    }
}
