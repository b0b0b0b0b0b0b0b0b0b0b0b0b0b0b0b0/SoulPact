package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanPermissionKeys;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.land.ClanLandProvider;
import bm.b0b0b0.SoulPact.api.treasury.TreasuryOperationResult;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarBridge;
import bm.b0b0b0.SoulPact.api.war.ClanWarInfoExtras;
import bm.b0b0b0.SoulPact.api.war.FlagBreakWarResult;
import bm.b0b0b0.SoulPact.api.war.OwnFlagWarBreakAction;
import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.gui.WarEnemyTarget;
import bm.b0b0b0.SoulPact.war.gui.WarHubViewData;
import bm.b0b0b0.SoulPact.war.message.WarFlagRevealPresenter;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.message.WarPendingChatPresenter;
import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import bm.b0b0b0.SoulPact.war.model.WarFlagSnapshot;
import bm.b0b0b0.SoulPact.war.model.WarStatuses;
import bm.b0b0b0.SoulPact.war.repository.WarRepository;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private final WarCoalitionWithdrawService coalitionWithdrawService;
    private final WarPlayerClanCache playerClanCache;
    private final WarPendingChatPresenter pendingChatPresenter;
    private final WarBossBarService bossBarService;
    private final CoalitionWarBridgeLookup coalitionWarBridgeLookup;
    private final WarLandBridgeLookup landBridgeLookup;
    private final WarFlagRevealPresenter flagRevealPresenter;
    private final WarClanLookup clanLookup;
    private final WarLandCombatService landCombatService;
    private final WarKillTracker killTracker;

    public ClanWarService(
            SoulPactApi api,
            WarConfig config,
            WarMessages messages,
            WarRepository repository,
            WarTreasuryBridge treasuryBridge,
            WarStateCache stateCache,
            WarVictoryService victoryService,
            WarCoalitionWithdrawService coalitionWithdrawService,
            WarPlayerClanCache playerClanCache,
            WarPendingChatPresenter pendingChatPresenter,
            WarBossBarService bossBarService,
            CoalitionWarBridgeLookup coalitionWarBridgeLookup,
            WarLandBridgeLookup landBridgeLookup,
            WarFlagRevealPresenter flagRevealPresenter,
            WarClanLookup clanLookup,
            WarLandCombatService landCombatService,
            WarKillTracker killTracker
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.repository = repository;
        this.treasuryBridge = treasuryBridge;
        this.stateCache = stateCache;
        this.victoryService = victoryService;
        this.coalitionWithdrawService = coalitionWithdrawService;
        this.playerClanCache = playerClanCache;
        this.pendingChatPresenter = pendingChatPresenter;
        this.bossBarService = bossBarService;
        this.coalitionWarBridgeLookup = coalitionWarBridgeLookup;
        this.landBridgeLookup = landBridgeLookup;
        this.flagRevealPresenter = flagRevealPresenter;
        this.clanLookup = clanLookup;
        this.landCombatService = landCombatService;
        this.killTracker = killTracker;
    }

    public void bootstrapCache() {
        api.scheduler().runAsync(() -> {
            for (ActiveWarRecord war : repository.listAllActiveWars()) {
                stateCache.putActiveWar(war);
                landCombatService.enableForWar(war);
            }
            for (bm.b0b0b0.SoulPact.war.model.WarCaptureRecord capture : repository.listActiveCaptures()) {
                stateCache.setCapture(
                        capture.warId(),
                        capture.holderClanId(),
                        capture.targetClanId(),
                        capture.deadlineAt()
                );
            }
            stateCache.rebuildPending(repository.listAllPendingDeclarations());
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
            if (viewerClan.id() == targetClanId) {
                return treasuryFuture.thenApply(treasury -> new ClanWarInfoExtras(treasury, false));
            }
            return api.clanAccess().hasPermission(
                    viewerClan.id(),
                    viewer.getUniqueId(),
                    ClanPermissionKeys.WAR_DECLARE
            ).thenCompose(hasPermission -> {
                if (!hasPermission) {
                    return treasuryFuture.thenApply(treasury ->
                            new ClanWarInfoExtras(treasury, false, "no-permission")
                    );
                }
                return treasuryFuture.thenCompose(treasury ->
                        resolveDeclareBlockReason(viewerClan.id(), targetClanId).thenApply(blockReason -> {
                            if (blockReason.isEmpty()) {
                                return new ClanWarInfoExtras(treasury, true);
                            }
                            return new ClanWarInfoExtras(treasury, false, blockReasonId(blockReason.get()));
                        })
                );
            });
        });
    }

    public CompletableFuture<Boolean> declareWar(Player player, long attackerClanId, long defenderClanId) {
        return api.clanAccess().hasPermission(attackerClanId, player.getUniqueId(), ClanPermissionKeys.WAR_DECLARE)
                .thenCompose(hasPermission -> {
                    if (!hasPermission) {
                        api.scheduler().runSync(() -> messages.send(player, "war.error.no-permission"));
                        return CompletableFuture.completedFuture(false);
                    }
                    return declareWarAfterPermissionCheck(player, attackerClanId, defenderClanId);
                });
    }

    private CompletableFuture<Boolean> declareWarAfterPermissionCheck(
            Player player,
            long attackerClanId,
            long defenderClanId
    ) {
        return resolveDeclareBlockReason(attackerClanId, defenderClanId).thenCompose(blockReason -> {
            if (blockReason.isPresent()) {
                api.scheduler().runSync(() -> messages.send(player, blockReason.get()));
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
                        stateCache.rebuildPending(repository.listAllPendingDeclarations());
                        repository.findPendingDeclaration(attackerClanId, defenderClanId)
                                .ifPresent(pendingChatPresenter::notifyDeclaration);
                        coalitionWarBridgeLookup.resolve().ifPresent(bridge ->
                                bridge.onWarDeclared(attackerClanId, defenderClanId)
                        );
                        bossBarService.refreshClan(defenderClanId);
                        bossBarService.refreshClan(attackerClanId);
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
            return resolveBaseBlockReason(declaration.attackerClanId(), declaration.defenderClanId())
                    .thenCompose(blockReason -> {
                        if (blockReason.isPresent()) {
                            api.scheduler().runSync(() -> messages.send(player, blockReason.get()));
                            return CompletableFuture.completedFuture(false);
                        }
                        return loadBothBases(declaration.attackerClanId(), declaration.defenderClanId())
                                .thenCompose(bases -> {
                                    if (bases.isEmpty()) {
                                        api.scheduler().runSync(() -> messages.send(player, "war.error.accept-no-base"));
                                        return CompletableFuture.completedFuture(false);
                                    }
                                    BasePair basePair = bases.get();
                                    long startedAt = System.currentTimeMillis();
                                    return api.scheduler().supplyAsync(() -> {
                                        repository.updateDeclarationStatus(declaration.id(), WarStatuses.RESOLVED);
                                        long warId = repository.createActiveWar(
                                                declaration.attackerClanId(),
                                                declaration.defenderClanId(),
                                                startedAt,
                                                basePair.attacker(),
                                                basePair.defender()
                                        );
                                        ActiveWarRecord war = new ActiveWarRecord(
                                                warId,
                                                declaration.attackerClanId(),
                                                declaration.defenderClanId(),
                                                startedAt,
                                                WarStatuses.ACTIVE,
                                                basePair.attacker().world(),
                                                basePair.attacker().x(),
                                                basePair.attacker().y(),
                                                basePair.attacker().z(),
                                                basePair.defender().world(),
                                                basePair.defender().x(),
                                                basePair.defender().y(),
                                                basePair.defender().z()
                                        );
                                        stateCache.putActiveWar(war);
                                        return war;
                                    }).thenCompose(war -> clanLookup.findClan(war.attackerClanId())
                                            .thenCombine(clanLookup.findClan(war.defenderClanId()), (attackerOptional, defenderOptional) -> {
                                                String attackerTag = attackerOptional.map(ClanSnapshot::tag)
                                                        .orElse(String.valueOf(war.attackerClanId()));
                                                String defenderTag = defenderOptional.map(ClanSnapshot::tag)
                                                        .orElse(String.valueOf(war.defenderClanId()));
                                                return new String[]{attackerTag, defenderTag};
                                            })
                                            .thenCompose(tags -> treasuryBridge.unlockTreasuryAfterDecision(declaration.defenderClanId())
                                                    .thenApply(unlocked -> {
                                                        api.scheduler().runSync(() -> {
                                                            messages.send(player, "war.response.accepted");
                                                            landCombatService.enableForWar(war);
                                                            flagRevealPresenter.revealStartedWar(war, tags[0], tags[1]);
                                                            refreshPendingCache(declaration.defenderClanId(), declaration.attackerClanId());
                                                            coalitionWarBridgeLookup.resolve().ifPresent(bridge ->
                                                                    bridge.onWarStarted(war.attackerClanId(), war.defenderClanId())
                                                            );
                                                            bossBarService.refreshWarClans(war);
                                                            bossBarService.tick();
                                                        });
                                                        return unlocked;
                                                    })));
                                });
                    });
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
                                            refreshPendingCache(declaration.defenderClanId(), declaration.attackerClanId());
                                            bossBarService.refreshClan(declaration.defenderClanId());
                                            bossBarService.refreshClan(declaration.attackerClanId());
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
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(List.of());
            }
            ClanSnapshot clan = clanOptional.get();
            return api.clanAccess().hasPermission(clan.id(), player.getUniqueId(), ClanPermissionKeys.WAR_RESPOND)
                    .thenCompose(hasPermission -> {
                        if (!hasPermission) {
                            api.scheduler().runSync(() -> messages.send(player, "war.error.no-permission"));
                            return CompletableFuture.completedFuture(List.of());
                        }
                        return api.scheduler().supplyAsync(() -> repository.listPendingForDefender(clan.id()));
                    });
        });
    }

    public boolean allowsEnemyStandardBreak(UUID breakerId, long baseOwnerClanId) {
        Long breakerClanId = resolvePlayerClanId(breakerId);
        if (breakerClanId == null || breakerClanId.equals(baseOwnerClanId)) {
            return false;
        }
        Optional<ActiveWarRecord> ownerWar = findWarForParticipant(baseOwnerClanId);
        if (ownerWar.isEmpty()) {
            return false;
        }
        ActiveWarRecord war = ownerWar.get();
        Optional<ActiveWarRecord> breakerWar = findWarForParticipant(breakerClanId);
        if (breakerWar.isEmpty() || breakerWar.get().id() != war.id()) {
            return false;
        }
        long ownerSide = warSideRoot(war, baseOwnerClanId);
        long breakerSide = warSideRoot(war, breakerClanId);
        if (ownerSide == 0L || breakerSide == 0L || ownerSide == breakerSide) {
            return false;
        }
        return api.clanAccess().hasPermissionSync(breakerClanId, breakerId, ClanPermissionKeys.WAR_FIGHT);
    }

    public void recordCombatKill(Player killer, Player victim) {
        Long killerClanId = resolvePlayerClanId(killer.getUniqueId());
        Long victimClanId = resolvePlayerClanId(victim.getUniqueId());
        if (killerClanId == null || victimClanId == null || killerClanId.equals(victimClanId)) {
            return;
        }
        Optional<ActiveWarRecord> victimWarOptional = findWarForParticipant(victimClanId);
        Optional<ActiveWarRecord> killerWarOptional = findWarForParticipant(killerClanId);
        if (victimWarOptional.isEmpty() || killerWarOptional.isEmpty()) {
            return;
        }
        ActiveWarRecord war = victimWarOptional.get();
        if (killerWarOptional.get().id() != war.id()) {
            return;
        }
        long victimSide = warSideRoot(war, victimClanId);
        long killerSide = warSideRoot(war, killerClanId);
        if (victimSide == 0L || killerSide == 0L || victimSide == killerSide) {
            return;
        }
        killTracker.recordKill(war.id(), killerClanId);
    }

    public Optional<OwnFlagWarBreakAction> resolveOwnFlagBreak(UUID breakerId, long baseOwnerClanId) {
        Long breakerClanId = resolvePlayerClanId(breakerId);
        if (breakerClanId == null || !breakerClanId.equals(baseOwnerClanId)) {
            return Optional.empty();
        }
        Optional<ActiveWarRecord> warOptional = findWarForParticipant(baseOwnerClanId);
        if (warOptional.isEmpty()) {
            return Optional.empty();
        }
        ActiveWarRecord war = warOptional.get();
        if (isMainWarParticipant(baseOwnerClanId, war)) {
            return Optional.of(OwnFlagWarBreakAction.MAIN_DEFEAT);
        }
        if (isCoalitionWarAlly(baseOwnerClanId, war)) {
            return Optional.of(OwnFlagWarBreakAction.COALITION_WITHDRAW);
        }
        return Optional.empty();
    }

    public void onOwnFlagBreakDuringWar(
            Player breaker,
            long baseOwnerClanId,
            Location flagLocation,
            Runnable destroyBase,
            OwnFlagWarBreakAction action
    ) {
        Optional<ActiveWarRecord> warOptional = findWarForParticipant(baseOwnerClanId);
        if (warOptional.isEmpty()) {
            return;
        }
        ActiveWarRecord war = warOptional.get();
        destroyBase.run();
        if (action == OwnFlagWarBreakAction.MAIN_DEFEAT) {
            long winnerClanId = war.enemyClanIdFor(baseOwnerClanId);
            victoryService.resolveSelfFlagDefeat(war.id(), winnerClanId, baseOwnerClanId);
            clanLookup.findClan(winnerClanId).thenAccept(winnerOptional -> {
                String enemyTag = winnerOptional.map(clan -> clan.tag()).orElse(String.valueOf(winnerClanId));
                api.scheduler().runSync(() -> messages.send(breaker, "war.own-flag.defeat", Map.of("enemy", enemyTag)));
            });
            return;
        }
        coalitionWithdrawService.withdrawFromOwnFlagBreak(war, baseOwnerClanId);
        messages.send(breaker, "war.own-flag.coalition-withdraw");
    }

    public void onEnemyStandardBroken(Player breaker, long baseOwnerClanId) {
        Long breakerClanId = resolvePlayerClanId(breaker.getUniqueId());
        if (breakerClanId == null) {
            return;
        }
        Optional<ActiveWarRecord> warOptional = findWarForParticipant(baseOwnerClanId);
        if (warOptional.isEmpty()) {
            return;
        }
        ActiveWarRecord war = warOptional.get();
        long capturedAt = System.currentTimeMillis();
        long deadline = capturedAt + config.captureSeconds() * 1000L;
        long targetClanId = baseOwnerClanId;
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
        broadcastCaptureAlerts(war, breakerClanId, targetClanId);
    }

    private void broadcastCaptureAlerts(ActiveWarRecord war, long holderClanId, long targetClanId) {
        String holderTag = clanLookup.findClanTagSync(holderClanId).orElse(String.valueOf(holderClanId));
        String targetTag = clanLookup.findClanTagSync(targetClanId).orElse(String.valueOf(targetClanId));
        String seconds = String.valueOf(config.captureSeconds());
        for (Player online : Bukkit.getOnlinePlayers()) {
            Long clanId = resolvePlayerClanId(online.getUniqueId());
            if (clanId == null) {
                continue;
            }
            Optional<ActiveWarRecord> playerWar = findWarForParticipant(clanId);
            if (playerWar.isEmpty() || playerWar.get().id() != war.id()) {
                continue;
            }
            long playerSide = warSideRoot(war, clanId);
            long holderSide = warSideRoot(war, holderClanId);
            long targetSide = warSideRoot(war, targetClanId);
            if (playerSide == holderSide) {
                messages.send(online, "war.capture.holder-alert", Map.of(
                        "seconds", seconds,
                        "target_tag", targetTag
                ));
            } else if (playerSide == targetSide) {
                messages.send(online, "war.capture.defender-alert", Map.of(
                        "seconds", seconds,
                        "holder_tag", holderTag
                ));
            }
        }
    }

    public FlagBreakWarResult handleBrokenFlag(
            Player breaker,
            long flagOwnerClanId,
            Location flagLocation,
            Runnable destroyBase,
            Runnable clearCapturedFlag
    ) {
        if (findWarForParticipant(flagOwnerClanId).isEmpty()) {
            return FlagBreakWarResult.PEACEFUL;
        }
        if (allowsEnemyStandardBreak(breaker.getUniqueId(), flagOwnerClanId)) {
            onEnemyStandardBreak(breaker, flagOwnerClanId, flagLocation, clearCapturedFlag);
            return FlagBreakWarResult.HANDLED;
        }
        Optional<OwnFlagWarBreakAction> ownBreak = resolveOwnFlagBreak(breaker.getUniqueId(), flagOwnerClanId);
        if (ownBreak.isPresent()) {
            onOwnFlagBreakDuringWar(breaker, flagOwnerClanId, flagLocation, destroyBase, ownBreak.get());
            return FlagBreakWarResult.HANDLED;
        }
        messages.send(breaker, "war.error.flag-break-blocked");
        return FlagBreakWarResult.BLOCKED;
    }

    private void onEnemyStandardBreak(Player breaker, long baseOwnerClanId, Location flagLocation, Runnable clearCapturedFlag) {
        deliverCapturedStandard(breaker, flagLocation, baseOwnerClanId);
        onEnemyStandardBroken(breaker, baseOwnerClanId);
        clearCapturedFlag.run();
    }

    private void deliverCapturedStandard(Player breaker, Location flagLocation, long ownerClanId) {
        var standard = api.clanStandard();
        String clanTag = standard.readClanTagFromBlock(flagLocation.getBlock().getState());
        if (clanTag == null || clanTag.isBlank()) {
            clanTag = clanLookup.findClanTagSync(ownerClanId).orElse(String.valueOf(ownerClanId));
        }
        standard.restoreToPlayer(breaker, ownerClanId, clanTag);
        standard.trackInventory(ownerClanId, breaker.getUniqueId());
        messages.send(breaker, "war.capture.standard-received", Map.of("target_tag", clanTag));
    }

    private Optional<ActiveWarRecord> findWarForParticipant(long clanId) {
        Optional<ActiveWarRecord> direct = stateCache.activeWarFor(clanId);
        if (direct.isPresent()) {
            return direct;
        }
        return coalitionWarBridgeLookup.resolve().flatMap(bridge -> findCoalitionParticipantWar(clanId, bridge));
    }

    private Optional<ActiveWarRecord> findCoalitionParticipantWar(long clanId, CoalitionWarBridge bridge) {
        for (ActiveWarRecord war : stateCache.allActiveWars()) {
            if (isCoalitionWarAlly(clanId, war, bridge)) {
                return Optional.of(war);
            }
        }
        return Optional.empty();
    }

    private boolean isMainWarParticipant(long clanId, ActiveWarRecord war) {
        return clanId == war.attackerClanId() || clanId == war.defenderClanId();
    }

    private boolean isCoalitionWarAlly(long clanId, ActiveWarRecord war) {
        return coalitionWarBridgeLookup.resolve()
                .map(bridge -> isCoalitionWarAlly(clanId, war, bridge))
                .orElse(false);
    }

    private boolean isCoalitionWarAlly(long clanId, ActiveWarRecord war, CoalitionWarBridge bridge) {
        if (isMainWarParticipant(clanId, war)) {
            return false;
        }
        return bridge.coalitionClanIds(war.attackerClanId()).contains(clanId)
                || bridge.coalitionClanIds(war.defenderClanId()).contains(clanId);
    }

    private long warSideRoot(ActiveWarRecord war, long clanId) {
        if (clanId == war.attackerClanId()) {
            return war.attackerClanId();
        }
        if (clanId == war.defenderClanId()) {
            return war.defenderClanId();
        }
        return coalitionWarBridgeLookup.resolve()
                .map(bridge -> {
                    if (bridge.coalitionClanIds(war.attackerClanId()).contains(clanId)) {
                        return war.attackerClanId();
                    }
                    if (bridge.coalitionClanIds(war.defenderClanId()).contains(clanId)) {
                        return war.defenderClanId();
                    }
                    return 0L;
                })
                .orElse(0L);
    }

    private Long resolvePlayerClanId(UUID playerId) {
        Long cached = playerClanCache.find(playerId);
        if (cached != null) {
            return cached;
        }
        Optional<Long> lookedUp = clanLookup.findClanIdByPlayerSync(playerId);
        lookedUp.ifPresent(clanId -> playerClanCache.put(playerId, clanId));
        return lookedUp.orElse(null);
    }

    public CompletableFuture<WarHubViewData> buildHubView(Player player) {
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(new WarHubViewData(false, 0, Optional.empty()));
            }
            ClanSnapshot clan = clanOptional.get();
            return api.clanAccess().hasPermission(clan.id(), player.getUniqueId(), ClanPermissionKeys.WAR_RESPOND)
                    .thenCompose(viewerCanRespond -> {
                        CompletableFuture<Integer> pendingFuture = viewerCanRespond
                                ? pendingCountForLeader(clan.id())
                                : CompletableFuture.completedFuture(0);
                        Optional<ActiveWarRecord> warOptional = stateCache.activeWarFor(clan.id());
                        if (warOptional.isEmpty()) {
                            return pendingFuture.thenApply(count -> new WarHubViewData(viewerCanRespond, count, Optional.empty()));
                        }
                        ActiveWarRecord war = warOptional.get();
                        long enemyClanId = war.enemyClanIdFor(clan.id());
                        return pendingFuture.thenCombine(
                                resolveEnemyTarget(clan.id(), war, enemyClanId),
                                (count, enemy) -> new WarHubViewData(viewerCanRespond, count, enemy)
                        );
                    });
        });
    }

    private CompletableFuture<Optional<WarEnemyTarget>> resolveEnemyTarget(
            long viewerClanId,
            ActiveWarRecord war,
            long enemyClanId
    ) {
        return clanLookup.findClan(enemyClanId).thenCompose(enemyClanOptional -> {
            String enemyTag = enemyClanOptional.map(ClanSnapshot::tag).orElse("#" + enemyClanId);
            String enemyName = enemyClanOptional.map(ClanSnapshot::name).orElse(String.valueOf(enemyClanId));
            Optional<WarFlagSnapshot> snapshotFlag = war.enemyFlagFor(viewerClanId);
            if (snapshotFlag.isPresent()) {
                WarFlagSnapshot flag = snapshotFlag.get();
                return CompletableFuture.completedFuture(Optional.of(new WarEnemyTarget(
                        enemyTag,
                        enemyName,
                        flag.world(),
                        flag.x(),
                        flag.y(),
                        flag.z()
                )));
            }
            Optional<ClanLandProvider> landProvider = landBridgeLookup.resolve();
            if (landProvider.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return landProvider.get().findBase(enemyClanId).thenApply(baseOptional -> baseOptional.map(base -> new WarEnemyTarget(
                    enemyTag,
                    enemyName,
                    base.world(),
                    base.flagX(),
                    base.flagY(),
                    base.flagZ()
            )));
        });
    }

    private CompletableFuture<Boolean> canDeclare(long attackerClanId, long defenderClanId) {
        return resolveDeclareBlockReason(attackerClanId, defenderClanId).thenApply(Optional::isEmpty);
    }

    private String blockReasonId(String messageKey) {
        if (messageKey == null || messageKey.isBlank()) {
            return "generic";
        }
        if (messageKey.startsWith("war.error.")) {
            return messageKey.substring("war.error.".length());
        }
        return "generic";
    }

    private CompletableFuture<Optional<String>> resolveDeclareBlockReason(long attackerClanId, long defenderClanId) {
        if (attackerClanId == defenderClanId) {
            return CompletableFuture.completedFuture(Optional.of("war.error.cannot-declare"));
        }
        if (stateCache.activeWarFor(attackerClanId).isPresent()) {
            return CompletableFuture.completedFuture(Optional.of("war.error.active-war"));
        }
        if (stateCache.activeWarFor(defenderClanId).isPresent()) {
            return CompletableFuture.completedFuture(Optional.of("war.error.target-in-war"));
        }
        CompletableFuture<Optional<String>> baseFuture = api.scheduler().supplyAsync(() -> {
            if (repository.findPendingDeclaration(attackerClanId, defenderClanId).isPresent()) {
                return Optional.of("war.error.already-pending");
            }
            if (repository.findActiveWarBetween(attackerClanId, defenderClanId).isPresent()) {
                return Optional.of("war.error.cannot-declare");
            }
            return Optional.empty();
        });
        Optional<bm.b0b0b0.SoulPact.api.coalition.CoalitionWarBridge> coalitionBridge = coalitionWarBridgeLookup.resolve();
        return baseFuture.thenCompose(baseReason -> {
            if (baseReason.isPresent()) {
                return CompletableFuture.completedFuture(baseReason);
            }
            if (coalitionBridge.isEmpty()) {
                return resolveBaseBlockReason(attackerClanId, defenderClanId);
            }
            return coalitionBridge.get().canDeclareWar(attackerClanId, defenderClanId).thenCompose(allowed -> {
                if (!allowed) {
                    return CompletableFuture.completedFuture(Optional.of("war.error.coalition-ally"));
                }
                return resolveBaseBlockReason(attackerClanId, defenderClanId);
            });
        });
    }

    private CompletableFuture<Optional<String>> resolveBaseBlockReason(long attackerClanId, long defenderClanId) {
        Optional<ClanLandProvider> landProvider = landBridgeLookup.resolve();
        if (landProvider.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.of("war.error.lands-required"));
        }
        ClanLandProvider provider = landProvider.get();
        return provider.findBase(attackerClanId).thenCompose(attackerBase -> {
            if (attackerBase.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.of("war.error.attacker-no-base"));
            }
            return provider.findBase(defenderClanId).thenApply(defenderBase ->
                    defenderBase.isEmpty() ? Optional.of("war.error.defender-no-base") : Optional.empty()
            );
        });
    }

    private CompletableFuture<Optional<BasePair>> loadBothBases(long attackerClanId, long defenderClanId) {
        Optional<ClanLandProvider> landProvider = landBridgeLookup.resolve();
        if (landProvider.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        ClanLandProvider provider = landProvider.get();
        return provider.findBase(attackerClanId).thenCompose(attackerBase -> {
            if (attackerBase.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return provider.findBase(defenderClanId).thenApply(defenderBase -> {
                if (defenderBase.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(new BasePair(
                        WarFlagSnapshot.from(attackerBase.get()),
                        WarFlagSnapshot.from(defenderBase.get())
                ));
            });
        });
    }

    private record BasePair(WarFlagSnapshot attacker, WarFlagSnapshot defender) {
    }

    private CompletableFuture<Optional<WarDeclarationRecord>> resolveDeclarationForDefenderLeader(Player player, long declarationId) {
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            ClanSnapshot clan = clanOptional.get();
            return api.clanAccess().hasPermission(clan.id(), player.getUniqueId(), ClanPermissionKeys.WAR_RESPOND)
                    .thenCompose(hasPermission -> {
                        if (!hasPermission) {
                            api.scheduler().runSync(() -> messages.send(player, "war.error.no-permission"));
                            return CompletableFuture.completedFuture(Optional.empty());
                        }
                        return api.scheduler().supplyAsync(() -> repository.listPendingForDefender(clan.id()).stream()
                                .filter(record -> record.id() == declarationId)
                                .findFirst());
                    });
        });
    }

    private void refreshPendingCache(long defenderClanId, long attackerClanId) {
        api.scheduler().runAsync(() -> {
            List<WarDeclarationRecord> allPending = repository.listAllPendingDeclarations();
            stateCache.rebuildPending(allPending);
            java.util.HashSet<Long> clans = new java.util.HashSet<>();
            clans.add(defenderClanId);
            clans.add(attackerClanId);
            for (WarDeclarationRecord record : allPending) {
                clans.add(record.attackerClanId());
                clans.add(record.defenderClanId());
            }
            api.scheduler().runSync(() -> {
                for (Long clanId : clans) {
                    bossBarService.refreshClan(clanId);
                }
            });
        });
    }
}
