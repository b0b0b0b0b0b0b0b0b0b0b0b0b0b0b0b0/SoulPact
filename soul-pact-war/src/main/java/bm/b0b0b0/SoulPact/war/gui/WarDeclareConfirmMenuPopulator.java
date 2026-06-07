package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarGuiItems;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class WarDeclareConfirmMenuPopulator {

    private final WarConfig config;
    private final WarMessages messages;

    public WarDeclareConfirmMenuPopulator(WarConfig config, WarMessages messages) {
        this.config = config;
        this.messages = messages;
    }

    public void populate(Inventory inventory, Player player, String targetTag, String targetName, String targetId) {
        Map<String, String> placeholders = Map.of(
                "tag", targetTag,
                "name", targetName,
                "id", targetId
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, WarGuiItems.filler(Material.GRAY_STAINED_GLASS_PANE));
        }
        inventory.setItem(config.declareConfirmSlot(), WarGuiItems.build(
                messages,
                player,
                Material.LIME_DYE,
                "war.gui.declare-confirm.item.confirm.name",
                "war.gui.declare-confirm.item.confirm.lore",
                placeholders
        ));
        inventory.setItem(config.declareDenySlot(), WarGuiItems.build(
                messages,
                player,
                Material.RED_DYE,
                "war.gui.declare-confirm.item.deny.name",
                "war.gui.declare-confirm.item.deny.lore",
                placeholders
        ));
    }
}
