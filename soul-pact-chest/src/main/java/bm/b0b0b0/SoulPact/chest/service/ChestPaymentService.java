package bm.b0b0b0.SoulPact.chest.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryProvider;
import bm.b0b0b0.SoulPact.api.treasury.TreasuryOperationResult;
import bm.b0b0b0.SoulPact.chest.economy.ChestVaultGateway;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ChestPaymentService {

    private final SoulPactApi api;
    private final ChestVaultGateway vaultGateway;

    public ChestPaymentService(SoulPactApi api, ChestVaultGateway vaultGateway) {
        this.api = api;
        this.vaultGateway = vaultGateway;
    }

    public boolean usesTreasury() {
        return resolveTreasury().isPresent();
    }

    public CompletableFuture<ChestPaymentResult> charge(Player player, long clanId, double amount) {
        Optional<ClanTreasuryApi> treasury = resolveTreasury();
        if (treasury.isPresent()) {
            return treasury.get()
                    .charge(clanId, player.getUniqueId(), amount, "clan-chest-cell")
                    .thenApply(this::mapTreasuryResult);
        }
        if (!vaultGateway.available()) {
            return CompletableFuture.completedFuture(ChestPaymentResult.ECONOMY_UNAVAILABLE);
        }
        return api.scheduler().supplyAsync(() -> chargeLeader(player, amount));
    }

    private ChestPaymentResult chargeLeader(Player player, double amount) {
        if (!vaultGateway.has(player, amount)) {
            return ChestPaymentResult.LEADER_INSUFFICIENT;
        }
        if (!vaultGateway.withdraw(player, amount)) {
            return ChestPaymentResult.FAILED;
        }
        return ChestPaymentResult.SUCCESS;
    }

    private ChestPaymentResult mapTreasuryResult(TreasuryOperationResult result) {
        return switch (result) {
            case SUCCESS -> ChestPaymentResult.SUCCESS;
            case INSUFFICIENT_TREASURY_FUNDS -> ChestPaymentResult.TREASURY_INSUFFICIENT;
            case TREASURY_LOCKED -> ChestPaymentResult.TREASURY_LOCKED;
            case ECONOMY_UNAVAILABLE -> ChestPaymentResult.ECONOMY_UNAVAILABLE;
            case INVALID_AMOUNT -> ChestPaymentResult.FAILED;
            default -> ChestPaymentResult.FAILED;
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
