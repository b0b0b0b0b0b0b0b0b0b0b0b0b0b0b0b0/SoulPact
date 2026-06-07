package bm.b0b0b0.SoulPact.api.coalition;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface CoalitionWarBridge {

    CompletableFuture<Boolean> canDeclareWar(long attackerClanId, long defenderClanId);

    boolean allowsAllyFlagBreak(long breakerClanId, long baseOwnerClanId, long warAttackerClanId, long warDefenderClanId);

    default Set<Long> coalitionClanIds(long clanId) {
        return Set.of(clanId);
    }

    void onWarDeclared(long attackerClanId, long defenderClanId);

    void onWarStarted(long attackerClanId, long defenderClanId);

    void onWarEnemyBaseRevealed(
            long friendClanId,
            String enemyTag,
            String enemyWorld,
            int enemyX,
            int enemyY,
            int enemyZ
    );

    void onCaptureStarted(long targetClanId, long holderClanId, long deadlineAt);

    CompletableFuture<CoalitionTreasuryDistribution> distributeVictoryTreasury(CoalitionWarOutcome outcome);

    void onWarEnded(CoalitionWarOutcome outcome, boolean disbanded);

    void onCoalitionWithdrawnFromWar(long warId, long attackerClanId, long defenderClanId, long triggerClanId);
}
