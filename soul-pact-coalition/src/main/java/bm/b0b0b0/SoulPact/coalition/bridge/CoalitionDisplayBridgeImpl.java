package bm.b0b0b0.SoulPact.coalition.bridge;

import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayBridge;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayExtras;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionService;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<CoalitionDisplayExtras> alliesForInfo(long clanId) {
        return coalitionService.coalitionLineForList(clanId).thenCombine(
                coalitionService.alliesForInfo(clanId),
                CoalitionDisplayExtras::new
        );
    }
}
