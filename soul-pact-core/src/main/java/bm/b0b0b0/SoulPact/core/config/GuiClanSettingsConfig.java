package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.SettingsGuiSettings;
import org.bukkit.Material;

public final class GuiClanSettingsConfig {

    private final int rows;
    private final int backSlot;
    private final int bannerSlot;
    private final int contentStart;
    private final int contentSize;
    private final Material roleMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;

    public GuiClanSettingsConfig(SettingsGuiSettings settings) {
        this.rows = settings.rows;
        this.backSlot = settings.slots.back;
        this.bannerSlot = settings.slots.banner;
        this.contentStart = 10;
        this.contentSize = Math.max(0, (rows - 2) * 9);
        this.roleMaterial = parseMaterial(settings.materials.role, Material.WRITABLE_BOOK);
        this.backMaterial = parseMaterial(settings.materials.back, Material.ARROW);
        this.fillerMaterial = parseMaterial(settings.materials.filler, Material.GRAY_STAINED_GLASS_PANE);
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public int backSlot() {
        return backSlot;
    }

    public int bannerSlot() {
        return bannerSlot;
    }

    public int contentStart() {
        return contentStart;
    }

    public int contentSize() {
        return contentSize;
    }

    public int contentSlot(int index) {
        return contentStart + index;
    }

    public Material roleMaterial() {
        return roleMaterial;
    }

    public Material backMaterial() {
        return backMaterial;
    }

    public Material fillerMaterial() {
        return fillerMaterial;
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
