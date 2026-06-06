package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanLeaveService;
import org.bukkit.entity.Player;

public final class ClanProfileClickHandler {

    private final ClanCreateChatPrompt createChatPrompt;
    private final ClanGuiOpenService guiOpenService;
    private final ClanLeaveService leaveService;

    public ClanProfileClickHandler(
            ClanCreateChatPrompt createChatPrompt,
            ClanGuiOpenService guiOpenService,
            ClanLeaveService leaveService
    ) {
        this.createChatPrompt = createChatPrompt;
        this.guiOpenService = guiOpenService;
        this.leaveService = leaveService;
    }

    public void handle(ClanProfileMenu menu, Player player, int slot) {
        if (menu.empty() && slot == menu.slotEmptyCreate()) {
            createChatPrompt.open(player);
            return;
        }
        if (menu.empty() && slot == menu.slotEmptyList()) {
            guiOpenService.openList(player, 0);
            return;
        }
        if (!menu.empty() && menu.leaderView() && slot == menu.slotRequests()) {
            guiOpenService.openRequests(player);
            return;
        }
        if (!menu.empty() && menu.memberCanLeave() && slot == menu.slotLeave()) {
            player.closeInventory();
            leaveService.leave(player);
            return;
        }
        if (!menu.empty() && slot == menu.slotMembers()) {
            guiOpenService.openMembers(player, ClanMembersNav.fromProfile(menu.clanId()));
            return;
        }
        if (slot == menu.slotBack()) {
            guiOpenService.openHub(player);
        }
    }
}
