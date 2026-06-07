package bm.b0b0b0.SoulPact.land.listener;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.clan.SoulPactClanStandard;
import bm.b0b0b0.SoulPact.api.war.ClanWarProvider;
import bm.b0b0b0.SoulPact.api.war.FlagBreakWarResult;
import bm.b0b0b0.SoulPact.api.war.WarFlagBreakGate;
import bm.b0b0b0.SoulPact.land.message.LandMessages;
import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import bm.b0b0b0.SoulPact.land.service.BorderBlockIndex;
import bm.b0b0b0.SoulPact.land.service.ClanBaseService;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public final class BaseWorldListener implements Listener {

    private final SoulPactApi api;
    private final SoulPactClanStandard clanStandard;
    private final ClanBaseService baseService;
    private final BorderBlockIndex borderBlockIndex;
    private final LandMessages messages;

    public BaseWorldListener(
            SoulPactApi api,
            ClanBaseService baseService,
            BorderBlockIndex borderBlockIndex,
            LandMessages messages
    ) {
        this.api = api;
        this.clanStandard = api.clanStandard();
        this.baseService = baseService;
        this.borderBlockIndex = borderBlockIndex;
        this.messages = messages;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStandardPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!clanStandard.isStandard(item)) {
            return;
        }
        Long clanId = clanStandard.readClanId(item);
        if (clanId == null) {
            return;
        }
        Player player = event.getPlayer();
        Location location = event.getBlockPlaced().getLocation().clone();
        ItemStack standardTemplate = item.clone();
        standardTemplate.setAmount(1);
        final long resolvedClanId = clanId;
        UUID resolvedUid = clanStandard.readStandardUid(item);
        if (resolvedUid == null) {
            resolvedUid = UUID.randomUUID();
        }
        final UUID standardUid = resolvedUid;
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional ->
                api.scheduler().runSync(() ->
                        completeStandardPlacement(player, resolvedClanId, standardUid, standardTemplate, location, clanOptional)
                )
        );
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBaseFlagBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!baseService.isKnownFlagBlock(block)) {
            return;
        }
        Player player = event.getPlayer() instanceof Player resolved ? resolved : null;
        if (player != null && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
            messages.send(player, "land.error.flag-creative-blocked");
            return;
        }
        event.setCancelled(true);
        Location location = block.getLocation();
        Optional<ClanBaseRecord> baseOptional = baseService.resolveBaseForBrokenFlag(block);
        if (baseOptional.isEmpty()) {
            breakFlagWithoutBase(player, block);
            return;
        }
        ClanBaseRecord base = baseOptional.get();
        Long bannerClanId = clanStandard.readClanIdFromBlock(block.getState());
        if (bannerClanId != null && bannerClanId != base.clanId()) {
            base = baseService.findBaseRecordByClanId(bannerClanId).orElse(base);
        }
        final ClanBaseRecord targetBase = base;
        long flagOwnerClanId = bannerClanId != null ? bannerClanId : targetBase.clanId();
        Optional<WarFlagBreakGate> warGateOptional = resolveWarGate();
        if (player != null && warGateOptional.isPresent()) {
            FlagBreakWarResult warResult = warGateOptional.get().handleBrokenFlag(
                    player,
                    flagOwnerClanId,
                    location,
                    () -> destroyBaseForWar(targetBase)
            );
            if (warResult != FlagBreakWarResult.PEACEFUL) {
                return;
            }
        }
        destroyBaseProtected(player, block, targetBase);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBorderBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (borderBlockIndex.find(
                block.getWorld().getName(),
                block.getX(),
                block.getY(),
                block.getZ()
        ).isEmpty()) {
            return;
        }
        event.setCancelled(true);
        if (event.getPlayer() instanceof Player player) {
            messages.send(player, "land.error.border-protected");
        }
    }

    private Optional<WarFlagBreakGate> resolveWarGate() {
        return api.extensions().find("war")
                .filter(ClanWarProvider.class::isInstance)
                .map(ClanWarProvider.class::cast)
                .map(ClanWarProvider::flagBreak);
    }

    private void breakFlagWithoutBase(Player player, Block block) {
        if (player != null) {
            block.breakNaturally(player.getInventory().getItemInMainHand());
        } else {
            block.setType(Material.AIR);
        }
    }

    private void destroyBaseProtected(Player player, Block block, ClanBaseRecord base) {
        String clanTag = clanStandard.readClanTagFromBlock(block.getState());
        if (clanTag == null || clanTag.isBlank()) {
            clanTag = String.valueOf(base.clanId());
        }
        baseService.clearFlagBlocks(base);
        baseService.destroyBase(base);
        if (player != null) {
            clanStandard.restoreToPlayer(player, base.clanId(), clanTag);
            clanStandard.trackInventory(base.clanId(), player.getUniqueId());
            messages.send(player, "land.base.destroyed");
        }
    }

    private void destroyBaseForWar(ClanBaseRecord base) {
        baseService.clearFlagBlocks(base);
        baseService.destroyBase(base);
    }

    private void completeStandardPlacement(
            Player player,
            long expectedClanId,
            UUID standardUid,
            ItemStack standardTemplate,
            Location location,
            Optional<ClanSnapshot> clanOptional
    ) {
        if (!player.isOnline()) {
            return;
        }
        if (clanOptional.isEmpty() || clanOptional.get().id() != expectedClanId) {
            revertStandardPlacement(player, expectedClanId, location, standardTemplate);
            messages.send(player, "land.error.not-in-clan");
            return;
        }
        ClanSnapshot clan = clanOptional.get();
        baseService.createBaseAsync(
                player,
                clan,
                location,
                standardUid,
                () -> revertStandardPlacement(player, expectedClanId, location, standardTemplate)
        );
    }

    private void revertStandardPlacement(Player player, long clanId, Location location, ItemStack standardTemplate) {
        if (location.getWorld() != null) {
            location.getBlock().setType(Material.AIR);
        }
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(standardTemplate);
        leftovers.values().forEach(leftover ->
                player.getWorld().dropItemNaturally(player.getLocation(), leftover)
        );
        clanStandard.trackInventory(clanId, player.getUniqueId());
    }
}
