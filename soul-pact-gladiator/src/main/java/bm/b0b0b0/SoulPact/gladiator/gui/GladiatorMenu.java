package bm.b0b0b0.SoulPact.gladiator.gui;

import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfig;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class GladiatorMenu implements InventoryHolder {

    private final Map<Integer, Arena> arenasBySlot = new HashMap<>();
    private final Inventory inventory;

    public GladiatorMenu(
            GladiatorConfig config,
            GladiatorMenuPopulator populator,
            GladiatorMessages messages,
            Player player
    ) {
        this.inventory = Bukkit.createInventory(
                this,
                config.guiSize(),
                messages.component(player, "gladiator.gui.title", Map.of())
        );
        populator.populate(inventory, player, arenasBySlot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Arena arenaAt(int slot) {
        return arenasBySlot.get(slot);
    }
}
