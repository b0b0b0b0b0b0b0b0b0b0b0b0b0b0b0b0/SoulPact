package bm.b0b0b0.SoulPact.core.api;

import bm.b0b0b0.SoulPact.api.clan.SoulPactClanLifecycle;
import bm.b0b0b0.SoulPact.clan.service.ClanDisbandService;
import java.util.concurrent.CompletableFuture;

public final class SoulPactClanLifecycleImpl implements SoulPactClanLifecycle {

    private final ClanDisbandService disbandService;

    public SoulPactClanLifecycleImpl(ClanDisbandService disbandService) {
        this.disbandService = disbandService;
    }

    @Override
    public CompletableFuture<Boolean> disbandByWarDefeat(long loserClanId) {
        return disbandService.disbandByWarDefeat(loserClanId);
    }
}
