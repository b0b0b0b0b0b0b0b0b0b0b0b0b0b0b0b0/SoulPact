package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.RoleSettingsGuiSettings;
import org.bukkit.Material;

public final class GuiClanRoleSettingsConfig {

    private final int rows;
    private final int kickSlot;
    private final int acceptSlot;
    private final int recruitLowerSlot;
    private final int backSlot;
    private final Material toggleOnMaterial;
    private final Material toggleOffMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;

    public GuiClanRoleSettingsConfig(RoleSettingsGuiSettings settings) {
        this.rows = settings.rows;
        this.kickSlot = settings.slots.kick;
        this.acceptSlot = settings.slots.accept;
        this.recruitLowerSlot = settings.slots.recruitLower;
        this.backSlot = settings.slots.back;
        this.toggleOnMaterial = parseMaterial(settings.materials.toggleOn, Material.LIME_DYE);
        this.toggleOffMaterial = parseMaterial(settings.materials.toggleOff, Material.GRAY_DYE);
        this.backMaterial = parseMaterial(settings.materials.back, Material.ARROW);
        this.fillerMaterial = parseMaterial(settings.materials.filler, Material.GRAY_STAINED_GLASS_PANE);
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public int kickSlot() {
        return kickSlot;
    }

    public int acceptSlot() {
        return acceptSlot;
    }

    public int recruitLowerSlot() {
        return recruitLowerSlot;
    }

    public int backSlot() {
        return backSlot;
    }

    public int slotForPermission(String permission) {
        return switch (permission) {
            case "kick" -> kickSlot;
            case "accept" -> acceptSlot;
            case "recruit_lower" -> recruitLowerSlot;
            default -> -1;
        };
    }

    public Material toggleOnMaterial() {
        return toggleOnMaterial;
    }

    public Material toggleOffMaterial() {
        return toggleOffMaterial;
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
