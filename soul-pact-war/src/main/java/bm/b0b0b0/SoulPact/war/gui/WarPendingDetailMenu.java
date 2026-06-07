package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class WarPendingDetailMenu implements InventoryHolder {

    private final WarConfig config;
    private final WarDeclarationRecord declaration;
    private final Inventory inventory;

    public WarPendingDetailMenu(
            WarConfig config,
            WarPendingDetailMenuPopulator populator,
            WarMessages messages,
            Player player,
            WarDeclarationRecord declaration,
            String attackerTag,
            String attackerName
    ) {
        this.config = config;
        this.declaration = declaration;
        this.inventory = Bukkit.createInventory(
                this,
                config.pendingDetailSize(),
                messages.component(player, "war.gui.pending-detail.title", Map.of(
                        "tag", attackerTag,
                        "name", attackerName
                ))
        );
        populator.populate(inventory, player, declaration, attackerTag, attackerName);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public WarConfig config() {
        return config;
    }

    public WarDeclarationRecord declaration() {
        return declaration;
    }

    public int acceptSlot() {
        return config.pendingAcceptSlot();
    }

    public int ransomSlot() {
        return config.pendingRansomSlot();
    }

    public int backSlot() {
        return config.pendingBackSlot();
    }
}
