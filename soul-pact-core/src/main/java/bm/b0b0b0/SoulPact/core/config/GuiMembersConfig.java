package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.MembersGuiSettings;
import org.bukkit.Material;

public final class GuiMembersConfig {

    private final int rows;
    private final int previousSlot;
    private final int backSlot;
    private final int nextSlot;
    private final int bannerSlot;
    private final int contentStart;
    private final int contentSize;
    private final Material pageArrowMaterial;
    private final Material pageArrowDisabledMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;

    public GuiMembersConfig(MembersGuiSettings settings) {
        this.rows = settings.rows;
        this.previousSlot = settings.slots.previous;
        this.backSlot = settings.slots.back;
        this.nextSlot = settings.slots.next;
        this.bannerSlot = settings.slots.banner;
        this.contentStart = 9;
        this.contentSize = Math.max(0, (rows - 2) * 9);
        this.pageArrowMaterial = parseMaterial(settings.materials.pageArrow, Material.ARROW);
        this.pageArrowDisabledMaterial = parseMaterial(settings.materials.pageArrowDisabled, Material.GRAY_DYE);
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

    public int previousSlot() {
        return previousSlot;
    }

    public int nextSlot() {
        return nextSlot;
    }

    public int bannerSlot() {
        return bannerSlot;
    }

    public Material pageArrowMaterial() {
        return pageArrowMaterial;
    }

    public Material pageArrowDisabledMaterial() {
        return pageArrowDisabledMaterial;
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
