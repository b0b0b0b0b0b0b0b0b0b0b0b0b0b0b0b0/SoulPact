package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanHubSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiHubConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanHubMenuPopulator {

    private final GuiHubConfig guiHubConfig;
    private final GuiItemBuilder guiItemBuilder;

    public ClanHubMenuPopulator(GuiHubConfig guiHubConfig, GuiItemBuilder guiItemBuilder) {
        this.guiHubConfig = guiHubConfig;
        this.guiItemBuilder = guiItemBuilder;
    }

    public void populate(Inventory inventory, Player player, ClanHubSnapshot snapshot) {
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
}
