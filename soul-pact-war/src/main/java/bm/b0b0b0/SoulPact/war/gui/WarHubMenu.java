package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class WarHubMenu implements InventoryHolder {

    private final WarConfig config;
    private final Inventory inventory;
    private final boolean viewerCanRespond;
    private final int pendingCount;

    public WarHubMenu(
            WarConfig config,
            WarHubMenuPopulator populator,
            WarMessages messages,
            Player player,
            WarHubViewData viewData
    ) {
        this.config = config;
        this.viewerCanRespond = viewData.viewerCanRespond();
        this.pendingCount = viewData.pendingCount();
        this.inventory = Bukkit.createInventory(
                this,
                config.hubSize(),
                messages.component(player, "war.gui.hub.title")
        );
        populator.populate(inventory, player, viewData);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public WarConfig config() {
        return config;
    }

    public boolean viewerCanRespond() {
        return viewerCanRespond;
    }

    public int pendingCount() {
        return pendingCount;
    }

    public int enemyFlagSlot() {
        return config.hubEnemyFlagSlot();
    }

    public int pendingSlot() {
        return config.hubPendingSlot();
    }

    public int backSlot() {
        return config.hubBackSlot();
    }
}
