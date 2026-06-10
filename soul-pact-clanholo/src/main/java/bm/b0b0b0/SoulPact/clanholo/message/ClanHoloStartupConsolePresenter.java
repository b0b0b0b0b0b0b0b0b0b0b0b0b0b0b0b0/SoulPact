package bm.b0b0b0.SoulPact.clanholo.message;

import org.bukkit.plugin.java.JavaPlugin;

public final class ClanHoloStartupConsolePresenter {

    private final JavaPlugin plugin;
    private final ClanHoloMessages messages;

    public ClanHoloStartupConsolePresenter(JavaPlugin plugin, ClanHoloMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logCoreMissing() {
        plugin.getLogger().severe(messages.resolve("clanholo.startup.core-missing", java.util.Map.of()));
    }

    public void logRegistered(int hologramCount) {
        plugin.getLogger().info(messages.resolve("clanholo.startup.registered", java.util.Map.of(
                "count", String.valueOf(hologramCount)
        )));
    }
}
