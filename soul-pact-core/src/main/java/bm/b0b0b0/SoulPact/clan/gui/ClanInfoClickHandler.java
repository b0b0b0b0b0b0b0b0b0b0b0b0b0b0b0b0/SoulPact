package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanInfoViewSnapshot;
import bm.b0b0b0.SoulPact.clan.service.ClanLeaveService;
import bm.b0b0b0.SoulPact.clan.service.ClanMembershipService;
import bm.b0b0b0.SoulPact.clan.service.ClanWarAccessService;
import org.bukkit.entity.Player;

public final class ClanInfoClickHandler {

    private final ClanGuiOpenService guiOpenService;
    private final ClanMembershipService membershipService;
    private final ClanLeaveService leaveService;
    private final ClanWarAccessService warAccessService;

    public ClanInfoClickHandler(
            ClanGuiOpenService guiOpenService,
            ClanMembershipService membershipService,
            ClanLeaveService leaveService,
            ClanWarAccessService warAccessService
    ) {
        this.guiOpenService = guiOpenService;
        this.membershipService = membershipService;
        this.leaveService = leaveService;
        this.warAccessService = warAccessService;
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
        if (slot == menu.slotDeclareWar()) {
            warAccessService.handleInfoDeclareClick(player, menu.clanId(), menu.listPage());
            return;
        }
        int allyIndex = menu.allySlotIndex(slot);
        if (allyIndex >= 0 && allyIndex < menu.snapshot().allies().size()) {
            long allyClanId = menu.snapshot().allies().get(allyIndex).clanId();
            guiOpenService.openInfo(player, allyClanId, menu.listPage());
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
