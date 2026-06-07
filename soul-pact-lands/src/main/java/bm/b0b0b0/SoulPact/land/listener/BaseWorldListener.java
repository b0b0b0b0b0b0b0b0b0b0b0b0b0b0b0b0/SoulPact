package bm.b0b0b0.SoulPact.land.listener;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.clan.SoulPactClanStandard;
import bm.b0b0b0.SoulPact.land.message.LandMessages;
import bm.b0b0b0.SoulPact.land.model.BaseSetupFailure;
import bm.b0b0b0.SoulPact.land.service.BorderBlockIndex;
import bm.b0b0b0.SoulPact.land.service.ClanBaseService;
import java.util.Map;
import java.util.Optional;
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
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional ->
                api.scheduler().runSync(() ->
                        completeStandardPlacement(player, clanId, standardTemplate, location, clanOptional)
                )
        );
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStandardBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!baseService.isBaseFlag(block.getLocation())) {
            return;
        }
        Long clanId = clanStandard.readClanIdFromBlock(block.getState());
        if (clanId == null) {
            return;
        }
        String clanTag = clanStandard.readClanTagFromBlock(block.getState());
        if (clanTag == null) {
            clanTag = String.valueOf(clanId);
        }
        event.setDropItems(false);
        baseService.destroyBaseAtFlag(block.getLocation());
        if (event.getPlayer() instanceof Player player) {
            clanStandard.restoreToPlayer(player, clanId, clanTag);
            messages.send(player, "land.base.destroyed");
        }
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

    private void completeStandardPlacement(
            Player player,
            long expectedClanId,
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
        Optional<BaseSetupFailure> failure = baseService.validateFlagPlacement(player, clan, location);
        if (failure.isPresent()) {
            revertStandardPlacement(player, expectedClanId, location, standardTemplate);
            baseService.notifyFailure(player, failure.get());
            return;
        }
        if (baseService.createBase(player, clan, location).isEmpty()) {
            revertStandardPlacement(player, expectedClanId, location, standardTemplate);
        }
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
