package bm.b0b0b0.SoulPact.chest.gui;

import bm.b0b0b0.SoulPact.chest.config.ChestConfig;
import bm.b0b0b0.SoulPact.chest.message.ChestMessages;
import bm.b0b0b0.SoulPact.chest.service.ChestGuiLayout;
import java.util.Map;
import java.util.OptionalInt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class ChestMenu implements InventoryHolder {

    private final ChestConfig config;
    private final ChestGuiLayout layout;
    private ChestMenuSnapshot snapshot;
    private final Inventory inventory;

    public ChestMenu(
            ChestConfig config,
            ChestMessages messages,
            ChestGuiLayout layout,
            ChestMenuPopulator populator,
            Player player,
            ChestMenuSnapshot snapshot
    ) {
        this.config = config;
        this.layout = layout;
        this.snapshot = snapshot;
        this.inventory = Bukkit.createInventory(
                this,
                config.guiSize(),
                messages.component(player, "chest.gui.title", Map.of(
                        "tag", snapshot.clan().tag(),
                        "page", String.valueOf(snapshot.page() + 1)
                ))
        );
        populator.populate(inventory, player, snapshot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ChestConfig config() {
        return config;
    }

    public ChestGuiLayout layout() {
        return layout;
    }

    public ChestMenuSnapshot snapshot() {
        return snapshot;
    }

    public void replaceSnapshot(ChestMenuSnapshot newSnapshot, Player player, ChestMenuPopulator populator) {
        syncPageToItems();
        this.snapshot = newSnapshot;
        populator.populate(inventory, player, snapshot);
    }

    public void syncPageToItems() {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            OptionalInt cellIndex = layout.cellIndex(snapshot.page(), slot);
            if (cellIndex.isEmpty()) {
                continue;
            }
            if (!layout.isUnlocked(cellIndex.getAsInt(), snapshot.unlockedCells())) {
                continue;
            }
            ItemStack current = inventory.getItem(slot);
            if (current == null || current.getType().isAir()) {
                snapshot.items().remove(cellIndex.getAsInt());
                continue;
            }
            snapshot.items().put(cellIndex.getAsInt(), current.clone());
        }
    }
}
