package bm.b0b0b0.SoulPact.coalition.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryProvider;
import bm.b0b0b0.SoulPact.api.treasury.TreasuryOperationResult;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class CoalitionTreasuryBridge {

    private final SoulPactApi api;

    public CoalitionTreasuryBridge(SoulPactApi api) {
        this.api = api;
    }

    public CompletableFuture<Double> balance(long clanId) {
        return resolveTreasury()
                .map(treasury -> treasury.balance(clanId))
                .orElseGet(() -> CompletableFuture.completedFuture(0.0D));
    }

    public CompletableFuture<TreasuryOperationResult> seize(long fromClanId, long toClanId, double amount, String note) {
        return resolveTreasury()
                .map(treasury -> treasury.seize(fromClanId, toClanId, amount, note))
                .orElseGet(() -> CompletableFuture.completedFuture(TreasuryOperationResult.ECONOMY_UNAVAILABLE));
    }

    public CompletableFuture<TreasuryOperationResult> transferAll(long fromClanId, long toClanId, String note) {
        return resolveTreasury()
                .map(treasury -> treasury.transferAll(fromClanId, toClanId, note))
                .orElseGet(() -> CompletableFuture.completedFuture(TreasuryOperationResult.ECONOMY_UNAVAILABLE));
    }

    private Optional<ClanTreasuryApi> resolveTreasury() {
        return api.extensions()
                .find("bank")
                .filter(ClanTreasuryProvider.class::isInstance)
                .map(ClanTreasuryProvider.class::cast)
                .map(ClanTreasuryProvider::treasury);
    }
}
