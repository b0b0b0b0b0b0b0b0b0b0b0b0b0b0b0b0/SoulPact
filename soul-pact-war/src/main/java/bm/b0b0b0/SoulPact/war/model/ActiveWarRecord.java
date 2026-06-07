package bm.b0b0b0.SoulPact.war.model;

import java.util.Optional;

public record ActiveWarRecord(
        long id,
        long attackerClanId,
        long defenderClanId,
        long startedAt,
        String status,
        String attackerFlagWorld,
        int attackerFlagX,
        int attackerFlagY,
        int attackerFlagZ,
        String defenderFlagWorld,
        int defenderFlagX,
        int defenderFlagY,
        int defenderFlagZ
) {

    public ActiveWarRecord(
            long id,
            long attackerClanId,
            long defenderClanId,
            long startedAt,
            String status
    ) {
        this(id, attackerClanId, defenderClanId, startedAt, status, null, 0, 0, 0, null, 0, 0, 0);
    }

    public Optional<WarFlagSnapshot> attackerFlag() {
        if (attackerFlagWorld == null || attackerFlagWorld.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new WarFlagSnapshot(attackerFlagWorld, attackerFlagX, attackerFlagY, attackerFlagZ));
    }

    public Optional<WarFlagSnapshot> defenderFlag() {
        if (defenderFlagWorld == null || defenderFlagWorld.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new WarFlagSnapshot(defenderFlagWorld, defenderFlagX, defenderFlagY, defenderFlagZ));
    }

    public Optional<WarFlagSnapshot> enemyFlagFor(long viewerClanId) {
        if (viewerClanId == attackerClanId) {
            return defenderFlag();
        }
        if (viewerClanId == defenderClanId) {
            return attackerFlag();
        }
        return Optional.empty();
    }

    public long enemyClanIdFor(long viewerClanId) {
        if (viewerClanId == attackerClanId) {
            return defenderClanId;
        }
        if (viewerClanId == defenderClanId) {
            return attackerClanId;
        }
        return 0L;
    }
}
