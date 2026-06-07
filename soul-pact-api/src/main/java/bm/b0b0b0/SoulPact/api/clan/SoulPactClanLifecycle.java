package bm.b0b0b0.SoulPact.api.clan;

import java.util.concurrent.CompletableFuture;

public interface SoulPactClanLifecycle {

    CompletableFuture<Boolean> disbandByWarDefeat(long loserClanId);
}
