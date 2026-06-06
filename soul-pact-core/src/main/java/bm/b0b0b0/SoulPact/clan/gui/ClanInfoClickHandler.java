package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanInfoViewSnapshot;
import bm.b0b0b0.SoulPact.clan.service.ClanLeaveService;
import bm.b0b0b0.SoulPact.clan.service.ClanMembershipService;
import org.bukkit.entity.Player;

public final class ClanInfoClickHandler {

    private final ClanGuiOpenService guiOpenService;
    private final ClanMembershipService membershipService;
    private final ClanLeaveService leaveService;

    public ClanInfoClickHandler(
            ClanGuiOpenService guiOpenService,
            ClanMembershipService membershipService,
            ClanLeaveService leaveService
    ) {
        this.guiOpenService = guiOpenService;
        this.membershipService = membershipService;
        this.leaveService = leaveService;
    }

    public void handle(ClanInfoMenu menu, Player player, int slot) {
        if (slot == menu.slotBack()) {
            guiOpenService.openList(player, menu.listPage());
            return;
        }
        if (slot == menu.slotMembers()) {
            guiOpenService.openMembers(player, ClanMembersNav.fromInfo(menu.clanId(), menu.listPage()));
            return;
        }
        if (slot != menu.slotAction()) {
            return;
        }
        if (menu.snapshot().joinClosedForViewer()) {
            return;
        }
        player.closeInventory();
        if (menu.snapshot().canJoin()) {
            membershipService.submitJoinRequest(player, "#" + menu.clanId());
            return;
        }
        if (menu.snapshot().canLeave()) {
            leaveService.leave(player);
        }
    }
}
