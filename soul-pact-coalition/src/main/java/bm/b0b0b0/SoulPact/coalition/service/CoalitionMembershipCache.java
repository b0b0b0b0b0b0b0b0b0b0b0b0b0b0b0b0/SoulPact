package bm.b0b0b0.SoulPact.coalition.service;

import bm.b0b0b0.SoulPact.coalition.repository.CoalitionRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CoalitionMembershipCache {

    private final CoalitionRepository repository;
    private final Map<Long, Long> clanToCoalition = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> coalitionToClans = new ConcurrentHashMap<>();

    public CoalitionMembershipCache(CoalitionRepository repository) {
        this.repository = repository;
    }

    public void reload() {
        clanToCoalition.clear();
        coalitionToClans.clear();
        for (long clanId : repository.listAllMemberClanIds()) {
            repository.findCoalitionIdByClan(clanId).ifPresent(coalitionId -> putMember(coalitionId, clanId));
        }
    }

    public void putMember(long coalitionId, long clanId) {
        clanToCoalition.put(clanId, coalitionId);
        coalitionToClans.compute(coalitionId, (ignored, members) -> {
            Set<Long> updated = members == null ? new HashSet<>() : new HashSet<>(members);
            updated.add(clanId);
            return updated;
        });
    }

    public void removeClan(long clanId) {
        Long coalitionId = clanToCoalition.remove(clanId);
        if (coalitionId == null) {
            return;
        }
        coalitionToClans.computeIfPresent(coalitionId, (ignored, members) -> {
            Set<Long> updated = new HashSet<>(members);
            updated.remove(clanId);
            return updated.isEmpty() ? null : updated;
        });
    }

    public Set<Long> membersOf(long clanId) {
        Long coalitionId = clanToCoalition.get(clanId);
        if (coalitionId == null) {
            return Set.of(clanId);
        }
        Set<Long> members = coalitionToClans.get(coalitionId);
        if (members == null || members.isEmpty()) {
            return Set.of(clanId);
        }
        return Collections.unmodifiableSet(members);
    }

    public boolean sharesCoalition(long clanA, long clanB) {
        if (clanA == clanB) {
            return true;
        }
        Set<Long> membersA = membersOf(clanA);
        Set<Long> membersB = membersOf(clanB);
        for (long member : membersA) {
            if (membersB.contains(member)) {
                return true;
            }
        }
        return false;
    }

    public boolean coalitionsOverlap(long clanA, long clanB) {
        Set<Long> membersA = membersOf(clanA);
        Set<Long> membersB = membersOf(clanB);
        for (long member : membersA) {
            if (membersB.contains(member)) {
                return true;
            }
        }
        return false;
    }

    public List<Long> otherMembers(long clanId) {
        return membersOf(clanId).stream().filter(member -> member != clanId).sorted().toList();
    }

    public int coalitionSize(long clanId) {
        return membersOf(clanId).size();
    }
}
