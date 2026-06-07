package bm.b0b0b0.SoulPact.bank.gui;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.platform.SoulPactClanGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class BankClanNavigation {

    private final SoulPactApi api;

    public BankClanNavigation(SoulPactApi api) {
        this.api = api;
    }

    public void openHub(Player player) {
        Runnable openAction = () -> {
            if (!player.isOnline()) {
                return;
            }
            try {
                SoulPactClanGui clanGui = resolveClanGui();
                if (clanGui != null) {
                    clanGui.openHub(player);
                    return;
                }
                player.performCommand("clan");
            } catch (Throwable throwable) {
                player.performCommand("clan");
            }
        };
        try {
            api.scheduler().runSyncLater(1L, openAction);
        } catch (Throwable throwable) {
            try {
                api.scheduler().runSync(openAction);
            } catch (Throwable syncFailure) {
                Bukkit.getScheduler().runTaskLater(
                        Bukkit.getPluginManager().getPlugin("SoulPact-Bank"),
                        openAction,
                        1L
                );
            }
        }
    }

    private SoulPactClanGui resolveClanGui() {
        try {
            SoulPactClanGui fromApi = api.clanGui();
            if (fromApi != null) {
                return fromApi;
            }
        } catch (Throwable ignored) {
        }
        RegisteredServiceProvider<SoulPactClanGui> provider =
                Bukkit.getServicesManager().getRegistration(SoulPactClanGui.class);
        return provider == null ? null : provider.getProvider();
    }
}
