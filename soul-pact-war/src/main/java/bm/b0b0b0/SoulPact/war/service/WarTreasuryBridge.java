package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryProvider;
import bm.b0b0b0.SoulPact.api.treasury.TreasuryOperationResult;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class WarTreasuryBridge {

    private final SoulPactApi api;

    public WarTreasuryBridge(SoulPactApi api) {
        this.api = api;
    }

    public CompletableFuture<Boolean> lockTreasury(long clanId) {
        return resolveTreasury()
                .map(treasury -> treasury.setLocked(clanId, true))
                .orElseGet(() -> CompletableFuture.completedFuture(false));
    }

    public CompletableFuture<Boolean> unlockTreasury(long clanId) {
        return resolveTreasury()
                .map(treasury -> treasury.setLocked(clanId, false))
                .orElseGet(() -> CompletableFuture.completedFuture(false));
    }

    public CompletableFuture<Boolean> unlockTreasuryAfterDecision(long clanId) {
        return resolveTreasury()
                .map(treasury -> treasury.setLocked(clanId, false))
                .orElseGet(() -> CompletableFuture.completedFuture(false));
    }

    public CompletableFuture<Double> balance(long clanId) {
        return resolveTreasury()
                .map(treasury -> treasury.balance(clanId))
                .orElseGet(() -> CompletableFuture.completedFuture(0.0D));
    }

    public CompletableFuture<TreasuryOperationResult> seize(long fromClanId, long toClanId, double amount) {
        return resolveTreasury()
                .map(treasury -> treasury.seize(fromClanId, toClanId, amount, "war-ransom"))
                .orElseGet(() -> CompletableFuture.completedFuture(TreasuryOperationResult.ECONOMY_UNAVAILABLE));
    }

    public CompletableFuture<TreasuryOperationResult> transferAll(long fromClanId, long toClanId) {
        return resolveTreasury()
                .map(treasury -> treasury.transferAll(fromClanId, toClanId, "war-victory"))
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
