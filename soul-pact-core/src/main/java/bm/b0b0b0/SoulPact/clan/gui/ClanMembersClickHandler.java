package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.entity.Player;

public final class ClanMembersClickHandler {

    private final ClanGuiOpenService guiOpenService;
    private final MessageService messageService;

    public ClanMembersClickHandler(ClanGuiOpenService guiOpenService, MessageService messageService) {
        this.guiOpenService = guiOpenService;
        this.messageService = messageService;
    }

    public void handle(ClanMembersMenu menu, Player player, int slot) {
        if (slot == menu.config().backSlot()) {
            navigateBack(menu.navigation(), player);
            return;
        }
        if (slot == menu.slotBanner()) {
            if (!menu.viewerIsLeader()) {
                messageService.send(player, "clan.banner.view-only");
                return;
            }
            guiOpenService.openBanner(player);
            return;
        }
        if (slot == menu.slotPrevious() && menu.membersPage().hasPrevious()) {
            guiOpenService.openMembers(player, menu.navigation().withMembersPage(menu.membersPage().page() - 1));
            return;
        }
        if (slot == menu.slotNext() && menu.membersPage().hasNext()) {
            guiOpenService.openMembers(player, menu.navigation().withMembersPage(menu.membersPage().page() + 1));
            return;
        }
        java.util.UUID memberId = menu.memberIdAtSlot(slot);
        if (memberId != null) {
            guiOpenService.openMemberDetail(player, menu.navigation().clanId(), memberId, menu.navigation());
        }
    }

    private void navigateBack(ClanMembersNav navigation, Player player) {
        if (navigation.backTarget() == ClanMembersNav.BackTarget.INFO) {
            guiOpenService.openInfo(player, navigation.clanId(), navigation.infoListPage());
            return;
        }
        guiOpenService.openProfile(player);
    }
}
