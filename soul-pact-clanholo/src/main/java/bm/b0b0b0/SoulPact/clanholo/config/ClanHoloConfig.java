package bm.b0b0b0.SoulPact.clanholo.config;

import bm.b0b0b0.SoulPact.clanholo.config.settings.ClanHoloSettings;
import java.util.List;
import java.util.Map;

public final class ClanHoloConfig {

    private final String locale;
    private final String fallbackLocale;
    private final String adminPermission;
    private final String clanPermissionKey;
    private final boolean requireClanBase;
    private final int maxLines;
    private final int maxLineLength;
    private final int maxHologramsDefault;
    private final Map<String, Integer> maxHologramsByPermission;
    private final String ownerLine;
    private final double lineSpacing;
    private final double displayScale;
    private final double selectRadius;
    private final List<String> blockedWords;
    private final List<String> defaultRulesLines;

    public ClanHoloConfig(ClanHoloSettings settings) {
        this.locale = settings.locale;
        this.fallbackLocale = settings.fallbackLocale;
        this.adminPermission = settings.adminPermission;
        this.clanPermissionKey = settings.clanPermissionKey;
        this.requireClanBase = settings.requireClanBase;
        this.maxLines = settings.maxLines;
        this.maxLineLength = settings.maxLineLength;
        this.maxHologramsDefault = settings.maxHologramsDefault;
        this.maxHologramsByPermission = Map.copyOf(settings.maxHologramsByPermission);
        this.ownerLine = settings.ownerLine;
        this.lineSpacing = settings.lineSpacing;
        this.displayScale = settings.displayScale;
        this.selectRadius = settings.selectRadius;
        this.blockedWords = List.copyOf(settings.blockedWords);
        this.defaultRulesLines = List.copyOf(settings.defaultRulesLines);
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public String adminPermission() {
        return adminPermission;
    }

    public String clanPermissionKey() {
        return clanPermissionKey;
    }

    public boolean requireClanBase() {
        return requireClanBase;
    }

    public int maxLines() {
        return maxLines;
    }

    public int maxLineLength() {
        return maxLineLength;
    }

    public int maxHologramsDefault() {
        return maxHologramsDefault;
    }

    public Map<String, Integer> maxHologramsByPermission() {
        return maxHologramsByPermission;
    }

    public String ownerLine() {
        return ownerLine;
    }

    public double lineSpacing() {
        return lineSpacing;
    }

    public double displayScale() {
        return displayScale;
    }

    public double selectRadius() {
        return selectRadius;
    }

    public List<String> blockedWords() {
        return blockedWords;
    }

    public List<String> defaultRulesLines() {
        return defaultRulesLines;
    }
}
