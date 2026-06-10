package bm.b0b0b0.SoulPact.quests.gui;

import bm.b0b0b0.SoulPact.quests.config.QuestsConfig;
import bm.b0b0b0.SoulPact.quests.message.QuestsMessages;
import bm.b0b0b0.SoulPact.quests.service.QuestEntryView;
import bm.b0b0b0.SoulPact.quests.service.QuestsOverview;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class QuestsMenu implements InventoryHolder {

    private final QuestsOverview overview;
    private final Map<Integer, QuestEntryView> entriesBySlot = new HashMap<>();
    private final Inventory inventory;

    public QuestsMenu(
            QuestsConfig config,
            QuestsMenuPopulator populator,
            QuestsMessages messages,
            Player player,
            QuestsOverview overview
    ) {
        this.overview = overview;
        this.inventory = Bukkit.createInventory(
                this,
                config.guiSize(),
                messages.component(player, "quests.gui.title", Map.of("tag", overview.clan().tag()))
        );
        populator.populate(inventory, player, overview, entriesBySlot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public QuestsOverview overview() {
        return overview;
    }

    public QuestEntryView entryAt(int slot) {
        return entriesBySlot.get(slot);
    }
}
