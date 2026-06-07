package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanKickService {

    private final ClanRepository clanRepository;
    private final ClanMembershipHistoryService membershipHistoryService;
    private final ClanRolePermissionService rolePermissionService;
    private final RoleThemeService roleThemeService;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanKickService(
            ClanRepository clanRepository,
            ClanMembershipHistoryService membershipHistoryService,
            ClanRolePermissionService rolePermissionService,
            RoleThemeService roleThemeService,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.clanRepository = clanRepository;
        this.membershipHistoryService = membershipHistoryService;
        this.rolePermissionService = rolePermissionService;
        this.roleThemeService = roleThemeService;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public CompletableFuture<Boolean> kick(Player actor, long clanId, UUID targetId) {
        if (actor.getUniqueId().equals(targetId)) {
            notify(actor, "clan.kick.self");
            return CompletableFuture.completedFuture(false);
        }
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                notify(actor, "clan.kick.target-not-found");
                return CompletableFuture.completedFuture(false);
            }
            Clan clan = clanOptional.get();
            return clanRepository.findMembersByClanId(clan.id()).thenCompose(members ->
                    rolePermissionService.loadByClanId(clan.id()).thenCompose(permissions ->
                            findMember(members, targetId).map(target -> {
                                if (!ClanStaffPermissions.canKick(
                                        clan,
                                        members,
                                        actor.getUniqueId(),
                                        target,
                                        permissions,
                                        roleThemeService.theme()
                                )) {
                                    notifyKickDenied(actor, clan, members, target, permissions);
                                    return CompletableFuture.completedFuture(false);
                                }
                                return membershipHistoryService.recordKick(clan, target, System.currentTimeMillis())
                                        .thenCompose(ignored -> clanRepository.removeMember(clan.id(), targetId))
                                        .thenApply(removed -> {
                                            asyncDatabaseExecutor.runSync(() -> {
                                                if (!actor.isOnline()) {
                                                    return;
                                                }
                                                if (!removed) {
                                                    messageService.send(actor, "clan.kick.failed");
                                                    return;
                                                }
                                                messageService.send(actor, "clan.kick.success", Map.of(
                                                        "player", ClanPlayerNames.displayName(targetId),
                                                        "tag", clan.tag()
                                                ));
                                                Player targetPlayer = Bukkit.getPlayer(targetId);
                                                if (targetPlayer != null && targetPlayer.isOnline()) {
                                                    messageService.send(targetPlayer, "clan.kick.target-notified", Map.of(
                                                            "tag", clan.tag()
                                                    ));
                                                }
                                            });
                                            return removed;
                                        });
                            }).orElseGet(() -> {
                                notify(actor, "clan.kick.target-not-found");
                                return CompletableFuture.completedFuture(false);
                            })
                    )
            );
        });
    }

    private void notifyKickDenied(
            Player actor,
            Clan clan,
            List<ClanMember> members,
            ClanMember target,
            bm.b0b0b0.SoulPact.clan.model.ClanRolePermissionMap permissions
    ) {
        if (ClanStaffPermissions.isLeader(clan, target.playerId())
                || ClanStaffPermissions.LEADER_ROLE.equals(target.role())) {
            notify(actor, "clan.kick.target-leader");
            return;
        }
        Optional<ClanMember> actorOptional = ClanStaffPermissions.findMember(members, actor.getUniqueId());
        if (actorOptional.isPresent()
                && !ClanStaffPermissions.isLeader(clan, actor.getUniqueId())
                && !ClanStaffPermissions.isLowerRank(roleThemeService.theme(), actorOptional.get().role(), target.role())) {
            notify(actor, "clan.kick.target-protected");
            return;
        }
        if (actorOptional.isPresent()
                && !ClanStaffPermissions.isLeader(clan, actor.getUniqueId())
                && !permissions.isEnabled(actorOptional.get().role(), bm.b0b0b0.SoulPact.api.clan.ClanPermissionKeys.KICK, false)) {
            notify(actor, "clan.kick.not-allowed");
        }
    }

    private static Optional<ClanMember> findMember(List<ClanMember> members, UUID playerId) {
        return members.stream().filter(member -> member.playerId().equals(playerId)).findFirst();
    }

    private void notify(Player player, String key) {
        asyncDatabaseExecutor.runSync(() -> messageService.send(player, key));
    }
}
