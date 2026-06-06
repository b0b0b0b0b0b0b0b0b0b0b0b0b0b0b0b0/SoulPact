package bm.b0b0b0.SoulPact.core.integration;

import org.bukkit.Bukkit;

public final class EssentialsIntegration implements PluginIntegration {

    private boolean available;

    @Override
    public String id() {
        return "essentials";
    }

    @Override
    public String displayName() {
        return "EssentialsX";
    }

    @Override
    public void hook() {
        available = Bukkit.getPluginManager().getPlugin("Essentials") != null;
    }

    @Override
    public boolean available() {
        return available;
    }
}
