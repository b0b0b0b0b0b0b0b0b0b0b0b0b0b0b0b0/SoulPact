package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanRequestsDataService {

    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;
    private final ClanRolePermissionService rolePermissionService;

    public ClanRequestsDataService(
            ClanRepository clanRepository,
            ClanMembershipRepository membershipRepository,
            ClanRolePermissionService rolePermissionService
    ) {
        this.clanRepository = clanRepository;
        this.membershipRepository = membershipRepository;
        this.rolePermissionService = rolePermissionService;
    }

    public CompletableFuture<Optional<ClanRequestsSnapshot>> load(Player player) {
        return clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            var clan = clanOptional.get();
            return clanRepository.findMembersByClanId(clan.id()).thenCompose(members ->
                    rolePermissionService.loadByClanId(clan.id()).thenCompose(permissions -> {
                        if (!ClanStaffPermissions.canReviewRequests(clan, members, player.getUniqueId(), permissions)) {
                            return CompletableFuture.completedFuture(Optional.empty());
                        }
                        boolean leaderControls = ClanStaffPermissions.canManageRecruitmentSettings(clan, player.getUniqueId());
                        return membershipRepository.findJoinRequestsByClanId(clan.id())
                                .thenApply(requests -> Optional.of(new ClanRequestsSnapshot(clan, requests, leaderControls)));
                    })
            );
        });
    }
}
