package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanRolePermissionService;
import org.bukkit.entity.Player;

public final class ClanRoleSettingsClickHandler {

    private final ClanGuiOpenService guiOpenService;
    private final ClanRolePermissionService rolePermissionService;

    public ClanRoleSettingsClickHandler(
            ClanGuiOpenService guiOpenService,
            ClanRolePermissionService rolePermissionService
    ) {
        this.guiOpenService = guiOpenService;
        this.rolePermissionService = rolePermissionService;
    }

    public void handle(ClanRoleSettingsMenu menu, Player player, int slot) {
        if (slot == menu.config().backSlot()) {
            guiOpenService.openSettings(player);
            return;
        }
        String permission = menu.permissionAtSlot(slot);
        if (permission == null) {
            return;
        }
        rolePermissionService.toggle(
                player,
                menu.snapshot().clan().id(),
                menu.snapshot().roleKey(),
                permission
        ).thenAccept(changed -> {
            if (!changed) {
                return;
            }
            guiOpenService.openRoleSettings(
                    player,
                    menu.snapshot().clan().id(),
                    menu.snapshot().roleKey()
            );
        });
    }
}
