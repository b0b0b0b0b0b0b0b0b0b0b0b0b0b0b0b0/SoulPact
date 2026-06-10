package bm.b0b0b0.SoulPact.gladiator.gui;

import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfig;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorActionResult;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventService;
import java.util.Map;
import java.util.function.Supplier;
import org.bukkit.entity.Player;

public final class GladiatorClickHandler {

    private final Supplier<GladiatorConfig> configSupplier;
    private final GladiatorMessages messages;
    private final GladiatorEventService eventService;
    private final GladiatorClanNavigation navigation;

    public GladiatorClickHandler(
            Supplier<GladiatorConfig> configSupplier,
            GladiatorMessages messages,
            GladiatorEventService eventService,
            GladiatorClanNavigation navigation
    ) {
        this.configSupplier = configSupplier;
        this.messages = messages;
        this.eventService = eventService;
        this.navigation = navigation;
    }

    public void handle(GladiatorMenu menu, Player player, int slot, boolean rightClick) {
        GladiatorConfig config = configSupplier.get();
        if (slot == config.backSlot()) {
            navigation.openHub(player);
            return;
        }
        Arena arena = menu.arenaAt(slot);
        if (arena == null) {
            return;
        }
        player.closeInventory();
        if (rightClick) {
            notifyResult(player, arena, eventService.watch(player, arena.name()));
            return;
        }
        eventService.requestJoin(player, arena.name(), result -> notifyResult(player, arena, result));
    }

    private void notifyResult(Player player, Arena arena, GladiatorActionResult result) {
        Map<String, String> placeholders = Map.of("arena", arena.name());
        switch (result) {
            case JOINED -> messages.send(player, "gladiator.event.you-joined", placeholders);
            case WATCHING -> messages.send(player, "gladiator.event.you-watching", placeholders);
            case NO_EVENT -> messages.send(player, "gladiator.error.no-event", placeholders);
            case FIGHT_ALREADY_STARTED -> messages.send(player, "gladiator.error.fight-started", placeholders);
            case ALREADY_JOINED -> messages.send(player, "gladiator.error.already-joined", placeholders);
            case NOT_IN_CLAN -> messages.send(player, "gladiator.error.not-in-clan", placeholders);
            case NO_WATCH_POINT -> messages.send(player, "gladiator.error.no-watch-point", placeholders);
            default -> messages.send(player, "gladiator.error.no-event", placeholders);
        }
    }
}
