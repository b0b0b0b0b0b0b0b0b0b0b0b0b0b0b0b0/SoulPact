package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import org.bukkit.entity.Player;

public final class WarGuiClickHandler {

    private final WarGuiService guiService;
    private final ClanWarService warService;
    private final SoulPactApi api;

    public WarGuiClickHandler(WarGuiService guiService, ClanWarService warService, SoulPactApi api) {
        this.guiService = guiService;
        this.warService = warService;
        this.api = api;
    }

    public void handleDeclareConfirm(WarDeclareConfirmMenu menu, Player player, int slot) {
        if (slot == menu.denySlot()) {
            api.clanGui().openInfo(player, menu.targetClanId(), menu.listPage());
            return;
        }
        if (slot != menu.confirmSlot()) {
            return;
        }
        player.closeInventory();
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return;
            }
            warService.declareWar(player, clanOptional.get().id(), menu.targetClanId());
        });
    }

    public void handlePendingList(WarPendingListMenu menu, Player player, int slot) {
        if (slot == menu.backSlot()) {
            player.closeInventory();
            return;
        }
        Long declarationId = menu.declarationIdsBySlot().get(slot);
        if (declarationId == null) {
            return;
        }
        warService.listPendingForDefenderLeader(player).thenAccept(declarations -> {
            WarDeclarationRecord declaration = declarations.stream()
                    .filter(record -> record.id() == declarationId)
                    .findFirst()
                    .orElse(null);
            if (declaration == null) {
                return;
            }
            guiService.openPendingDetail(player, declaration);
        });
    }

    public void handlePendingDetail(WarPendingDetailMenu menu, Player player, int slot) {
        if (slot == menu.backSlot()) {
            guiService.reopenPendingList(player);
            return;
        }
        if (slot == menu.acceptSlot()) {
            player.closeInventory();
            warService.acceptWar(player, menu.declaration().id());
            return;
        }
        if (slot == menu.ransomSlot()) {
            player.closeInventory();
            warService.payRansom(player, menu.declaration().id());
        }
    }

    public void handleWarHub(WarHubMenu menu, Player player, int slot) {
        if (slot == menu.backSlot()) {
            api.clanGui().openProfile(player);
            return;
        }
        if (slot == menu.pendingSlot() && menu.viewerCanRespond() && menu.pendingCount() > 0) {
            guiService.openPendingList(player);
        }
    }
}
