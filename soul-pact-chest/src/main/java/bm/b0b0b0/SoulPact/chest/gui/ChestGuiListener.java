package bm.b0b0b0.SoulPact.chest.gui;

import bm.b0b0b0.SoulPact.chest.service.ChestGuiLayout;
import java.util.OptionalInt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ChestGuiListener implements Listener {

    private final ChestGuiService guiService;
    private final ChestClickHandler clickHandler;
    private final ChestGuiLayout layout;

    public ChestGuiListener(ChestGuiService guiService, ChestClickHandler clickHandler, ChestGuiLayout layout) {
        this.guiService = guiService;
        this.clickHandler = clickHandler;
        this.layout = layout;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!(topInventory.getHolder(false) instanceof ChestMenu chestMenu)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            event.setCancelled(true);
            return;
        }
        int rawSlot = event.getRawSlot();
        if (rawSlot >= 0 && rawSlot < topInventory.getSize()) {
            handleTopClick(event, chestMenu, player, rawSlot);
            return;
        }
        handleBottomClick(event, chestMenu);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!(topInventory.getHolder(false) instanceof ChestMenu chestMenu)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            event.setCancelled(true);
            return;
        }
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot >= topInventory.getSize()) {
                if (!chestMenu.snapshot().canDeposit()) {
                    event.setCancelled(true);
                    guiService.messages().send(player, "chest.error.no-deposit");
                }
                continue;
            }
            if (layout.isControlSlot(rawSlot) || isLockedCell(chestMenu, rawSlot)) {
                event.setCancelled(true);
                return;
            }
            if (!chestMenu.snapshot().canDeposit()) {
                event.setCancelled(true);
                guiService.messages().send(player, "chest.error.no-deposit");
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!(topInventory.getHolder(false) instanceof ChestMenu chestMenu)) {
            return;
        }
        chestMenu.syncPageToItems();
        guiService.persist(chestMenu);
    }

    private void handleTopClick(InventoryClickEvent event, ChestMenu chestMenu, Player player, int rawSlot) {
        if (layout.isControlSlot(rawSlot)) {
            event.setCancelled(true);
            clickHandler.handle(chestMenu, player, rawSlot);
            return;
        }
        OptionalInt cellIndex = layout.cellIndex(chestMenu.snapshot().page(), rawSlot);
        if (cellIndex.isEmpty()) {
            event.setCancelled(true);
            return;
        }
        if (!layout.isUnlocked(cellIndex.getAsInt(), chestMenu.snapshot().unlockedCells())) {
            event.setCancelled(true);
            return;
        }
        if (!allowStorageAction(event, chestMenu)) {
            event.setCancelled(true);
            notifyDenied(event, chestMenu, player);
        }
    }

    private void handleBottomClick(InventoryClickEvent event, ChestMenu chestMenu) {
        if (!event.isShiftClick()) {
            return;
        }
        InventoryAction action = event.getAction();
        if (action != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return;
        }
        if (!chestMenu.snapshot().canDeposit()) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                guiService.messages().send(player, "chest.error.no-deposit");
            }
        }
    }

    private boolean allowStorageAction(InventoryClickEvent event, ChestMenu chestMenu) {
        InventoryAction action = event.getAction();
        ClickType clickType = event.getClick();
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        boolean placing = cursor != null && !cursor.getType().isAir();
        boolean taking = current != null && !current.getType().isAir();
        if (clickType == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if (hotbarItem != null && !hotbarItem.getType().isAir()) {
                placing = true;
            }
            if (taking && (hotbarItem == null || hotbarItem.getType().isAir())) {
                return chestMenu.snapshot().canWithdraw();
            }
        }
        if (action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.PICKUP_SOME
                || action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || action == InventoryAction.DROP_ALL_SLOT
                || action == InventoryAction.DROP_ONE_SLOT
                || action == InventoryAction.DROP_ALL_CURSOR
                || action == InventoryAction.DROP_ONE_CURSOR
                || action == InventoryAction.HOTBAR_SWAP
                || action == InventoryAction.HOTBAR_MOVE_AND_READD) {
            if (taking && !placing) {
                return chestMenu.snapshot().canWithdraw();
            }
        }
        if (action == InventoryAction.PLACE_ALL
                || action == InventoryAction.PLACE_ONE
                || action == InventoryAction.PLACE_SOME
                || action == InventoryAction.SWAP_WITH_CURSOR) {
            return chestMenu.snapshot().canDeposit();
        }
        if (placing && !taking) {
            return chestMenu.snapshot().canDeposit();
        }
        if (taking && !placing) {
            return chestMenu.snapshot().canWithdraw();
        }
        if (taking && placing) {
            return chestMenu.snapshot().canWithdraw() && chestMenu.snapshot().canDeposit();
        }
        return chestMenu.snapshot().canDeposit() || chestMenu.snapshot().canWithdraw();
    }

    private void notifyDenied(InventoryClickEvent event, ChestMenu chestMenu, Player player) {
        if (!chestMenu.snapshot().canDeposit() && !chestMenu.snapshot().canWithdraw()) {
            guiService.messages().send(player, "chest.error.no-access");
            return;
        }
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        boolean placing = cursor != null && !cursor.getType().isAir();
        boolean taking = current != null && !current.getType().isAir();
        if (placing && !chestMenu.snapshot().canDeposit()) {
            guiService.messages().send(player, "chest.error.no-deposit");
            return;
        }
        if (taking && !chestMenu.snapshot().canWithdraw()) {
            guiService.messages().send(player, "chest.error.no-withdraw");
        }
    }

    private boolean isLockedCell(ChestMenu chestMenu, int rawSlot) {
        OptionalInt cellIndex = layout.cellIndex(chestMenu.snapshot().page(), rawSlot);
        if (cellIndex.isEmpty()) {
            return true;
        }
        return !layout.isUnlocked(cellIndex.getAsInt(), chestMenu.snapshot().unlockedCells());
    }
}
