package bm.b0b0b0.SoulPact.gladiator.gui;

import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfig;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.service.ArenaCatalog;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEvent;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class GladiatorMenuPopulator {

    private final GladiatorConfig config;
    private final GladiatorMessages messages;
    private final ArenaCatalog catalog;
    private final GladiatorEventService eventService;

    public GladiatorMenuPopulator(
            GladiatorConfig config,
            GladiatorMessages messages,
            ArenaCatalog catalog,
            GladiatorEventService eventService
    ) {
        this.config = config;
        this.messages = messages;
        this.catalog = catalog;
        this.eventService = eventService;
    }

    public void populate(Inventory inventory, Player player, Map<Integer, Arena> arenasBySlot) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, GladiatorGuiItems.filler(config.fillerMaterial()));
        }
        int slot = config.listStartSlot();
        for (Arena arena : catalog.all()) {
            if (slot > config.listEndSlot() || slot >= inventory.getSize()) {
                break;
            }
            inventory.setItem(slot, buildArenaItem(player, arena));
            arenasBySlot.put(slot, arena);
            slot++;
        }
        inventory.setItem(config.backSlot(), GladiatorGuiItems.build(
                messages,
                player,
                config.backMaterial(),
                "gladiator.gui.item.back.name",
                "gladiator.gui.item.back.lore",
                Map.of()
        ));
    }

    private ItemStack buildArenaItem(Player player, Arena arena) {
        Optional<GladiatorEvent> event = eventService.eventOf(arena.name());
        String stateKey = resolveStateKey(arena, event);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("arena", arena.name());
        placeholders.put("description", arena.description().isBlank()
                ? messages.resolve(player, "gladiator.gui.no-description")
                : arena.description());
        placeholders.put("state", messages.resolve(player, stateKey));
        placeholders.put("tag", arena.tag().isBlank()
                ? messages.resolve(player, "gladiator.place.none")
                : arena.tag());
        placeholders.put("holder", arena.holderClanTag().isBlank()
                ? messages.resolve(player, "gladiator.place.none")
                : arena.holderClanTag());
        placeholders.put("players", event.map(value -> String.valueOf(value.fighters().size())).orElse("0"));
        placeholders.put("clans", event.map(value -> String.valueOf(value.remainingClans().size())).orElse("0"));
        List<String> lore = new ArrayList<>(messages.resolveList(player, "gladiator.gui.item.arena.lore", placeholders));
        if (event.isPresent() && event.get().phase() == GladiatorEvent.Phase.LOBBY) {
            lore.add(messages.resolve(player, "gladiator.gui.action.join"));
        }
        if (event.isPresent()) {
            lore.add(messages.resolve(player, "gladiator.gui.action.watch"));
        }
        return GladiatorGuiItems.named(
                resolveIcon(arena),
                messages.resolve(player, "gladiator.gui.item.arena.name", placeholders),
                lore
        );
    }

    private String resolveStateKey(Arena arena, Optional<GladiatorEvent> event) {
        if (!arena.enabled()) {
            return "gladiator.state.disabled";
        }
        if (event.isEmpty()) {
            return "gladiator.state.idle";
        }
        return event.get().phase() == GladiatorEvent.Phase.LOBBY
                ? "gladiator.state.lobby"
                : "gladiator.state.running";
    }

    private Material resolveIcon(Arena arena) {
        Material material = Material.matchMaterial(arena.icon());
        return material == null ? config.arenaDefaultMaterial() : material;
    }
}
