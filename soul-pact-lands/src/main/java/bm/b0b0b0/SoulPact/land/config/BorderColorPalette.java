package bm.b0b0b0.SoulPact.land.config;

import java.util.List;
import java.util.Locale;
import org.bukkit.Material;

public final class BorderColorPalette {

    private final List<Material> guiColors;
    private final Material defaultGuiColor;

    public BorderColorPalette(List<Material> guiColors, Material defaultGuiColor) {
        this.guiColors = List.copyOf(guiColors);
        this.defaultGuiColor = defaultGuiColor;
    }

    public Material defaultGuiColor() {
        return defaultGuiColor;
    }

    public Material defaultWorldColor() {
        return toWorldMaterial(defaultGuiColor);
    }

    public List<Material> guiColors() {
        return guiColors;
    }

    public Material resolveGui(String rawValue) {
        Material guiColor = normalizeToGui(rawValue);
        if (guiColor != null && guiColors.contains(guiColor)) {
            return guiColor;
        }
        return defaultGuiColor;
    }

    public Material resolveWorld(String rawValue) {
        return toWorldMaterial(resolveGui(rawValue));
    }

    public Material nextGui(Material current) {
        Material resolved = resolveGui(current.name());
        int index = guiColors.indexOf(resolved);
        if (index < 0) {
            return guiColors.getFirst();
        }
        return guiColors.get((index + 1) % guiColors.size());
    }

    public boolean isBorderMaterial(Material material) {
        if (material == null) {
            return false;
        }
        if (guiColors.contains(material)) {
            return true;
        }
        Material guiColor = woolFromConcrete(material);
        return guiColor != null && guiColors.contains(guiColor);
    }

    public String displayKey(Material material) {
        Material guiColor = normalizeToGui(material.name());
        if (guiColor == null) {
            return material.name().toLowerCase(Locale.ROOT);
        }
        return guiColor.name().replace("_WOOL", "").toLowerCase(Locale.ROOT);
    }

    public static Material toWorldMaterial(Material guiColor) {
        if (guiColor == null) {
            return Material.RED_CONCRETE;
        }
        if (guiColor.name().endsWith("_CONCRETE")) {
            return guiColor;
        }
        if (guiColor.name().endsWith("_WOOL")) {
            Material concrete = Material.matchMaterial(guiColor.name().replace("_WOOL", "_CONCRETE"));
            return concrete == null ? guiColor : concrete;
        }
        return guiColor;
    }

    private Material normalizeToGui(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        Material material = Material.matchMaterial(rawValue);
        if (material == null) {
            return null;
        }
        if (material.name().endsWith("_WOOL")) {
            return material;
        }
        return woolFromConcrete(material);
    }

    private Material woolFromConcrete(Material material) {
        if (!material.name().endsWith("_CONCRETE")) {
            return null;
        }
        return Material.matchMaterial(material.name().replace("_CONCRETE", "_WOOL"));
    }
}
