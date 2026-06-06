package bm.b0b0b0.SoulPact.clan.gui;

import org.bukkit.entity.Player;

public final class ClanSettingsClickHandler {

    private final ClanGuiOpenService guiOpenService;

    public ClanSettingsClickHandler(ClanGuiOpenService guiOpenService) {
        this.guiOpenService = guiOpenService;
    }

    public void handle(ClanSettingsMenu menu, Player player, int slot) {
        if (slot == menu.config().backSlot()) {
            guiOpenService.openHub(player);
            return;
        }
        String roleKey = menu.roleAtSlot(slot);
        if (roleKey == null) {
            return;
        }
        guiOpenService.openRoleSettings(player, menu.snapshot().clan().id(), roleKey);
    }
}
