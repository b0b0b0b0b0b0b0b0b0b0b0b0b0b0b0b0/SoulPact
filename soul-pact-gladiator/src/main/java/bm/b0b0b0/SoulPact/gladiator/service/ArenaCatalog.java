package bm.b0b0b0.SoulPact.gladiator.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaSchedule;
import bm.b0b0b0.SoulPact.gladiator.repository.ArenaRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.UnaryOperator;

public final class ArenaCatalog {

    private final SoulPactApi api;
    private final ArenaRepository repository;
    private final ConcurrentHashMap<String, Arena> arenas = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> rewards = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<ArenaSchedule>> schedules = new ConcurrentHashMap<>();

    public ArenaCatalog(SoulPactApi api, ArenaRepository repository) {
        this.api = api;
        this.repository = repository;
    }

    public void loadAll() {
        arenas.clear();
        rewards.clear();
        schedules.clear();
        for (Arena arena : repository.loadArenas()) {
            arenas.put(normalize(arena.name()), arena);
        }
        repository.loadRewards().forEach((arenaName, commands) ->
                rewards.put(normalize(arenaName), new CopyOnWriteArrayList<>(commands)));
        repository.loadSchedules().forEach((arenaName, list) ->
                schedules.put(normalize(arenaName), new CopyOnWriteArrayList<>(list)));
    }

    public Optional<Arena> find(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(arenas.get(normalize(name)));
    }

    public Collection<Arena> all() {
        return List.copyOf(arenas.values());
    }

    public int size() {
        return arenas.size();
    }

    public boolean create(String name) {
        Arena arena = Arena.create(name);
        if (arenas.putIfAbsent(normalize(name), arena) != null) {
            return false;
        }
        api.scheduler().runAsync(() -> repository.upsertArena(arena));
        return true;
    }

    public boolean delete(String name) {
        String key = normalize(name);
        if (arenas.remove(key) == null) {
            return false;
        }
        rewards.remove(key);
        schedules.remove(key);
        api.scheduler().runAsync(() -> repository.deleteArena(name));
        return true;
    }

    public Optional<Arena> mutate(String name, UnaryOperator<Arena> mutation) {
        String key = normalize(name);
        Arena updated = arenas.computeIfPresent(key, (unused, current) -> mutation.apply(current));
        if (updated != null) {
            api.scheduler().runAsync(() -> repository.upsertArena(updated));
        }
        return Optional.ofNullable(updated);
    }

    public List<String> rewardsOf(String arenaName) {
        return List.copyOf(rewards.getOrDefault(normalize(arenaName), List.of()));
    }

    public void addReward(String arenaName, String command) {
        rewards.computeIfAbsent(normalize(arenaName), key -> new CopyOnWriteArrayList<>()).add(command);
        api.scheduler().runAsync(() -> repository.addReward(arenaName, command));
    }

    public void clearRewards(String arenaName) {
        rewards.remove(normalize(arenaName));
        api.scheduler().runAsync(() -> repository.clearRewards(arenaName));
    }

    public List<ArenaSchedule> schedulesOf(String arenaName) {
        return List.copyOf(schedules.getOrDefault(normalize(arenaName), List.of()));
    }

    public Map<String, List<ArenaSchedule>> allSchedules() {
        return Map.copyOf(schedules);
    }

    public void addSchedule(String arenaName, ArenaSchedule schedule) {
        api.scheduler().supplyAsync(() -> repository.addSchedule(arenaName, schedule)).thenAccept(generatedId -> {
            ArenaSchedule persisted = new ArenaSchedule(
                    generatedId,
                    schedule.arenaName(),
                    schedule.type(),
                    schedule.dayOfWeek(),
                    schedule.hour(),
                    schedule.minute()
            );
            schedules.computeIfAbsent(normalize(arenaName), key -> new CopyOnWriteArrayList<>()).add(persisted);
        });
    }

    public boolean removeSchedule(String arenaName, long scheduleId) {
        List<ArenaSchedule> list = schedules.get(normalize(arenaName));
        if (list == null) {
            return false;
        }
        boolean removed = list.removeIf(schedule -> schedule.id() == scheduleId);
        if (removed) {
            api.scheduler().runAsync(() -> repository.deleteSchedule(scheduleId));
        }
        return removed;
    }

    public List<Arena> tagsHeldBy(long clanId) {
        List<Arena> held = new ArrayList<>();
        for (Arena arena : arenas.values()) {
            if (arena.holderClanId() == clanId && !arena.tag().isBlank()) {
                held.add(arena);
            }
        }
        return held;
    }

    private String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }
}
