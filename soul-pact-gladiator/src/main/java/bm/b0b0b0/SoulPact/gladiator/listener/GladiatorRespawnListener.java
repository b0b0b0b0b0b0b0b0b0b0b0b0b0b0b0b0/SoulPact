package bm.b0b0b0.SoulPact.gladiator.listener;

import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class GladiatorRespawnListener implements Listener {

    private final GladiatorEventService eventService;

    public GladiatorRespawnListener(GladiatorEventService eventService) {
        this.eventService = eventService;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        eventService.consumeRespawnTarget(event.getPlayer().getUniqueId())
                .ifPresent(event::setRespawnLocation);
    }
}
