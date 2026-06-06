package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.PermissionSettings;

public final class PermissionsConfig {

    private final String clanUse;
    private final String clanAdmin;

    public PermissionsConfig(PermissionSettings settings) {
        this.clanUse = settings.clanUse;
        this.clanAdmin = settings.clanAdmin;
    }

    public String clanUse() {
        return clanUse;
    }

    public String clanAdmin() {
        return clanAdmin;
    }
}
