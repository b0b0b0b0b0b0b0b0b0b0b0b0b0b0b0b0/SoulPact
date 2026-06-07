package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.InfoGuiSettings;
import org.bukkit.Material;

public final class GuiInfoConfig {

    private final int rows;
    private final int clanCardSlot;
    private final int actionSlot;
    private final int declareWarSlot;
    private final int membersSlot;
    private final int allyFirstSlot;
    private final int allySecondSlot;
    private final int allyThirdSlot;
    private final int backSlot;
    private final Material clanCardMaterial;
    private final Material joinMaterial;
    private final Material joinClosedMaterial;
    private final Material leaveMaterial;
    private final Material declareWarMaterial;
    private final Material allyMaterial;
    private final Material membersMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;

    public GuiInfoConfig(InfoGuiSettings settings) {
        this.rows = settings.rows;
        this.clanCardSlot = settings.slots.clanCard;
        this.actionSlot = settings.slots.action;
        this.declareWarSlot = settings.slots.declareWar;
        this.membersSlot = settings.slots.members;
        this.allyFirstSlot = settings.slots.allyFirst;
        this.allySecondSlot = settings.slots.allySecond;
        this.allyThirdSlot = settings.slots.allyThird;
        this.backSlot = settings.slots.back;
        this.clanCardMaterial = parseMaterial(settings.materials.clanCard, Material.LEATHER);
        this.joinMaterial = parseMaterial(settings.materials.join, Material.LIME_DYE);
        this.joinClosedMaterial = parseMaterial(settings.materials.joinClosed, Material.GRAY_DYE);
        this.leaveMaterial = parseMaterial(settings.materials.leave, Material.RED_DYE);
        this.declareWarMaterial = parseMaterial(settings.materials.declareWar, Material.IRON_SWORD);
        this.allyMaterial = parseMaterial(settings.materials.ally, Material.PLAYER_HEAD);
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

    public int declareWarSlot() {
        return declareWarSlot;
    }

    public int membersSlot() {
        return membersSlot;
    }

    public int allyFirstSlot() {
        return allyFirstSlot;
    }

    public int allySecondSlot() {
        return allySecondSlot;
    }

    public int allyThirdSlot() {
        return allyThirdSlot;
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

    public Material declareWarMaterial() {
        return declareWarMaterial;
    }

    public Material allyMaterial() {
        return allyMaterial;
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
