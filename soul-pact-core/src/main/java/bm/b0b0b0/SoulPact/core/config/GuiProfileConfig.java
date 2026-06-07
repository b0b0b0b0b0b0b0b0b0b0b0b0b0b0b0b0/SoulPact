package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.ProfileGuiSettings;
import org.bukkit.Material;

public final class GuiProfileConfig {

    private final int rows;
    private final int clanInfoSlot;
    private final int membersSlot;
    private final int requestsSlot;
    private final int leaveSlot;
    private final int emptyMessageSlot;
    private final int emptyCreateSlot;
    private final int emptyListSlot;
    private final int backSlot;
    private final int bannerSlot;
    private final int warSlot;
    private final Material clanInfoMaterial;
    private final Material membersMaterial;
    private final Material requestsMaterial;
    private final Material leaveMaterial;
    private final Material emptyMaterial;
    private final Material createMaterial;
    private final Material emptyListMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;
    private final Material warMaterial;

    public GuiProfileConfig(ProfileGuiSettings settings) {
        this.rows = settings.rows;
        this.clanInfoSlot = settings.slots.clanInfo;
        this.membersSlot = settings.slots.members;
        this.requestsSlot = settings.slots.requests;
        this.leaveSlot = settings.slots.leave;
        this.emptyMessageSlot = settings.slots.emptyMessage;
        this.emptyCreateSlot = settings.slots.emptyCreate;
        this.emptyListSlot = settings.slots.emptyList;
        this.backSlot = settings.slots.back;
        this.bannerSlot = settings.slots.banner;
        this.warSlot = settings.slots.war;
        this.clanInfoMaterial = parseMaterial(settings.materials.clanInfo, Material.LEATHER);
        this.membersMaterial = parseMaterial(settings.materials.members, Material.PLAYER_HEAD);
        this.requestsMaterial = parseMaterial(settings.materials.requests, Material.WRITABLE_BOOK);
        this.leaveMaterial = parseMaterial(settings.materials.leave, Material.RED_DYE);
        this.emptyMaterial = parseMaterial(settings.materials.empty, Material.BARRIER);
        this.createMaterial = parseMaterial(settings.materials.create, Material.EMERALD);
        this.emptyListMaterial = parseMaterial(settings.materials.emptyList, Material.NETHER_STAR);
        this.backMaterial = parseMaterial(settings.materials.back, Material.ARROW);
        this.fillerMaterial = parseMaterial(settings.materials.filler, Material.GRAY_STAINED_GLASS_PANE);
        this.warMaterial = parseMaterial(settings.materials.war, Material.IRON_SWORD);
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public int clanInfoSlot() {
        return clanInfoSlot;
    }

    public int membersSlot() {
        return membersSlot;
    }

    public int requestsSlot() {
        return requestsSlot;
    }

    public int leaveSlot() {
        return leaveSlot;
    }

    public int emptyMessageSlot() {
        return emptyMessageSlot;
    }

    public int emptyCreateSlot() {
        return emptyCreateSlot;
    }

    public int emptyListSlot() {
        return emptyListSlot;
    }

    public int backSlot() {
        return backSlot;
    }

    public int bannerSlot() {
        return bannerSlot;
    }

    public int warSlot() {
        return warSlot;
    }

    public Material clanInfoMaterial() {
        return clanInfoMaterial;
    }

    public Material membersMaterial() {
        return membersMaterial;
    }

    public Material requestsMaterial() {
        return requestsMaterial;
    }

    public Material leaveMaterial() {
        return leaveMaterial;
    }

    public Material emptyMaterial() {
        return emptyMaterial;
    }

    public Material createMaterial() {
        return createMaterial;
    }

    public Material emptyListMaterial() {
        return emptyListMaterial;
    }

    public Material backMaterial() {
        return backMaterial;
    }

    public Material fillerMaterial() {
        return fillerMaterial;
    }

    public Material warMaterial() {
        return warMaterial;
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
