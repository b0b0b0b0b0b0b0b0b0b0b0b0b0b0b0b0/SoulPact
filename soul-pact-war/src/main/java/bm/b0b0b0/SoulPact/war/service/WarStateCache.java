package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarCaptureState;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class WarStateCache {

    private final Map<Long, ActiveWarRecord> activeByClan = new ConcurrentHashMap<>();
    private final WarPendingIndex pendingIndex = new WarPendingIndex();
    private final Map<Long, WarCaptureState> captureByWar = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> combatZonesByWarId = new ConcurrentHashMap<>();

    public void putActiveWar(ActiveWarRecord war) {
        activeByClan.put(war.attackerClanId(), war);
        activeByClan.put(war.defenderClanId(), war);
    }

    public void removeActiveWar(ActiveWarRecord war) {
        activeByClan.remove(war.attackerClanId(), war);
        activeByClan.remove(war.defenderClanId(), war);
        captureByWar.remove(war.id());
    }

    public void rememberCombatZones(long warId, Set<Long> clanIds) {
        combatZonesByWarId.put(warId, Set.copyOf(clanIds));
    }

    public Set<Long> forgetCombatZones(long warId) {
        Set<Long> clanIds = combatZonesByWarId.remove(warId);
        return clanIds == null ? Set.of() : clanIds;
    }

    public void removeCombatClansFromWar(long warId, Set<Long> clanIds) {
        Set<Long> current = combatZonesByWarId.get(warId);
        if (current == null || current.isEmpty() || clanIds.isEmpty()) {
            return;
        }
        Set<Long> updated = new HashSet<>(current);
        updated.removeAll(clanIds);
        combatZonesByWarId.put(warId, Set.copyOf(updated));
    }

    public Collection<ActiveWarRecord> allActiveWars() {
        Map<Long, ActiveWarRecord> byId = new HashMap<>();
        for (ActiveWarRecord war : activeByClan.values()) {
            byId.putIfAbsent(war.id(), war);
        }
        return byId.values();
    }

    public Optional<ActiveWarRecord> findWarById(long warId) {
        for (ActiveWarRecord war : allActiveWars()) {
            if (war.id() == warId) {
                return Optional.of(war);
            }
        }
        return Optional.empty();
    }

    public Optional<ActiveWarRecord> activeWarFor(long clanId) {
        return Optional.ofNullable(activeByClan.get(clanId));
    }

    public boolean areAtWar(long clanA, long clanB) {
        Optional<ActiveWarRecord> warOptional = activeWarFor(clanA);
        if (warOptional.isEmpty()) {
            return false;
        }
        ActiveWarRecord war = warOptional.get();
        return war.attackerClanId() == clanB || war.defenderClanId() == clanB;
    }

    public void rebuildPending(List<WarDeclarationRecord> records) {
        pendingIndex.rebuild(records);
    }

    public void setPendingForDefender(long defenderClanId, List<WarDeclarationRecord> records) {
        rebuildPending(records);
    }

    public boolean hasPendingForDefender(long defenderClanId) {
        return pendingIndex.hasPendingForDefender(defenderClanId);
    }

    public boolean hasPendingForAttacker(long attackerClanId) {
        return pendingIndex.hasPendingForAttacker(attackerClanId);
    }

    public void setCapture(long warId, long holderClanId, long targetClanId, long deadlineAt) {
        captureByWar.put(warId, new WarCaptureState(holderClanId, targetClanId, deadlineAt));
    }

    public void clearCapture(long warId) {
        captureByWar.remove(warId);
    }

    public boolean clearCaptureIfMatches(long warId, WarCaptureState expected) {
        return captureByWar.remove(warId, expected);
    }

    public Optional<WarCaptureState> captureForWar(long warId) {
        return Optional.ofNullable(captureByWar.get(warId));
    }

    public Set<Long> activeCaptureWarIds() {
        return Set.copyOf(captureByWar.keySet());
    }

    public Optional<WarCaptureState> takeCaptureIfDue(long warId) {
        WarCaptureState capture = captureByWar.get(warId);
        if (capture == null || System.currentTimeMillis() < capture.deadlineAt()) {
            return Optional.empty();
        }
        return captureByWar.remove(warId, capture) ? Optional.of(capture) : Optional.empty();
    }

    public Optional<Long> captureDeadline(long warId) {
        return captureForWar(warId).map(WarCaptureState::deadlineAt);
    }
}
