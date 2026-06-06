package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanRequestDetailSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiRequestDetailConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanRequestDetailMenu implements InventoryHolder {

    private final GuiRequestDetailConfig guiRequestDetailConfig;
    private final ClanRequestDetailSnapshot snapshot;
    private final Inventory inventory;

    public ClanRequestDetailMenu(
            GuiRequestDetailConfig guiRequestDetailConfig,
            ClanRequestDetailMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanRequestDetailSnapshot snapshot
    ) {
        this.guiRequestDetailConfig = guiRequestDetailConfig;
        this.snapshot = snapshot;
        this.inventory = Bukkit.createInventory(
                this,
                guiRequestDetailConfig.size(),
                messageService.component(player, "clan.gui.requests.detail.title", Map.of(
                        "player", snapshot.playerName()
                ))
        );
        populator.populate(inventory, player, snapshot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiRequestDetailConfig config() {
        return guiRequestDetailConfig;
    }

    public ClanRequestDetailSnapshot snapshot() {
        return snapshot;
    }
}
