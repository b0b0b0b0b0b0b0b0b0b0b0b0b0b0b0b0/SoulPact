package bm.b0b0b0.SoulPact.war.config;

import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Locale;

public final class WarConfigLoader {

    private WarConfigLoader() {
    }

    public static WarConfig load(JavaPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        return new WarConfig(
                configuration.getString("locale", "ru"),
                configuration.getString("fallback-locale", "en"),
                configuration.getDouble("ransom-percent", 0.8D),
                configuration.getInt("capture-seconds", 60),
                configuration.getInt("gui.declare-confirm.rows", 3),
                configuration.getInt("gui.declare-confirm.slots.confirm", 11),
                configuration.getInt("gui.declare-confirm.slots.deny", 15),
                configuration.getInt("gui.pending-list.rows", 6),
                configuration.getInt("gui.pending-list.page-size", 45),
                configuration.getInt("gui.pending-list.slots.back", 49),
                configuration.getInt("gui.pending-detail.rows", 3),
                configuration.getInt("gui.pending-detail.slots.accept", 11),
                configuration.getInt("gui.pending-detail.slots.ransom", 13),
                configuration.getInt("gui.pending-detail.slots.back", 22),
                parseColor(configuration.getString("bossbar.pending-color"), BarColor.YELLOW),
                parseColor(configuration.getString("bossbar.active-color"), BarColor.RED),
                parseColor(configuration.getString("bossbar.capture-defending-color"), BarColor.RED),
                parseColor(configuration.getString("bossbar.capture-attacking-color"), BarColor.GREEN)
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
