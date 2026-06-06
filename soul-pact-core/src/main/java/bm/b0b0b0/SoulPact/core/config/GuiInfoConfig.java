package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.InfoGuiSettings;
import org.bukkit.Material;

public final class GuiInfoConfig {

    private final int rows;
    private final int clanCardSlot;
    private final int actionSlot;
    private final int membersSlot;
    private final int backSlot;
    private final Material clanCardMaterial;
    private final Material joinMaterial;
    private final Material joinClosedMaterial;
    private final Material leaveMaterial;
    private final Material membersMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;

    public GuiInfoConfig(InfoGuiSettings settings) {
        this.rows = settings.rows;
        this.clanCardSlot = settings.slots.clanCard;
        this.actionSlot = settings.slots.action;
        this.membersSlot = settings.slots.members;
        this.backSlot = settings.slots.back;
        this.clanCardMaterial = parseMaterial(settings.materials.clanCard, Material.LEATHER);
        this.joinMaterial = parseMaterial(settings.materials.join, Material.LIME_DYE);
        this.joinClosedMaterial = parseMaterial(settings.materials.joinClosed, Material.GRAY_DYE);
        this.leaveMaterial = parseMaterial(settings.materials.leave, Material.RED_DYE);
        this.membersMaterial = parseMaterial(settings.materials.members, Material.PLAYER_HEAD);
        this.backMaterial = parseMaterial(settings.materials.back, Material.ARROW);
        this.fillerMaterial = parseMaterial(settings.materials.filler, Material.GRAY_STAINED_GLASS_PANE);
    }

    public int size() {
        return rows * 9;
    }

    public int clanCardSlot() {
        return clanCardSlot;
    }

    public int actionSlot() {
        return actionSlot;
    }

    public int membersSlot() {
        return membersSlot;
    }

    public int backSlot() {
        return backSlot;
    }

    public Material clanCardMaterial() {
        return clanCardMaterial;
    }

    public Material joinMaterial() {
        return joinMaterial;
    }

    public Material joinClosedMaterial() {
        return joinClosedMaterial;
    }

    public Material leaveMaterial() {
        return leaveMaterial;
    }

    public Material membersMaterial() {
        return membersMaterial;
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
