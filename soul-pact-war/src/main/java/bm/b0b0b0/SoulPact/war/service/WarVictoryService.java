package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionTreasuryDistribution;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarOutcome;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarCaptureState;
import bm.b0b0b0.SoulPact.war.model.WarStatuses;
import bm.b0b0b0.SoulPact.war.repository.WarRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class WarVictoryService {

    private final SoulPactApi api;
    private final WarMessages messages;
    private final WarRepository repository;
    private final WarTreasuryBridge treasuryBridge;
    private final WarSpoilsBridge spoilsBridge;
    private final WarStateCache stateCache;
    private final WarBossBarService bossBarService;
    private final CoalitionWarBridgeLookup coalitionWarBridgeLookup;

    public WarVictoryService(
            SoulPactApi api,
            WarMessages messages,
            WarRepository repository,
            WarTreasuryBridge treasuryBridge,
            WarSpoilsBridge spoilsBridge,
            WarStateCache stateCache,
            WarBossBarService bossBarService,
            CoalitionWarBridgeLookup coalitionWarBridgeLookup
    ) {
        this.api = api;
        this.messages = messages;
        this.repository = repository;
        this.treasuryBridge = treasuryBridge;
        this.spoilsBridge = spoilsBridge;
        this.stateCache = stateCache;
        this.bossBarService = bossBarService;
        this.coalitionWarBridgeLookup = coalitionWarBridgeLookup;
    }

    public void tryResolveCaptureDeadline(long warId, long winnerClanId, long loserClanId) {
        Optional<Long> deadlineOptional = stateCache.captureDeadline(warId);
        if (deadlineOptional.isEmpty() || System.currentTimeMillis() < deadlineOptional.get()) {
            return;
        }
        resolveVictory(warId, winnerClanId, loserClanId);
    }

    public void resolveVictory(long warId, long winnerClanId, long loserClanId) {
        Optional<ActiveWarRecord> warOptional = stateCache.activeWarFor(loserClanId);
        long flagCaptureClanId = stateCache.captureForWar(warId)
                .map(WarCaptureState::holderClanId)
                .orElse(winnerClanId);
        api.scheduler().runAsync(() -> {
            repository.finishWar(warId, WarStatuses.FINISHED);
            repository.clearCapture(warId);
            stateCache.clearCapture(warId);
            warOptional.ifPresent(stateCache::removeActiveWar);
            ActiveWarRecord war = warOptional.orElse(null);
            long attackerClanId = war == null ? winnerClanId : war.attackerClanId();
            long defenderClanId = war == null ? loserClanId : war.defenderClanId();
            CoalitionWarOutcome outcome = new CoalitionWarOutcome(
                    attackerClanId,
                    defenderClanId,
                    loserClanId,
                    winnerClanId,
                    flagCaptureClanId
            );
            CompletableFuture<Boolean> treasuryFuture = resolveTreasury(outcome);
            treasuryFuture.thenCompose(ignored -> spoilsBridge.transferWarSpoils(loserClanId, winnerClanId))
                    .thenCompose(ignored -> api.clanLifecycle().disbandByWarDefeat(loserClanId))
                    .thenAccept(disbanded -> {
                        coalitionWarBridgeLookup.resolve().ifPresent(bridge -> bridge.onWarEnded(outcome, disbanded));
                        api.scheduler().runSync(() -> {
                            warOptional.ifPresent(bossBarService::refreshWarClans);
                            broadcastVictory(winnerClanId, loserClanId, disbanded);
                        });
                    });
        });
    }

    private CompletableFuture<Boolean> resolveTreasury(CoalitionWarOutcome outcome) {
        Optional<bm.b0b0b0.SoulPact.api.coalition.CoalitionWarBridge> coalitionBridge = coalitionWarBridgeLookup.resolve();
        if (coalitionBridge.isEmpty()) {
            return treasuryBridge.transferAll(outcome.loserClanId(), outcome.winnerClanId())
                    .thenApply(result -> true);
        }
        return coalitionBridge.get().distributeVictoryTreasury(outcome).thenCompose(distribution -> {
            if (distribution == CoalitionTreasuryDistribution.COALITION_SPLIT) {
                return CompletableFuture.completedFuture(true);
            }
            return treasuryBridge.transferAll(outcome.loserClanId(), outcome.winnerClanId())
                    .thenApply(result -> true);
        });
    }

    private void broadcastVictory(long winnerClanId, long loserClanId, boolean disbanded) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            messages.send(online, "war.victory.broadcast", Map.of(
                    "winner", String.valueOf(winnerClanId),
                    "loser", String.valueOf(loserClanId),
                    "disbanded", disbanded ? "1" : "0"
            ));
        }
    }
}
