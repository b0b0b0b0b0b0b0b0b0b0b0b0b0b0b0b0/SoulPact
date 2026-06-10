package bm.b0b0b0.SoulPact.gladiator.service;

import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public final class GladiatorEvent {

    public enum Phase {
        LOBBY,
        RUNNING
    }

    private final Arena arena;
    private final BossBar bossBar;
    private final Map<UUID, ClanRef> fighters = new HashMap<>();
    private final Map<UUID, ClanRef> participants = new HashMap<>();
    private final Set<UUID> spectators = new HashSet<>();
    private Phase phase = Phase.LOBBY;
    private int countdownRemaining;
    private int boundsCounter;

    public GladiatorEvent(Arena arena, int countdownSeconds) {
        this.arena = arena;
        this.countdownRemaining = countdownSeconds;
        this.bossBar = BossBar.bossBar(Component.empty(), 1.0F, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    public Arena arena() {
        return arena;
    }

    public BossBar bossBar() {
        return bossBar;
    }

    public Phase phase() {
        return phase;
    }

    public void beginFight() {
        phase = Phase.RUNNING;
    }

    public int countdownRemaining() {
        return countdownRemaining;
    }

    public int tickCountdown() {
        return --countdownRemaining;
    }

    public int tickBounds(int intervalSeconds) {
        boundsCounter++;
        if (boundsCounter >= intervalSeconds) {
            boundsCounter = 0;
        }
        return boundsCounter;
    }

    public void addFighter(UUID playerId, ClanRef clan) {
        fighters.put(playerId, clan);
        participants.put(playerId, clan);
    }

    public ClanRef removeFighter(UUID playerId) {
        return fighters.remove(playerId);
    }

    public boolean isFighter(UUID playerId) {
        return fighters.containsKey(playerId);
    }

    public ClanRef fighterClan(UUID playerId) {
        return fighters.get(playerId);
    }

    public Map<UUID, ClanRef> fighters() {
        return Map.copyOf(fighters);
    }

    public Map<UUID, ClanRef> participants() {
        return Map.copyOf(participants);
    }

    public void addSpectator(UUID playerId) {
        spectators.add(playerId);
    }

    public void removeSpectator(UUID playerId) {
        spectators.remove(playerId);
    }

    public boolean isSpectator(UUID playerId) {
        return spectators.contains(playerId);
    }

    public Set<UUID> spectators() {
        return Set.copyOf(spectators);
    }

    public Set<UUID> everyone() {
        Set<UUID> all = new LinkedHashSet<>(fighters.keySet());
        all.addAll(spectators);
        return all;
    }

    public Collection<ClanRef> remainingClans() {
        Map<Long, ClanRef> distinct = new HashMap<>();
        for (ClanRef clan : fighters.values()) {
            distinct.putIfAbsent(clan.id(), clan);
        }
        return distinct.values();
    }
}
