package bm.b0b0b0.SoulPact.core.api;

import bm.b0b0b0.SoulPact.api.platform.SoulPactClanAccess;
import bm.b0b0b0.SoulPact.api.clan.ClanMemberSnapshot;
import bm.b0b0b0.SoulPact.api.clan.ClanPermissionKeys;
import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.service.ClanPermissionEvaluator;
import bm.b0b0b0.SoulPact.clan.service.ClanRolePermissionService;
import bm.b0b0b0.SoulPact.clan.service.ClanStaffPermissions;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SoulPactClanAccessImpl implements SoulPactClanAccess {

    private final ClanRepository clanRepository;
    private final ClanRolePermissionService rolePermissionService;
    private final ClanPermissionEvaluator permissionEvaluator;

    public SoulPactClanAccessImpl(
            ClanRepository clanRepository,
            ClanRolePermissionService rolePermissionService,
            ClanPermissionEvaluator permissionEvaluator
    ) {
        this.clanRepository = clanRepository;
        this.rolePermissionService = rolePermissionService;
        this.permissionEvaluator = permissionEvaluator;
    }

    @Override
    public CompletableFuture<Optional<ClanMemberSnapshot>> findMember(long clanId, UUID playerId) {
        return clanRepository.findMembersByClanId(clanId).thenApply(members ->
                ClanStaffPermissions.findMember(members, playerId).map(member -> toSnapshot(clanId, member))
        );
    }

    @Override
    public CompletableFuture<Boolean> hasPermission(long clanId, UUID playerId, String permissionKey) {
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            Clan clan = clanOptional.get();
            if (ClanStaffPermissions.isLeader(clan, playerId)) {
                return CompletableFuture.completedFuture(true);
            }
            return findMember(clanId, playerId).thenCompose(memberOptional -> {
                if (memberOptional.isEmpty()) {
                    return CompletableFuture.completedFuture(false);
                }
                String role = memberOptional.get().role();
                if (ClanStaffPermissions.LEADER_ROLE.equals(role)) {
                    return CompletableFuture.completedFuture(true);
                }
                return rolePermissionService.loadByClanId(clanId).thenApply(permissions ->
                        permissions.isEnabled(
                                role,
                                permissionKey,
                                rolePermissionService.defaultFor(role, permissionKey)
                        )
                );
            });
        });
    }

    @Override
    public boolean hasPermissionSync(long clanId, UUID playerId, String permissionKey) {
        return permissionEvaluator.hasPermission(clanId, playerId, permissionKey);
    }

    private ClanMemberSnapshot toSnapshot(long clanId, ClanMember member) {
        return new ClanMemberSnapshot(
                clanId,
                member.playerId(),
                member.role(),
                member.nickname(),
                member.kills(),
                member.deaths(),
                member.joinedAt()
        );
    }
}
