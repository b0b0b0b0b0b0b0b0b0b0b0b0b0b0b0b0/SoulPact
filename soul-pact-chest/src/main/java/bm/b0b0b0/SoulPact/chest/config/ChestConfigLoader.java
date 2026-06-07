package bm.b0b0b0.SoulPact.chest.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChestConfigLoader {

    private ChestConfigLoader() {
    }

    public static ChestConfig load(JavaPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        ChestPricingSettings pricing = new ChestPricingSettings(
                configuration.getDouble("pricing.base-cost", 1000.0D),
                configuration.getDouble("pricing.linear-step", 250.0D),
                configuration.getInt("pricing.tier-size", 9),
                configuration.getDouble("pricing.tier-multiplier", 1.12D),
                configuration.getDouble("pricing.max-cost", 75000.0D)
        );
        return new ChestConfig(
                configuration.getString("locale", "ru"),
                configuration.getString("fallback-locale", "en"),
                configuration.getInt("pages", 3),
                configuration.getInt("cells-per-page", 36),
                pricing,
                configuration.getInt("gui.rows", 6),
                configuration.getInt("gui.slots.buy-cell", 1),
                configuration.getInt("gui.slots.bank-link", 3),
                configuration.getInt("gui.slots.page-1", 5),
                configuration.getInt("gui.slots.page-2", 6),
                configuration.getInt("gui.slots.page-3", 7),
                configuration.getInt("gui.slots.back", 45),
                configuration.getInt("gui.slots.prev-page", 48),
                configuration.getInt("gui.slots.next-page", 50),
                parseMaterial(configuration.getString("gui.materials.filler"), Material.GRAY_STAINED_GLASS_PANE),
                parseMaterial(configuration.getString("gui.materials.barrier"), Material.BARRIER),
                parseMaterial(configuration.getString("gui.materials.buy"), Material.EMERALD),
                parseMaterial(configuration.getString("gui.materials.bank"), Material.GOLD_BLOCK),
                parseMaterial(configuration.getString("gui.materials.page-active"), Material.LIME_DYE),
                parseMaterial(configuration.getString("gui.materials.page-inactive"), Material.GRAY_DYE),
                parseMaterial(configuration.getString("gui.materials.back"), Material.ARROW),
                parseMaterial(configuration.getString("gui.materials.prev"), Material.ARROW),
                parseMaterial(configuration.getString("gui.materials.next"), Material.ARROW)
        );
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
