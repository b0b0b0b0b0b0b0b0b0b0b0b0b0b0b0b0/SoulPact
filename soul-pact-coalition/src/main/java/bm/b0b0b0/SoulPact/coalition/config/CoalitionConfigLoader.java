package bm.b0b0b0.SoulPact.coalition.config;

import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Locale;

public final class CoalitionConfigLoader {

    private CoalitionConfigLoader() {
    }

    public static CoalitionConfig load(JavaPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        return new CoalitionConfig(
                configuration.getString("locale", "ru"),
                configuration.getString("fallback-locale", "en"),
                configuration.getInt("max-members", 3),
                configuration.getDouble("treasury.war-share-percent", 0.25D),
                configuration.getDouble("treasury.capture-share-percent", 0.25D),
                configuration.getDouble("treasury.pool-share-percent", 0.50D),
                configuration.getInt("gui.hub.rows", 3),
                configuration.getInt("gui.hub.slots.member-start", 10),
                configuration.getInt("gui.hub.slots.invite", 16),
                configuration.getInt("gui.hub.slots.leave", 22),
                parseColor(configuration.getString("bossbar.declared-color"), BarColor.YELLOW),
                parseColor(configuration.getString("bossbar.active-color"), BarColor.RED),
                parseColor(configuration.getString("bossbar.capture-color"), BarColor.RED)
        );
    }

    private static BarColor parseColor(String rawValue, BarColor fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        try {
            return BarColor.valueOf(rawValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }
}
