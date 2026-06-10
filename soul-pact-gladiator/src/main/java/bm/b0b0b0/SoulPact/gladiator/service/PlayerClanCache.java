package bm.b0b0b0.SoulPact.gladiator.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class PlayerClanCache {

    private final SoulPactApi api;
    private final long ttlMillis;
    private final ConcurrentHashMap<UUID, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Set<UUID> resolving = ConcurrentHashMap.newKeySet();

    public PlayerClanCache(SoulPactApi api, long ttlMillis) {
        this.api = api;
        this.ttlMillis = ttlMillis;
    }

    public Long cachedClanId(UUID playerId) {
        CacheEntry entry = cache.get(playerId);
        if (entry == null || entry.isExpired(System.currentTimeMillis(), ttlMillis)) {
            scheduleResolve(playerId, null);
            return null;
        }
        return entry.clanId();
    }

    public void lookup(UUID playerId, Consumer<Long> clanIdConsumer) {
        CacheEntry entry = cache.get(playerId);
        if (entry != null && !entry.isExpired(System.currentTimeMillis(), ttlMillis)) {
            clanIdConsumer.accept(entry.clanId());
            return;
        }
        scheduleResolve(playerId, clanIdConsumer);
    }

    public void invalidateAll() {
        cache.clear();
    }

    private void scheduleResolve(UUID playerId, Consumer<Long> clanIdConsumer) {
        if (!resolving.add(playerId)) {
            return;
        }
        api.findClanByPlayer(playerId).whenComplete((clanOptional, throwable) -> {
            resolving.remove(playerId);
            if (throwable != null) {
                return;
            }
            Long clanId = Optional.ofNullable(clanOptional)
                    .flatMap(optional -> optional.map(ClanSnapshot::id))
                    .orElse(null);
            cache.put(playerId, new CacheEntry(clanId, System.currentTimeMillis()));
            if (clanIdConsumer != null) {
                clanIdConsumer.accept(clanId);
            }
        });
    }

    private record CacheEntry(Long clanId, long cachedAt) {

        boolean isExpired(long now, long ttlMillis) {
            return now - cachedAt >= ttlMillis;
        }
    }
}
