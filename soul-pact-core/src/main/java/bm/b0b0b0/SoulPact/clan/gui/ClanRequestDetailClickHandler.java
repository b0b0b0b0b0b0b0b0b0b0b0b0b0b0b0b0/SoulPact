package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanMembershipService;
import org.bukkit.entity.Player;

public final class ClanRequestDetailClickHandler {

    private final ClanGuiOpenService guiOpenService;
    private final ClanMembershipService membershipService;

    public ClanRequestDetailClickHandler(
            ClanGuiOpenService guiOpenService,
            ClanMembershipService membershipService
    ) {
        this.guiOpenService = guiOpenService;
        this.membershipService = membershipService;
    }

    public void handle(ClanRequestDetailMenu menu, Player player, int slot) {
        var config = menu.config();
        long requestId = menu.snapshot().request().id();
        if (slot == config.backSlot()) {
            guiOpenService.openRequests(player);
            return;
        }
        if (slot == config.acceptSlot()) {
            membershipService.acceptRequest(player, requestId)
                    .thenAccept(ignored -> guiOpenService.openRequests(player));
            return;
        }
        if (slot == config.denySlot()) {
            membershipService.denyRequest(player, requestId)
                    .thenAccept(ignored -> guiOpenService.openRequests(player));
            return;
        }
        if (slot == config.blockSlot() && menu.snapshot().leaderControls()) {
            membershipService.blockRequest(player, requestId)
                    .thenAccept(ignored -> guiOpenService.openRequests(player));
        }
    }
}
