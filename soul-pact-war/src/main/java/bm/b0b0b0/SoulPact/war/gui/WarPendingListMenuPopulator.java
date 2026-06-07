package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarGuiItems;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class WarPendingListMenuPopulator {

    private final WarConfig config;
    private final WarMessages messages;

    public WarPendingListMenuPopulator(WarConfig config, WarMessages messages) {
        this.config = config;
        this.messages = messages;
    }

    public void populate(
            Inventory inventory,
            Player player,
            List<WarPendingListEntry> entries,
            Map<Integer, Long> declarationIdsBySlot
    ) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, WarGuiItems.filler(Material.GRAY_STAINED_GLASS_PANE));
        }
        if (entries.isEmpty()) {
            inventory.setItem(22, WarGuiItems.build(
                    messages,
                    player,
                    Material.BARRIER,
                    "war.gui.pending-list.item.empty.name",
                    "war.gui.pending-list.item.empty.lore",
                    Map.of()
            ));
        } else {
            int index = 0;
            for (WarPendingListEntry entry : entries) {
                if (index >= config.pendingListPageSize()) {
                    break;
                }
                Map<String, String> placeholders = Map.of(
                        "id", String.valueOf(entry.declaration().id()),
                        "attacker_id", String.valueOf(entry.declaration().attackerClanId()),
                        "attacker_tag", entry.attackerTag(),
                        "attacker_name", entry.attackerName()
                );
                inventory.setItem(index, WarGuiItems.build(
                        messages,
                        player,
                        Material.PAPER,
                        "war.gui.pending-list.item.entry.name",
                        "war.gui.pending-list.item.entry.lore",
                        placeholders
                ));
                declarationIdsBySlot.put(index, entry.declaration().id());
                index++;
            }
        }
        inventory.setItem(config.pendingListBackSlot(), WarGuiItems.build(
                messages,
                player,
                Material.ARROW,
                "war.gui.pending-list.item.back.name",
                "war.gui.pending-list.item.back.lore",
                Map.of()
        ));
    }
}
