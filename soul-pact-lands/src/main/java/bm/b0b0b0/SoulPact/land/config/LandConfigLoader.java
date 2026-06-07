package bm.b0b0b0.SoulPact.land.config;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class LandConfigLoader {

    private static final List<String> DEFAULT_BORDER_COLORS = List.of(
            "RED_WOOL",
            "ORANGE_WOOL",
            "YELLOW_WOOL",
            "LIME_WOOL",
            "GREEN_WOOL",
            "CYAN_WOOL",
            "LIGHT_BLUE_WOOL",
            "BLUE_WOOL",
            "PURPLE_WOOL",
            "MAGENTA_WOOL",
            "PINK_WOOL",
            "WHITE_WOOL",
            "LIGHT_GRAY_WOOL",
            "GRAY_WOOL",
            "BLACK_WOOL",
            "BROWN_WOOL"
    );

    private LandConfigLoader() {
    }

    public static LandConfig load(JavaPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        Material defaultMaterial = parseMaterial(configuration.getString("border-material"), Material.RED_WOOL);
        BorderColorPalette borderColors = loadPalette(configuration, defaultMaterial);
        LandExpansionSettings expansion = new LandExpansionSettings(
                configuration.getInt("expansion.step", configuration.getInt("base-radius", 8)),
                configuration.getInt("expansion.max-extent", 32),
                configuration.getDouble("expansion.base-cost", 1000.0D),
                configuration.getDouble("expansion.cost-per-block", 250.0D)
        );
        return new LandConfig(
                configuration.getString("locale", "ru"),
                configuration.getString("fallback-locale", "en"),
                configuration.getInt("region-buffer", 5),
                configuration.getInt("base-radius", 8),
                borderColors,
                expansion,
                configuration.getInt("gui.rows", 4),
                configuration.getInt("gui.slots.info", 4),
                configuration.getInt("gui.slots.expand-north", 1),
                configuration.getInt("gui.slots.expand-west", 9),
                configuration.getInt("gui.slots.expand-east", 17),
                configuration.getInt("gui.slots.expand-south", 25),
                configuration.getInt("gui.slots.pvp", 20),
                configuration.getInt("gui.slots.mob-spawn", 22),
                configuration.getInt("gui.slots.border-color", 24),
                configuration.getInt("gui.slots.back", 31)
        );
    }

    private static BorderColorPalette loadPalette(FileConfiguration configuration, Material defaultMaterial) {
        List<String> rawColors = configuration.getStringList("border-colors");
        if (rawColors.isEmpty()) {
            rawColors = DEFAULT_BORDER_COLORS;
        }
        List<Material> materials = new ArrayList<>();
        for (String rawColor : rawColors) {
            Material material = parseMaterial(rawColor, null);
            if (material == null || !material.name().endsWith("_WOOL")) {
                continue;
            }
            if (!materials.contains(material)) {
                materials.add(material);
            }
        }
        if (materials.isEmpty()) {
            materials.add(defaultMaterial);
        }
        if (!materials.contains(defaultMaterial)) {
            materials.addFirst(defaultMaterial);
        }
        return new BorderColorPalette(materials, defaultMaterial);
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
