package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.ClanRolePermissionDefaultsSettings;
import bm.b0b0b0.SoulPact.core.config.settings.ClanSettings;
import bm.b0b0b0.SoulPact.core.config.settings.ClanStandardSettings;

public final class ClanConfig {

    private final int maxMembersDefault;
    private final int tagMinLength;
    private final int tagMaxLength;
    private final int nameMaxLength;
    private final int descriptionMaxLength;
    private final int listChatLimit;
    private final int homesMax;
    private final int homeNameMaxLength;
    private final int mailMessageMaxLength;
    private final int mailMaxStored;
    private final int mailPageSize;
    private final String roleTheme;
    private final ClanRolePermissionDefaultsSettings rolePermissionDefaults;
    private final ClanStandardConfig standardConfig;

    public ClanConfig(ClanSettings settings) {
        this.maxMembersDefault = settings.maxMembersDefault;
        this.tagMinLength = settings.tagMinLength;
        this.tagMaxLength = settings.tagMaxLength;
        this.nameMaxLength = settings.nameMaxLength;
        this.descriptionMaxLength = settings.descriptionMaxLength;
        this.listChatLimit = settings.listChatLimit;
        this.homesMax = settings.homesMax;
        this.homeNameMaxLength = settings.homeNameMaxLength;
        this.mailMessageMaxLength = settings.mailMessageMaxLength;
        this.mailMaxStored = settings.mailMaxStored;
        this.mailPageSize = settings.mailPageSize;
        this.roleTheme = settings.roleTheme == null || settings.roleTheme.isBlank()
                ? "military"
                : settings.roleTheme.trim();
        this.rolePermissionDefaults = settings.rolePermissionDefaults;
        this.standardConfig = new ClanStandardConfig(
                settings.standard == null ? new ClanStandardSettings() : settings.standard
        );
    }

    public int maxMembersDefault() {
        return maxMembersDefault;
    }

    public int tagMinLength() {
        return tagMinLength;
    }

    public int tagMaxLength() {
        return tagMaxLength;
    }

    public int nameMaxLength() {
        return nameMaxLength;
    }

    public int descriptionMaxLength() {
        return descriptionMaxLength;
    }

    public int listChatLimit() {
        return listChatLimit;
    }

    public int homesMax() {
        return homesMax;
    }

    public int homeNameMaxLength() {
        return homeNameMaxLength;
    }

    public int mailMessageMaxLength() {
        return mailMessageMaxLength;
    }

    public int mailMaxStored() {
        return mailMaxStored;
    }

    public int mailPageSize() {
        return mailPageSize;
    }

    public String roleTheme() {
        return roleTheme;
    }

    public ClanRolePermissionDefaultsSettings rolePermissionDefaults() {
        return rolePermissionDefaults;
    }

    public ClanStandardConfig standard() {
        return standardConfig;
    }
}
