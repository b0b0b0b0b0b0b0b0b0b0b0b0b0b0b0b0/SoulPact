package bm.b0b0b0.SoulPact.clan.gui;

import org.bukkit.entity.Player;

public final class ClanExtensionsClickHandler {

    private final ClanGuiOpenService guiOpenService;

    public ClanExtensionsClickHandler(ClanGuiOpenService guiOpenService) {
        this.guiOpenService = guiOpenService;
    }

    public void handle(ClanExtensionsMenu menu, Player player, int slot) {
        if (slot == menu.slotBack()) {
            guiOpenService.openHub(player);
            return;
        }
        if (slot == menu.slotPrevious() && menu.extensionsPage().hasPrevious()) {
            guiOpenService.openExtensions(player, menu.extensionsPage().page() - 1);
            return;
        }
        if (slot == menu.slotNext() && menu.extensionsPage().hasNext()) {
            guiOpenService.openExtensions(player, menu.extensionsPage().page() + 1);
        }
    }
}
