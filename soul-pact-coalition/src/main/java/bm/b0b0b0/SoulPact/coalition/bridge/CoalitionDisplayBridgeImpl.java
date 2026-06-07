package bm.b0b0b0.SoulPact.coalition.bridge;

import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayBridge;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayExtras;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionService;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class CoalitionDisplayBridgeImpl implements CoalitionDisplayBridge {

    private final CoalitionService coalitionService;

    public CoalitionDisplayBridgeImpl(CoalitionService coalitionService) {
        this.coalitionService = coalitionService;
    }

    @Override
    public CompletableFuture<String> coalitionLineForList(long clanId) {
        return coalitionService.coalitionLineForList(clanId);
    }

    @Override
    public CompletableFuture<CoalitionDisplayExtras> enrichInfoView(Player viewer, long targetClanId) {
        return coalitionService.enrichInfoView(viewer, targetClanId);
    }

    @Override
    public void handleInfoInviteClick(Player player, long targetClanId, int listPage) {
        coalitionService.handleInfoInviteClick(player, targetClanId, listPage);
    }
}
