package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanExtensionsPage;
import bm.b0b0b0.SoulPact.core.config.GuiExtensionsConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanExtensionsMenu implements InventoryHolder {

    private final GuiExtensionsConfig guiExtensionsConfig;
    private final ClanExtensionsPage extensionsPage;
    private final Inventory inventory;

    public ClanExtensionsMenu(
            GuiExtensionsConfig guiExtensionsConfig,
            ClanExtensionsMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanExtensionsPage extensionsPage
    ) {
        this.guiExtensionsConfig = guiExtensionsConfig;
        this.extensionsPage = extensionsPage;
        this.inventory = Bukkit.createInventory(
                this,
                guiExtensionsConfig.size(),
                messageService.component(player, "clan.gui.extensions.title", java.util.Map.of(
                        "page", String.valueOf(extensionsPage.totalPages() == 0 ? 0 : extensionsPage.page() + 1),
                        "pages", String.valueOf(Math.max(extensionsPage.totalPages(), 1)),
                        "count", String.valueOf(extensionsPage.totalExtensions())
                ))
        );
        populator.populate(inventory, player, extensionsPage);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ClanExtensionsPage extensionsPage() {
        return extensionsPage;
    }

    public int slotPrevious() {
        return guiExtensionsConfig.previousSlot();
    }

    public int slotBack() {
        return guiExtensionsConfig.backSlot();
    }

    public int slotNext() {
        return guiExtensionsConfig.nextSlot();
    }
}
