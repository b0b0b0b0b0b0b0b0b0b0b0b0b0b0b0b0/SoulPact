package bm.b0b0b0.SoulPact.gladiator.listener;

import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class GladiatorDeathListener implements Listener {

    private final GladiatorEventService eventService;

    public GladiatorDeathListener(GladiatorEventService eventService) {
        this.eventService = eventService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!eventService.hasEvents()) {
            return;
        }
        eventService.handleDeath(event.getEntity());
    }
}
