package bm.b0b0b0.SoulPact.bank.config;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class BankConfigLoader {

    private BankConfigLoader() {
    }

    public static BankConfig load(JavaPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        List<Double> depositPresets = readPresets(configuration.getDoubleList("deposit-presets"));
        List<Double> withdrawPresets = readPresets(configuration.getDoubleList("withdraw-presets"));
        if (depositPresets.isEmpty()) {
            depositPresets = List.of(100D, 1000D, 10000D);
        }
        if (withdrawPresets.isEmpty()) {
            withdrawPresets = List.of(100D, 1000D, 10000D);
        }
        return new BankConfig(
                configuration.getString("locale", "ru"),
                configuration.getString("fallback-locale", "en"),
                configuration.getDouble("min-amount", 1D),
                configuration.getDouble("max-deposit", 1_000_000D),
                configuration.getDouble("max-withdraw", 1_000_000D),
                configuration.getDouble("notify-deposit-above", 1000D),
                configuration.getInt("contributor-top-size", 5),
                configuration.getInt("ledger-preview-size", 8),
                depositPresets,
                withdrawPresets,
                configuration.getInt("gui.rows", 6),
                configuration.getInt("gui.slots.balance", 4),
                configuration.getInt("gui.slots.deposit-start", 20),
                configuration.getInt("gui.slots.withdraw-start", 29),
                configuration.getInt("gui.slots.deposit-all", 24),
                configuration.getInt("gui.slots.withdraw-all", 33),
                configuration.getInt("gui.slots.back", 49),
                parseMaterial(configuration.getString("gui.materials.filler"), Material.GRAY_STAINED_GLASS_PANE),
                parseMaterial(configuration.getString("gui.materials.balance"), Material.GOLD_BLOCK),
                parseMaterial(configuration.getString("gui.materials.deposit"), Material.LIME_DYE),
                parseMaterial(configuration.getString("gui.materials.withdraw"), Material.RED_DYE),
                parseMaterial(configuration.getString("gui.materials.deposit-all"), Material.EMERALD),
                parseMaterial(configuration.getString("gui.materials.withdraw-all"), Material.GOLD_INGOT),
                parseMaterial(configuration.getString("gui.materials.back"), Material.ARROW)
        );
    }

    private static List<Double> readPresets(List<Double> rawValues) {
        if (rawValues == null || rawValues.isEmpty()) {
            return List.of();
        }
        List<Double> values = new ArrayList<>();
        for (Double value : rawValues) {
            if (value != null && value > 0D) {
                values.add(value);
            }
        }
        return values;
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
