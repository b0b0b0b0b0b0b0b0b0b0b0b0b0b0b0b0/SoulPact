package bm.b0b0b0.SoulPact.land.service;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;

public final class BorderBlockOccupancyGuard {

    public boolean blocksBorderPlacement(Block block) {
        if (!isLootContainer(block)) {
            return false;
        }
        Container container = (Container) block.getState(false);
        Inventory inventory = container.getInventory();
        return inventory != null && !inventory.isEmpty();
    }

    private boolean isLootContainer(Block block) {
        if (!(block.getState(false) instanceof Container)) {
            return false;
        }
        return block.getType() != Material.ENDER_CHEST;
    }
}
