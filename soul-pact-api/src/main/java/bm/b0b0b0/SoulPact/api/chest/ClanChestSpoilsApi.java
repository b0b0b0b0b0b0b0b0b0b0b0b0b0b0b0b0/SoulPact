package bm.b0b0b0.SoulPact.api.chest;

import java.util.concurrent.CompletableFuture;

public interface ClanChestSpoilsApi {

    CompletableFuture<Boolean> transferWarSpoils(long defeatedClanId, long winnerClanId);
}
