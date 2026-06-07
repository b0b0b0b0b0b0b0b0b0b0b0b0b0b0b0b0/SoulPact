package bm.b0b0b0.SoulPact.coalition.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class CoalitionWarStateTracker {

    public enum Phase {
        DECLARED,
        ACTIVE,
        CAPTURE
    }

    public record AllyWarView(
            long friendClanId,
            String friendTag,
            long enemyClanId,
            String enemyTag,
            Phase phase,
            long deadlineAt
    ) {
    }

    private final Map<Long, AllyWarView> viewsByFriendClan = new ConcurrentHashMap<>();

    public void setView(long friendClanId, AllyWarView view) {
        viewsByFriendClan.put(friendClanId, view);
    }

    public void clearForClan(long clanId) {
        viewsByFriendClan.remove(clanId);
    }

    public Optional<AllyWarView> viewFor(long friendClanId) {
        return Optional.ofNullable(viewsByFriendClan.get(friendClanId));
    }

    public void clearAll() {
        viewsByFriendClan.clear();
    }
}
