package bm.b0b0b0.SoulPact.clan.listener;

import bm.b0b0b0.SoulPact.clan.standard.ClanStandardItem;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardPresence;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardService;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public final class ClanStandardListener implements Listener {

    private final ClanStandardItem clanStandardItem;
    private final ClanStandardPresence presence;
    private final ClanStandardService clanStandardService;

    public ClanStandardListener(
            ClanStandardItem clanStandardItem,
            ClanStandardPresence presence,
            ClanStandardService clanStandardService
    ) {
        this.clanStandardItem = clanStandardItem;
        this.presence = presence;
        this.clanStandardService = clanStandardService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        clanStandardService.recoverOnlineStandards(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Long clanId = clanStandardItem.readClanIdFromItem(event.getItemDrop().getItemStack());
        if (clanId == null) {
            return;
        }
        presence.trackEntity(clanId, event.getItemDrop().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Long clanId = clanStandardItem.readClanIdFromItem(event.getItem().getItemStack());
        if (clanId == null) {
            return;
        }
        presence.trackInventory(clanId, player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        Long clanId = clanStandardItem.readClanIdFromItem(event.getEntity().getItemStack());
        if (clanId == null) {
            return;
        }
        presence.trackEntity(clanId, event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        Long clanId = clanStandardItem.readClanIdFromItem(itemStack);
        if (clanId == null) {
            return;
        }
        if (!(event.getBlockPlaced().getState() instanceof TileState tileState)) {
            return;
        }
        String clanTag = clanStandardItem.readClanTagFromItem(itemStack);
        if (clanTag == null) {
            clanTag = String.valueOf(clanId);
        }
        clanStandardItem.markBlock(tileState, clanId, clanTag);
        tileState.update(true);
        presence.trackBlock(clanId, event.getBlockPlaced().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDespawn(ItemDespawnEvent event) {
        destroyIfStandardItem(event.getEntity().getItemStack());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Item item)) {
            return;
        }
        if (!shouldDestroyStandardItem(event, item)) {
            return;
        }
        destroyIfStandardItem(item.getItemStack());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        destroyIfStandardBlock(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            destroyIfStandardBlock(block);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            destroyIfStandardBlock(block);
        }
    }

    private void destroyIfStandardItem(ItemStack itemStack) {
        Long clanId = clanStandardItem.readClanIdFromItem(itemStack);
        if (clanId == null) {
            return;
        }
        clanStandardService.handleStandardDestroyed(clanId);
    }

    private void destroyIfStandardBlock(Block block) {
        Long clanId = clanStandardItem.readClanIdFromBlock(block.getState());
        if (clanId == null) {
            return;
        }
        clanStandardService.handleStandardDestroyed(clanId);
    }

    private boolean shouldDestroyStandardItem(EntityDamageEvent event, Item item) {
        if (isImmediateItemDestructionCause(event.getCause())) {
            return true;
        }
        if (item.isDead() || item.getHealth() <= 0.0D) {
            return true;
        }
        return event.getFinalDamage() >= item.getHealth();
    }

    private boolean isImmediateItemDestructionCause(EntityDamageEvent.DamageCause cause) {
        return switch (cause) {
            case LAVA, FIRE, FIRE_TICK, HOT_FLOOR, VOID, CONTACT,
                 ENTITY_EXPLOSION, BLOCK_EXPLOSION, DRAGON_BREATH, SONIC_BOOM -> true;
            default -> false;
        };
    }
}
