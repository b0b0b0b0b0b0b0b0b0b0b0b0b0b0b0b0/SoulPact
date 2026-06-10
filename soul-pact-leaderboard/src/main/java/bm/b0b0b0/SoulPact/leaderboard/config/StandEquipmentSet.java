package bm.b0b0b0.SoulPact.leaderboard.config;

import java.util.Locale;
import org.bukkit.Material;

public record StandEquipmentSet(Material chestplate, Material leggings, Material boots) {

    public static StandEquipmentSet parse(String rawValue, StandEquipmentSet fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        String[] parts = rawValue.split(",");
        if (parts.length < 3) {
            return fallback;
        }
        Material chest = material(parts[0], fallback == null ? null : fallback.chestplate());
        Material legs = material(parts[1], fallback == null ? null : fallback.leggings());
        Material feet = material(parts[2], fallback == null ? null : fallback.boots());
        return new StandEquipmentSet(chest, legs, feet);
    }

    private static Material material(String rawValue, Material fallback) {
        Material parsed = Material.matchMaterial(rawValue.trim().toUpperCase(Locale.ROOT));
        return parsed == null ? fallback : parsed;
    }
}
