package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.HubGuiSettings;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;

public final class GuiHubConfig {

    private final int rows;
    private final int overviewSlot;
    private final int profileSlot;
    private final int settingsSlot;
    private final int bannerSlot;
    private final int createSlot;
    private final int helpSlot;
    private final HubModuleSlotMapping moduleSlotMapping;
    private final Material overviewMaterial;
    private final Material profileMaterial;
    private final Material settingsMaterial;
    private final Material bannerMaterial;
    private final Material createMaterial;
    private final Material helpMaterial;
    private final Material moduleMaterial;
    private final Material fillerMaterial;

    public GuiHubConfig(HubGuiSettings settings) {
        this.rows = settings.rows;
        this.overviewSlot = settings.slots.overview;
        this.profileSlot = settings.slots.profile;
        this.settingsSlot = settings.slots.settings;
        this.bannerSlot = settings.slots.banner;
        this.createSlot = settings.slots.create;
        this.helpSlot = settings.slots.help;
        this.moduleSlotMapping = parseModuleSlots(settings.slots.modules);
        this.overviewMaterial = parseMaterial(settings.materials.overview, Material.NETHER_STAR);
        this.profileMaterial = parseMaterial(settings.materials.profile, Material.PLAYER_HEAD);
        this.settingsMaterial = parseMaterial(settings.materials.settings, Material.COMPARATOR);
        this.bannerMaterial = parseMaterial(settings.materials.banner, Material.WHITE_BANNER);
        this.createMaterial = parseMaterial(settings.materials.create, Material.EMERALD);
        this.helpMaterial = parseMaterial(settings.materials.help, Material.BOOK);
        this.moduleMaterial = parseMaterial(settings.materials.module, Material.GOLD_INGOT);
        this.fillerMaterial = parseMaterial(settings.materials.filler, Material.GRAY_STAINED_GLASS_PANE);
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public int overviewSlot() {
        return overviewSlot;
    }

    public int profileSlot() {
        return profileSlot;
    }

    public int settingsSlot() {
        return settingsSlot;
    }

    public int bannerSlot() {
        return bannerSlot;
    }

    public int createSlot() {
        return createSlot;
    }

    public int helpSlot() {
        return helpSlot;
    }

    public HubModuleSlotMapping moduleSlotMapping() {
        return moduleSlotMapping;
    }

    public Material overviewMaterial() {
        return overviewMaterial;
    }

    public Material profileMaterial() {
        return profileMaterial;
    }

    public Material settingsMaterial() {
        return settingsMaterial;
    }

    public Material bannerMaterial() {
        return bannerMaterial;
    }

    public Material createMaterial() {
        return createMaterial;
    }

    public Material helpMaterial() {
        return helpMaterial;
    }

    public Material moduleMaterial() {
        return moduleMaterial;
    }

    public Material fillerMaterial() {
        return fillerMaterial;
    }

    private static HubModuleSlotMapping parseModuleSlots(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return new HubModuleSlotMapping(Map.of("bank", 26, "land", 25), List.of());
        }
        Map<String, Integer> byExtensionId = new LinkedHashMap<>();
        List<Integer> legacyOrderSlots = new ArrayList<>();
        for (String part : rawValue.split(",")) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int separator = trimmed.indexOf(':');
            if (separator > 0) {
                String extensionId = trimmed.substring(0, separator).trim().toLowerCase();
                try {
                    byExtensionId.put(extensionId, Integer.parseInt(trimmed.substring(separator + 1).trim()));
                } catch (NumberFormatException ignored) {
                }
                continue;
            }
            try {
                legacyOrderSlots.add(Integer.parseInt(trimmed));
            } catch (NumberFormatException ignored) {
            }
        }
        if (byExtensionId.isEmpty() && legacyOrderSlots.isEmpty()) {
            return new HubModuleSlotMapping(Map.of("bank", 26, "land", 25), List.of());
        }
        return new HubModuleSlotMapping(byExtensionId, legacyOrderSlots);
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
