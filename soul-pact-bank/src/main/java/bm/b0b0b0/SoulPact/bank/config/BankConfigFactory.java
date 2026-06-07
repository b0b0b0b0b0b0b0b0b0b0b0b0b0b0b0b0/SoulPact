package bm.b0b0b0.SoulPact.bank.config;

import bm.b0b0b0.SoulPact.bank.config.settings.BankGuiMaterialsSettings;
import bm.b0b0b0.SoulPact.bank.config.settings.BankGuiSettings;
import bm.b0b0b0.SoulPact.bank.config.settings.BankGuiSlotsSettings;
import bm.b0b0b0.SoulPact.bank.config.settings.BankSettings;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

public final class BankConfigFactory {

    private static final List<Double> DEFAULT_PRESETS = List.of(100D, 1000D, 10000D);

    private BankConfigFactory() {
    }

    public static BankConfig from(BankSettings settings) {
        BankGuiSettings gui = settings.gui;
        BankGuiSlotsSettings slots = gui.slots;
        BankGuiMaterialsSettings materials = gui.materials;
        return new BankConfig(
                settings.locale,
                settings.fallbackLocale,
                settings.minAmount,
                settings.maxDeposit,
                settings.maxWithdraw,
                settings.notifyDepositAbove,
                settings.contributorTopSize,
                settings.ledgerPreviewSize,
                normalizePresets(settings.depositPresets),
                normalizePresets(settings.withdrawPresets),
                gui.rows,
                slots.balance,
                slots.depositStart,
                slots.withdrawStart,
                slots.depositAll,
                slots.withdrawAll,
                slots.back,
                parseMaterial(materials.filler, Material.GRAY_STAINED_GLASS_PANE),
                parseMaterial(materials.balance, Material.GOLD_BLOCK),
                parseMaterial(materials.deposit, Material.LIME_DYE),
                parseMaterial(materials.withdraw, Material.RED_DYE),
                parseMaterial(materials.depositAll, Material.EMERALD),
                parseMaterial(materials.withdrawAll, Material.GOLD_INGOT),
                parseMaterial(materials.back, Material.ARROW)
        );
    }

    private static List<Double> normalizePresets(List<Double> rawValues) {
        if (rawValues == null || rawValues.isEmpty()) {
            return DEFAULT_PRESETS;
        }
        List<Double> values = new ArrayList<>();
        for (Double value : rawValues) {
            if (value != null && value > 0D) {
                values.add(value);
            }
        }
        return values.isEmpty() ? DEFAULT_PRESETS : values;
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
