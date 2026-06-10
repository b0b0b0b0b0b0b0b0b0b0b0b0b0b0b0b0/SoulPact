package bm.b0b0b0.SoulPact.quests.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public final class QuestsGuiListener implements Listener {

    private final QuestsClickHandler clickHandler;

    public QuestsGuiListener(QuestsClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!(topInventory.getHolder(false) instanceof QuestsMenu questsMenu)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int rawSlot = event.getRawSlot();
        if (rawSlot >= topInventory.getSize()) {
            return;
        }
        clickHandler.handle(questsMenu, player, rawSlot);
    }
}
