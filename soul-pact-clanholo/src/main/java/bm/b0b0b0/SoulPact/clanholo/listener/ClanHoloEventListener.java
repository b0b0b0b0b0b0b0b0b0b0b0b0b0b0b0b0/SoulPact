package bm.b0b0b0.SoulPact.clanholo.listener;

import bm.b0b0b0.SoulPact.api.event.ClanDescriptionChangeEvent;
import bm.b0b0b0.SoulPact.api.event.ClanDisbandEvent;
import bm.b0b0b0.SoulPact.clanholo.repository.HologramRepository;
import bm.b0b0b0.SoulPact.clanholo.service.HologramService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ClanHoloEventListener implements Listener {

    private final HologramRepository repository;
    private final HologramService hologramService;

    public ClanHoloEventListener(HologramRepository repository, HologramService hologramService) {
        this.repository = repository;
        this.hologramService = hologramService;
    }

    @EventHandler
    public void onDisband(ClanDisbandEvent event) {
        hologramService.destroyClanHolograms(event.clanId());
    }

    @EventHandler
    public void onDescriptionChange(ClanDescriptionChangeEvent event) {
        repository.findByClanId(event.clanId()).thenAccept(holograms -> {
            for (var hologram : holograms) {
                if (hologram.template() != null && !hologram.template().isBlank()) {
                    hologramService.renderHologram(hologram);
                }
            }
        });
    }
}
