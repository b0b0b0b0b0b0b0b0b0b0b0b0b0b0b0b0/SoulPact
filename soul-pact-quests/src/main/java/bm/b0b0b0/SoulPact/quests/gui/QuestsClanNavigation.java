package bm.b0b0b0.SoulPact.quests.gui;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.platform.SoulPactClanGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class QuestsClanNavigation {

    private final SoulPactApi api;

    public QuestsClanNavigation(SoulPactApi api) {
        this.api = api;
    }

    public void openHub(Player player) {
        api.scheduler().runSyncLater(1L, () -> {
            if (!player.isOnline()) {
                return;
            }
            SoulPactClanGui clanGui = resolveClanGui();
            if (clanGui != null) {
                clanGui.openHub(player);
                return;
            }
            player.performCommand("clan");
        });
    }

    private SoulPactClanGui resolveClanGui() {
        SoulPactClanGui fromApi = api.clanGui();
        if (fromApi != null) {
            return fromApi;
        }
        RegisteredServiceProvider<SoulPactClanGui> provider =
                Bukkit.getServicesManager().getRegistration(SoulPactClanGui.class);
        return provider == null ? null : provider.getProvider();
    }
}
