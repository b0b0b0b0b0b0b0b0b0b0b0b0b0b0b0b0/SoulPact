package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanHubSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiHubConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanHubMenu implements InventoryHolder {

    private final GuiHubConfig guiHubConfig;
    private final Inventory inventory;

    public ClanHubMenu(
            GuiHubConfig guiHubConfig,
            ClanHubMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanHubSnapshot snapshot
    ) {
        this.guiHubConfig = guiHubConfig;
        this.inventory = Bukkit.createInventory(
                this,
                guiHubConfig.size(),
                messageService.component(player, "clan.gui.hub.title")
        );
        populator.populate(inventory, player, snapshot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiHubConfig config() {
        return guiHubConfig;
    }

    public int slotOverview() {
        return guiHubConfig.overviewSlot();
    }

    public int slotProfile() {
        return guiHubConfig.profileSlot();
    }

    public int slotSettings() {
        return guiHubConfig.settingsSlot();
    }

    public int slotBanner() {
        return guiHubConfig.bannerSlot();
    }

    public int slotCreate() {
        return guiHubConfig.createSlot();
    }

    public int slotHelp() {
        return guiHubConfig.helpSlot();
    }
}
