package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class WarPendingListMenu implements InventoryHolder {

    private final WarConfig config;
    private final Map<Integer, Long> declarationIdsBySlot = new HashMap<>();
    private final Inventory inventory;

    public WarPendingListMenu(
            WarConfig config,
            WarPendingListMenuPopulator populator,
            WarMessages messages,
            Player player,
            List<WarPendingListEntry> entries
    ) {
        this.config = config;
        this.inventory = Bukkit.createInventory(
                this,
                config.pendingListSize(),
                messages.component(player, "war.gui.pending-list.title", Map.of(
                        "count", String.valueOf(entries.size())
                ))
        );
        populator.populate(inventory, player, entries, declarationIdsBySlot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public WarConfig config() {
        return config;
    }

    public Map<Integer, Long> declarationIdsBySlot() {
        return declarationIdsBySlot;
    }

    public int backSlot() {
        return config.pendingListBackSlot();
    }
}
