package bm.b0b0b0.SoulPact.land.config;

import java.util.List;
import java.util.Locale;
import org.bukkit.Material;

public final class BorderColorPalette {

    private final List<Material> colors;
    private final Material defaultColor;

    public BorderColorPalette(List<Material> colors, Material defaultColor) {
        this.colors = List.copyOf(colors);
        this.defaultColor = defaultColor;
    }

    public Material defaultColor() {
        return defaultColor;
    }

    public List<Material> colors() {
        return colors;
    }

    public boolean isBorderMaterial(Material material) {
        return colors.contains(material);
    }

    public Material resolve(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return defaultColor;
        }
        Material material = Material.matchMaterial(rawValue);
        if (material == null || !isBorderMaterial(material)) {
            return defaultColor;
        }
        return material;
    }

    public Material next(Material current) {
        Material resolved = resolve(current.name());
        int index = colors.indexOf(resolved);
        if (index < 0) {
            return colors.getFirst();
        }
        return colors.get((index + 1) % colors.size());
    }

    public String displayKey(Material material) {
        return material.name().replace("_WOOL", "").toLowerCase(Locale.ROOT);
    }
}
