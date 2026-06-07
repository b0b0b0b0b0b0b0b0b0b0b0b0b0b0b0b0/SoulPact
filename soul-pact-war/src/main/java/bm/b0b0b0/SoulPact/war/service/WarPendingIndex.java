package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WarPendingIndex {

    private final Map<Long, List<WarDeclarationRecord>> pendingByDefender = new HashMap<>();
    private final Map<Long, List<WarDeclarationRecord>> pendingByAttacker = new HashMap<>();

    public void rebuild(List<WarDeclarationRecord> records) {
        pendingByDefender.clear();
        pendingByAttacker.clear();
        for (WarDeclarationRecord record : records) {
            pendingByDefender.computeIfAbsent(record.defenderClanId(), ignored -> new ArrayList<>()).add(record);
            pendingByAttacker.computeIfAbsent(record.attackerClanId(), ignored -> new ArrayList<>()).add(record);
        }
    }

    public boolean hasPendingForDefender(long defenderClanId) {
        List<WarDeclarationRecord> records = pendingByDefender.get(defenderClanId);
        return records != null && !records.isEmpty();
    }

    public boolean hasPendingForAttacker(long attackerClanId) {
        List<WarDeclarationRecord> records = pendingByAttacker.get(attackerClanId);
        return records != null && !records.isEmpty();
    }
}
