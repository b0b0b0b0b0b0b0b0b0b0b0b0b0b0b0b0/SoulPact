package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import bm.b0b0b0.SoulPact.clan.service.ClanHubModuleSlotLayout;
import bm.b0b0b0.SoulPact.clan.service.ClanHubSnapshot;
import bm.b0b0b0.SoulPact.clan.service.ExtensionDisplayService;
import bm.b0b0b0.SoulPact.core.config.GuiHubConfig;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanHubMenuPopulator {

    private final GuiHubConfig guiHubConfig;
    private final GuiItemBuilder guiItemBuilder;
    private final ExtensionDisplayService extensionDisplayService;

    public ClanHubMenuPopulator(
            GuiHubConfig guiHubConfig,
            GuiItemBuilder guiItemBuilder,
            ExtensionDisplayService extensionDisplayService
    ) {
        this.guiHubConfig = guiHubConfig;
        this.guiItemBuilder = guiItemBuilder;
        this.extensionDisplayService = extensionDisplayService;
    }

    public void populate(
            Inventory inventory,
            Player player,
            ClanHubSnapshot snapshot,
            ClanHubModuleSlotLayout moduleLayout
    ) {
        var placeholders = snapshot.placeholders();
        ItemStack filler = guiItemBuilder.filler(player, guiHubConfig.fillerMaterial(), "clan.gui.hub.item.filler.name");
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
        inventory.setItem(guiHubConfig.overviewSlot(), guiItemBuilder.build(
                player,
                guiHubConfig.overviewMaterial(),
                "clan.gui.hub.item.overview.name",
                "clan.gui.hub.item.overview.lore",
                placeholders
        ));
        inventory.setItem(guiHubConfig.profileSlot(), guiItemBuilder.buildPlayerHead(
                player,
                player.getUniqueId(),
                player.getName(),
                "clan.gui.hub.item.profile.name",
                "clan.gui.hub.item.profile.lore",
                placeholders
        ));
        if (snapshot.clanLeader()) {
            inventory.setItem(guiHubConfig.settingsSlot(), guiItemBuilder.build(
                    player,
                    guiHubConfig.settingsMaterial(),
                    "clan.gui.hub.item.settings.name",
                    "clan.gui.hub.item.settings.lore",
                    placeholders
            ));
        }
        if (snapshot.inClan() && snapshot.bannerItem() != null) {
            inventory.setItem(guiHubConfig.bannerSlot(), guiItemBuilder.buildFromStack(
                    player,
                    snapshot.bannerItem(),
                    "clan.gui.hub.item.banner.name",
                    "clan.gui.hub.item.banner.lore",
                    placeholders
            ));
        }
        if (snapshot.inClan()) {
            populateModules(inventory, player, moduleLayout);
        }
        if (!snapshot.inClan()) {
            inventory.setItem(guiHubConfig.createSlot(), guiItemBuilder.build(
                    player,
                    guiHubConfig.createMaterial(),
                    "clan.gui.hub.item.create.name",
                    "clan.gui.hub.item.create.lore",
                    placeholders
            ));
        }
        inventory.setItem(guiHubConfig.helpSlot(), guiItemBuilder.build(
                player,
                guiHubConfig.helpMaterial(),
                "clan.gui.hub.item.help.name",
                "clan.gui.hub.item.help.lore",
                placeholders
        ));
    }

    private void populateModules(Inventory inventory, Player player, ClanHubModuleSlotLayout moduleLayout) {
        for (Map.Entry<Integer, SoulPactGuiExtension> entry : moduleLayout.bySlot().entrySet()) {
            SoulPactGuiExtension extension = entry.getValue();
            String displayName = extensionDisplayService.displayName(player, extension.id());
            List<String> lore = extensionDisplayService.lore(player, extension.id(), displayName);
            inventory.setItem(entry.getKey(), guiItemBuilder.buildNamed(
                    player,
                    guiHubConfig.moduleMaterial(),
                    "clan.gui.extensions.item.entry.name",
                    lore,
                    Map.of(
                            "id", extension.id(),
                            "display_name", displayName
                    )
            ));
        }
    }
}
