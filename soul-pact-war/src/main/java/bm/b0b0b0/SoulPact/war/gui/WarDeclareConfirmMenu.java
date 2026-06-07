package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class WarDeclareConfirmMenu implements InventoryHolder {

    private final WarConfig config;
    private final long targetClanId;
    private final int listPage;
    private final Inventory inventory;

    public WarDeclareConfirmMenu(
            WarConfig config,
            WarDeclareConfirmMenuPopulator populator,
            WarMessages messages,
            Player player,
            long targetClanId,
            String targetTag,
            String targetName,
            int listPage
    ) {
        this.config = config;
        this.targetClanId = targetClanId;
        this.listPage = listPage;
        this.inventory = Bukkit.createInventory(
                this,
                config.declareConfirmSize(),
                messages.component(player, "war.gui.declare-confirm.title", Map.of(
                        "tag", targetTag,
                        "name", targetName
                ))
        );
        populator.populate(inventory, player, targetTag, targetName, String.valueOf(targetClanId));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public WarConfig config() {
        return config;
    }

    public long targetClanId() {
        return targetClanId;
    }

    public int listPage() {
        return listPage;
    }

    public int confirmSlot() {
        return config.declareConfirmSlot();
    }

    public int denySlot() {
        return config.declareDenySlot();
    }
}
