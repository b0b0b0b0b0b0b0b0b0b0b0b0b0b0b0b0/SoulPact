package bm.b0b0b0.SoulPact.bank.gui;

import bm.b0b0b0.SoulPact.bank.config.BankConfig;
import bm.b0b0b0.SoulPact.bank.message.BankMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class BankMenu implements InventoryHolder {

    private final BankConfig config;
    private final BankMenuSnapshot snapshot;
    private final Inventory inventory;

    public BankMenu(
            BankConfig config,
            BankMenuPopulator populator,
            BankMessages messages,
            Player player,
            BankMenuSnapshot snapshot
    ) {
        this.config = config;
        this.snapshot = snapshot;
        this.inventory = Bukkit.createInventory(
                this,
                config.guiSize(),
                messages.component(player, "bank.gui.title", java.util.Map.of(
                        "tag", snapshot.view().clan().tag()
                ))
        );
        populator.populate(inventory, player, snapshot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public BankConfig config() {
        return config;
    }

    public BankMenuSnapshot snapshot() {
        return snapshot;
    }
}
