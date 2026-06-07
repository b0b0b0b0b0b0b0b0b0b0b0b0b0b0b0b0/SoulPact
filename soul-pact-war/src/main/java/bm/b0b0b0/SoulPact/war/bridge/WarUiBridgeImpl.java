package bm.b0b0b0.SoulPact.war.bridge;

import bm.b0b0b0.SoulPact.api.war.ClanWarInfoExtras;
import bm.b0b0b0.SoulPact.api.war.ClanWarUiBridge;
import bm.b0b0b0.SoulPact.api.war.WarFlagBreakGate;
import bm.b0b0b0.SoulPact.war.gui.WarGuiService;
import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class WarUiBridgeImpl implements ClanWarUiBridge {

    private final ClanWarService warService;
    private final WarGuiService guiService;

    public WarUiBridgeImpl(ClanWarService warService, WarGuiService guiService) {
        this.warService = warService;
        this.guiService = guiService;
    }

    @Override
    public CompletableFuture<String> treasuryLineForList(long clanId) {
        return warService.treasuryLineForList(clanId);
    }

    @Override
    public CompletableFuture<ClanWarInfoExtras> enrichInfoView(Player viewer, long targetClanId) {
        return warService.enrichInfoView(viewer, targetClanId);
    }

    @Override
    public void openDeclareConfirm(Player player, long targetClanId, int listPage) {
        guiService.openDeclareConfirm(player, targetClanId, listPage);
    }

    @Override
    public void handleInfoDeclareClick(Player player, long targetClanId, int listPage) {
        openDeclareConfirm(player, targetClanId, listPage);
    }

    @Override
    public CompletableFuture<Integer> pendingCountForLeader(long defenderClanId) {
        return warService.pendingCountForLeader(defenderClanId);
    }

    @Override
    public void openPendingWars(Player player) {
        guiService.openPendingList(player);
    }

    @Override
    public void openWarHub(Player player) {
        guiService.openWarHub(player);
    }

    @Override
    public boolean available() {
        return true;
    }
}
