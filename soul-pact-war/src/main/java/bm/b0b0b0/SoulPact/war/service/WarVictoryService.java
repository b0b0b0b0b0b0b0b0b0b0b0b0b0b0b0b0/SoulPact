package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionTreasuryDistribution;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarOutcome;
import bm.b0b0b0.SoulPact.api.event.ClanWarEndEvent;
import bm.b0b0b0.SoulPact.api.event.SoulPactEvents;
import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarCaptureState;
import bm.b0b0b0.SoulPact.war.model.WarStatuses;
import bm.b0b0b0.SoulPact.war.repository.WarRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class WarVictoryService {

    private final SoulPactApi api;
    private final WarRepository repository;
    private final WarTreasuryBridge treasuryBridge;
    private final WarSpoilsBridge spoilsBridge;
    private final WarStateCache stateCache;
    private final WarBossBarService bossBarService;
    private final CoalitionWarBridgeLookup coalitionWarBridgeLookup;
    private final WarLandCombatService landCombatService;
    private final WarCoalitionWithdrawService coalitionWithdrawService;
    private final WarKillTracker killTracker;
    private final WarVictoryAnnouncer victoryAnnouncer;
    private final WarClanLookup clanLookup;
    private final WarLandBridgeLookup landBridgeLookup;

    public WarVictoryService(
            SoulPactApi api,
            WarRepository repository,
            WarTreasuryBridge treasuryBridge,
            WarSpoilsBridge spoilsBridge,
            WarStateCache stateCache,
            WarBossBarService bossBarService,
            CoalitionWarBridgeLookup coalitionWarBridgeLookup,
            WarLandCombatService landCombatService,
            WarCoalitionWithdrawService coalitionWithdrawService,
            WarKillTracker killTracker,
            WarVictoryAnnouncer victoryAnnouncer,
            WarClanLookup clanLookup,
            WarLandBridgeLookup landBridgeLookup
    ) {
        this.api = api;
        this.repository = repository;
        this.treasuryBridge = treasuryBridge;
        this.spoilsBridge = spoilsBridge;
        this.stateCache = stateCache;
        this.bossBarService = bossBarService;
        this.coalitionWarBridgeLookup = coalitionWarBridgeLookup;
        this.landCombatService = landCombatService;
        this.coalitionWithdrawService = coalitionWithdrawService;
        this.killTracker = killTracker;
        this.victoryAnnouncer = victoryAnnouncer;
        this.clanLookup = clanLookup;
        this.landBridgeLookup = landBridgeLookup;
    }

    public void resolveDueCaptures() {
        for (long warId : stateCache.activeCaptureWarIds()) {
            stateCache.takeCaptureIfDue(warId).ifPresent(capture -> finishDueCapture(warId, capture));
        }
    }

    public void tryResolveCaptureDeadline(long warId, long captorClanId, long targetClanId) {
        Optional<WarCaptureState> captureOptional = stateCache.captureForWar(warId);
        if (captureOptional.isEmpty()) {
            return;
        }
        WarCaptureState capture = captureOptional.get();
        if (capture.holderClanId() != captorClanId || capture.targetClanId() != targetClanId) {
            return;
        }
        if (System.currentTimeMillis() < capture.deadlineAt()) {
            return;
        }
        stateCache.takeCaptureIfDue(warId).ifPresent(resolved -> finishDueCapture(warId, resolved));
    }

    private void finishDueCapture(long warId, WarCaptureState capture) {
        Optional<ActiveWarRecord> warOptional = stateCache.findWarById(warId);
        if (warOptional.isEmpty()) {
            return;
        }
        ActiveWarRecord war = warOptional.get();
        long targetClanId = capture.targetClanId();
        long captorClanId = capture.holderClanId();
        if (targetClanId == war.attackerClanId() || targetClanId == war.defenderClanId()) {
            long winnerClanId = warSideWinnerForCapture(war, captorClanId, targetClanId);
            resolveVictory(warId, winnerClanId, targetClanId, captorClanId);
            return;
        }
        coalitionWithdrawService.withdrawFromCapture(war, targetClanId);
    }

    private long warSideWinnerForCapture(ActiveWarRecord war, long captorClanId, long targetClanId) {
        if (captorClanId == war.attackerClanId() || captorClanId == war.defenderClanId()) {
            return captorClanId;
        }
        return war.enemyClanIdFor(targetClanId);
    }

    public void resolveSelfFlagDefeat(long warId, long winnerClanId, long loserClanId) {
        stateCache.clearCapture(warId);
        resolveVictory(warId, winnerClanId, loserClanId, winnerClanId);
    }

    public void resolveVictory(long warId, long winnerClanId, long loserClanId) {
        long flagCaptureClanId = stateCache.captureForWar(warId)
                .map(WarCaptureState::holderClanId)
                .orElse(winnerClanId);
        resolveVictory(warId, winnerClanId, loserClanId, flagCaptureClanId);
    }

    private void resolveVictory(long warId, long winnerClanId, long loserClanId, long flagCaptureClanId) {
        Optional<ActiveWarRecord> resolvedWar = stateCache.findWarById(warId);
        if (resolvedWar.isEmpty()) {
            resolvedWar = stateCache.activeWarFor(loserClanId);
        }
        final Optional<ActiveWarRecord> warOptional = resolvedWar;
        Map<Long, Integer> killsByClan = killTracker.snapshot(warId);
        stateCache.clearCapture(warId);
        killTracker.clear(warId);
        landCombatService.disableForWar(warId);
        warOptional.ifPresent(stateCache::removeActiveWar);
        warOptional.ifPresent(bossBarService::refreshWarClans);
        api.scheduler().runSync(() -> victoryAnnouncer.announce(winnerClanId, loserClanId, killsByClan));
        api.scheduler().runAsync(() -> {
            repository.finishWar(warId, WarStatuses.FINISHED);
            repository.clearCapture(warId);
            ActiveWarRecord war = warOptional.orElse(null);
            long attackerClanId = war == null ? winnerClanId : war.attackerClanId();
            long defenderClanId = war == null ? loserClanId : war.defenderClanId();
            String winnerTag = clanLookup.findClanTagSync(winnerClanId).orElse(String.valueOf(winnerClanId));
            String loserTag = clanLookup.findClanTagSync(loserClanId).orElse(String.valueOf(loserClanId));
            String attackerTag = attackerClanId == winnerClanId ? winnerTag : loserTag;
            String defenderTag = defenderClanId == winnerClanId ? winnerTag : loserTag;
            api.scheduler().runSync(() -> SoulPactEvents.fire(
                    new ClanWarEndEvent(attackerTag, defenderTag, winnerTag, loserTag)
            ));
            CoalitionWarOutcome outcome = new CoalitionWarOutcome(
                    attackerClanId,
                    defenderClanId,
                    loserClanId,
                    winnerClanId,
                    flagCaptureClanId
            );
            CompletableFuture<Boolean> treasuryFuture = resolveTreasury(outcome);
            treasuryFuture.thenCompose(ignored -> spoilsBridge.transferWarSpoils(loserClanId, winnerClanId))
                    .thenAccept(ignored -> clanLookup.incrementWarsWon(winnerClanId))
                    .thenCompose(ignored -> destroyLoserBase(loserClanId))
                    .thenCompose(ignored -> api.clanLifecycle().disbandByWarDefeat(loserClanId))
                    .thenAccept(disbanded -> coalitionWarBridgeLookup.resolve()
                            .ifPresent(bridge -> bridge.onWarEnded(outcome, disbanded)));
        });
    }

    private CompletableFuture<Void> destroyLoserBase(long loserClanId) {
        return landBridgeLookup.resolve()
                .map(land -> land.destroyClanBase(loserClanId))
                .orElseGet(() -> CompletableFuture.completedFuture(null));
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
}
