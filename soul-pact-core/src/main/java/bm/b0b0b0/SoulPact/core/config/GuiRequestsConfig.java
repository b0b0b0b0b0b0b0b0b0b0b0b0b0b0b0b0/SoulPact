package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.RequestsGuiSettings;
import org.bukkit.Material;

public final class GuiRequestsConfig {

    private final int rows;
    private final int acceptAllSlot;
    private final int denyAllSlot;
    private final int blockAllSlot;
    private final int toggleSlot;
    private final int backSlot;
    private final int contentSize;
    private final int contentStart;
    private final Material acceptAllMaterial;
    private final Material denyAllMaterial;
    private final Material blockAllMaterial;
    private final Material toggleOpenMaterial;
    private final Material toggleClosedMaterial;
    private final Material backMaterial;
    private final Material emptyMaterial;
    private final Material fillerMaterial;

    public GuiRequestsConfig(RequestsGuiSettings settings) {
        this.rows = settings.rows;
        this.acceptAllSlot = settings.slots.acceptAll;
        this.denyAllSlot = settings.slots.denyAll;
        this.blockAllSlot = settings.slots.blockAll;
        this.toggleSlot = settings.slots.toggle;
        this.backSlot = settings.slots.back;
        this.contentStart = 9;
        this.contentSize = Math.max(0, (rows - 2) * 9);
        this.acceptAllMaterial = parseMaterial(settings.materials.acceptAll, Material.LIME_CONCRETE);
        this.denyAllMaterial = parseMaterial(settings.materials.denyAll, Material.RED_CONCRETE);
        this.blockAllMaterial = parseMaterial(settings.materials.blockAll, Material.PURPLE_CONCRETE);
        this.toggleOpenMaterial = parseMaterial(settings.materials.toggleOpen, Material.OAK_DOOR);
        this.toggleClosedMaterial = parseMaterial(settings.materials.toggleClosed, Material.IRON_DOOR);
        this.backMaterial = parseMaterial(settings.materials.back, Material.ARROW);
        this.emptyMaterial = parseMaterial(settings.materials.empty, Material.BARRIER);
        this.fillerMaterial = parseMaterial(settings.materials.filler, Material.GRAY_STAINED_GLASS_PANE);
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public int acceptAllSlot() {
        return acceptAllSlot;
    }

    public int denyAllSlot() {
        return denyAllSlot;
    }

    public int blockAllSlot() {
        return blockAllSlot;
    }

    public int toggleSlot() {
        return toggleSlot;
    }

    public int backSlot() {
        return backSlot;
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

    public Material acceptAllMaterial() {
        return acceptAllMaterial;
    }

    public Material denyAllMaterial() {
        return denyAllMaterial;
    }

    public Material blockAllMaterial() {
        return blockAllMaterial;
    }

    public Material toggleOpenMaterial(boolean open) {
        return open ? toggleOpenMaterial : toggleClosedMaterial;
    }

    public Material backMaterial() {
        return backMaterial;
    }

    public Material emptyMaterial() {
        return emptyMaterial;
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
