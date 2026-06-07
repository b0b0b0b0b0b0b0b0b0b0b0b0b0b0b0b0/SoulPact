package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.land.ClanBaseSnapshot;
import bm.b0b0b0.SoulPact.land.config.BorderColorPalette;
import bm.b0b0b0.SoulPact.land.config.LandConfig;
import bm.b0b0b0.SoulPact.land.integration.WorldGuardGateway;
import bm.b0b0b0.SoulPact.land.message.LandMessages;
import bm.b0b0b0.SoulPact.land.model.BaseBounds;
import bm.b0b0b0.SoulPact.land.model.BaseExpansionAxis;
import bm.b0b0b0.SoulPact.land.model.BaseSetupFailure;
import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import bm.b0b0b0.SoulPact.land.repository.ClanBaseRepository;
import bm.b0b0b0.SoulPact.land.repository.SqlClanMemberUuidRepository;
import bm.b0b0b0.SoulPact.land.util.BaseRegionNames;
import bm.b0b0b0.SoulPact.land.util.MoneyFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class ClanBaseService {

    private final SoulPactApi api;
    private final LandConfig config;
    private final LandMessages messages;
    private final ClanBaseRepository repository;
    private final SqlClanMemberUuidRepository memberRepository;
    private final WorldGuardGateway worldGuardGateway;
    private final BaseBorderService borderService;
    private final BaseExpansionPaymentService paymentService;
    private final BaseFlagIndex flagIndex;
    private final ClanBaseRecordIndex recordIndex;

    public ClanBaseService(
            SoulPactApi api,
            LandConfig config,
            LandMessages messages,
            ClanBaseRepository repository,
            SqlClanMemberUuidRepository memberRepository,
            WorldGuardGateway worldGuardGateway,
            BaseBorderService borderService,
            BaseExpansionPaymentService paymentService,
            BaseFlagIndex flagIndex,
            ClanBaseRecordIndex recordIndex
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.repository = repository;
        this.memberRepository = memberRepository;
        this.worldGuardGateway = worldGuardGateway;
        this.borderService = borderService;
        this.paymentService = paymentService;
        this.flagIndex = flagIndex;
        this.recordIndex = recordIndex;
    }

    public Optional<ClanBaseRecord> findBaseRecordAtFlag(Location location) {
        if (location.getWorld() == null) {
            return Optional.empty();
        }
        return recordIndex.findByFlag(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    public Optional<ClanBaseRecord> findBaseRecordByClanId(long clanId) {
        return recordIndex.findByClanId(clanId);
    }

    public Optional<ClanBaseRecord> resolveBaseForBrokenFlag(org.bukkit.block.Block block) {
        Long bannerClanId = api.clanStandard().readClanIdFromBlock(block.getState());
        Optional<ClanBaseRecord> byBanner = bannerClanId == null
                ? Optional.empty()
                : recordIndex.findByClanId(bannerClanId);
        Optional<ClanBaseRecord> byLocation = findBaseRecordAtFlag(block.getLocation());
        if (byBanner.isPresent()) {
            if (byLocation.isPresent() && byLocation.get().clanId() != byBanner.get().clanId()) {
                return byBanner;
            }
            return byBanner;
        }
        return byLocation;
    }

    public boolean isKnownFlagBlock(org.bukkit.block.Block block) {
        if (api.clanStandard().readClanIdFromBlock(block.getState()) != null) {
            return true;
        }
        if (block.getWorld() == null) {
            return false;
        }
        return flagIndex.find(
                block.getWorld().getName(),
                block.getX(),
                block.getY(),
                block.getZ()
        ).isPresent();
    }

    public void clearFlagBlocks(ClanBaseRecord base) {
        World world = api.plugin().getServer().getWorld(base.world());
        if (world == null) {
            return;
        }
        clearBannerAt(world, base.flagX(), base.flagY(), base.flagZ());
        clearBannerAt(world, base.flagX(), base.flagY() + 1, base.flagZ());
        clearBannerAt(world, base.flagX(), base.flagY() - 1, base.flagZ());
    }

    private void clearBannerAt(World world, int x, int y, int z) {
        org.bukkit.block.Block block = world.getBlockAt(x, y, z);
        if (block.getType().name().endsWith("_BANNER")) {
            block.setType(Material.AIR);
        }
    }

    public void applyWarCombatZone(long clanId) {
        Optional<ClanBaseRecord> cached = recordIndex.findByClanId(clanId);
        if (cached.isPresent()) {
            applyWarCombatZoneSync(cached.get());
            return;
        }
        api.scheduler().supplyAsync(() -> repository.findByClanId(clanId)).thenAccept(baseOptional ->
                api.scheduler().runSync(() -> baseOptional.ifPresent(this::applyWarCombatZoneSync))
        );
    }

    public void restoreCombatZone(long clanId) {
        Optional<ClanBaseRecord> cached = recordIndex.findByClanId(clanId);
        if (cached.isPresent()) {
            restoreCombatZoneSync(cached.get());
            return;
        }
        api.scheduler().supplyAsync(() -> repository.findByClanId(clanId)).thenAccept(baseOptional ->
                api.scheduler().runSync(() -> baseOptional.ifPresent(this::restoreCombatZoneSync))
        );
    }

    private void applyWarCombatZoneSync(ClanBaseRecord base) {
        World world = api.plugin().getServer().getWorld(base.world());
        if (world == null) {
            return;
        }
        worldGuardGateway.forcePvpAllow(world, base.regionName());
    }

    private void restoreCombatZoneSync(ClanBaseRecord base) {
        World world = api.plugin().getServer().getWorld(base.world());
        if (world == null) {
            return;
        }
        worldGuardGateway.restorePvp(world, base.regionName(), base.pvpEnabled());
    }

    public CompletableFuture<Optional<ClanBaseSnapshot>> findBase(long clanId) {
        return api.scheduler().supplyAsync(() -> repository.findByClanId(clanId).map(ClanBaseRecord::toSnapshot));
    }

    public CompletableFuture<Optional<ClanBaseRecord>> findBaseRecord(long clanId) {
        return api.scheduler().supplyAsync(() -> repository.findByClanId(clanId));
    }

    public CompletableFuture<Optional<ClanBaseRecord>> findBaseAtFlag(Location location) {
        if (location.getWorld() == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return api.scheduler().supplyAsync(() -> repository.findByFlag(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        ));
    }

    public CompletableFuture<Optional<BaseSetupFailure>> validateFlagPlacementAsync(
            Player player,
            ClanSnapshot clan,
            Location location
    ) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            return CompletableFuture.completedFuture(Optional.of(BaseSetupFailure.NOT_LEADER));
        }
        World world = location.getWorld();
        if (world == null) {
            return CompletableFuture.completedFuture(Optional.of(BaseSetupFailure.WORLD_BLOCKED));
        }
        if (!worldGuardGateway.available()) {
            return CompletableFuture.completedFuture(Optional.of(BaseSetupFailure.WORLDGUARD_MISSING));
        }
        String worldName = world.getName();
        BaseBounds bounds = boundsAt(location);
        int minHeight = world.getMinHeight();
        int maxHeight = world.getMaxHeight();
        return api.scheduler().supplyAsync(() -> {
            if (repository.findByClanId(clan.id()).isPresent()) {
                return Optional.of(BaseSetupFailure.ALREADY_EXISTS);
            }
            if (conflictsWithStoredBases(worldName, bounds, clan.id(), minHeight, maxHeight)) {
                return Optional.of(BaseSetupFailure.TOO_CLOSE);
            }
            return Optional.<BaseSetupFailure>empty();
        }).thenCompose(dbFailure -> {
            if (dbFailure.isPresent()) {
                return CompletableFuture.completedFuture(dbFailure);
            }
            CompletableFuture<Optional<BaseSetupFailure>> worldGuardCheck = new CompletableFuture<>();
            api.scheduler().runSync(() -> {
                String regionName = BaseRegionNames.forClan(clan.id());
                if (worldGuardGateway.hasConflict(world, bounds, config.regionBuffer(), regionName)) {
                    worldGuardCheck.complete(Optional.of(BaseSetupFailure.TOO_CLOSE));
                    return;
                }
                worldGuardCheck.complete(Optional.empty());
            });
            return worldGuardCheck;
        });
    }

    public void createBaseAsync(
            Player player,
            ClanSnapshot clan,
            Location flagLocation,
            UUID standardUid,
            Runnable onFailure
    ) {
        validateFlagPlacementAsync(player, clan, flagLocation).thenAccept(failureOptional ->
                api.scheduler().runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    if (failureOptional.isPresent()) {
                        notifyFailure(player, failureOptional.get());
                        onFailure.run();
                        return;
                    }
                    api.scheduler().supplyAsync(() -> memberRepository.findMemberIds(clan.id()))
                            .thenAccept(memberIds -> api.scheduler().runSync(() -> {
                                if (!player.isOnline()) {
                                    return;
                                }
                                persistNewBase(player, clan, flagLocation, standardUid, memberIds, onFailure);
                            }));
                })
        );
    }

    public void destroyBase(ClanBaseRecord base) {
        destroyBase(base, null);
    }

    public void destroyBase(ClanBaseRecord base, Runnable onComplete) {
        api.scheduler().supplyAsync(() -> repository.findBorderBlocks(base.id()))
                .thenAccept(borderBlocks -> api.scheduler().runSync(() -> {
                    flagIndex.unregister(base);
                    recordIndex.unregister(base);
                    World world = api.plugin().getServer().getWorld(base.world());
                    if (world != null) {
                        borderService.unregisterBorder(base.id(), base.world(), borderBlocks);
                        borderService.restoreBorder(world, borderBlocks);
                        worldGuardGateway.removeRegion(world, base.regionName());
                    }
                    api.clanStandard().clearDeployed(base.clanId());
                    api.scheduler().runAsync(() -> repository.delete(base.id()))
                            .thenRun(() -> {
                                if (onComplete != null) {
                                    api.scheduler().runSync(onComplete);
                                }
                            });
                }));
    }

    public void reconcileDeployedFlags() {
        api.scheduler().supplyAsync(repository::findAll)
                .thenAccept(bases -> api.scheduler().runSync(() -> {
                    for (ClanBaseRecord base : bases) {
                        reconcileOneBase(base);
                    }
                }));
    }

    public void addMemberToRegion(long clanId, UUID playerId) {
        api.scheduler().supplyAsync(() -> repository.findByClanId(clanId))
                .thenAccept(baseOptional -> api.scheduler().runSync(() ->
                        baseOptional.ifPresent(base -> {
                            World world = api.plugin().getServer().getWorld(base.world());
                            if (world == null) {
                                return;
                            }
                            worldGuardGateway.addMember(world, base.regionName(), playerId);
                        })
                ));
    }

    public void removeMemberFromRegion(long clanId, UUID playerId) {
        api.scheduler().supplyAsync(() -> repository.findByClanId(clanId))
                .thenAccept(baseOptional -> api.scheduler().runSync(() ->
                        baseOptional.ifPresent(base -> {
                            World world = api.plugin().getServer().getWorld(base.world());
                            if (world == null) {
                                return;
                            }
                            worldGuardGateway.removeMember(world, base.regionName(), playerId);
                        })
                ));
    }

    public void transferRegionOwnership(long clanId, UUID previousLeaderId, UUID newLeaderId) {
        api.scheduler().supplyAsync(() -> repository.findByClanId(clanId))
                .thenAccept(baseOptional -> api.scheduler().runSync(() ->
                        baseOptional.ifPresent(base -> {
                            World world = api.plugin().getServer().getWorld(base.world());
                            if (world == null) {
                                return;
                            }
                            worldGuardGateway.transferOwnership(world, base.regionName(), previousLeaderId, newLeaderId);
                        })
                ));
    }

    public void togglePvp(Player player, ClanSnapshot clan, ClanBaseRecord base, Runnable onComplete) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            messages.send(player, "land.error.not-leader");
            return;
        }
        boolean next = !base.pvpEnabled();
        api.scheduler().runAsync(() -> repository.updatePvp(base.id(), next))
                .thenRun(() -> api.scheduler().runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    World world = api.plugin().getServer().getWorld(base.world());
                    if (world != null) {
                        worldGuardGateway.applyFlags(world, base.regionName(), next, base.mobSpawnEnabled());
                    }
                    messages.send(player, next ? "land.settings.pvp-enabled" : "land.settings.pvp-disabled");
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }));
    }

    public void toggleMobSpawn(Player player, ClanSnapshot clan, ClanBaseRecord base, Runnable onComplete) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            messages.send(player, "land.error.not-leader");
            return;
        }
        boolean next = !base.mobSpawnEnabled();
        api.scheduler().runAsync(() -> repository.updateMobSpawn(base.id(), next))
                .thenRun(() -> api.scheduler().runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    World world = api.plugin().getServer().getWorld(base.world());
                    if (world != null) {
                        worldGuardGateway.applyFlags(world, base.regionName(), base.pvpEnabled(), next);
                    }
                    messages.send(player, next ? "land.settings.mob-enabled" : "land.settings.mob-disabled");
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }));
    }

    public void cycleBorderColor(Player player, ClanSnapshot clan, ClanBaseRecord base, Runnable onComplete) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            messages.send(player, "land.error.not-leader");
            return;
        }
        Material currentGui = config.borderColors().resolveGui(base.borderMaterial());
        Material nextGui = config.borderColors().nextGui(currentGui);
        Material nextWorld = BorderColorPalette.toWorldMaterial(nextGui);
        api.scheduler().supplyAsync(() -> {
            repository.updateBorderMaterial(base.id(), nextWorld.name());
            return repository.findBorderBlocks(base.id());
        }).thenAccept(borderBlocks -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            World world = api.plugin().getServer().getWorld(base.world());
            if (world != null) {
                borderService.applyBorderColor(world, borderBlocks, nextWorld);
            }
            messages.send(player, "land.settings.border-color-changed", Map.of(
                    "color",
                    messages.resolve(player, "land.gui.border-colors." + config.borderColors().displayKey(nextGui))
            ));
            if (onComplete != null) {
                onComplete.run();
            }
        }));
    }

    public void expandBase(Player player, ClanSnapshot clan, ClanBaseRecord base, BaseExpansionAxis axis, Runnable onComplete) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            messages.send(player, "land.error.not-leader");
            return;
        }
        World world = api.plugin().getServer().getWorld(base.world());
        if (world == null) {
            messages.send(player, "land.error.failed");
            return;
        }
        int currentExtent = axis.resolvedExtent(base, config.baseRadius());
        int nextExtent = currentExtent + config.expansion().step();
        if (nextExtent > config.expansion().maxExtent()) {
            messages.send(player, "land.expansion.max-reached", Map.of(
                    "max", String.valueOf(config.expansion().maxExtent())
            ));
            return;
        }
        BaseBounds oldBounds = base.bounds(config.baseRadius(), world.getMinHeight(), world.getMaxHeight());
        ClanBaseRecord expandedRecord = axis.withExtent(base, nextExtent, config.baseRadius());
        BaseBounds newBounds = expandedRecord.bounds(config.baseRadius(), world.getMinHeight(), world.getMaxHeight());
        BaseBounds strip = axis.expansionStrip(oldBounds, newBounds);
        int minHeight = world.getMinHeight();
        int maxHeight = world.getMaxHeight();
        double cost = config.expansion().costForExtent(currentExtent, config.baseRadius());
        api.scheduler().supplyAsync(() -> conflictsWithStoredBases(
                base.world(),
                strip,
                clan.id(),
                minHeight,
                maxHeight
        )).thenAccept(storedConflict -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (storedConflict || worldGuardGateway.hasConflict(world, strip, config.regionBuffer(), base.regionName())) {
                messages.send(player, "land.error.too-close", Map.of("buffer", String.valueOf(config.regionBuffer())));
                return;
            }
            paymentService.charge(player, clan.id(), cost).thenAccept(paymentResult -> api.scheduler().runSync(() -> {
                if (!player.isOnline()) {
                    return;
                }
                if (paymentResult != ExpansionPaymentResult.SUCCESS) {
                    notifyPaymentFailure(player, paymentResult);
                    return;
                }
                applyExpansion(player, expandedRecord, axis, oldBounds, newBounds, world, cost, onComplete);
            }));
        }));
    }

    public double expansionCost(ClanBaseRecord base, BaseExpansionAxis axis) {
        int currentExtent = axis.resolvedExtent(base, config.baseRadius());
        return config.expansion().costForExtent(currentExtent, config.baseRadius());
    }

    public boolean usesTreasuryPayments() {
        return paymentService.usesTreasury();
    }

    public void notifyFailure(Player player, BaseSetupFailure failure) {
        String key = switch (failure) {
            case NOT_LEADER -> "land.error.not-leader";
            case ALREADY_EXISTS -> "land.error.already-exists";
            case WORLDGUARD_MISSING -> "land.error.worldguard-missing";
            case WORLD_BLOCKED -> "land.error.failed";
            case TOO_CLOSE -> "land.error.too-close";
            case DATABASE_ERROR -> "land.error.failed";
        };
        if (failure == BaseSetupFailure.TOO_CLOSE) {
            messages.send(player, key, Map.of("buffer", String.valueOf(config.regionBuffer())));
            return;
        }
        messages.send(player, key);
    }

    private void persistNewBase(
            Player player,
            ClanSnapshot clan,
            Location flagLocation,
            UUID standardUid,
            List<UUID> memberIds,
            Runnable onFailure
    ) {
        World world = flagLocation.getWorld();
        BaseBounds bounds = boundsAt(flagLocation);
        String regionName = BaseRegionNames.forClan(clan.id());
        Set<UUID> regionMembers = new LinkedHashSet<>(memberIds);
        regionMembers.remove(clan.leaderId());
        try {
            worldGuardGateway.createRegion(world, regionName, bounds, clan.leaderId(), regionMembers, false, true);
            List<ClanBaseRepository.BorderBlock> borderBlocks = borderService.placeBorder(
                    world,
                    bounds,
                    flagLocation.getBlockY(),
                    config.borderMaterial()
            );
            api.scheduler().supplyAsync(() -> {
                ClanBaseRecord saved = repository.insert(new ClanBaseRecord(
                        0L,
                        clan.id(),
                        regionName,
                        world.getName(),
                        flagLocation.getBlockX(),
                        flagLocation.getBlockY(),
                        flagLocation.getBlockZ(),
                        config.borderMaterial().name(),
                        config.baseRadius(),
                        config.baseRadius(),
                        config.baseRadius(),
                        config.baseRadius(),
                        standardUid.toString(),
                        false,
                        true,
                        System.currentTimeMillis()
                ));
                repository.saveBorderBlocks(saved.id(), world.getName(), borderBlocks);
                return saved;
            }).thenAccept(saved -> api.scheduler().runSync(() -> {
                if (!player.isOnline()) {
                    return;
                }
                flagIndex.register(saved);
                recordIndex.register(saved);
                borderService.registerBorder(saved.id(), world.getName(), borderBlocks);
                api.clanStandard().stampBlock(
                        flagLocation.getBlock().getState(),
                        clan.id(),
                        clan.tag(),
                        standardUid
                );
                api.clanStandard().trackDeployedBlock(clan.id(), flagLocation);
                messages.send(player, "land.base.created", Map.of(
                        "tag", clan.tag(),
                        "region", regionName
                ));
            })).exceptionally(error -> {
                api.scheduler().runSync(() -> {
                    worldGuardGateway.removeRegion(world, regionName);
                    borderService.restoreBorder(world, borderBlocks);
                    messages.send(player, "land.error.failed");
                    onFailure.run();
                });
                return null;
            });
        } catch (RuntimeException exception) {
            worldGuardGateway.removeRegion(world, regionName);
            messages.send(player, "land.error.failed");
            onFailure.run();
        }
    }

    private void reconcileOneBase(ClanBaseRecord base) {
        World world = api.plugin().getServer().getWorld(base.world());
        if (world == null) {
            return;
        }
        org.bukkit.block.Block block = world.getBlockAt(base.flagX(), base.flagY(), base.flagZ());
        if (!block.getType().name().endsWith("_BANNER")) {
            destroyBase(base);
            return;
        }
        UUID standardUid = base.parsedStandardUid();
        if (standardUid == null) {
            standardUid = UUID.randomUUID();
            UUID resolvedUid = standardUid;
            api.scheduler().runAsync(() -> repository.updateStandardUid(base.id(), resolvedUid.toString()));
        }
        String clanTag = clanStandardTag(base, block);
        api.clanStandard().stampBlock(block.getState(), base.clanId(), clanTag, standardUid);
        api.clanStandard().trackDeployedBlock(base.clanId(), block.getLocation());
    }

    private String clanStandardTag(ClanBaseRecord base, org.bukkit.block.Block block) {
        String tagFromBlock = api.clanStandard().readClanTagFromBlock(block.getState());
        if (tagFromBlock != null && !tagFromBlock.isBlank()) {
            return tagFromBlock;
        }
        return String.valueOf(base.clanId());
    }

    private void applyExpansion(
            Player player,
            ClanBaseRecord expandedRecord,
            BaseExpansionAxis axis,
            BaseBounds oldBounds,
            BaseBounds newBounds,
            World world,
            double cost,
            Runnable onComplete
    ) {
        api.scheduler().supplyAsync(() -> new ArrayList<>(repository.findBorderBlocks(expandedRecord.id())))
                .thenAccept(borderBlocks -> api.scheduler().runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    Material material = config.borderColors().resolveWorld(expandedRecord.borderMaterial());
                    try {
                        worldGuardGateway.resizeRegion(world, expandedRecord.regionName(), newBounds);
                        BorderExpansionDelta delta = borderService.expandBorder(
                                world,
                                newBounds,
                                expandedRecord.flagY(),
                                material,
                                borderBlocks
                        );
                        borderService.unregisterBorder(expandedRecord.id(), expandedRecord.world(), delta.removed());
                        borderService.registerBorder(expandedRecord.id(), expandedRecord.world(), borderBlocks);
                        sendExpansionSuccess(player, expandedRecord, axis, cost);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                        List<ClanBaseRepository.BorderBlock> blocksToPersist = List.copyOf(borderBlocks);
                        api.scheduler().runAsync(() -> {
                            repository.updateExtents(
                                    expandedRecord.id(),
                                    expandedRecord.extentXPos(),
                                    expandedRecord.extentXNeg(),
                                    expandedRecord.extentZPos(),
                                    expandedRecord.extentZNeg()
                            );
                            repository.saveBorderBlocks(
                                    expandedRecord.id(),
                                    expandedRecord.world(),
                                    blocksToPersist
                            );
                        });
                    } catch (RuntimeException exception) {
                        messages.send(player, "land.error.failed");
                    }
                }));
    }

    private void sendExpansionSuccess(
            Player player,
            ClanBaseRecord expandedRecord,
            BaseExpansionAxis axis,
            double cost
    ) {
        Map<String, String> placeholders = Map.of(
                "direction", messages.resolve(player, "land.expansion.direction." + axis.messageKey()),
                "size", String.valueOf(axis.resolvedExtent(expandedRecord, config.baseRadius())),
                "cost", MoneyFormat.format(cost),
                "source", messages.resolve(
                        player,
                        paymentService.usesTreasury()
                                ? "land.expansion.source.treasury"
                                : "land.expansion.source.leader"
                )
        );
        messages.send(player, "land.expansion.success", placeholders);
    }

    private void notifyPaymentFailure(Player player, ExpansionPaymentResult result) {
        String key = switch (result) {
            case TREASURY_INSUFFICIENT -> "land.expansion.insufficient-treasury";
            case LEADER_INSUFFICIENT -> "land.expansion.insufficient-leader";
            case TREASURY_LOCKED -> "land.expansion.treasury-locked";
            case ECONOMY_UNAVAILABLE -> "land.expansion.economy-unavailable";
            case FAILED -> "land.error.failed";
            case SUCCESS -> "land.error.failed";
        };
        messages.send(player, key);
    }

    private BaseBounds boundsAt(Location location) {
        World world = location.getWorld();
        return new BaseBounds(
                location.getBlockX() - config.baseRadius(),
                location.getBlockX() + config.baseRadius(),
                world.getMinHeight(),
                world.getMaxHeight(),
                location.getBlockZ() - config.baseRadius(),
                location.getBlockZ() + config.baseRadius()
        );
    }

    private boolean conflictsWithStoredBases(
            String worldName,
            BaseBounds bounds,
            long ignoredClanId,
            int minHeight,
            int maxHeight
    ) {
        for (ClanBaseRecord existing : repository.findAllInWorld(worldName)) {
            if (existing.clanId() == ignoredClanId) {
                continue;
            }
            BaseBounds other = existing.bounds(config.baseRadius(), minHeight, maxHeight);
            if (overlapsXZ(bounds, other, config.regionBuffer())) {
                return true;
            }
        }
        return false;
    }

    private static boolean overlapsXZ(BaseBounds first, BaseBounds second, int buffer) {
        return first.minX() - buffer <= second.maxX() + buffer
                && first.maxX() + buffer >= second.minX() - buffer
                && first.minZ() - buffer <= second.maxZ() + buffer
                && first.maxZ() + buffer >= second.minZ() - buffer;
    }
}
