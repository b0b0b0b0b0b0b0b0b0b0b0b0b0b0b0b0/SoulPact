package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.RequestDetailGuiSettings;
import org.bukkit.Material;

public final class GuiRequestDetailConfig {

    private final int rows;
    private final int playerSlot;
    private final int acceptSlot;
    private final int denySlot;
    private final int blockSlot;
    private final int backSlot;
    private final Material acceptMaterial;
    private final Material denyMaterial;
    private final Material blockMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;

    public GuiRequestDetailConfig(RequestDetailGuiSettings settings) {
        this.rows = settings.rows;
        this.playerSlot = settings.slots.player;
        this.acceptSlot = settings.slots.accept;
        this.denySlot = settings.slots.deny;
        this.blockSlot = settings.slots.block;
        this.backSlot = settings.slots.back;
        this.acceptMaterial = parseMaterial(settings.materials.accept, Material.LIME_CONCRETE);
        this.denyMaterial = parseMaterial(settings.materials.deny, Material.RED_CONCRETE);
        this.blockMaterial = parseMaterial(settings.materials.block, Material.PURPLE_CONCRETE);
        this.backMaterial = parseMaterial(settings.materials.back, Material.ARROW);
        this.fillerMaterial = parseMaterial(settings.materials.filler, Material.GRAY_STAINED_GLASS_PANE);
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public int playerSlot() {
        return playerSlot;
    }

    public int acceptSlot() {
        return acceptSlot;
    }

    public int denySlot() {
        return denySlot;
    }

    public int blockSlot() {
        return blockSlot;
    }

    public int backSlot() {
        return backSlot;
    }

    public Material acceptMaterial() {
        return acceptMaterial;
    }

    public Material denyMaterial() {
        return denyMaterial;
    }

    public Material blockMaterial() {
        return blockMaterial;
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
