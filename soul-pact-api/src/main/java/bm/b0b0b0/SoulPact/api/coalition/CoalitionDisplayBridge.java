package bm.b0b0b0.SoulPact.api.coalition;

import java.util.concurrent.CompletableFuture;

public interface CoalitionDisplayBridge {

    CompletableFuture<String> coalitionLineForList(long clanId);

    CompletableFuture<CoalitionDisplayExtras> alliesForInfo(long clanId);
}
