package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.Bukkit;

public final class SoulPactEvents {

    private SoulPactEvents() {
    }

    public static void fire(SoulPactEvent event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
