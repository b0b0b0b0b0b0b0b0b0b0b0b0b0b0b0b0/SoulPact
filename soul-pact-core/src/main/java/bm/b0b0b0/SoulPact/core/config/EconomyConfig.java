package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.EconomySettings;
import bm.b0b0b0.SoulPact.core.integration.VaultIntegration;

public final class EconomyConfig {

    private final boolean economyDisabled;
    private final double createCostAmount;

    public EconomyConfig(EconomySettings settings) {
        this.economyDisabled = settings.economyDisabled;
        this.createCostAmount = settings.createCostAmount;
    }

    public boolean economyDisabled() {
        return economyDisabled;
    }

    public double createCostAmount() {
        return createCostAmount;
    }

    public CreateEconomyState resolveCreateState(VaultIntegration vaultIntegration) {
        if (economyDisabled) {
            return CreateEconomyState.ADMIN_DISABLED;
        }
        if (!vaultIntegration.available()) {
            return CreateEconomyState.VAULT_MISSING;
        }
        if (createCostAmount <= 0.0) {
            return CreateEconomyState.ADMIN_DISABLED;
        }
        return CreateEconomyState.ACTIVE;
    }

    public boolean shouldChargeCreate(VaultIntegration vaultIntegration) {
        return resolveCreateState(vaultIntegration) == CreateEconomyState.ACTIVE;
    }

    public boolean shouldWarnMissingVault(VaultIntegration vaultIntegration) {
        return !economyDisabled && !vaultIntegration.available();
    }
}
