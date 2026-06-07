package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryProvider;
import bm.b0b0b0.SoulPact.api.treasury.TreasuryOperationResult;
import bm.b0b0b0.SoulPact.land.economy.LandVaultGateway;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class BaseExpansionPaymentService {

    private final SoulPactApi api;
    private final LandVaultGateway vaultGateway;

    public BaseExpansionPaymentService(SoulPactApi api, LandVaultGateway vaultGateway) {
        this.api = api;
        this.vaultGateway = vaultGateway;
    }

    public boolean usesTreasury() {
        return resolveTreasury().isPresent();
    }

    public CompletableFuture<ExpansionPaymentResult> charge(Player player, long clanId, double amount) {
        Optional<ClanTreasuryApi> treasury = resolveTreasury();
        if (treasury.isPresent()) {
            return treasury.get()
                    .charge(clanId, player.getUniqueId(), amount, "base-expansion")
                    .thenApply(this::mapTreasuryResult);
        }
        if (!vaultGateway.available()) {
            return CompletableFuture.completedFuture(ExpansionPaymentResult.ECONOMY_UNAVAILABLE);
        }
        return api.scheduler().supplyAsync(() -> chargeLeader(player, amount));
    }

    private ExpansionPaymentResult chargeLeader(Player player, double amount) {
        if (!vaultGateway.has(player, amount)) {
            return ExpansionPaymentResult.LEADER_INSUFFICIENT;
        }
        if (!vaultGateway.withdraw(player, amount)) {
            return ExpansionPaymentResult.FAILED;
        }
        return ExpansionPaymentResult.SUCCESS;
    }

    private ExpansionPaymentResult mapTreasuryResult(TreasuryOperationResult result) {
        return switch (result) {
            case SUCCESS -> ExpansionPaymentResult.SUCCESS;
            case INSUFFICIENT_TREASURY_FUNDS -> ExpansionPaymentResult.TREASURY_INSUFFICIENT;
            case TREASURY_LOCKED -> ExpansionPaymentResult.TREASURY_LOCKED;
            case ECONOMY_UNAVAILABLE -> ExpansionPaymentResult.ECONOMY_UNAVAILABLE;
            case INVALID_AMOUNT -> ExpansionPaymentResult.FAILED;
            default -> ExpansionPaymentResult.FAILED;
        };
    }

    private Optional<ClanTreasuryApi> resolveTreasury() {
        return api.extensions()
                .find("bank")
                .filter(ClanTreasuryProvider.class::isInstance)
                .map(ClanTreasuryProvider.class::cast)
                .map(ClanTreasuryProvider::treasury);
    }
}
