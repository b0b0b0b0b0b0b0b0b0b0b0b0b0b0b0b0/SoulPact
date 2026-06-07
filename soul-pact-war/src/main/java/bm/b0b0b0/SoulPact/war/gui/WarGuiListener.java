package bm.b0b0b0.SoulPact.war.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public final class WarGuiListener implements Listener {

    private final WarGuiClickHandler clickHandler;

    public WarGuiListener(WarGuiClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (clickedInventory.getHolder(false) instanceof WarDeclareConfirmMenu declareMenu) {
            event.setCancelled(true);
            clickHandler.handleDeclareConfirm(declareMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof WarPendingListMenu pendingListMenu) {
            event.setCancelled(true);
            clickHandler.handlePendingList(pendingListMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof WarPendingDetailMenu pendingDetailMenu) {
            event.setCancelled(true);
            clickHandler.handlePendingDetail(pendingDetailMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof WarHubMenu warHubMenu) {
            event.setCancelled(true);
            clickHandler.handleWarHub(warHubMenu, player, event.getSlot());
        }
    }
}
