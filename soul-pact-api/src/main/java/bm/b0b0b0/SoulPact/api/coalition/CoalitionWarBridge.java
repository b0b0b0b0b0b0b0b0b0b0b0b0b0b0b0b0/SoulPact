package bm.b0b0b0.SoulPact.api.coalition;

import java.util.concurrent.CompletableFuture;

public interface CoalitionWarBridge {

    CompletableFuture<Boolean> canDeclareWar(long attackerClanId, long defenderClanId);

    boolean allowsAllyFlagBreak(long breakerClanId, long baseOwnerClanId, long warAttackerClanId, long warDefenderClanId);

    void onWarDeclared(long attackerClanId, long defenderClanId);

    void onWarStarted(long attackerClanId, long defenderClanId);

    void onCaptureStarted(long targetClanId, long holderClanId, long deadlineAt);

    CompletableFuture<CoalitionTreasuryDistribution> distributeVictoryTreasury(CoalitionWarOutcome outcome);

    void onWarEnded(CoalitionWarOutcome outcome, boolean disbanded);
}
