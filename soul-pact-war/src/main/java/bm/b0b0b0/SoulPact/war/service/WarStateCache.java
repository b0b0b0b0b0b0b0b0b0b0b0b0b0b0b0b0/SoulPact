package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarCaptureState;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class WarStateCache {

    private final Map<Long, ActiveWarRecord> activeByClan = new ConcurrentHashMap<>();
    private final Map<Long, List<WarDeclarationRecord>> pendingByDefender = new ConcurrentHashMap<>();
    private final Map<Long, WarCaptureState> captureByWar = new ConcurrentHashMap<>();

    public void putActiveWar(ActiveWarRecord war) {
        activeByClan.put(war.attackerClanId(), war);
        activeByClan.put(war.defenderClanId(), war);
    }

    public void removeActiveWar(ActiveWarRecord war) {
        activeByClan.remove(war.attackerClanId(), war);
        activeByClan.remove(war.defenderClanId(), war);
        captureByWar.remove(war.id());
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

    public void setPendingForDefender(long defenderClanId, List<WarDeclarationRecord> records) {
        if (records.isEmpty()) {
            pendingByDefender.remove(defenderClanId);
            return;
        }
        pendingByDefender.put(defenderClanId, List.copyOf(records));
    }

    public boolean hasPendingForDefender(long defenderClanId) {
        List<WarDeclarationRecord> records = pendingByDefender.get(defenderClanId);
        return records != null && !records.isEmpty();
    }

    public void setCapture(long warId, long holderClanId, long targetClanId, long deadlineAt) {
        captureByWar.put(warId, new WarCaptureState(holderClanId, targetClanId, deadlineAt));
    }

    public void clearCapture(long warId) {
        captureByWar.remove(warId);
    }

    public Optional<WarCaptureState> captureForWar(long warId) {
        return Optional.ofNullable(captureByWar.get(warId));
    }

    public Optional<Long> captureDeadline(long warId) {
        return captureForWar(warId).map(WarCaptureState::deadlineAt);
    }
}
