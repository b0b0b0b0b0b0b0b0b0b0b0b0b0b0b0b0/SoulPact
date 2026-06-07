package bm.b0b0b0.SoulPact.land.config;

import bm.b0b0b0.SoulPact.land.config.settings.LandExpansionSettingsYaml;
import bm.b0b0b0.SoulPact.land.config.settings.LandGuiSettings;
import bm.b0b0b0.SoulPact.land.config.settings.LandSettings;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

public final class LandConfigFactory {

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

    private LandConfigFactory() {
    }

    public static LandConfig from(LandSettings settings) {
        Material defaultGuiColor = normalizeDefaultGuiColor(parseMaterial(settings.borderMaterial, Material.RED_WOOL));
        BorderColorPalette borderColors = loadPalette(settings.borderColors, defaultGuiColor);
        LandExpansionSettingsYaml expansionYaml = settings.expansion;
        LandExpansionSettings expansion = new LandExpansionSettings(
                expansionYaml.step,
                expansionYaml.maxExtent,
                expansionYaml.baseCost,
                expansionYaml.costPerBlock
        );
        LandGuiSettings gui = settings.gui;
        return new LandConfig(
                settings.locale,
                settings.fallbackLocale,
                settings.regionBuffer,
                settings.baseRadius,
                borderColors,
                expansion,
                gui.rows,
                gui.slots.info,
                gui.slots.expandNorth,
                gui.slots.expandWest,
                gui.slots.expandEast,
                gui.slots.expandSouth,
                gui.slots.pvp,
                gui.slots.mobSpawn,
                gui.slots.borderColor,
                gui.slots.back
        );
    }

    private static BorderColorPalette loadPalette(List<String> rawColors, Material defaultGuiColor) {
        List<String> colors = rawColors == null || rawColors.isEmpty() ? DEFAULT_BORDER_COLORS : rawColors;
        List<Material> materials = new ArrayList<>();
        for (String rawColor : colors) {
            Material material = parseMaterial(rawColor, null);
            Material guiColor = toGuiColor(material);
            if (guiColor == null) {
                continue;
            }
            if (!materials.contains(guiColor)) {
                materials.add(guiColor);
            }
        }
        if (materials.isEmpty()) {
            materials.add(defaultGuiColor);
        }
        if (!materials.contains(defaultGuiColor)) {
            materials.addFirst(defaultGuiColor);
        }
        return new BorderColorPalette(materials, defaultGuiColor);
    }

    private static Material normalizeDefaultGuiColor(Material material) {
        Material guiColor = toGuiColor(material);
        return guiColor == null ? Material.RED_WOOL : guiColor;
    }

    private static Material toGuiColor(Material material) {
        if (material == null) {
            return null;
        }
        if (material.name().endsWith("_WOOL")) {
            return material;
        }
        if (material.name().endsWith("_CONCRETE")) {
            Material wool = Material.matchMaterial(material.name().replace("_CONCRETE", "_WOOL"));
            return wool == null ? null : wool;
        }
        return null;
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
