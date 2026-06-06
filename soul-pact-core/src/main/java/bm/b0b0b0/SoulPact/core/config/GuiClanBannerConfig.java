package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.BannerGuiSettings;
import org.bukkit.Material;

public final class GuiClanBannerConfig {

    private final int rows;
    private final int previewSlot;
    private final int patternStartSlot;
    private final int baseColorRowOneStart;
    private final int baseColorRowTwoStart;
    private final int clearPatternsSlot;
    private final int patternColorSlot;
    private final int fromHandSlot;
    private final int undoPatternSlot;
    private final int saveSlot;
    private final int takeStandardSlot;
    private final int backSlot;
    private final Material clearPatternsMaterial;
    private final Material patternColorMaterial;
    private final Material fromHandMaterial;
    private final Material undoPatternMaterial;
    private final Material saveMaterial;
    private final Material takeStandardMaterial;
    private final Material depositStandardMaterial;
    private final Material editLockedMaterial;
    private final Material standardAwayMaterial;
    private final Material backMaterial;
    private final Material fillerMaterial;

    public GuiClanBannerConfig(BannerGuiSettings settings) {
        this.rows = settings.rows;
        this.previewSlot = settings.slots.preview;
        this.patternStartSlot = settings.slots.patternStart;
        this.baseColorRowOneStart = settings.slots.baseColorRowOneStart;
        this.baseColorRowTwoStart = settings.slots.baseColorRowTwoStart;
        this.clearPatternsSlot = settings.slots.clearPatterns;
        this.patternColorSlot = settings.slots.patternColor;
        this.fromHandSlot = settings.slots.fromHand;
        this.undoPatternSlot = settings.slots.undoPattern;
        this.saveSlot = settings.slots.save;
        this.takeStandardSlot = settings.slots.takeStandard;
        this.backSlot = settings.slots.back;
        this.clearPatternsMaterial = parseMaterial(settings.materials.clearPatterns, Material.BARRIER);
        this.patternColorMaterial = parseMaterial(settings.materials.patternColor, Material.WHITE_DYE);
        this.fromHandMaterial = parseMaterial(settings.materials.fromHand, Material.HOPPER);
        this.undoPatternMaterial = parseMaterial(settings.materials.undoPattern, Material.SHEARS);
        this.saveMaterial = parseMaterial(settings.materials.save, Material.LIME_DYE);
        this.takeStandardMaterial = parseMaterial(settings.materials.takeStandard, Material.GLOW_ITEM_FRAME);
        this.depositStandardMaterial = parseMaterial(settings.materials.depositStandard, Material.CHEST);
        this.editLockedMaterial = parseMaterial(settings.materials.editLocked, Material.IRON_BARS);
        this.standardAwayMaterial = parseMaterial(settings.materials.standardAway, Material.COMPASS);
        this.backMaterial = parseMaterial(settings.materials.back, Material.ARROW);
        this.fillerMaterial = parseMaterial(settings.materials.filler, Material.GRAY_STAINED_GLASS_PANE);
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public int previewSlot() {
        return previewSlot;
    }

    public int patternStartSlot() {
        return patternStartSlot;
    }

    public int patternSlot(int index) {
        return patternStartSlot + index;
    }

    public int baseColorRowOneStart() {
        return baseColorRowOneStart;
    }

    public int baseColorRowTwoStart() {
        return baseColorRowTwoStart;
    }

    public int baseColorSlot(int index) {
        if (index < 8) {
            return baseColorRowOneStart + index;
        }
        return baseColorRowTwoStart + (index - 8);
    }

    public int clearPatternsSlot() {
        return clearPatternsSlot;
    }

    public int patternColorSlot() {
        return patternColorSlot;
    }

    public int fromHandSlot() {
        return fromHandSlot;
    }

    public int undoPatternSlot() {
        return undoPatternSlot;
    }

    public int saveSlot() {
        return saveSlot;
    }

    public int takeStandardSlot() {
        return takeStandardSlot;
    }

    public Material takeStandardMaterial() {
        return takeStandardMaterial;
    }

    public Material depositStandardMaterial() {
        return depositStandardMaterial;
    }

    public Material editLockedMaterial() {
        return editLockedMaterial;
    }

    public Material standardAwayMaterial() {
        return standardAwayMaterial;
    }

    public int backSlot() {
        return backSlot;
    }

    public Material clearPatternsMaterial() {
        return clearPatternsMaterial;
    }

    public Material patternColorMaterial() {
        return patternColorMaterial;
    }

    public Material fromHandMaterial() {
        return fromHandMaterial;
    }

    public Material undoPatternMaterial() {
        return undoPatternMaterial;
    }

    public Material saveMaterial() {
        return saveMaterial;
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
