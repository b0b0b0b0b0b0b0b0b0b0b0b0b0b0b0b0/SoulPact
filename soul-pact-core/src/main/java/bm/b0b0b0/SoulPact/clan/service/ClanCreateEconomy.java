package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.core.config.EconomyConfig;
import bm.b0b0b0.SoulPact.core.integration.VaultIntegration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public final class ClanCreateEconomy {

    public enum ChargeResult {
        NOT_REQUIRED,
        CHARGED,
        INSUFFICIENT_FUNDS,
        FAILED
    }

    private final EconomyConfig economyConfig;
    private final VaultIntegration vaultIntegration;

    public ClanCreateEconomy(EconomyConfig economyConfig, VaultIntegration vaultIntegration) {
        this.economyConfig = economyConfig;
        this.vaultIntegration = vaultIntegration;
    }

    public ChargeResult chargeCreate(Player player) {
        if (!economyConfig.shouldChargeCreate(vaultIntegration)) {
            return ChargeResult.NOT_REQUIRED;
        }
        Economy economy = vaultIntegration.economy();
        double cost = economyConfig.createCostAmount();
        if (!economy.has(player, cost)) {
            return ChargeResult.INSUFFICIENT_FUNDS;
        }
        return economy.withdrawPlayer(player, cost).transactionSuccess()
                ? ChargeResult.CHARGED
                : ChargeResult.FAILED;
    }

    public double createCostAmount() {
        return economyConfig.createCostAmount();
    }
}
