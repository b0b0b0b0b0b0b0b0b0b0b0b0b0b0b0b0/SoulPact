package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipHistoryRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanRequestDetailDataService {

    private static final int HISTORY_LIMIT = 8;

    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;
    private final ClanMembershipHistoryRepository historyRepository;
    private final ClanRolePermissionService rolePermissionService;

    public ClanRequestDetailDataService(
            ClanRepository clanRepository,
            ClanMembershipRepository membershipRepository,
            ClanMembershipHistoryRepository historyRepository,
            ClanRolePermissionService rolePermissionService
    ) {
        this.clanRepository = clanRepository;
        this.membershipRepository = membershipRepository;
        this.historyRepository = historyRepository;
        this.rolePermissionService = rolePermissionService;
    }

    public CompletableFuture<Optional<ClanRequestDetailSnapshot>> load(Player player, long requestId) {
        return membershipRepository.findJoinRequestById(requestId).thenCompose(requestOptional -> {
            if (requestOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            var request = requestOptional.get();
            return clanRepository.findById(request.clanId()).thenCompose(clanOptional -> {
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
                            return historyRepository.findByPlayerId(request.playerId(), HISTORY_LIMIT)
                                    .thenApply(history -> Optional.of(new ClanRequestDetailSnapshot(
                                            request,
                                            ClanPlayerNames.displayName(request.playerId()),
                                            history,
                                            leaderControls
                                    )));
                        })
                );
            });
        });
    }
}
