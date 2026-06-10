package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.clan.ClanPermissionKeys;
import bm.b0b0b0.SoulPact.clan.model.ClanRolePermissionMap;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanRolePermissionRepository;
import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.config.settings.ClanRolePermissionFlagsSettings;
import bm.b0b0b0.SoulPact.core.config.settings.ClanRolePermissionDefaultsSettings;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanRolePermissionService {

    private final ClanRolePermissionRepository permissionRepository;
    private final ClanRepository clanRepository;
    private final ClanConfig clanConfig;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanRolePermissionService(
            ClanRolePermissionRepository permissionRepository,
            ClanRepository clanRepository,
            ClanConfig clanConfig,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.permissionRepository = permissionRepository;
        this.clanRepository = clanRepository;
        this.clanConfig = clanConfig;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public CompletableFuture<ClanRolePermissionMap> loadByClanId(long clanId) {
        return permissionRepository.findByClanId(clanId).thenApply(stored -> {
            if (stored.byRole().isEmpty()) {
                return buildConfigDefaults();
            }
            return mergeWithDefaults(stored);
        });
    }

    public CompletableFuture<Void> seedDefaults(long clanId) {
        return permissionRepository.seedDefaults(clanId, buildConfigDefaults());
    }

    public CompletableFuture<Boolean> toggle(Player leader, long clanId, String role, String permission) {
        if (!ClanRolePermissionMap.PERMISSION_ORDER.contains(permission)) {
            return CompletableFuture.completedFuture(false);
        }
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty() || !clanOptional.get().leaderId().equals(leader.getUniqueId())) {
                notify(leader, "clan.settings.not-leader");
                return CompletableFuture.completedFuture(false);
            }
            if (ClanStaffPermissions.LEADER_ROLE.equals(role)) {
                return CompletableFuture.completedFuture(false);
            }
            return loadByClanId(clanId).thenCompose(permissions -> {
                boolean current = permissions.isEnabled(role, permission, defaultFor(role, permission));
                boolean next = !current;
                return permissionRepository.upsert(clanId, role, permission, next).thenApply(ignored -> {
                    String permissionLabel = messageService.resolve(
                            leader,
                            "clan.settings.permission-label." + permission
                    );
                    notify(leader, next
                            ? "clan.settings.permission.enabled"
                            : "clan.settings.permission.disabled", Map.of(
                            "permission", permissionLabel
                    ));
                    return true;
                });
            });
        });
    }

    public boolean defaultFor(String role, String permission) {
        ClanRolePermissionFlagsSettings flags = flagsForRole(role);
        if (flags == null) {
            return false;
        }
        return switch (permission) {
            case ClanPermissionKeys.KICK -> flags.kick;
            case ClanPermissionKeys.ACCEPT -> flags.accept;
            case ClanPermissionKeys.RECRUIT_LOWER -> flags.recruitLower;
            case ClanPermissionKeys.BANK_DEPOSIT -> flags.bankDeposit;
            case ClanPermissionKeys.BANK_WITHDRAW -> flags.bankWithdraw;
            case ClanPermissionKeys.CHEST_DEPOSIT -> flags.chestDeposit;
            case ClanPermissionKeys.CHEST_WITHDRAW -> flags.chestWithdraw;
            case ClanPermissionKeys.LAND_MANAGE -> flags.landManage;
            case ClanPermissionKeys.WAR_DECLARE -> flags.warDeclare;
            case ClanPermissionKeys.WAR_RESPOND -> flags.warRespond;
            case ClanPermissionKeys.WAR_FIGHT -> flags.warFight;
            case ClanPermissionKeys.COALITION_MANAGE -> flags.coalitionManage;
            default -> false;
        };
    }

    public Map<String, Boolean> effectivePermissionsForRole(ClanRolePermissionMap permissions, String role) {
        Map<String, Boolean> effective = new HashMap<>();
        for (String permission : ClanRolePermissionMap.PERMISSION_ORDER) {
            effective.put(permission, permissions.isEnabled(role, permission, defaultFor(role, permission)));
        }
        return Map.copyOf(effective);
    }

    private ClanRolePermissionMap mergeWithDefaults(ClanRolePermissionMap stored) {
        Map<String, Map<String, Boolean>> merged = new HashMap<>(stored.byRole());
        for (String role : configDefaultRoles()) {
            merged.computeIfAbsent(role, ignored -> flagsToMap(flagsForRole(role)));
        }
        return new ClanRolePermissionMap(merged);
    }

    private ClanRolePermissionMap buildConfigDefaults() {
        Map<String, Map<String, Boolean>> defaults = new HashMap<>();
        for (String role : configDefaultRoles()) {
            defaults.put(role, flagsToMap(flagsForRole(role)));
        }
        return new ClanRolePermissionMap(defaults);
    }

    private Iterable<String> configDefaultRoles() {
        return java.util.List.of("deputy", "officer", "member");
    }

    private ClanRolePermissionFlagsSettings flagsForRole(String role) {
        ClanRolePermissionDefaultsSettings defaults = clanConfig.rolePermissionDefaults();
        return switch (role) {
            case "deputy" -> defaults.deputy;
            case "officer" -> defaults.officer;
            case "member" -> defaults.member;
            default -> null;
        };
    }

    private static Map<String, Boolean> flagsToMap(ClanRolePermissionFlagsSettings flags) {
        if (flags == null) {
            return Map.of();
        }
        Map<String, Boolean> map = new HashMap<>();
        map.put(ClanPermissionKeys.KICK, flags.kick);
        map.put(ClanPermissionKeys.ACCEPT, flags.accept);
        map.put(ClanPermissionKeys.RECRUIT_LOWER, flags.recruitLower);
        map.put(ClanPermissionKeys.BANK_DEPOSIT, flags.bankDeposit);
        map.put(ClanPermissionKeys.BANK_WITHDRAW, flags.bankWithdraw);
        map.put(ClanPermissionKeys.CHEST_DEPOSIT, flags.chestDeposit);
        map.put(ClanPermissionKeys.CHEST_WITHDRAW, flags.chestWithdraw);
        map.put(ClanPermissionKeys.LAND_MANAGE, flags.landManage);
        map.put(ClanPermissionKeys.WAR_DECLARE, flags.warDeclare);
        map.put(ClanPermissionKeys.WAR_RESPOND, flags.warRespond);
        map.put(ClanPermissionKeys.WAR_FIGHT, flags.warFight);
        map.put(ClanPermissionKeys.COALITION_MANAGE, flags.coalitionManage);
        return map;
    }

    private void notify(Player player, String key) {
        asyncDatabaseExecutor.runSync(() -> messageService.send(player, key));
    }

    private void notify(Player player, String key, Map<String, String> placeholders) {
        asyncDatabaseExecutor.runSync(() -> messageService.send(player, key, placeholders));
    }
}
