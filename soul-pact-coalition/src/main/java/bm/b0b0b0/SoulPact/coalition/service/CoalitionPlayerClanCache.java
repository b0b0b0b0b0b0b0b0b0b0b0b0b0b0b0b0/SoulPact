package bm.b0b0b0.SoulPact.coalition.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CoalitionPlayerClanCache {

    private final Map<UUID, Long> clanByPlayer = new ConcurrentHashMap<>();

    public void put(UUID playerId, long clanId) {
        clanByPlayer.put(playerId, clanId);
    }

    public void remove(UUID playerId) {
        clanByPlayer.remove(playerId);
    }

    public Long find(UUID playerId) {
        return clanByPlayer.get(playerId);
    }
}
