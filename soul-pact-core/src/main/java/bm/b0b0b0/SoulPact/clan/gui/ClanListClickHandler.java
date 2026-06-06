package bm.b0b0b0.SoulPact.clan.gui;

import org.bukkit.entity.Player;

public final class ClanListClickHandler {

    private final ClanGuiOpenService guiOpenService;

    public ClanListClickHandler(ClanGuiOpenService guiOpenService) {
        this.guiOpenService = guiOpenService;
    }

    public void handle(ClanListMenu menu, Player player, int slot) {
        Long clanId = menu.clanIdAtSlot(slot);
        if (clanId != null) {
            guiOpenService.openInfo(player, clanId, menu.listPage().page());
            return;
        }
        if (slot == menu.slotBack()) {
            guiOpenService.openHub(player);
            return;
        }
        if (slot == menu.slotPrevious() && menu.listPage().hasPrevious()) {
            guiOpenService.openList(player, menu.listPage().page() - 1);
            return;
        }
        if (slot == menu.slotNext() && menu.listPage().hasNext()) {
            guiOpenService.openList(player, menu.listPage().page() + 1);
        }
    }
}
