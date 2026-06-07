package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanLeaveService;
import bm.b0b0b0.SoulPact.clan.service.ClanWarAccessService;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.entity.Player;

public final class ClanProfileClickHandler {

    private final ClanCreateChatPrompt createChatPrompt;
    private final ClanGuiOpenService guiOpenService;
    private final ClanLeaveService leaveService;
    private final MessageService messageService;
    private final ClanWarAccessService warAccessService;

    public ClanProfileClickHandler(
            ClanCreateChatPrompt createChatPrompt,
            ClanGuiOpenService guiOpenService,
            ClanLeaveService leaveService,
            MessageService messageService,
            ClanWarAccessService warAccessService
    ) {
        this.createChatPrompt = createChatPrompt;
        this.guiOpenService = guiOpenService;
        this.leaveService = leaveService;
        this.messageService = messageService;
        this.warAccessService = warAccessService;
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
        if (!menu.empty() && slot == menu.slotBanner()) {
            handleBannerClick(menu, player);
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
        if (!menu.empty() && warAccessService.available() && slot == menu.slotWar()) {
            warAccessService.openWarHub(player);
            return;
        }
        if (slot == menu.slotBack()) {
            guiOpenService.openHub(player);
        }
    }

    private void handleBannerClick(ClanProfileMenu menu, Player player) {
        if (!menu.viewerIsLeader()) {
            messageService.send(player, "clan.banner.view-only");
            return;
        }
        guiOpenService.openBanner(player);
    }
}
