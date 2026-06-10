package bm.b0b0b0.SoulPact.gladiator.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfig;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaPoint;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaRegion;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class GladiatorEventService {

    private final SoulPactApi api;
    private final GladiatorConfig config;
    private final ArenaCatalog catalog;
    private final GladiatorEventPresenter presenter;
    private final GladiatorRewardDispatcher rewardDispatcher;
    private final Map<String, GladiatorEvent> events = new HashMap<>();
    private final Map<UUID, Location> respawnTargets = new HashMap<>();

    public GladiatorEventService(
            SoulPactApi api,
            GladiatorConfig config,
            ArenaCatalog catalog,
            GladiatorEventPresenter presenter,
            GladiatorRewardDispatcher rewardDispatcher
    ) {
        this.api = api;
        this.config = config;
        this.catalog = catalog;
        this.presenter = presenter;
        this.rewardDispatcher = rewardDispatcher;
    }

    public GladiatorActionResult start(String arenaName) {
        Optional<Arena> arenaOptional = catalog.find(arenaName);
        if (arenaOptional.isEmpty()) {
            return GladiatorActionResult.UNKNOWN_ARENA;
        }
        Arena arena = arenaOptional.get();
        if (!arena.enabled()) {
            return GladiatorActionResult.ARENA_DISABLED;
        }
        if (!arena.hasRequiredPoints()) {
            return GladiatorActionResult.MISSING_POINTS;
        }
        if (events.containsKey(key(arena.name()))) {
            return GladiatorActionResult.ALREADY_RUNNING;
        }
        GladiatorEvent event = new GladiatorEvent(arena, config.lobbyCountdownSeconds());
        events.put(key(arena.name()), event);
        presenter.updateLobbyBossBar(event, config.lobbyCountdownSeconds());
        presenter.broadcastAll("gladiator.broadcast.lobby-open", Map.of(
                "arena", arena.name(),
                "seconds", String.valueOf(config.lobbyCountdownSeconds())
        ));
        return GladiatorActionResult.STARTED;
    }

    public GladiatorActionResult stop(String arenaName) {
        GladiatorEvent event = events.get(key(arenaName));
        if (event == null) {
            return GladiatorActionResult.NO_EVENT;
        }
        endEvent(event, "gladiator.broadcast.stopped", Map.of("arena", event.arena().name()));
        return GladiatorActionResult.STOPPED;
    }

    public void requestJoin(Player player, String arenaName, Consumer<GladiatorActionResult> callback) {
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (clanOptional.isEmpty()) {
                callback.accept(GladiatorActionResult.NOT_IN_CLAN);
                return;
            }
            ClanRef clan = new ClanRef(clanOptional.get().id(), clanOptional.get().tag());
            callback.accept(join(player, arenaName, clan));
        }));
    }

    public GladiatorActionResult leave(Player player) {
        for (GladiatorEvent event : events.values()) {
            if (event.isFighter(player.getUniqueId())) {
                removeFromFight(event, player, true);
                checkWin(event);
                return GladiatorActionResult.LEFT;
            }
            if (event.isSpectator(player.getUniqueId())) {
                event.removeSpectator(player.getUniqueId());
                presenter.hideBossBar(event, player.getUniqueId());
                teleport(player, event.arena(), ArenaPoint.EXIT);
                return GladiatorActionResult.LEFT;
            }
        }
        return GladiatorActionResult.NOT_IN_EVENT;
    }

    public GladiatorActionResult watch(Player player, String arenaName) {
        GladiatorEvent event = events.get(key(arenaName));
        if (event == null) {
            return GladiatorActionResult.NO_EVENT;
        }
        if (!event.arena().hasPoint(ArenaPoint.WATCH)) {
            return GladiatorActionResult.NO_WATCH_POINT;
        }
        event.addSpectator(player.getUniqueId());
        presenter.showBossBar(event, player.getUniqueId());
        teleport(player, event.arena(), ArenaPoint.WATCH);
        return GladiatorActionResult.WATCHING;
    }

    public void handleDeath(Player victim) {
        for (GladiatorEvent event : events.values()) {
            if (event.phase() == GladiatorEvent.Phase.RUNNING && event.isFighter(victim.getUniqueId())) {
                eliminate(event, victim);
                return;
            }
        }
    }

    public void handleQuit(Player player) {
        for (GladiatorEvent event : events.values()) {
            if (event.isFighter(player.getUniqueId())) {
                event.removeFighter(player.getUniqueId());
                presenter.hideBossBar(event, player.getUniqueId());
                checkWin(event);
                return;
            }
            if (event.isSpectator(player.getUniqueId())) {
                event.removeSpectator(player.getUniqueId());
                return;
            }
        }
    }

    public Optional<Location> consumeRespawnTarget(UUID playerId) {
        return Optional.ofNullable(respawnTargets.remove(playerId));
    }

    public Optional<GladiatorEvent> eventOf(String arenaName) {
        return Optional.ofNullable(events.get(key(arenaName)));
    }

    public boolean isFighting(UUID playerId) {
        for (GladiatorEvent event : events.values()) {
            if (event.phase() == GladiatorEvent.Phase.RUNNING && event.isFighter(playerId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public void tick() {
        for (GladiatorEvent event : Map.copyOf(events).values()) {
            if (event.phase() == GladiatorEvent.Phase.LOBBY) {
                tickLobby(event);
            } else {
                tickFight(event);
            }
        }
    }

    public void shutdown() {
        for (GladiatorEvent event : Map.copyOf(events).values()) {
            endEvent(event, "gladiator.broadcast.stopped", Map.of("arena", event.arena().name()));
        }
    }

    private GladiatorActionResult join(Player player, String arenaName, ClanRef clan) {
        GladiatorEvent event = events.get(key(arenaName));
        if (event == null) {
            return GladiatorActionResult.NO_EVENT;
        }
        if (event.phase() != GladiatorEvent.Phase.LOBBY) {
            return GladiatorActionResult.FIGHT_ALREADY_STARTED;
        }
        if (event.isFighter(player.getUniqueId())) {
            return GladiatorActionResult.ALREADY_JOINED;
        }
        event.removeSpectator(player.getUniqueId());
        event.addFighter(player.getUniqueId(), clan);
        presenter.showBossBar(event, player.getUniqueId());
        teleport(player, event.arena(), ArenaPoint.LOBBY);
        presenter.sendTo(event.everyone(), "gladiator.event.joined", Map.of(
                "player", player.getName(),
                "tag", clan.tag(),
                "arena", event.arena().name()
        ));
        return GladiatorActionResult.JOINED;
    }

    private void tickLobby(GladiatorEvent event) {
        int remaining = event.tickCountdown();
        presenter.updateLobbyBossBar(event, config.lobbyCountdownSeconds());
        if (remaining > 0) {
            return;
        }
        if (event.remainingClans().size() < config.minClans()) {
            endEvent(event, "gladiator.broadcast.cancelled", Map.of(
                    "arena", event.arena().name(),
                    "min", String.valueOf(config.minClans())
            ));
            return;
        }
        event.beginFight();
        for (UUID fighterId : event.fighters().keySet()) {
            Player fighter = Bukkit.getPlayer(fighterId);
            if (fighter != null) {
                teleport(fighter, event.arena(), ArenaPoint.SPAWN);
            }
        }
        presenter.playSound(event.everyone(), config.startSound());
        presenter.updateFightBossBar(event);
        presenter.broadcastAll("gladiator.broadcast.fight-started", Map.of(
                "arena", event.arena().name(),
                "players", String.valueOf(event.fighters().size()),
                "clans", String.valueOf(event.remainingClans().size())
        ));
    }

    private void tickFight(GladiatorEvent event) {
        presenter.updateFightBossBar(event);
        if (config.boundsCheckSeconds() <= 0 || event.tickBounds(config.boundsCheckSeconds()) != 0) {
            return;
        }
        Optional<ArenaRegion> region = event.arena().regionBounds();
        if (region.isEmpty()) {
            return;
        }
        Optional<Location> spawn = event.arena().location(ArenaPoint.SPAWN);
        if (spawn.isEmpty()) {
            return;
        }
        for (UUID fighterId : event.fighters().keySet()) {
            Player fighter = Bukkit.getPlayer(fighterId);
            if (fighter != null && !region.get().contains(fighter.getLocation())) {
                fighter.teleport(spawn.get());
            }
        }
    }

    private void eliminate(GladiatorEvent event, Player victim) {
        ClanRef clan = event.removeFighter(victim.getUniqueId());
        if (clan == null) {
            return;
        }
        event.addSpectator(victim.getUniqueId());
        rememberRespawnTarget(victim.getUniqueId(), event.arena());
        presenter.playSound(event.everyone(), config.eliminateSound());
        presenter.sendTo(event.everyone(), "gladiator.event.eliminated", Map.of(
                "player", victim.getName(),
                "tag", clan.tag(),
                "arena", event.arena().name()
        ));
        checkWin(event);
    }

    private void removeFromFight(GladiatorEvent event, Player player, boolean teleportExit) {
        event.removeFighter(player.getUniqueId());
        presenter.hideBossBar(event, player.getUniqueId());
        if (teleportExit) {
            teleport(player, event.arena(), ArenaPoint.EXIT);
        }
    }

    private void checkWin(GladiatorEvent event) {
        if (event.phase() != GladiatorEvent.Phase.RUNNING) {
            return;
        }
        var remaining = event.remainingClans();
        if (remaining.size() > 1) {
            return;
        }
        if (remaining.isEmpty()) {
            endEvent(event, "gladiator.broadcast.cancelled", Map.of(
                    "arena", event.arena().name(),
                    "min", String.valueOf(config.minClans())
            ));
            return;
        }
        finish(event, remaining.iterator().next());
    }

    private void finish(GladiatorEvent event, ClanRef winner) {
        catalog.mutate(event.arena().name(), arena -> arena.withHolder(winner.id(), winner.tag()));
        var winnerParticipants = event.participants().entrySet().stream()
                .filter(entry -> entry.getValue().id() == winner.id())
                .map(Map.Entry::getKey)
                .toList();
        rewardDispatcher.dispatch(event.arena().name(), winner, winnerParticipants);
        presenter.playSound(event.everyone(), config.winSound());
        endEvent(event, "gladiator.broadcast.won", Map.of(
                "arena", event.arena().name(),
                "tag", winner.tag()
        ));
    }

    private void endEvent(GladiatorEvent event, String broadcastKey, Map<String, String> placeholders) {
        for (UUID participantId : event.everyone()) {
            Player participant = Bukkit.getPlayer(participantId);
            if (participant != null) {
                teleport(participant, event.arena(), ArenaPoint.EXIT);
            }
        }
        presenter.hideBossBarAll(event);
        events.remove(key(event.arena().name()));
        presenter.broadcastAll(broadcastKey, placeholders);
    }

    private void rememberRespawnTarget(UUID playerId, Arena arena) {
        arena.location(ArenaPoint.WATCH)
                .or(() -> arena.location(ArenaPoint.EXIT))
                .ifPresent(location -> respawnTargets.put(playerId, location));
    }

    private void teleport(Player player, Arena arena, ArenaPoint point) {
        arena.location(point).ifPresent(player::teleport);
    }

    private String key(String arenaName) {
        return arenaName.toLowerCase(Locale.ROOT);
    }
}
