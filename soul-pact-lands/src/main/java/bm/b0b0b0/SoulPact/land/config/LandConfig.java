package bm.b0b0b0.SoulPact.land.config;

import org.bukkit.Material;

public final class LandConfig {

    private final String locale;
    private final String fallbackLocale;
    private final int regionBuffer;
    private final int baseRadius;
    private final BorderColorPalette borderColors;
    private final LandExpansionSettings expansion;
    private final int guiRows;
    private final int infoSlot;
    private final int expandNorthSlot;
    private final int expandWestSlot;
    private final int expandEastSlot;
    private final int expandSouthSlot;
    private final int pvpSlot;
    private final int mobSpawnSlot;
    private final int borderColorSlot;
    private final int backSlot;

    public LandConfig(
            String locale,
            String fallbackLocale,
            int regionBuffer,
            int baseRadius,
            BorderColorPalette borderColors,
            LandExpansionSettings expansion,
            int guiRows,
            int infoSlot,
            int expandNorthSlot,
            int expandWestSlot,
            int expandEastSlot,
            int expandSouthSlot,
            int pvpSlot,
            int mobSpawnSlot,
            int borderColorSlot,
            int backSlot
    ) {
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
        this.regionBuffer = regionBuffer;
        this.baseRadius = baseRadius;
        this.borderColors = borderColors;
        this.expansion = expansion;
        this.guiRows = guiRows;
        this.infoSlot = infoSlot;
        this.expandNorthSlot = expandNorthSlot;
        this.expandWestSlot = expandWestSlot;
        this.expandEastSlot = expandEastSlot;
        this.expandSouthSlot = expandSouthSlot;
        this.pvpSlot = pvpSlot;
        this.mobSpawnSlot = mobSpawnSlot;
        this.borderColorSlot = borderColorSlot;
        this.backSlot = backSlot;
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public int regionBuffer() {
        return regionBuffer;
    }

    public int baseRadius() {
        return baseRadius;
    }

    public BorderColorPalette borderColors() {
        return borderColors;
    }

    public LandExpansionSettings expansion() {
        return expansion;
    }

    public Material borderMaterial() {
        return borderColors.defaultColor();
    }

    public int guiRows() {
        return guiRows;
    }

    public int guiSize() {
        return guiRows * 9;
    }

    public int infoSlot() {
        return infoSlot;
    }

    public int expandNorthSlot() {
        return expandNorthSlot;
    }

    public int expandWestSlot() {
        return expandWestSlot;
    }

    public int expandEastSlot() {
        return expandEastSlot;
    }

    public int expandSouthSlot() {
        return expandSouthSlot;
    }

    public int pvpSlot() {
        return pvpSlot;
    }

    public int mobSpawnSlot() {
        return mobSpawnSlot;
    }

    public int borderColorSlot() {
        return borderColorSlot;
    }

    public int backSlot() {
        return backSlot;
    }
}
