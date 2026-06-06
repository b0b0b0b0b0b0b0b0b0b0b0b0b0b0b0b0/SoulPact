package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanMembershipService;
import org.bukkit.entity.Player;

public final class ClanRequestsClickHandler {

    private final ClanGuiOpenService guiOpenService;
    private final ClanMembershipService membershipService;

    public ClanRequestsClickHandler(
            ClanGuiOpenService guiOpenService,
            ClanMembershipService membershipService
    ) {
        this.guiOpenService = guiOpenService;
        this.membershipService = membershipService;
    }

    public void handle(ClanRequestsMenu menu, Player player, int slot) {
        var config = menu.config();
        if (slot == config.backSlot()) {
            guiOpenService.openProfile(player);
            return;
        }
        if (slot == config.acceptAllSlot() && !menu.snapshot().requests().isEmpty()) {
            membershipService.acceptAllRequests(player)
                    .thenAccept(ignored -> guiOpenService.openRequests(player));
            return;
        }
        if (slot == config.denyAllSlot() && !menu.snapshot().requests().isEmpty()) {
            membershipService.denyAllRequests(player)
                    .thenAccept(ignored -> guiOpenService.openRequests(player));
            return;
        }
        if (slot == config.blockAllSlot() && menu.snapshot().leaderControls() && !menu.snapshot().requests().isEmpty()) {
            membershipService.blockAllRequests(player)
                    .thenAccept(ignored -> guiOpenService.openRequests(player));
            return;
        }
        if (slot == config.toggleSlot() && menu.snapshot().leaderControls()) {
            membershipService.toggleJoinRequests(player)
                    .thenAccept(ignored -> guiOpenService.openRequests(player));
            return;
        }
        Long requestId = menu.requestIdAtSlot(slot);
        if (requestId != null) {
            guiOpenService.openRequestDetail(player, requestId);
        }
    }
}
