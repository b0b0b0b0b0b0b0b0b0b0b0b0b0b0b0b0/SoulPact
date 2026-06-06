package bm.b0b0b0.SoulPact.core.integration;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class SkinRestorerIntegration implements PluginIntegration {

    private boolean available;
    private SkinsRestorer skinsRestorer;

    @Override
    public String id() {
        return "skinsrestorer";
    }

    @Override
    public String displayName() {
        return "SkinsRestorer";
    }

    @Override
    public void hook() {
        available = false;
        skinsRestorer = null;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("SkinsRestorer");
        if (plugin == null || !plugin.isEnabled()) {
            return;
        }
        try {
            SkinsRestorer api = SkinsRestorerProvider.get();
            if (api == null) {
                return;
            }
            skinsRestorer = api;
            available = true;
        } catch (Throwable ignored) {
            available = false;
            skinsRestorer = null;
        }
    }

    @Override
    public boolean available() {
        return available;
    }

    public SkinsRestorer skinsRestorer() {
        return skinsRestorer;
    }
}
