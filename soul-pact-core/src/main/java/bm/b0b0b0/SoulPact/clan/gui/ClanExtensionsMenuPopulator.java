package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import bm.b0b0b0.SoulPact.clan.service.ClanExtensionsPage;
import bm.b0b0b0.SoulPact.clan.service.ExtensionDisplayService;
import bm.b0b0b0.SoulPact.core.config.GuiExtensionsConfig;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanExtensionsMenuPopulator {

    private final GuiExtensionsConfig guiExtensionsConfig;
    private final GuiItemBuilder guiItemBuilder;
    private final ExtensionDisplayService extensionDisplayService;

    public ClanExtensionsMenuPopulator(
            GuiExtensionsConfig guiExtensionsConfig,
            GuiItemBuilder guiItemBuilder,
            ExtensionDisplayService extensionDisplayService
    ) {
        this.guiExtensionsConfig = guiExtensionsConfig;
        this.guiItemBuilder = guiItemBuilder;
        this.extensionDisplayService = extensionDisplayService;
    }

    public void populate(Inventory inventory, Player player, ClanExtensionsPage extensionsPage) {
        fillBackground(inventory, player);
        if (extensionsPage.totalExtensions() == 0) {
            inventory.setItem(22, guiItemBuilder.build(
                    player,
                    guiExtensionsConfig.emptyMaterial(),
                    "clan.gui.extensions.item.empty.name",
                    "clan.gui.extensions.item.empty.lore"
            ));
            placeNavigation(inventory, player, extensionsPage);
            return;
        }
        int index = 0;
        for (SoulPactExtension extension : extensionsPage.extensions()) {
            if (index >= guiExtensionsConfig.pageSize()) {
                break;
            }
            inventory.setItem(guiExtensionsConfig.contentSlot(index), buildEntryItem(player, extension));
            index++;
        }
        placeNavigation(inventory, player, extensionsPage);
    }

    private ItemStack buildEntryItem(Player player, SoulPactExtension extension) {
        String displayName = extensionDisplayService.displayName(player, extension.id());
        List<String> lore = extensionDisplayService.lore(player, extension.id(), displayName);
        return guiItemBuilder.buildNamed(
                player,
                guiExtensionsConfig.entryMaterial(),
                "clan.gui.extensions.item.entry.name",
                lore,
                Map.of(
                        "id", extension.id(),
                        "display_name", displayName
                )
        );
    }

    private void placeNavigation(Inventory inventory, Player player, ClanExtensionsPage extensionsPage) {
        inventory.setItem(guiExtensionsConfig.previousSlot(), buildPageArrow(
                player,
                extensionsPage.hasPrevious(),
                "clan.gui.extensions.item.previous.name",
                "clan.gui.extensions.item.previous.lore"
        ));
        inventory.setItem(guiExtensionsConfig.backSlot(), guiItemBuilder.build(
                player,
                guiExtensionsConfig.backMaterial(),
                "clan.gui.extensions.item.back.name",
                "clan.gui.extensions.item.back.lore"
        ));
        inventory.setItem(guiExtensionsConfig.nextSlot(), buildPageArrow(
                player,
                extensionsPage.hasNext(),
                "clan.gui.extensions.item.next.name",
                "clan.gui.extensions.item.next.lore"
        ));
    }

    private ItemStack buildPageArrow(Player player, boolean enabled, String nameKey, String loreKey) {
        return guiItemBuilder.build(
                player,
                enabled ? guiExtensionsConfig.pageArrowMaterial() : guiExtensionsConfig.pageArrowDisabledMaterial(),
                nameKey,
                loreKey
        );
    }

    private void fillBackground(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                guiExtensionsConfig.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }
}
