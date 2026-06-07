package bm.b0b0b0.SoulPact.land.gui;

import bm.b0b0b0.SoulPact.land.config.LandConfig;
import bm.b0b0b0.SoulPact.land.message.LandMessages;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class LandMenu implements InventoryHolder {

    private final LandConfig config;
    private final LandMenuSnapshot snapshot;
    private final Inventory inventory;

    public LandMenu(LandConfig config, LandMessages messages, LandMenuPopulator populator, Player player, LandMenuSnapshot snapshot) {
        this.config = config;
        this.snapshot = snapshot;
        this.inventory = Bukkit.createInventory(
                this,
                config.guiSize(),
                messages.component(player, "land.gui.title", Map.of("tag", snapshot.clan().tag()))
        );
        populator.populate(inventory, player, snapshot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public LandConfig config() {
        return config;
    }

    public LandMenuSnapshot snapshot() {
        return snapshot;
    }
}
