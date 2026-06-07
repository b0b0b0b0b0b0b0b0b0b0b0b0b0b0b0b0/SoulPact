package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.land.ClanBaseSnapshot;
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

    public ClanBaseService(
            SoulPactApi api,
            LandConfig config,
            LandMessages messages,
            ClanBaseRepository repository,
            SqlClanMemberUuidRepository memberRepository,
            WorldGuardGateway worldGuardGateway,
            BaseBorderService borderService,
            BaseExpansionPaymentService paymentService
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.repository = repository;
        this.memberRepository = memberRepository;
        this.worldGuardGateway = worldGuardGateway;
        this.borderService = borderService;
        this.paymentService = paymentService;
    }

    public CompletableFuture<Optional<ClanBaseSnapshot>> findBase(long clanId) {
        return CompletableFuture.supplyAsync(() -> repository.findByClanId(clanId).map(ClanBaseRecord::toSnapshot));
    }

    public CompletableFuture<Optional<ClanBaseRecord>> findBaseRecord(long clanId) {
        return CompletableFuture.supplyAsync(() -> repository.findByClanId(clanId));
    }

    public Optional<BaseSetupFailure> validateFlagPlacement(Player player, ClanSnapshot clan, Location location) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            return Optional.of(BaseSetupFailure.NOT_LEADER);
        }
        if (repository.findByClanId(clan.id()).isPresent()) {
            return Optional.of(BaseSetupFailure.ALREADY_EXISTS);
        }
        if (!worldGuardGateway.available()) {
            return Optional.of(BaseSetupFailure.WORLDGUARD_MISSING);
        }
        BaseBounds bounds = boundsAt(location);
        if (conflictsWithStoredBases(location.getWorld().getName(), bounds, clan.id())) {
            return Optional.of(BaseSetupFailure.TOO_CLOSE);
        }
        String regionName = BaseRegionNames.forClan(clan.id());
        if (worldGuardGateway.hasConflict(location.getWorld(), bounds, config.regionBuffer(), regionName)) {
            return Optional.of(BaseSetupFailure.TOO_CLOSE);
        }
        return Optional.empty();
    }

    public Optional<ClanBaseRecord> createBase(Player player, ClanSnapshot clan, Location flagLocation) {
        Optional<BaseSetupFailure> failure = validateFlagPlacement(player, clan, flagLocation);
        if (failure.isPresent()) {
            notifyFailure(player, failure.get());
            return Optional.empty();
        }
        World world = flagLocation.getWorld();
        BaseBounds bounds = boundsAt(flagLocation);
        String regionName = BaseRegionNames.forClan(clan.id());
        Set<UUID> memberIds = new LinkedHashSet<>(memberRepository.findMemberIds(clan.id()));
        memberIds.remove(clan.leaderId());
        try {
            worldGuardGateway.createRegion(world, regionName, bounds, clan.leaderId(), memberIds, false, true);
            List<ClanBaseRepository.BorderBlock> borderBlocks = borderService.placeBorder(
                    world,
                    bounds,
                    flagLocation.getBlockY(),
                    config.borderMaterial()
            );
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
                    false,
                    true,
                    System.currentTimeMillis()
            ));
            repository.saveBorderBlocks(saved.id(), world.getName(), borderBlocks);
            borderService.registerBorder(saved.id(), world.getName(), borderBlocks);
            messages.send(player, "land.base.created", java.util.Map.of(
                    "tag", clan.tag(),
                    "region", regionName
            ));
            return Optional.of(saved);
        } catch (RuntimeException exception) {
            worldGuardGateway.removeRegion(world, regionName);
            messages.send(player, "land.error.failed");
            return Optional.empty();
        }
    }

    public boolean destroyBaseAtFlag(org.bukkit.Location location) {
        Optional<ClanBaseRecord> baseOptional = repository.findByFlag(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
        if (baseOptional.isEmpty()) {
            return false;
        }
        destroyBase(baseOptional.get());
        return true;
    }

    public boolean isBaseFlag(org.bukkit.Location location) {
        return repository.findByFlag(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        ).isPresent();
    }

    public void destroyBase(ClanBaseRecord base) {
        World world = api.plugin().getServer().getWorld(base.world());
        if (world == null) {
            repository.delete(base.id());
            return;
        }
        List<ClanBaseRepository.BorderBlock> borderBlocks = repository.findBorderBlocks(base.id());
        borderService.unregisterBorder(base.id(), base.world(), borderBlocks);
        borderService.restoreBorder(world, borderBlocks);
        worldGuardGateway.removeRegion(world, base.regionName());
        repository.delete(base.id());
    }

    public void addMemberToRegion(long clanId, UUID playerId) {
        repository.findByClanId(clanId).ifPresent(base -> {
            World world = api.plugin().getServer().getWorld(base.world());
            if (world == null) {
                return;
            }
            worldGuardGateway.addMember(world, base.regionName(), playerId);
        });
    }

    public void removeMemberFromRegion(long clanId, UUID playerId) {
        repository.findByClanId(clanId).ifPresent(base -> {
            World world = api.plugin().getServer().getWorld(base.world());
            if (world == null) {
                return;
            }
            worldGuardGateway.removeMember(world, base.regionName(), playerId);
        });
    }

    public void transferRegionOwnership(long clanId, UUID previousLeaderId, UUID newLeaderId) {
        repository.findByClanId(clanId).ifPresent(base -> {
            World world = api.plugin().getServer().getWorld(base.world());
            if (world == null) {
                return;
            }
            worldGuardGateway.transferOwnership(world, base.regionName(), previousLeaderId, newLeaderId);
        });
    }

    public void togglePvp(Player player, ClanSnapshot clan, ClanBaseRecord base) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            messages.send(player, "land.error.not-leader");
            return;
        }
        boolean next = !base.pvpEnabled();
        repository.updatePvp(base.id(), next);
        World world = api.plugin().getServer().getWorld(base.world());
        if (world != null) {
            worldGuardGateway.applyFlags(world, base.regionName(), next, base.mobSpawnEnabled());
        }
        messages.send(player, next ? "land.settings.pvp-enabled" : "land.settings.pvp-disabled");
    }

    public void toggleMobSpawn(Player player, ClanSnapshot clan, ClanBaseRecord base) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            messages.send(player, "land.error.not-leader");
            return;
        }
        boolean next = !base.mobSpawnEnabled();
        repository.updateMobSpawn(base.id(), next);
        World world = api.plugin().getServer().getWorld(base.world());
        if (world != null) {
            worldGuardGateway.applyFlags(world, base.regionName(), base.pvpEnabled(), next);
        }
        messages.send(player, next ? "land.settings.mob-enabled" : "land.settings.mob-disabled");
    }

    public void cycleBorderColor(Player player, ClanSnapshot clan, ClanBaseRecord base) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            messages.send(player, "land.error.not-leader");
            return;
        }
        Material current = config.borderColors().resolve(base.borderMaterial());
        Material next = config.borderColors().next(current);
        repository.updateBorderMaterial(base.id(), next.name());
        World world = api.plugin().getServer().getWorld(base.world());
        if (world != null) {
            List<ClanBaseRepository.BorderBlock> borderBlocks = repository.findBorderBlocks(base.id());
            borderService.applyBorderColor(world, borderBlocks, next);
        }
        messages.send(player, "land.settings.border-color-changed", Map.of(
                "color",
                messages.resolve(player, "land.gui.border-colors." + config.borderColors().displayKey(next))
        ));
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
        if (conflictsWithStoredBases(base.world(), strip, clan.id())
                || worldGuardGateway.hasConflict(world, strip, config.regionBuffer(), base.regionName())) {
            messages.send(player, "land.error.too-close", Map.of("buffer", String.valueOf(config.regionBuffer())));
            return;
        }
        double cost = config.expansion().costForExtent(currentExtent, config.baseRadius());
        paymentService.charge(player, clan.id(), cost).thenAccept(paymentResult -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (paymentResult != ExpansionPaymentResult.SUCCESS) {
                notifyPaymentFailure(player, paymentResult);
                return;
            }
            applyExpansion(player, expandedRecord, axis, oldBounds, newBounds, world, cost);
            if (onComplete != null) {
                onComplete.run();
            }
        }));
    }

    public double expansionCost(ClanBaseRecord base, BaseExpansionAxis axis) {
        int currentExtent = axis.resolvedExtent(base, config.baseRadius());
        return config.expansion().costForExtent(currentExtent, config.baseRadius());
    }

    public boolean usesTreasuryPayments() {
        return paymentService.usesTreasury();
    }

    private void applyExpansion(
            Player player,
            ClanBaseRecord expandedRecord,
            BaseExpansionAxis axis,
            BaseBounds oldBounds,
            BaseBounds newBounds,
            World world,
            double cost
    ) {
        List<ClanBaseRepository.BorderBlock> borderBlocks = new ArrayList<>(repository.findBorderBlocks(expandedRecord.id()));
        Material material = config.borderColors().resolve(expandedRecord.borderMaterial());
        BorderExpansionDelta delta = borderService.expandBorder(
                world,
                oldBounds,
                newBounds,
                axis,
                expandedRecord.flagY(),
                material,
                borderBlocks
        );
        try {
            borderService.unregisterBorder(expandedRecord.id(), expandedRecord.world(), delta.removed());
            worldGuardGateway.resizeRegion(world, expandedRecord.regionName(), newBounds);
            repository.updateExtents(
                    expandedRecord.id(),
                    expandedRecord.extentXPos(),
                    expandedRecord.extentXNeg(),
                    expandedRecord.extentZPos(),
                    expandedRecord.extentZNeg()
            );
            repository.saveBorderBlocks(expandedRecord.id(), expandedRecord.world(), borderBlocks);
            borderService.registerBorder(expandedRecord.id(), expandedRecord.world(), delta.added());
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
        } catch (RuntimeException exception) {
            messages.send(player, "land.error.failed");
        }
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

    private boolean conflictsWithStoredBases(String worldName, BaseBounds bounds, long ignoredClanId) {
        for (ClanBaseRecord existing : repository.findAllInWorld(worldName)) {
            if (existing.clanId() == ignoredClanId) {
                continue;
            }
            World world = api.plugin().getServer().getWorld(worldName);
            if (world == null) {
                continue;
            }
            BaseBounds other = existing.bounds(config.baseRadius(), world.getMinHeight(), world.getMaxHeight());
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
            messages.send(player, key, java.util.Map.of("buffer", String.valueOf(config.regionBuffer())));
            return;
        }
        messages.send(player, key);
    }
}
