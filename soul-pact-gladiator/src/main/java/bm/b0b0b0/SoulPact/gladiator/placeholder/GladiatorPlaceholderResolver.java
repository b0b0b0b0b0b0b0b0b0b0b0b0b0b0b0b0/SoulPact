package bm.b0b0b0.SoulPact.gladiator.placeholder;

import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.service.ArenaCatalog;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEvent;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventService;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorScheduleService;
import bm.b0b0b0.SoulPact.gladiator.service.PlayerClanCache;
import bm.b0b0b0.SoulPact.gladiator.util.GladTimeFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public final class GladiatorPlaceholderResolver {

    private static final String PREFIX = "glad_";
    private static final String ARG_SEPARATOR = ":";

    private final GladiatorMessages messages;
    private final ArenaCatalog catalog;
    private final GladiatorEventService eventService;
    private final GladiatorScheduleService scheduleService;
    private final PlayerClanCache playerClanCache;

    public GladiatorPlaceholderResolver(
            GladiatorMessages messages,
            ArenaCatalog catalog,
            GladiatorEventService eventService,
            GladiatorScheduleService scheduleService,
            PlayerClanCache playerClanCache
    ) {
        this.messages = messages;
        this.catalog = catalog;
        this.eventService = eventService;
        this.scheduleService = scheduleService;
        this.playerClanCache = playerClanCache;
    }

    public String resolve(Player player, String params) {
        if (player == null || params == null) {
            return null;
        }
        String normalized = params.toLowerCase(Locale.ROOT);
        if (!normalized.startsWith(PREFIX)) {
            return null;
        }
        String key = normalized.substring(PREFIX.length());
        int separator = key.indexOf(ARG_SEPARATOR);
        String argument = separator < 0 ? "" : key.substring(separator + 1);
        String base = separator < 0 ? key : key.substring(0, separator);
        return switch (base) {
            case "inwar" -> yesNo(eventService.isFighting(player.getUniqueId()));
            case "hastag" -> yesNo(heldTags(player).findAny().isPresent());
            case "tags" -> formatHeldTags(player);
            case "tag" -> arena(argument).map(Arena::tag).orElse(null);
            case "holder" -> arena(argument)
                    .map(value -> value.holderClanTag().isBlank() ? none() : value.holderClanTag())
                    .orElse(null);
            case "arena_state" -> arena(argument).map(this::stateText).orElse(null);
            case "next_name" -> scheduleService.nextEvent()
                    .map(GladiatorScheduleService.NextEvent::arenaName)
                    .orElseGet(this::none);
            case "next_time" -> scheduleService.nextEvent()
                    .map(next -> GladTimeFormat.remaining(messages, next.atMillis()))
                    .orElseGet(this::none);
            default -> null;
        };
    }

    private java.util.stream.Stream<Arena> heldTags(Player player) {
        Long clanId = playerClanCache.cachedClanId(player.getUniqueId());
        if (clanId == null) {
            return java.util.stream.Stream.empty();
        }
        return catalog.tagsHeldBy(clanId).stream();
    }

    private String formatHeldTags(Player player) {
        String joined = heldTags(player).map(Arena::tag).collect(Collectors.joining(" "));
        return joined.isBlank() ? "" : joined;
    }

    private Optional<Arena> arena(String argument) {
        return catalog.find(argument);
    }

    private String stateText(Arena arena) {
        if (!arena.enabled()) {
            return messages.resolveDefault("gladiator.state.disabled");
        }
        Optional<GladiatorEvent> event = eventService.eventOf(arena.name());
        if (event.isEmpty()) {
            return messages.resolveDefault("gladiator.state.idle");
        }
        return messages.resolveDefault(event.get().phase() == GladiatorEvent.Phase.LOBBY
                ? "gladiator.state.lobby"
                : "gladiator.state.running");
    }

    private String yesNo(boolean value) {
        return messages.resolveDefault(value ? "gladiator.place.yes" : "gladiator.place.no");
    }

    private String none() {
        return messages.resolveDefault("gladiator.place.none");
    }
}
