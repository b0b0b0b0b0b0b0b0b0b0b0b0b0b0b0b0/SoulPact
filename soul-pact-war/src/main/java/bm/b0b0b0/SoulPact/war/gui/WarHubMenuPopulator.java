package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarGuiItems;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class WarHubMenuPopulator {

    private final WarConfig config;
    private final WarMessages messages;

    public WarHubMenuPopulator(WarConfig config, WarMessages messages) {
        this.config = config;
        this.messages = messages;
    }

    public void populate(Inventory inventory, Player player, WarHubViewData viewData) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, WarGuiItems.filler(Material.GRAY_STAINED_GLASS_PANE));
        }
        if (viewData.enemy().isPresent()) {
            WarEnemyTarget enemy = viewData.enemy().get();
            inventory.setItem(config.hubEnemyFlagSlot(), WarGuiItems.build(
                    messages,
                    player,
                    Material.RED_BANNER,
                    "war.gui.hub.item.enemy-flag.name",
                    "war.gui.hub.item.enemy-flag.lore",
                    Map.of(
                            "enemy_tag", enemy.enemyTag(),
                            "enemy_name", enemy.enemyName(),
                            "world", enemy.world(),
                            "x", String.valueOf(enemy.x()),
                            "y", String.valueOf(enemy.y()),
                            "z", String.valueOf(enemy.z())
                    )
            ));
        } else {
            inventory.setItem(config.hubEnemyFlagSlot(), WarGuiItems.build(
                    messages,
                    player,
                    Material.BARRIER,
                    "war.gui.hub.item.no-war.name",
                    "war.gui.hub.item.no-war.lore",
                    Map.of()
            ));
        }
        if (viewData.viewerIsLeader() && viewData.pendingCount() > 0) {
            inventory.setItem(config.hubPendingSlot(), WarGuiItems.build(
                    messages,
                    player,
                    Material.WRITABLE_BOOK,
                    "war.gui.hub.item.pending.name",
                    "war.gui.hub.item.pending.lore",
                    Map.of("count", String.valueOf(viewData.pendingCount()))
            ));
        }
        inventory.setItem(config.hubBackSlot(), WarGuiItems.build(
                messages,
                player,
                Material.ARROW,
                "war.gui.hub.item.back.name",
                "war.gui.hub.item.back.lore",
                Map.of()
        ));
    }
}
