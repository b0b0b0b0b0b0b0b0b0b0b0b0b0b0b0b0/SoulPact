package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.MemberDetailGuiSettings;
import org.bukkit.Material;

public final class GuiMemberDetailConfig {

    private final int rows;
    private final int playerSlot;
    private final int backSlot;
    private final int contentStart;
    private final int contentSize;
    private final Material assignRoleMaterial;
    private final Material transferMaterial;
    private final Material kickMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;

    public GuiMemberDetailConfig(MemberDetailGuiSettings settings) {
        this.rows = settings.rows;
        this.playerSlot = settings.slots.player;
        this.backSlot = settings.slots.back;
        this.contentStart = 19;
        this.contentSize = Math.max(0, (rows - 2) * 9);
        this.assignRoleMaterial = parseMaterial(settings.materials.assignRole, Material.WRITABLE_BOOK);
        this.transferMaterial = parseMaterial(settings.materials.transfer, Material.GOLD_INGOT);
        this.kickMaterial = parseMaterial(settings.materials.kick, Material.BARRIER);
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

    public Material assignRoleMaterial() {
        return assignRoleMaterial;
    }

    public Material transferMaterial() {
        return transferMaterial;
    }

    public Material kickMaterial() {
        return kickMaterial;
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
