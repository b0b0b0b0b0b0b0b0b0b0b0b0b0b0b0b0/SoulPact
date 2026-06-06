package bm.b0b0b0.SoulPact.core.integration;

import org.bukkit.Bukkit;

public final class PlaceholderApiIntegration implements PluginIntegration {

    private boolean available;

    @Override
    public String id() {
        return "placeholderapi";
    }

    @Override
    public String displayName() {
        return "PlaceholderAPI";
    }

    @Override
    public void hook() {
        available = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    @Override
    public boolean available() {
        return available;
    }
}
