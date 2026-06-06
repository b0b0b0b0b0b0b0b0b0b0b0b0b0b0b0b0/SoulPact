package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipHistoryRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ClanMemberDetailDataService {

    private static final int HISTORY_LIMIT = 5;

    private final ClanRepository clanRepository;
    private final ClanMembershipHistoryRepository historyRepository;
    private final RoleThemeService roleThemeService;
    private final ClanMemberManagementPlanner managementPlanner;
    private final ClanRolePermissionService rolePermissionService;

    public ClanMemberDetailDataService(
            ClanRepository clanRepository,
            ClanMembershipHistoryRepository historyRepository,
            RoleThemeService roleThemeService,
            ClanMemberManagementPlanner managementPlanner,
            ClanRolePermissionService rolePermissionService
    ) {
        this.clanRepository = clanRepository;
        this.historyRepository = historyRepository;
        this.roleThemeService = roleThemeService;
        this.managementPlanner = managementPlanner;
        this.rolePermissionService = rolePermissionService;
    }

    public CompletableFuture<Optional<ClanMemberDetailSnapshot>> load(long clanId, UUID memberId, UUID viewerId) {
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            var clan = clanOptional.get();
            return clanRepository.findMembersByClanId(clan.id()).thenCompose(members -> {
                Optional<bm.b0b0b0.SoulPact.clan.model.ClanMember> memberOptional = members.stream()
                        .filter(member -> member.playerId().equals(memberId))
                        .findFirst();
                if (memberOptional.isEmpty()) {
                    return CompletableFuture.completedFuture(Optional.empty());
                }
                var member = memberOptional.get();
                RoleDefinition roleDefinition = roleThemeService.theme().definition(member.role());
                String roleTitle = roleDefinition == null ? member.role() : roleDefinition.title();
                return rolePermissionService.loadByClanId(clan.id()).thenCompose(permissions -> {
                    var managementActions = managementPlanner.plan(clan, member, viewerId, members, permissions);
                    return historyRepository.findByPlayerId(memberId, HISTORY_LIMIT).thenApply(history ->
                            Optional.of(new ClanMemberDetailSnapshot(
                                    clan,
                                    member,
                                    ClanPlayerNames.displayName(memberId),
                                    roleTitle,
                                    history,
                                    managementActions
                            ))
                    );
                });
            });
        });
    }
}
