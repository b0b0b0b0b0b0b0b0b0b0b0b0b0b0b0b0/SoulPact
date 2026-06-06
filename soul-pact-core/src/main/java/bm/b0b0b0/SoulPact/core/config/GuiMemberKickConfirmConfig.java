package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.MemberKickConfirmGuiSettings;
import org.bukkit.Material;

public final class GuiMemberKickConfirmConfig {

    private final int rows;
    private final int confirmSlot;
    private final int denySlot;
    private final Material confirmMaterial;
    private final Material denyMaterial;
    private final Material fillerMaterial;

    public GuiMemberKickConfirmConfig(MemberKickConfirmGuiSettings settings) {
        this.rows = settings.rows;
        this.confirmSlot = settings.slots.confirm;
        this.denySlot = settings.slots.deny;
        this.confirmMaterial = parseMaterial(settings.materials.confirm, Material.LIME_CONCRETE);
        this.denyMaterial = parseMaterial(settings.materials.deny, Material.RED_CONCRETE);
        this.fillerMaterial = parseMaterial(settings.materials.filler, Material.GRAY_STAINED_GLASS_PANE);
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public int confirmSlot() {
        return confirmSlot;
    }

    public int denySlot() {
        return denySlot;
    }

    public Material confirmMaterial() {
        return confirmMaterial;
    }

    public Material denyMaterial() {
        return denyMaterial;
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
