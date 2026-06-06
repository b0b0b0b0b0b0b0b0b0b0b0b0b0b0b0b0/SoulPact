package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.ClanRolePermissionDefaultsSettings;
import bm.b0b0b0.SoulPact.core.config.settings.ClanSettings;

public final class ClanConfig {

    private final int maxMembersDefault;
    private final int tagMinLength;
    private final int tagMaxLength;
    private final int nameMaxLength;
    private final int descriptionMaxLength;
    private final int listChatLimit;
    private final String roleTheme;
    private final ClanRolePermissionDefaultsSettings rolePermissionDefaults;

    public ClanConfig(ClanSettings settings) {
        this.maxMembersDefault = settings.maxMembersDefault;
        this.tagMinLength = settings.tagMinLength;
        this.tagMaxLength = settings.tagMaxLength;
        this.nameMaxLength = settings.nameMaxLength;
        this.descriptionMaxLength = settings.descriptionMaxLength;
        this.listChatLimit = settings.listChatLimit;
        this.roleTheme = settings.roleTheme == null || settings.roleTheme.isBlank()
                ? "military"
                : settings.roleTheme.trim();
        this.rolePermissionDefaults = settings.rolePermissionDefaults;
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

    public String roleTheme() {
        return roleTheme;
    }

    public ClanRolePermissionDefaultsSettings rolePermissionDefaults() {
        return rolePermissionDefaults;
    }
}
