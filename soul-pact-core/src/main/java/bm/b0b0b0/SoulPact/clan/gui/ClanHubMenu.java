package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import bm.b0b0b0.SoulPact.clan.service.ClanHubModuleSlotLayout;
import bm.b0b0b0.SoulPact.clan.service.ClanHubSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiHubConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanHubMenu implements InventoryHolder {

    private final GuiHubConfig guiHubConfig;
    private final Inventory inventory;
    private final ClanHubModuleSlotLayout moduleLayout;
    private final boolean clanLeader;
    private final boolean inClan;

    public ClanHubMenu(
            GuiHubConfig guiHubConfig,
            ClanHubMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanHubSnapshot snapshot,
            ClanHubModuleSlotLayout moduleLayout
    ) {
        this.guiHubConfig = guiHubConfig;
        this.moduleLayout = moduleLayout;
        this.inClan = snapshot.inClan();
        this.clanLeader = snapshot.clanLeader();
        this.inventory = Bukkit.createInventory(
                this,
                guiHubConfig.size(),
                messageService.component(player, "clan.gui.hub.title")
        );
        populator.populate(inventory, player, snapshot, moduleLayout);
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

    public int slotCreate() {
        return guiHubConfig.createSlot();
    }

    public int slotHelp() {
        return guiHubConfig.helpSlot();
    }

    public boolean clanLeader() {
        return clanLeader;
    }

    public boolean inClan() {
        return inClan;
    }

    public Optional<SoulPactGuiExtension> extensionAtSlot(int slot) {
        return moduleLayout.extensionAt(slot);
    }
}
