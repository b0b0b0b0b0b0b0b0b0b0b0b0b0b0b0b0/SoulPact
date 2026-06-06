package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.model.ClanMemberManagementAction;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberManagementService;
import org.bukkit.entity.Player;

public final class ClanMemberDetailClickHandler {

    private final ClanGuiOpenService guiOpenService;
    private final ClanMemberManagementService memberManagementService;

    public ClanMemberDetailClickHandler(
            ClanGuiOpenService guiOpenService,
            ClanMemberManagementService memberManagementService
    ) {
        this.guiOpenService = guiOpenService;
        this.memberManagementService = memberManagementService;
    }

    public void handle(ClanMemberDetailMenu menu, Player player, int slot) {
        if (slot == menu.config().backSlot()) {
            guiOpenService.openMembers(player, menu.navigation());
            return;
        }
        ClanMemberManagementAction action = menu.actionAtSlot(slot);
        if (action == null) {
            return;
        }
        if (action.kind() == ClanMemberManagementAction.Kind.TRANSFER) {
            memberManagementService.transferLeadership(
                    player,
                    menu.navigation().clanId(),
                    menu.snapshot().member().playerId()
            ).thenAccept(ignored -> reopen(player, menu));
            return;
        }
        if (action.kind() == ClanMemberManagementAction.Kind.KICK) {
            guiOpenService.openMemberKickConfirm(
                    player,
                    menu.navigation().clanId(),
                    menu.snapshot().member().playerId(),
                    menu.snapshot().playerName(),
                    menu.navigation()
            );
            return;
        }
        memberManagementService.setRole(
                player,
                menu.navigation().clanId(),
                menu.snapshot().member().playerId(),
                action.roleKey()
        ).thenAccept(ignored -> reopen(player, menu));
    }

    private void reopen(Player player, ClanMemberDetailMenu menu) {
        guiOpenService.openMemberDetail(
                player,
                menu.navigation().clanId(),
                menu.snapshot().member().playerId(),
                menu.navigation()
        );
    }
}
