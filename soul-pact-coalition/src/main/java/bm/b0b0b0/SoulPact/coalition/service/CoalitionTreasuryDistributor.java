package bm.b0b0b0.SoulPact.coalition.service;

import bm.b0b0b0.SoulPact.api.coalition.CoalitionTreasuryDistribution;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarOutcome;
import bm.b0b0b0.SoulPact.api.treasury.TreasuryOperationResult;
import bm.b0b0b0.SoulPact.coalition.config.CoalitionConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class CoalitionTreasuryDistributor {

    private final CoalitionConfig config;
    private final CoalitionMembershipCache membershipCache;
    private final CoalitionTreasuryBridge treasuryBridge;

    public CoalitionTreasuryDistributor(
            CoalitionConfig config,
            CoalitionMembershipCache membershipCache,
            CoalitionTreasuryBridge treasuryBridge
    ) {
        this.config = config;
        this.membershipCache = membershipCache;
        this.treasuryBridge = treasuryBridge;
    }

    public CompletableFuture<CoalitionTreasuryDistribution> distribute(CoalitionWarOutcome outcome) {
        Set<Long> winnerCoalition = membershipCache.membersOf(outcome.winnerClanId());
        if (winnerCoalition.size() <= 1) {
            return CompletableFuture.completedFuture(CoalitionTreasuryDistribution.NOT_APPLICABLE);
        }
        return treasuryBridge.balance(outcome.loserClanId()).thenCompose(totalBalance -> {
            if (totalBalance <= 0.0D) {
                return CompletableFuture.completedFuture(CoalitionTreasuryDistribution.COALITION_SPLIT);
            }
            Map<Long, Double> shares = buildShares(outcome, winnerCoalition, totalBalance);
            CompletableFuture<TreasuryOperationResult> chain = CompletableFuture.completedFuture(TreasuryOperationResult.SUCCESS);
            for (Map.Entry<Long, Double> entry : shares.entrySet()) {
                if (entry.getValue() <= 0.0D) {
                    continue;
                }
                chain = chain.thenCompose(ignored -> treasuryBridge.seize(
                        outcome.loserClanId(),
                        entry.getKey(),
                        entry.getValue(),
                        "coalition-war-victory"
                ));
            }
            return chain.thenApply(result -> result == TreasuryOperationResult.SUCCESS
                    ? CoalitionTreasuryDistribution.COALITION_SPLIT
                    : CoalitionTreasuryDistribution.FAILED);
        });
    }

    private Map<Long, Double> buildShares(CoalitionWarOutcome outcome, Set<Long> winnerCoalition, double totalBalance) {
        Map<Long, Double> shares = new HashMap<>();
        long warParticipant = outcome.warParticipantClanId();
        long flagCapture = outcome.flagCaptureClanId();
        if (warParticipant == flagCapture) {
            shares.put(warParticipant, totalBalance * (config.warSharePercent() + config.captureSharePercent()));
        } else {
            shares.put(warParticipant, totalBalance * config.warSharePercent());
            shares.put(flagCapture, totalBalance * config.captureSharePercent());
        }
        Set<Long> poolRecipients = new HashSet<>(winnerCoalition);
        poolRecipients.remove(warParticipant);
        poolRecipients.remove(flagCapture);
        double poolAmount = totalBalance * config.poolSharePercent();
        if (poolRecipients.isEmpty()) {
            shares.merge(warParticipant, poolAmount, Double::sum);
            return shares;
        }
        double each = poolAmount / poolRecipients.size();
        for (long recipient : poolRecipients) {
            shares.put(recipient, each);
        }
        return shares;
    }
}
