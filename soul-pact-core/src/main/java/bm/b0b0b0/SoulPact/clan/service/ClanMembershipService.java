package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.event.ClanMemberJoinEvent;
import bm.b0b0b0.SoulPact.api.event.SoulPactEvents;
import bm.b0b0b0.SoulPact.clan.message.ClanPendingChatPresenter;
import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanInvite;
import bm.b0b0b0.SoulPact.clan.model.ClanJoinRequest;
import bm.b0b0b0.SoulPact.clan.model.ClanMembershipNotification;
import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipNotificationRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import bm.b0b0b0.SoulPact.core.module.ClanExtensionMembershipNotifier;
import bm.b0b0b0.SoulPact.core.placeholder.ClanPlaceholderInvalidatorRegistry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanMembershipService {

    private static final String NOTIFICATION_ACCEPTED = "accepted";
    private static final String NOTIFICATION_DENIED = "denied";
    private static final String NOTIFICATION_BLOCKED = "blocked";

    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;
    private final ClanMembershipNotificationRepository notificationRepository;
    private final ClanTargetResolver targetResolver;
    private final ClanPendingChatPresenter pendingChatPresenter;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;
    private final ClanRolePermissionService rolePermissionService;
    private final ClanExtensionMembershipNotifier extensionMembershipNotifier;

    public ClanMembershipService(
            ClanRepository clanRepository,
            ClanMembershipRepository membershipRepository,
            ClanMembershipNotificationRepository notificationRepository,
            ClanTargetResolver targetResolver,
            ClanPendingChatPresenter pendingChatPresenter,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor,
            ClanRolePermissionService rolePermissionService,
            ClanExtensionMembershipNotifier extensionMembershipNotifier
    ) {
        this.clanRepository = clanRepository;
        this.membershipRepository = membershipRepository;
        this.notificationRepository = notificationRepository;
        this.targetResolver = targetResolver;
        this.pendingChatPresenter = pendingChatPresenter;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
        this.rolePermissionService = rolePermissionService;
        this.extensionMembershipNotifier = extensionMembershipNotifier;
    }

    public void submitJoinRequest(Player player, String target) {
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(playerClanOptional -> {
            if (playerClanOptional.isPresent()) {
                notify(player, "clan.request.already-in-clan");
                return CompletableFuture.completedFuture(null);
            }
            return targetResolver.resolveClan(target).thenCompose(clanOptional -> {
                if (clanOptional.isEmpty()) {
                    notify(player, "clan.request.target-not-found");
                    return CompletableFuture.completedFuture(null);
                }
                Clan clan = clanOptional.get();
                if (!clan.joinRequestsOpen()) {
                    notify(player, "clan.request.closed");
                    return CompletableFuture.completedFuture(null);
                }
                return membershipRepository.isJoinBlocked(clan.id(), player.getUniqueId()).thenCompose(blocked -> {
                    if (blocked) {
                        notify(player, "clan.request.blocked");
                        return CompletableFuture.completedFuture(null);
                    }
                    return clanRepository.countMembers(clan.id()).thenCompose(memberCount -> {
                        if (memberCount >= clan.maxSlots()) {
                            notify(player, "clan.request.clan-full");
                            return CompletableFuture.completedFuture(null);
                        }
                        return membershipRepository.findJoinRequest(clan.id(), player.getUniqueId()).thenCompose(existing -> {
                            if (existing.isPresent()) {
                                notify(player, "clan.request.already-sent");
                                return CompletableFuture.completedFuture(null);
                            }
                            long createdAt = System.currentTimeMillis();
                            return membershipRepository.createJoinRequest(clan.id(), player.getUniqueId(), createdAt)
                                    .thenAccept(request -> asyncDatabaseExecutor.runSync(() -> {
                                        if (!player.isOnline()) {
                                            return;
                                        }
                                        messageService.send(player, "clan.request.sent", Map.of(
                                                "tag", clan.tag(),
                                                "name", clan.name(),
                                                "id", String.valueOf(clan.id())
                                        ));
                                        notifyLeaderAboutRequest(clan, request, player.getName());
                                    }));
                        });
                    });
                });
            });
        });
    }

    public void inviteMember(Player inviter, String targetName) {
        clanRepository.findByPlayerId(inviter.getUniqueId()).thenCompose(inviterClanOptional -> {
            if (inviterClanOptional.isEmpty()) {
                notify(inviter, "clan.invite.not-in-clan");
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = inviterClanOptional.get();
            if (!clan.leaderId().equals(inviter.getUniqueId())) {
                notify(inviter, "clan.invite.not-leader");
                return CompletableFuture.completedFuture(null);
            }
            Optional<UUID> targetIdOptional = targetResolver.resolvePlayerId(targetName);
            if (targetIdOptional.isEmpty()) {
                notify(inviter, "clan.invite.player-not-found");
                return CompletableFuture.completedFuture(null);
            }
            UUID targetId = targetIdOptional.get();
            if (targetId.equals(inviter.getUniqueId())) {
                notify(inviter, "clan.invite.self");
                return CompletableFuture.completedFuture(null);
            }
            return clanRepository.findByPlayerId(targetId).thenCompose(targetClanOptional -> {
                if (targetClanOptional.isPresent()) {
                    notify(inviter, "clan.invite.target-in-clan");
                    return CompletableFuture.completedFuture(null);
                }
                return clanRepository.countMembers(clan.id()).thenCompose(memberCount -> {
                    if (memberCount >= clan.maxSlots()) {
                        notify(inviter, "clan.invite.clan-full");
                        return CompletableFuture.completedFuture(null);
                    }
                    return membershipRepository.findInvite(clan.id(), targetId).thenCompose(existing -> {
                        if (existing.isPresent()) {
                            notify(inviter, "clan.invite.already-sent");
                            return CompletableFuture.completedFuture(null);
                        }
                        long createdAt = System.currentTimeMillis();
                        return membershipRepository.createInvite(clan.id(), targetId, inviter.getUniqueId(), createdAt)
                                .thenAccept(invite -> asyncDatabaseExecutor.runSync(() -> {
                                    if (!inviter.isOnline()) {
                                        return;
                                    }
                                    messageService.send(inviter, "clan.invite.sent", Map.of(
                                            "player", ClanPlayerNames.displayName(targetId),
                                            "tag", clan.tag()
                                    ));
                                    Player targetPlayer = Bukkit.getPlayer(targetId);
                                    if (targetPlayer != null && targetPlayer.isOnline()) {
                                        pendingChatPresenter.showInvite(
                                                targetPlayer,
                                                invite,
                                                clan,
                                                inviter.getName()
                                        );
                                    }
                                }));
                    });
                });
            });
        });
    }

    public void acceptInvite(Player player, long inviteId) {
        membershipRepository.findInviteById(inviteId).thenCompose(inviteOptional -> {
            if (inviteOptional.isEmpty()) {
                notify(player, "clan.invite.not-found");
                return CompletableFuture.completedFuture(null);
            }
            ClanInvite invite = inviteOptional.get();
            if (!invite.playerId().equals(player.getUniqueId())) {
                notify(player, "clan.invite.not-found");
                return CompletableFuture.completedFuture(null);
            }
            return finalizeJoin(player.getUniqueId(), invite.clanId(), "clan.invite.accepted", player);
        });
    }

    public void denyInvite(Player player, long inviteId) {
        membershipRepository.findInviteById(inviteId).thenCompose(inviteOptional -> {
            if (inviteOptional.isEmpty() || !inviteOptional.get().playerId().equals(player.getUniqueId())) {
                notify(player, "clan.invite.not-found");
                return CompletableFuture.completedFuture(null);
            }
            return membershipRepository.deleteInvite(inviteId).thenAccept(deleted -> notify(player, "clan.invite.denied"));
        });
    }

    public CompletableFuture<Void> acceptRequest(Player staff, long requestId) {
        return resolveStaffRequest(staff, requestId).thenCompose(requestOptional -> {
            if (requestOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            ClanJoinRequest request = requestOptional.get();
            return finalizeJoin(
                    request.playerId(),
                    request.clanId(),
                    "clan.request.accepted-leader",
                    staff,
                    request.id()
            );
        });
    }

    public CompletableFuture<Void> denyRequest(Player staff, long requestId) {
        return resolveStaffRequest(staff, requestId).thenCompose(requestOptional -> {
            if (requestOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            ClanJoinRequest request = requestOptional.get();
            return clanRepository.findById(request.clanId()).thenCompose(clanOptional -> {
                if (clanOptional.isEmpty()) {
                    return CompletableFuture.completedFuture(null);
                }
                Clan clan = clanOptional.get();
                return membershipRepository.deleteJoinRequest(request.id()).thenCompose(ignored ->
                        notifyPlayerAboutRequestDecision(request.playerId(), NOTIFICATION_DENIED, clan)
                ).thenAccept(ignored -> notify(staff, "clan.request.denied-leader", Map.of(
                        "player", ClanPlayerNames.displayName(request.playerId())
                )));
            });
        });
    }

    public CompletableFuture<Void> blockRequest(Player leader, long requestId) {
        return resolveLeaderRequest(leader, requestId).thenCompose(requestOptional -> {
            if (requestOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            ClanJoinRequest request = requestOptional.get();
            return clanRepository.findById(request.clanId()).thenCompose(clanOptional -> {
                if (clanOptional.isEmpty()) {
                    return CompletableFuture.completedFuture(null);
                }
                Clan clan = clanOptional.get();
                long blockedAt = System.currentTimeMillis();
                return membershipRepository.deleteJoinRequest(request.id()).thenCompose(ignored ->
                        membershipRepository.createJoinBlock(request.clanId(), request.playerId(), blockedAt)
                ).thenCompose(ignored ->
                        notifyPlayerAboutRequestDecision(request.playerId(), NOTIFICATION_BLOCKED, clan)
                ).thenAccept(ignored -> notify(leader, "clan.request.blocked-leader", Map.of(
                        "player", ClanPlayerNames.displayName(request.playerId())
                )));
            });
        });
    }

    public CompletableFuture<Void> acceptAllRequests(Player staff) {
        return resolveStaffClan(staff).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = clanOptional.get();
            return membershipRepository.findJoinRequestsByClanId(clan.id()).thenCompose(requests -> {
                if (requests.isEmpty()) {
                    return CompletableFuture.completedFuture(null);
                }
                CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
                for (ClanJoinRequest request : requests) {
                    chain = chain.thenCompose(ignored -> finalizeJoin(
                            request.playerId(),
                            request.clanId(),
                            "clan.request.accepted-leader",
                            staff,
                            request.id()
                    ));
                }
                return chain.thenAccept(ignored -> notify(staff, "clan.request.bulk.accepted"));
            });
        });
    }

    public CompletableFuture<Void> denyAllRequests(Player staff) {
        return resolveStaffClan(staff).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = clanOptional.get();
            return membershipRepository.deleteJoinRequestsByClanId(clan.id()).thenCompose(requests -> {
                if (requests.isEmpty()) {
                    return CompletableFuture.completedFuture(null);
                }
                CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
                for (ClanJoinRequest request : requests) {
                    chain = chain.thenCompose(ignored ->
                            notifyPlayerAboutRequestDecision(request.playerId(), NOTIFICATION_DENIED, clan)
                    );
                }
                return chain.thenAccept(ignored -> notify(staff, "clan.request.bulk.denied"));
            });
        });
    }

    public CompletableFuture<Void> blockAllRequests(Player leader) {
        return resolveLeaderClan(leader).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = clanOptional.get();
            long blockedAt = System.currentTimeMillis();
            return membershipRepository.deleteJoinRequestsByClanId(clan.id()).thenCompose(requests -> {
                if (requests.isEmpty()) {
                    return CompletableFuture.completedFuture(null);
                }
                CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
                for (ClanJoinRequest request : requests) {
                    chain = chain.thenCompose(ignored ->
                            membershipRepository.createJoinBlock(clan.id(), request.playerId(), blockedAt)
                    ).thenCompose(ignored ->
                            notifyPlayerAboutRequestDecision(request.playerId(), NOTIFICATION_BLOCKED, clan)
                    );
                }
                return chain.thenAccept(ignored -> notify(leader, "clan.request.bulk.blocked"));
            });
        });
    }

    public CompletableFuture<Void> toggleJoinRequests(Player leader) {
        return resolveLeaderClan(leader).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = clanOptional.get();
            boolean nextValue = !clan.joinRequestsOpen();
            return clanRepository.updateJoinRequestsOpen(clan.id(), nextValue).thenAccept(updated -> {
                if (!updated) {
                    notify(leader, "clan.request.toggle.failed");
                    return;
                }
                notify(leader, nextValue ? "clan.request.toggle.opened" : "clan.request.toggle.closed");
            });
        });
    }

    public void deliverPending(Player player) {
        notificationRepository.findByPlayerId(player.getUniqueId()).thenAccept(notifications -> {
            if (notifications.isEmpty()) {
                deliverPendingInvitesAndChatRequests(player);
                return;
            }
            asyncDatabaseExecutor.runSync(() -> {
                if (!player.isOnline()) {
                    return;
                }
                for (ClanMembershipNotification notification : notifications) {
                    deliverStoredNotification(player, notification);
                }
            });
            notificationRepository.deleteByPlayerId(player.getUniqueId()).thenAccept(ignored ->
                    deliverPendingInvitesAndChatRequests(player)
            );
        });
    }

    private void deliverPendingInvitesAndChatRequests(Player player) {
        membershipRepository.findInvitesByPlayerId(player.getUniqueId()).thenAccept(invites -> {
            for (ClanInvite invite : invites) {
                clanRepository.findById(invite.clanId()).thenAccept(clanOptional -> clanOptional.ifPresent(clan ->
                        asyncDatabaseExecutor.runSync(() -> {
                            if (!player.isOnline()) {
                                return;
                            }
                            pendingChatPresenter.showInvite(
                                    player,
                                    invite,
                                    clan,
                                    ClanPlayerNames.displayName(invite.inviterId())
                            );
                        })
                ));
            }
        });
    }

    private CompletableFuture<Optional<Clan>> resolveStaffClan(Player staff) {
        return clanRepository.findByPlayerId(staff.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                notify(staff, "clan.request.not-staff");
                return CompletableFuture.completedFuture(Optional.empty());
            }
            Clan clan = clanOptional.get();
            return clanRepository.findMembersByClanId(clan.id()).thenCompose(members ->
                    rolePermissionService.loadByClanId(clan.id()).thenApply(permissions -> {
                        if (!ClanStaffPermissions.canReviewRequests(clan, members, staff.getUniqueId(), permissions)) {
                            notify(staff, "clan.request.not-staff");
                            return Optional.empty();
                        }
                        return Optional.of(clan);
                    })
            );
        });
    }

    private CompletableFuture<Optional<ClanJoinRequest>> resolveStaffRequest(Player staff, long requestId) {
        return membershipRepository.findJoinRequestById(requestId).thenCompose(requestOptional -> {
            if (requestOptional.isEmpty()) {
                notify(staff, "clan.request.not-found");
                return CompletableFuture.completedFuture(Optional.empty());
            }
            ClanJoinRequest request = requestOptional.get();
            return clanRepository.findById(request.clanId()).thenCompose(clanOptional -> {
                if (clanOptional.isEmpty()) {
                    notify(staff, "clan.request.not-found");
                    return CompletableFuture.completedFuture(Optional.empty());
                }
                var clan = clanOptional.get();
                return clanRepository.findMembersByClanId(clan.id()).thenCompose(members ->
                        rolePermissionService.loadByClanId(clan.id()).thenApply(permissions -> {
                            if (!ClanStaffPermissions.canReviewRequests(clan, members, staff.getUniqueId(), permissions)) {
                                notify(staff, "clan.request.not-staff");
                                return Optional.empty();
                            }
                            return Optional.of(request);
                        })
                );
            });
        });
    }

    private CompletableFuture<Optional<Clan>> resolveLeaderClan(Player leader) {
        return clanRepository.findByPlayerId(leader.getUniqueId()).thenApply(clanOptional -> {
            if (clanOptional.isEmpty() || !clanOptional.get().leaderId().equals(leader.getUniqueId())) {
                notify(leader, "clan.request.not-leader");
                return Optional.empty();
            }
            return clanOptional;
        });
    }

    private CompletableFuture<Optional<ClanJoinRequest>> resolveLeaderRequest(Player leader, long requestId) {
        return membershipRepository.findJoinRequestById(requestId).thenCompose(requestOptional -> {
            if (requestOptional.isEmpty()) {
                notify(leader, "clan.request.not-found");
                return CompletableFuture.completedFuture(Optional.empty());
            }
            ClanJoinRequest request = requestOptional.get();
            return clanRepository.findById(request.clanId()).thenApply(clanOptional -> {
                if (clanOptional.isEmpty() || !clanOptional.get().leaderId().equals(leader.getUniqueId())) {
                    notify(leader, "clan.request.not-leader");
                    return Optional.empty();
                }
                return Optional.of(request);
            });
        });
    }

    private CompletableFuture<Void> finalizeJoin(
            UUID playerId,
            long clanId,
            String leaderSuccessKey,
            Player notifyReceiver
    ) {
        return finalizeJoin(playerId, clanId, leaderSuccessKey, notifyReceiver, null);
    }

    private CompletableFuture<Void> finalizeJoin(
            UUID playerId,
            long clanId,
            String leaderSuccessKey,
            Player notifyReceiver,
            Long requestIdToDelete
    ) {
        return clanRepository.findByPlayerId(playerId).thenCompose(playerClanOptional -> {
            if (playerClanOptional.isPresent()) {
                notify(notifyReceiver, "clan.join.already-in-clan");
                return CompletableFuture.completedFuture(null);
            }
            return clanRepository.findById(clanId).thenCompose(clanOptional -> {
                if (clanOptional.isEmpty()) {
                    notify(notifyReceiver, "clan.join.clan-not-found");
                    return CompletableFuture.completedFuture(null);
                }
                Clan clan = clanOptional.get();
                return clanRepository.countMembers(clan.id()).thenCompose(memberCount -> {
                    if (memberCount >= clan.maxSlots()) {
                        notify(notifyReceiver, "clan.join.clan-full");
                        return CompletableFuture.completedFuture(null);
                    }
                    long joinedAt = System.currentTimeMillis();
                    return clanRepository.addMember(clan.id(), playerId, "member", joinedAt).thenCompose(added -> {
                        if (!added) {
                            notify(notifyReceiver, "clan.join.failed");
                            return CompletableFuture.completedFuture(null);
                        }
                        extensionMembershipNotifier.memberJoined(clan.id(), playerId);
                        ClanPlaceholderInvalidatorRegistry.invalidateClan(clan.id());
                        ClanPlaceholderInvalidatorRegistry.invalidatePlayer(playerId);
                        CompletableFuture<Integer> deleteRequestFuture = requestIdToDelete == null
                                ? CompletableFuture.completedFuture(0)
                                : membershipRepository.deleteJoinRequest(requestIdToDelete).thenApply(deleted -> deleted ? 1 : 0);
                        return deleteRequestFuture.thenCompose(ignored ->
                                membershipRepository.deleteInvitesByPlayerId(playerId)
                        ).thenCompose(ignored ->
                                membershipRepository.deleteJoinRequestsByPlayerId(playerId)
                        ).thenCompose(ignored ->
                                notifyPlayerAboutRequestDecision(playerId, NOTIFICATION_ACCEPTED, clan)
                        ).thenAccept(ignored -> asyncDatabaseExecutor.runSync(() -> {
                            SoulPactEvents.fire(new ClanMemberJoinEvent(
                                    clan.id(),
                                    clan.tag(),
                                    playerId,
                                    ClanPlayerNames.displayName(playerId)
                            ));
                            if (!notifyReceiver.isOnline()) {
                                return;
                            }
                            messageService.send(notifyReceiver, leaderSuccessKey, Map.of(
                                    "tag", clan.tag(),
                                    "name", clan.name(),
                                    "player", ClanPlayerNames.displayName(playerId)
                            ));
                        }));
                    });
                });
            });
        });
    }

    private CompletableFuture<Void> notifyPlayerAboutRequestDecision(UUID playerId, String kind, Clan clan) {
        Player onlinePlayer = Bukkit.getPlayer(playerId);
        if (onlinePlayer != null && onlinePlayer.isOnline()) {
            asyncDatabaseExecutor.runSync(() -> deliverImmediateNotification(onlinePlayer, kind, clan));
            return CompletableFuture.completedFuture(null);
        }
        return notificationRepository.create(
                playerId,
                kind,
                clan.id(),
                clan.tag(),
                clan.name(),
                System.currentTimeMillis()
        );
    }

    private void deliverStoredNotification(Player player, ClanMembershipNotification notification) {
        Clan clan = new Clan(
                notification.clanId(),
                notification.clanTag(),
                notification.clanName(),
                "",
                UUID.randomUUID(),
                0,
                0,
                0,
                false,
                false,
                true,
                notification.createdAt()
        );
        deliverImmediateNotification(player, notification.kind(), clan);
    }

    private void deliverImmediateNotification(Player player, String kind, Clan clan) {
        switch (kind) {
            case NOTIFICATION_ACCEPTED -> pendingChatPresenter.showRequestAccepted(player, clan);
            case NOTIFICATION_DENIED -> messageService.send(player, "clan.request.denied-player", Map.of(
                    "tag", clan.tag(),
                    "name", clan.name()
            ));
            case NOTIFICATION_BLOCKED -> messageService.send(player, "clan.request.blocked-player", Map.of(
                    "tag", clan.tag(),
                    "name", clan.name()
            ));
            default -> {
            }
        }
    }

    private void notifyLeaderAboutRequest(Clan clan, ClanJoinRequest request, String playerName) {
        Player leader = Bukkit.getPlayer(clan.leaderId());
        if (leader == null || !leader.isOnline()) {
            return;
        }
        pendingChatPresenter.showJoinRequest(leader, request, clan, playerName);
    }

    private void notify(Player player, String key) {
        notify(player, key, Map.of());
    }

    private void notify(Player player, String key, Map<String, String> placeholders) {
        asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            messageService.send(player, key, placeholders);
        });
    }
}
