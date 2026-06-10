package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.HubGuiModuleMaterialsSettings;
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
    private final int createSlot;
    private final int helpSlot;
    private final HubModuleSlotMapping moduleSlotMapping;
    private final Material overviewMaterial;
    private final Material profileMaterial;
    private final Material settingsMaterial;
    private final Material createMaterial;
    private final Material helpMaterial;
    private final Material moduleMaterial;
    private final Map<String, Material> moduleMaterialsById;
    private final Material fillerMaterial;

    public GuiHubConfig(HubGuiSettings settings) {
        this.rows = settings.rows;
        this.overviewSlot = settings.slots.overview;
        this.profileSlot = settings.slots.profile;
        this.settingsSlot = settings.slots.settings;
        this.createSlot = settings.slots.create;
        this.helpSlot = settings.slots.help;
        this.moduleSlotMapping = parseModuleSlots(settings.slots.modules, settings.slots.modulesOverflow);
        this.overviewMaterial = parseMaterial(settings.materials.overview, Material.NETHER_STAR);
        this.profileMaterial = parseMaterial(settings.materials.profile, Material.PLAYER_HEAD);
        this.settingsMaterial = parseMaterial(settings.materials.settings, Material.COMPARATOR);
        this.createMaterial = parseMaterial(settings.materials.create, Material.EMERALD);
        this.helpMaterial = parseMaterial(settings.materials.help, Material.BOOK);
        this.moduleMaterial = parseMaterial(settings.materials.module, Material.GOLD_INGOT);
        this.moduleMaterialsById = parseModuleMaterials(settings.materials.modules, settings.materials.module);
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

    public Material createMaterial() {
        return createMaterial;
    }

    public Material helpMaterial() {
        return helpMaterial;
    }

    public Material moduleMaterial() {
        return moduleMaterial;
    }

    public Material moduleMaterial(String extensionId) {
        if (extensionId == null || extensionId.isBlank()) {
            return moduleMaterial;
        }
        Material mapped = moduleMaterialsById.get(extensionId.toLowerCase());
        return mapped == null ? moduleMaterial : mapped;
    }

    public Material fillerMaterial() {
        return fillerMaterial;
    }

    private static Map<String, Material> parseModuleMaterials(
            HubGuiModuleMaterialsSettings settings,
            String fallbackModuleMaterial
    ) {
        Map<String, Material> materials = new LinkedHashMap<>();
        Material fallback = parseMaterial(fallbackModuleMaterial, Material.GOLD_INGOT);
        if (settings == null) {
            return materials;
        }
        putModuleMaterial(materials, "bank", settings.bank, fallback);
        putModuleMaterial(materials, "land", settings.land, fallback);
        putModuleMaterial(materials, "chest", settings.chest, fallback);
        putModuleMaterial(materials, "war", settings.war, fallback);
        putModuleMaterial(materials, "coalition", settings.coalition, fallback);
        putModuleMaterial(materials, "quests", settings.quests, fallback);
        putModuleMaterial(materials, "gladiator", settings.gladiator, fallback);
        putModuleMaterial(materials, "clanholo", settings.clanholo, fallback);
        return Map.copyOf(materials);
    }

    private static void putModuleMaterial(
            Map<String, Material> materials,
            String extensionId,
            String rawValue,
            Material fallback
    ) {
        materials.put(extensionId, parseMaterial(rawValue, fallback));
    }

    private static HubModuleSlotMapping parseModuleSlots(String rawValue, String overflowRawValue) {
        List<Integer> overflowSlots = parseSlotList(overflowRawValue);
        if (rawValue == null || rawValue.isBlank()) {
            return new HubModuleSlotMapping(Map.of("chest", 10, "land", 12, "bank", 14), List.of(), overflowSlots);
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
            return new HubModuleSlotMapping(Map.of("chest", 10, "land", 12, "bank", 14), List.of(), overflowSlots);
        }
        return new HubModuleSlotMapping(byExtensionId, legacyOrderSlots, overflowSlots);
    }

    private static List<Integer> parseSlotList(String rawValue) {
        List<Integer> slots = new ArrayList<>();
        if (rawValue == null || rawValue.isBlank()) {
            return slots;
        }
        for (String part : rawValue.split(",")) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                slots.add(Integer.parseInt(trimmed));
            } catch (NumberFormatException ignored) {
            }
        }
        return slots;
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
