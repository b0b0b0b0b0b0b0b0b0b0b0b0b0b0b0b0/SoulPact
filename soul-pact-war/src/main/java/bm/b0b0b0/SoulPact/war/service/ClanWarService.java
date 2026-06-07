package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.treasury.TreasuryOperationResult;
import bm.b0b0b0.SoulPact.api.war.ClanWarInfoExtras;
import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.message.WarPendingChatPresenter;
import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import bm.b0b0b0.SoulPact.war.model.WarStatuses;
import bm.b0b0b0.SoulPact.war.repository.WarRepository;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanWarService {

    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));

    private final SoulPactApi api;
    private final WarConfig config;
    private final WarMessages messages;
    private final WarRepository repository;
    private final WarTreasuryBridge treasuryBridge;
    private final WarStateCache stateCache;
    private final WarVictoryService victoryService;
    private final WarPlayerClanCache playerClanCache;
    private final WarPendingChatPresenter pendingChatPresenter;
    private final WarBossBarService bossBarService;
    private final CoalitionWarBridgeLookup coalitionWarBridgeLookup;

    public ClanWarService(
            SoulPactApi api,
            WarConfig config,
            WarMessages messages,
            WarRepository repository,
            WarTreasuryBridge treasuryBridge,
            WarStateCache stateCache,
            WarVictoryService victoryService,
            WarPlayerClanCache playerClanCache,
            WarPendingChatPresenter pendingChatPresenter,
            WarBossBarService bossBarService,
            CoalitionWarBridgeLookup coalitionWarBridgeLookup
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.repository = repository;
        this.treasuryBridge = treasuryBridge;
        this.stateCache = stateCache;
        this.victoryService = victoryService;
        this.playerClanCache = playerClanCache;
        this.pendingChatPresenter = pendingChatPresenter;
        this.bossBarService = bossBarService;
        this.coalitionWarBridgeLookup = coalitionWarBridgeLookup;
    }

    public void bootstrapCache() {
        api.scheduler().runAsync(() -> {
            for (ActiveWarRecord war : repository.listAllActiveWars()) {
                stateCache.putActiveWar(war);
            }
            for (bm.b0b0b0.SoulPact.war.model.WarCaptureRecord capture : repository.listActiveCaptures()) {
                stateCache.setCapture(
                        capture.warId(),
                        capture.holderClanId(),
                        capture.targetClanId(),
                        capture.deadlineAt()
                );
            }
        }).thenAccept(ignored -> api.scheduler().runSync(bossBarService::tick));
        for (Player player : Bukkit.getOnlinePlayers()) {
            trackPlayer(player);
        }
    }

    public void trackPlayer(Player player) {
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> {
            clanOptional.ifPresent(clan -> playerClanCache.put(player.getUniqueId(), clan.id()));
            api.scheduler().runSync(() -> bossBarService.refreshPlayer(player));
        });
    }

    public void untrackPlayer(java.util.UUID playerId) {
        playerClanCache.remove(playerId);
        api.scheduler().runSync(() -> bossBarService.removePlayer(playerId));
    }

    public CompletableFuture<String> treasuryLineForList(long clanId) {
        return treasuryBridge.balance(clanId).thenApply(MONEY::format);
    }

    public CompletableFuture<ClanWarInfoExtras> enrichInfoView(Player viewer, long targetClanId) {
        return api.findClanByPlayer(viewer.getUniqueId()).thenCompose(viewerClanOptional -> {
            CompletableFuture<String> treasuryFuture = treasuryLineForList(targetClanId);
            if (viewerClanOptional.isEmpty()) {
                return treasuryFuture.thenApply(treasury -> new ClanWarInfoExtras(treasury, false));
            }
            ClanSnapshot viewerClan = viewerClanOptional.get();
            if (!viewerClan.leaderId().equals(viewer.getUniqueId()) || viewerClan.id() == targetClanId) {
                return treasuryFuture.thenApply(treasury -> new ClanWarInfoExtras(treasury, false));
            }
            return treasuryFuture.thenCombine(canDeclare(viewerClan.id(), targetClanId), ClanWarInfoExtras::new);
        });
    }

    public CompletableFuture<Boolean> declareWar(Player player, long attackerClanId, long defenderClanId) {
        return canDeclare(attackerClanId, defenderClanId).thenCompose(canDeclare -> {
            if (!canDeclare) {
                api.scheduler().runSync(() -> messages.send(player, "war.error.cannot-declare"));
                return CompletableFuture.completedFuture(false);
            }
            return api.scheduler().supplyAsync(() -> {
                if (repository.findPendingDeclaration(attackerClanId, defenderClanId).isPresent()) {
                    return false;
                }
                return repository.createDeclaration(
                        attackerClanId,
                        defenderClanId,
                        player.getUniqueId(),
                        System.currentTimeMillis()
                ) > 0L;
            }).thenCompose(created -> {
                if (!created) {
                    api.scheduler().runSync(() -> messages.send(player, "war.error.already-pending"));
                    return CompletableFuture.completedFuture(false);
                }
                return treasuryBridge.lockTreasury(defenderClanId).thenApply(locked -> {
                    api.scheduler().runSync(() -> {
                        messages.send(player, "war.declare.sent");
                        stateCache.setPendingForDefender(
                                defenderClanId,
                                repository.listPendingForDefender(defenderClanId)
                        );
                        repository.findPendingDeclaration(attackerClanId, defenderClanId)
                                .ifPresent(pendingChatPresenter::notifyDeclaration);
                        coalitionWarBridgeLookup.resolve().ifPresent(bridge ->
                                bridge.onWarDeclared(attackerClanId, defenderClanId)
                        );
                        bossBarService.refreshClan(defenderClanId);
                    });
                    return locked;
                });
            });
        });
    }

    public CompletableFuture<Boolean> acceptWar(Player player, long declarationId) {
        return resolveDeclarationForDefenderLeader(player, declarationId).thenCompose(declarationOptional -> {
            if (declarationOptional.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            WarDeclarationRecord declaration = declarationOptional.get();
            return api.scheduler().supplyAsync(() -> {
                repository.updateDeclarationStatus(declaration.id(), WarStatuses.RESOLVED);
                long warId = repository.createActiveWar(
                        declaration.attackerClanId(),
                        declaration.defenderClanId(),
                        System.currentTimeMillis()
                );
                ActiveWarRecord war = new ActiveWarRecord(
                        warId,
                        declaration.attackerClanId(),
                        declaration.defenderClanId(),
                        System.currentTimeMillis(),
                        WarStatuses.ACTIVE
                );
                stateCache.putActiveWar(war);
                return war;
            }).thenCompose(war -> treasuryBridge.unlockTreasuryAfterDecision(declaration.defenderClanId())
                    .thenApply(unlocked -> {
                        api.scheduler().runSync(() -> {
                            messages.send(player, "war.response.accepted");
                            refreshPendingCache(declaration.defenderClanId());
                            coalitionWarBridgeLookup.resolve().ifPresent(bridge ->
                                    bridge.onWarStarted(war.attackerClanId(), war.defenderClanId())
                            );
                            bossBarService.refreshWarClans(war);
                        });
                        return unlocked;
                    }));
        });
    }

    public CompletableFuture<Boolean> payRansom(Player player, long declarationId) {
        return resolveDeclarationForDefenderLeader(player, declarationId).thenCompose(declarationOptional -> {
            if (declarationOptional.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            WarDeclarationRecord declaration = declarationOptional.get();
            return treasuryBridge.balance(declaration.defenderClanId()).thenCompose(balance -> {
                double amount = balance * config.ransomPercent();
                if (amount <= 0.0D) {
                    api.scheduler().runSync(() -> messages.send(player, "war.error.empty-treasury"));
                    return CompletableFuture.completedFuture(false);
                }
                return treasuryBridge.seize(declaration.defenderClanId(), declaration.attackerClanId(), amount)
                        .thenCompose(result -> {
                            if (result != TreasuryOperationResult.SUCCESS) {
                                api.scheduler().runSync(() -> messages.send(player, "war.error.ransom-failed"));
                                return CompletableFuture.completedFuture(false);
                            }
                            return api.scheduler().supplyAsync(() -> {
                                repository.updateDeclarationStatus(declaration.id(), WarStatuses.RANSOM);
                                return true;
                            }).thenCompose(ignored -> treasuryBridge.unlockTreasuryAfterDecision(declaration.defenderClanId()))
                                    .thenApply(unlocked -> {
                                        api.scheduler().runSync(() -> {
                                            messages.send(player, "war.response.ransomed", Map.of(
                                                    "amount", MONEY.format(amount)
                                            ));
                                            refreshPendingCache(declaration.defenderClanId());
                                            bossBarService.refreshClan(declaration.defenderClanId());
                                        });
                                        return unlocked;
                                    });
                        });
            });
        });
    }

    public CompletableFuture<Integer> pendingCountForLeader(long defenderClanId) {
        return api.scheduler().supplyAsync(() -> repository.listPendingForDefender(defenderClanId).size());
    }

    public CompletableFuture<List<WarDeclarationRecord>> listPendingForDefenderLeader(Player player) {
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty() || !clanOptional.get().leaderId().equals(player.getUniqueId())) {
                api.scheduler().runSync(() -> messages.send(player, "war.error.not-leader"));
                return CompletableFuture.completedFuture(List.of());
            }
            return api.scheduler().supplyAsync(() -> repository.listPendingForDefender(clanOptional.get().id()));
        });
    }

    public boolean allowsEnemyStandardBreak(UUID breakerId, long baseOwnerClanId) {
        Long breakerClanId = playerClanCache.find(breakerId);
        if (breakerClanId == null || breakerClanId == baseOwnerClanId) {
            return false;
        }
        if (stateCache.areAtWar(breakerClanId, baseOwnerClanId)) {
            return true;
        }
        Optional<ActiveWarRecord> warOptional = stateCache.activeWarFor(baseOwnerClanId);
        if (warOptional.isEmpty()) {
            return false;
        }
        ActiveWarRecord war = warOptional.get();
        return coalitionWarBridgeLookup.resolve()
                .map(bridge -> bridge.allowsAllyFlagBreak(
                        breakerClanId,
                        baseOwnerClanId,
                        war.attackerClanId(),
                        war.defenderClanId()
                ))
                .orElse(false);
    }

    public void onEnemyStandardBroken(Player breaker, long defenderClanId) {
        Long breakerClanId = playerClanCache.find(breaker.getUniqueId());
        if (breakerClanId == null) {
            return;
        }
        Optional<ActiveWarRecord> warOptional = stateCache.activeWarFor(defenderClanId);
        if (warOptional.isEmpty()) {
            return;
        }
        ActiveWarRecord war = warOptional.get();
        long capturedAt = System.currentTimeMillis();
        long deadline = capturedAt + config.captureSeconds() * 1000L;
        long targetClanId = defenderClanId;
        api.scheduler().runAsync(() -> repository.upsertCapture(
                war.id(),
                breakerClanId,
                targetClanId,
                capturedAt,
                deadline
        ));
        stateCache.setCapture(war.id(), breakerClanId, targetClanId, deadline);
        coalitionWarBridgeLookup.resolve().ifPresent(bridge ->
                bridge.onCaptureStarted(targetClanId, breakerClanId, deadline)
        );
        bossBarService.refreshWarClans(war);
        api.scheduler().runSyncLater(config.captureSeconds() * 20L, () ->
                victoryService.tryResolveCaptureDeadline(war.id(), breakerClanId, targetClanId)
        );
        messages.send(breaker, "war.capture.started", Map.of(
                "seconds", String.valueOf(config.captureSeconds())
        ));
    }

    private CompletableFuture<Boolean> canDeclare(long attackerClanId, long defenderClanId) {
        if (attackerClanId == defenderClanId) {
            return CompletableFuture.completedFuture(false);
        }
        if (stateCache.activeWarFor(attackerClanId).isPresent() || stateCache.activeWarFor(defenderClanId).isPresent()) {
            return CompletableFuture.completedFuture(false);
        }
        CompletableFuture<Boolean> baseFuture = api.scheduler().supplyAsync(() ->
                repository.findPendingDeclaration(attackerClanId, defenderClanId).isEmpty()
                        && repository.findActiveWarBetween(attackerClanId, defenderClanId).isEmpty()
        );
        Optional<bm.b0b0b0.SoulPact.api.coalition.CoalitionWarBridge> coalitionBridge = coalitionWarBridgeLookup.resolve();
        if (coalitionBridge.isEmpty()) {
            return baseFuture;
        }
        return baseFuture.thenCompose(baseAllowed -> {
            if (!baseAllowed) {
                return CompletableFuture.completedFuture(false);
            }
            return coalitionBridge.get().canDeclareWar(attackerClanId, defenderClanId);
        });
    }

    private CompletableFuture<Optional<WarDeclarationRecord>> resolveDeclarationForDefenderLeader(Player player, long declarationId) {
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty() || !clanOptional.get().leaderId().equals(player.getUniqueId())) {
                api.scheduler().runSync(() -> messages.send(player, "war.error.not-leader"));
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return api.scheduler().supplyAsync(() -> repository.listPendingForDefender(clanOptional.get().id()).stream()
                    .filter(record -> record.id() == declarationId)
                    .findFirst());
        });
    }

    private void refreshPendingCache(long defenderClanId) {
        api.scheduler().runAsync(() -> {
            List<WarDeclarationRecord> pending = repository.listPendingForDefender(defenderClanId);
            stateCache.setPendingForDefender(defenderClanId, pending);
        });
    }
}
