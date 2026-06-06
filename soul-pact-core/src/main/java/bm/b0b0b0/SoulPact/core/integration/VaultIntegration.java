package bm.b0b0b0.SoulPact.core.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultIntegration implements PluginIntegration {

    private Economy economy;

    @Override
    public String id() {
        return "vault";
    }

    @Override
    public String displayName() {
        return "Vault";
    }

    @Override
    public void hook() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            economy = null;
            return;
        }
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        economy = provider == null ? null : provider.getProvider();
    }

    @Override
    public boolean available() {
        return economy != null;
    }

    public Economy economy() {
        return economy;
    }
}
