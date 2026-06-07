package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.entity.Player;

public final class ClanSettingsClickHandler {

    private final ClanGuiOpenService guiOpenService;
    private final MessageService messageService;

    public ClanSettingsClickHandler(ClanGuiOpenService guiOpenService, MessageService messageService) {
        this.guiOpenService = guiOpenService;
        this.messageService = messageService;
    }

    public void handle(ClanSettingsMenu menu, Player player, int slot) {
        if (slot == menu.config().backSlot()) {
            guiOpenService.openHub(player);
            return;
        }
        if (slot == menu.config().bannerSlot()) {
            if (menu.snapshot().bannerItem() == null) {
                return;
            }
            if (!menu.snapshot().clan().leaderId().equals(player.getUniqueId())) {
                messageService.send(player, "clan.banner.view-only");
                return;
            }
            guiOpenService.openBanner(player);
            return;
        }
        String roleKey = menu.roleAtSlot(slot);
        if (roleKey == null) {
            return;
        }
        guiOpenService.openRoleSettings(player, menu.snapshot().clan().id(), roleKey);
    }
}
