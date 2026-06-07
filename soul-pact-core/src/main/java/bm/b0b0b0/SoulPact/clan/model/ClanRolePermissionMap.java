package bm.b0b0b0.SoulPact.clan.model;

import bm.b0b0b0.SoulPact.api.clan.ClanPermissionKeys;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClanRolePermissionMap {

    public static final List<String> PERMISSION_ORDER = List.of(
            ClanPermissionKeys.KICK,
            ClanPermissionKeys.ACCEPT,
            ClanPermissionKeys.RECRUIT_LOWER,
            ClanPermissionKeys.BANK_DEPOSIT,
            ClanPermissionKeys.BANK_WITHDRAW
    );

    private final Map<String, Map<String, Boolean>> byRole;

    public ClanRolePermissionMap(Map<String, Map<String, Boolean>> byRole) {
        Map<String, Map<String, Boolean>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, Boolean>> entry : byRole.entrySet()) {
            copy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        this.byRole = Collections.unmodifiableMap(copy);
    }

    public static ClanRolePermissionMap empty() {
        return new ClanRolePermissionMap(Map.of());
    }

    public Map<String, Map<String, Boolean>> byRole() {
        return byRole;
    }

    public boolean isEnabled(String role, String permission, boolean defaultValue) {
        Map<String, Boolean> rolePermissions = byRole.get(role);
        if (rolePermissions == null) {
            return defaultValue;
        }
        return rolePermissions.getOrDefault(permission, defaultValue);
    }
}
