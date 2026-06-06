package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import java.util.Map;

public final class ClanRoleSettingsSnapshot {

    private final Clan clan;
    private final String roleKey;
    private final String roleTitle;
    private final Map<String, Boolean> permissions;

    public ClanRoleSettingsSnapshot(
            Clan clan,
            String roleKey,
            String roleTitle,
            Map<String, Boolean> permissions
    ) {
        this.clan = clan;
        this.roleKey = roleKey;
        this.roleTitle = roleTitle;
        this.permissions = Map.copyOf(permissions);
    }

    public Clan clan() {
        return clan;
    }

    public String roleKey() {
        return roleKey;
    }

    public String roleTitle() {
        return roleTitle;
    }

    public Map<String, Boolean> permissions() {
        return permissions;
    }

    public boolean isEnabled(String permission) {
        return permissions.getOrDefault(permission, false);
    }
}
