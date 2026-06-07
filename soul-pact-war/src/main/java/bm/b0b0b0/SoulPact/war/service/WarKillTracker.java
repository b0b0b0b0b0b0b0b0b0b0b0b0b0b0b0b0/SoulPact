package bm.b0b0b0.SoulPact.war.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class WarKillTracker {

    private final Map<Long, Map<Long, Integer>> killsByWarId = new ConcurrentHashMap<>();

    public void recordKill(long warId, long killerClanId) {
        killsByWarId.computeIfAbsent(warId, ignored -> new ConcurrentHashMap<>())
                .merge(killerClanId, 1, Integer::sum);
    }

    public Map<Long, Integer> snapshot(long warId) {
        Map<Long, Integer> kills = killsByWarId.get(warId);
        if (kills == null || kills.isEmpty()) {
            return Map.of();
        }
        return Map.copyOf(kills);
    }

    public void clear(long warId) {
        killsByWarId.remove(warId);
    }
}
