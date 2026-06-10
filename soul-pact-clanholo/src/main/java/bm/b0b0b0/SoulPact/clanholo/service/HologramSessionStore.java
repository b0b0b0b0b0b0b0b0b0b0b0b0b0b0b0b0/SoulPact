package bm.b0b0b0.SoulPact.clanholo.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HologramSessionStore {

    private final Map<UUID, Long> selectedByPlayer = new ConcurrentHashMap<>();

    public void select(UUID playerId, long hologramId) {
        selectedByPlayer.put(playerId, hologramId);
    }

    public Long selected(UUID playerId) {
        return selectedByPlayer.get(playerId);
    }

    public void clear(UUID playerId, long hologramId) {
        selectedByPlayer.computeIfPresent(playerId, (id, selected) -> selected == hologramId ? null : selected);
    }
}
