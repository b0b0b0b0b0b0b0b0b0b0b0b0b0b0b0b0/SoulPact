package bm.b0b0b0.SoulPact.chest.config;

import bm.b0b0b0.SoulPact.chest.config.settings.ChestGuiMaterialsSettings;
import bm.b0b0b0.SoulPact.chest.config.settings.ChestGuiSettings;
import bm.b0b0b0.SoulPact.chest.config.settings.ChestGuiSlotsSettings;
import bm.b0b0b0.SoulPact.chest.config.settings.ChestPricingSettingsYaml;
import bm.b0b0b0.SoulPact.chest.config.settings.ChestSettings;
import org.bukkit.Material;

public final class ChestConfigFactory {

    private ChestConfigFactory() {
    }

    public static ChestConfig from(ChestSettings settings) {
        ChestPricingSettingsYaml pricingYaml = settings.pricing;
        ChestPricingSettings pricing = new ChestPricingSettings(
                pricingYaml.baseCost,
                pricingYaml.linearStep,
                pricingYaml.tierSize,
                pricingYaml.tierMultiplier,
                pricingYaml.maxCost
        );
        ChestGuiSettings gui = settings.gui;
        ChestGuiSlotsSettings slots = gui.slots;
        ChestGuiMaterialsSettings materials = gui.materials;
        return new ChestConfig(
                settings.locale,
                settings.fallbackLocale,
                settings.pages,
                settings.cellsPerPage,
                pricing,
                gui.rows,
                slots.buyCell,
                slots.bankLink,
                slots.page1,
                slots.page2,
                slots.page3,
                slots.back,
                slots.prevPage,
                slots.nextPage,
                parseMaterial(materials.filler, Material.GRAY_STAINED_GLASS_PANE),
                parseMaterial(materials.barrier, Material.BARRIER),
                parseMaterial(materials.buy, Material.EMERALD),
                parseMaterial(materials.bank, Material.GOLD_BLOCK),
                parseMaterial(materials.pageActive, Material.LIME_DYE),
                parseMaterial(materials.pageInactive, Material.GRAY_DYE),
                parseMaterial(materials.back, Material.ARROW),
                parseMaterial(materials.prev, Material.ARROW),
                parseMaterial(materials.next, Material.ARROW)
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
