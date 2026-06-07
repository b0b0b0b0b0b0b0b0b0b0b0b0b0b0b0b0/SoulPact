package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.chest.ClanChestSpoilsApi;
import bm.b0b0b0.SoulPact.api.chest.ClanChestSpoilsProvider;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class WarSpoilsBridge {

    private final SoulPactApi api;

    public WarSpoilsBridge(SoulPactApi api) {
        this.api = api;
    }

    public CompletableFuture<Boolean> transferWarSpoils(long defeatedClanId, long winnerClanId) {
        return resolveSpoils()
                .map(spoils -> spoils.transferWarSpoils(defeatedClanId, winnerClanId))
                .orElseGet(() -> CompletableFuture.completedFuture(true));
    }

    private Optional<ClanChestSpoilsApi> resolveSpoils() {
        return api.extensions()
                .find("chest")
                .filter(ClanChestSpoilsProvider.class::isInstance)
                .map(ClanChestSpoilsProvider.class::cast)
                .map(ClanChestSpoilsProvider::spoils);
    }
}
