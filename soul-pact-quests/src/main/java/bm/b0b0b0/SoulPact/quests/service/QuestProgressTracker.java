package bm.b0b0b0.SoulPact.quests.service;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class QuestProgressTracker {

    private final ConcurrentHashMap<Long, ActiveQuestState> states = new ConcurrentHashMap<>();

    public Optional<ActiveQuestState> state(long clanId) {
        return Optional.ofNullable(states.get(clanId));
    }

    public boolean hasActive(long clanId) {
        return states.containsKey(clanId);
    }

    public boolean putIfAbsent(ActiveQuestState state) {
        return states.putIfAbsent(state.clanId(), state) == null;
    }

    public void remove(long clanId) {
        states.remove(clanId);
    }

    public Collection<ActiveQuestState> all() {
        return states.values();
    }

    public void clear() {
        states.clear();
    }
}
