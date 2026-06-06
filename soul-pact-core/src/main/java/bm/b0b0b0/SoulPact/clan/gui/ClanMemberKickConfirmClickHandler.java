package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanKickService;
import org.bukkit.entity.Player;

public final class ClanMemberKickConfirmClickHandler {

    private final ClanGuiOpenService guiOpenService;
    private final ClanKickService kickService;

    public ClanMemberKickConfirmClickHandler(
            ClanGuiOpenService guiOpenService,
            ClanKickService kickService
    ) {
        this.guiOpenService = guiOpenService;
        this.kickService = kickService;
    }

    public void handle(ClanMemberKickConfirmMenu menu, Player player, int slot) {
        if (slot == menu.config().denySlot()) {
            guiOpenService.openMemberDetail(
                    player,
                    menu.clanId(),
                    menu.targetId(),
                    menu.navigation()
            );
            return;
        }
        if (slot != menu.config().confirmSlot()) {
            return;
        }
        kickService.kick(player, menu.clanId(), menu.targetId()).thenAccept(kicked -> {
            if (!kicked) {
                return;
            }
            guiOpenService.openMembers(player, menu.navigation());
        });
    }
}
