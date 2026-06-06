package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanRoleSettingsDataService {

    private final ClanRepository clanRepository;
    private final RoleThemeService roleThemeService;
    private final ClanRolePermissionService rolePermissionService;

    public ClanRoleSettingsDataService(
            ClanRepository clanRepository,
            RoleThemeService roleThemeService,
            ClanRolePermissionService rolePermissionService
    ) {
        this.clanRepository = clanRepository;
        this.roleThemeService = roleThemeService;
        this.rolePermissionService = rolePermissionService;
    }

    public CompletableFuture<Optional<ClanRoleSettingsSnapshot>> load(Player player, long clanId, String roleKey) {
        if (ClanStaffPermissions.LEADER_ROLE.equals(roleKey)) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            var clan = clanOptional.get();
            if (!ClanStaffPermissions.isLeader(clan, player.getUniqueId())) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            RoleDefinition roleDefinition = roleThemeService.theme().definition(roleKey);
            if (roleDefinition == null) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return rolePermissionService.loadByClanId(clan.id()).thenApply(permissions ->
                    Optional.of(new ClanRoleSettingsSnapshot(
                            clan,
                            roleKey,
                            roleDefinition.title(),
                            rolePermissionService.effectivePermissionsForRole(permissions, roleKey)
                    ))
            );
        });
    }
}
