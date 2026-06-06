package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.ListGuiSettings;
import org.bukkit.Material;

public final class GuiListConfig {

    private final int rows;
    private final int previousSlot;
    private final int backSlot;
    private final int nextSlot;
    private final int pageSize;
    private final int contentSize;
    private final Material entryMaterial;
    private final Material emptyMaterial;
    private final Material pageArrowMaterial;
    private final Material pageArrowDisabledMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;

    public GuiListConfig(ListGuiSettings settings) {
        this.rows = settings.rows;
        this.previousSlot = settings.slots.previous;
        this.backSlot = settings.slots.back;
        this.nextSlot = settings.slots.next;
        this.contentSize = Math.max(0, (rows - 1) * 9);
        this.pageSize = contentSize;
        this.entryMaterial = parseMaterial(settings.materials.entry, Material.LEATHER);
        this.emptyMaterial = parseMaterial(settings.materials.empty, Material.BARRIER);
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

    public int pageSize() {
        return pageSize;
    }

    public int contentSize() {
        return contentSize;
    }

    public int contentSlot(int index) {
        return index;
    }

    public int navigationRowStart() {
        return contentSize;
    }

    public int previousSlot() {
        return previousSlot;
    }

    public int backSlot() {
        return backSlot;
    }

    public int nextSlot() {
        return nextSlot;
    }

    public Material entryMaterial() {
        return entryMaterial;
    }

    public Material emptyMaterial() {
        return emptyMaterial;
    }

    public Material pageArrowMaterial() {
        return pageArrowMaterial;
    }

    public Material pageArrowDisabledMaterial() {
        return pageArrowDisabledMaterial;
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
