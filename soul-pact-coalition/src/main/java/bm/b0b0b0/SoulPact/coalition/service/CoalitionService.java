package bm.b0b0b0.SoulPact.coalition.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionAllySnapshot;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayExtras;
import bm.b0b0b0.SoulPact.coalition.config.CoalitionConfig;
import bm.b0b0b0.SoulPact.coalition.message.CoalitionInviteChatPresenter;
import bm.b0b0b0.SoulPact.coalition.message.CoalitionMessages;
import bm.b0b0b0.SoulPact.coalition.model.CoalitionInviteRecord;
import bm.b0b0b0.SoulPact.coalition.model.CoalitionInviteStatuses;
import bm.b0b0b0.SoulPact.coalition.repository.CoalitionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class CoalitionService {

    private final SoulPactApi api;
    private final CoalitionConfig config;
    private final CoalitionMessages messages;
    private final CoalitionRepository repository;
    private final CoalitionMembershipCache membershipCache;
    private final CoalitionClanLookup clanLookup;
    private final CoalitionInviteChatPresenter inviteChatPresenter;
    private final CoalitionBossBarService bossBarService;
    private final CoalitionPlayerClanCache playerClanCache;

    public CoalitionService(
            SoulPactApi api,
            CoalitionConfig config,
            CoalitionMessages messages,
            CoalitionRepository repository,
            CoalitionMembershipCache membershipCache,
            CoalitionClanLookup clanLookup,
            CoalitionInviteChatPresenter inviteChatPresenter,
            CoalitionBossBarService bossBarService,
            CoalitionPlayerClanCache playerClanCache
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.repository = repository;
        this.membershipCache = membershipCache;
        this.clanLookup = clanLookup;
        this.inviteChatPresenter = inviteChatPresenter;
        this.bossBarService = bossBarService;
        this.playerClanCache = playerClanCache;
    }

    public void bootstrapCache() {
        api.scheduler().runAsync(membershipCache::reload)
                .thenAccept(ignored -> api.scheduler().runSync(bossBarService::tick));
        for (Player player : Bukkit.getOnlinePlayers()) {
            trackPlayer(player);
        }
    }

    public void trackPlayer(Player player) {
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> {
            if (clanOptional.isPresent()) {
                playerClanCache.put(player.getUniqueId(), clanOptional.get().id());
            } else {
                playerClanCache.remove(player.getUniqueId());
            }
            api.scheduler().runSync(() -> bossBarService.refreshPlayer(player));
        });
    }

    public CompletableFuture<String> coalitionLineForList(long clanId) {
        List<Long> allies = membershipCache.otherMembers(clanId);
        if (allies.isEmpty()) {
            return CompletableFuture.completedFuture("");
        }
        CompletableFuture<String> chain = CompletableFuture.completedFuture("");
        for (long allyId : allies) {
            chain = chain.thenCompose(current -> clanLookup.findClan(allyId).thenApply(clanOptional -> {
                if (clanOptional.isEmpty()) {
                    return current;
                }
                String tag = "[" + clanOptional.get().tag() + "]";
                return current.isBlank() ? tag : current + ", " + tag;
            }));
        }
        return chain;
    }

    public CompletableFuture<List<CoalitionAllySnapshot>> alliesForInfo(long clanId) {
        List<Long> allies = membershipCache.otherMembers(clanId);
        if (allies.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }
        CompletableFuture<List<CoalitionAllySnapshot>> chain = CompletableFuture.completedFuture(new ArrayList<>());
        for (long allyId : allies.stream().limit(config.maxMembers() - 1).toList()) {
            chain = chain.thenCompose(list -> clanLookup.findClan(allyId).thenApply(clanOptional -> {
                clanOptional.ifPresent(clan -> list.add(new CoalitionAllySnapshot(
                        clan.id(),
                        clan.tag(),
                        clan.name(),
                        clan.leaderId()
                )));
                return list;
            }));
        }
        return chain;
    }

    public CompletableFuture<CoalitionDisplayExtras> enrichInfoView(Player viewer, long targetClanId) {
        return coalitionLineForList(targetClanId).thenCombine(
                alliesForInfo(targetClanId),
                (line, allies) -> new CoalitionDisplayExtras(line, allies, false)
        ).thenCombine(canShowInviteFromInfo(viewer, targetClanId), (extras, showInvite) ->
                new CoalitionDisplayExtras(extras.coalitionLine(), extras.allies(), showInvite)
        );
    }

    public CompletableFuture<Boolean> inviteByClanId(Player player, long targetClanId) {
        return clanLookup.findClan(targetClanId).thenCompose(targetOptional -> {
            if (targetOptional.isEmpty()) {
                api.scheduler().runSync(() -> messages.send(player, "coalition.error.target-not-found"));
                return CompletableFuture.completedFuture(false);
            }
            return invite(player, targetOptional.get().tag());
        });
    }

    public void handleInfoInviteClick(Player player, long targetClanId, int listPage) {
        player.closeInventory();
        inviteByClanId(player, targetClanId);
    }

    public CompletableFuture<Boolean> canShowInviteFromInfo(Player viewer, long targetClanId) {
        return api.findClanByPlayer(viewer.getUniqueId()).thenCompose(viewerClanOptional -> {
            if (viewerClanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            ClanSnapshot viewerClan = viewerClanOptional.get();
            if (!viewerClan.leaderId().equals(viewer.getUniqueId()) || viewerClan.id() == targetClanId) {
                return CompletableFuture.completedFuture(false);
            }
            if (membershipCache.coalitionsOverlap(viewerClan.id(), targetClanId)) {
                return CompletableFuture.completedFuture(false);
            }
            return api.scheduler().supplyAsync(() -> canInviteToCoalition(viewerClan.id(), targetClanId));
        });
    }

    public CompletableFuture<Boolean> canDeclareWar(long attackerClanId, long defenderClanId) {
        return api.scheduler().supplyAsync(() -> !membershipCache.coalitionsOverlap(attackerClanId, defenderClanId));
    }

    public boolean allowsAllyFlagBreak(long breakerClanId, long baseOwnerClanId, long warAttackerClanId, long warDefenderClanId) {
        long aggressorSide;
        if (warDefenderClanId == baseOwnerClanId) {
            aggressorSide = warAttackerClanId;
        } else if (warAttackerClanId == baseOwnerClanId) {
            aggressorSide = warDefenderClanId;
        } else {
            return false;
        }
        if (breakerClanId == aggressorSide) {
            return false;
        }
        return membershipCache.sharesCoalition(breakerClanId, aggressorSide);
    }

    public CompletableFuture<Boolean> invite(Player player, String targetTag) {
        return resolveLeaderClan(player).thenCompose(leaderClanOptional -> {
            if (leaderClanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            ClanSnapshot inviterClan = leaderClanOptional.get();
            return clanLookup.findClanByTag(targetTag).thenCompose(targetOptional -> {
                if (targetOptional.isEmpty()) {
                    api.scheduler().runSync(() -> messages.send(player, "coalition.error.target-not-found"));
                    return CompletableFuture.completedFuture(false);
                }
                ClanSnapshot targetClan = targetOptional.get();
                if (targetClan.id() == inviterClan.id()) {
                    api.scheduler().runSync(() -> messages.send(player, "coalition.error.self"));
                    return CompletableFuture.completedFuture(false);
                }
                if (membershipCache.coalitionsOverlap(inviterClan.id(), targetClan.id())) {
                    api.scheduler().runSync(() -> messages.send(player, "coalition.error.already-allied"));
                    return CompletableFuture.completedFuture(false);
                }
                return api.scheduler().supplyAsync(() -> prepareInvite(inviterClan, targetClan, player))
                        .thenApply(inviteId -> {
                            if (inviteId == -2L) {
                                api.scheduler().runSync(() -> messages.send(player, "coalition.error.blocked-by-target"));
                                return false;
                            }
                            if (inviteId == -1L) {
                                api.scheduler().runSync(() -> messages.send(player, "coalition.error.already-sent"));
                                return false;
                            }
                            if (inviteId <= 0L) {
                                api.scheduler().runSync(() -> messages.send(player, "coalition.error.coalition-full"));
                                return false;
                            }
                            api.scheduler().runSync(() -> {
                                messages.send(player, "coalition.invite.sent", Map.of(
                                        "tag", targetClan.tag()
                                ));
                                repository.findPendingInvite(inviteId).ifPresent(inviteChatPresenter::notifyInvite);
                            });
                            return true;
                        });
            });
        });
    }

    public CompletableFuture<Boolean> acceptInvite(Player player, long inviteId) {
        return resolveLeaderClan(player).thenCompose(leaderClanOptional -> {
            if (leaderClanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            ClanSnapshot targetClan = leaderClanOptional.get();
            return api.scheduler().supplyAsync(() ->
                    repository.findPendingInviteForTarget(targetClan.id(), inviteId)
            ).thenCompose(inviteOptional -> {
                if (inviteOptional.isEmpty()) {
                    api.scheduler().runSync(() -> messages.send(player, "coalition.error.invite-not-found"));
                    return CompletableFuture.completedFuture(false);
                }
                CoalitionInviteRecord invite = inviteOptional.get();
                return api.scheduler().supplyAsync(() -> finalizeAccept(invite, targetClan.id()))
                        .thenApply(accepted -> {
                            if (!accepted) {
                                api.scheduler().runSync(() -> messages.send(player, "coalition.error.coalition-full"));
                                return false;
                            }
                            api.scheduler().runSync(() -> {
                                messages.send(player, "coalition.invite.accepted");
                                inviteChatPresenter.notifyInviterAccepted(invite);
                                bossBarService.refreshCoalition(targetClan.id());
                                bossBarService.refreshCoalition(invite.inviterClanId());
                            });
                            return true;
                        });
            });
        });
    }

    public CompletableFuture<Boolean> denyInvite(Player player, long inviteId) {
        return resolveLeaderClan(player).thenCompose(leaderClanOptional -> {
            if (leaderClanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            long targetClanId = leaderClanOptional.get().id();
            return api.scheduler().supplyAsync(() ->
                    repository.findPendingInviteForTarget(targetClanId, inviteId)
            ).thenApply(inviteOptional -> {
                if (inviteOptional.isEmpty()) {
                    api.scheduler().runSync(() -> messages.send(player, "coalition.error.invite-not-found"));
                    return false;
                }
                repository.updateInviteStatus(inviteOptional.get().id(), CoalitionInviteStatuses.DENIED);
                CoalitionInviteRecord invite = inviteOptional.get();
                api.scheduler().runSync(() -> {
                    messages.send(player, "coalition.invite.denied");
                    inviteChatPresenter.notifyInviterDenied(invite);
                });
                return true;
            });
        });
    }

    public CompletableFuture<Boolean> blockInvite(Player player, long inviteId) {
        return resolveLeaderClan(player).thenCompose(leaderClanOptional -> {
            if (leaderClanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            long targetClanId = leaderClanOptional.get().id();
            return api.scheduler().supplyAsync(() ->
                    repository.findPendingInviteForTarget(targetClanId, inviteId)
            ).thenCompose(inviteOptional -> {
                if (inviteOptional.isEmpty()) {
                    api.scheduler().runSync(() -> messages.send(player, "coalition.error.invite-not-found"));
                    return CompletableFuture.completedFuture(false);
                }
                CoalitionInviteRecord invite = inviteOptional.get();
                return api.scheduler().supplyAsync(() -> {
                    repository.updateInviteStatus(invite.id(), CoalitionInviteStatuses.DENIED);
                    repository.blockInviter(targetClanId, invite.inviterClanId(), System.currentTimeMillis());
                    return invite;
                }).thenCompose(blockedInvite -> clanLookup.findClan(blockedInvite.inviterClanId()).thenApply(inviterOptional -> {
                    String inviterTag = inviterOptional.map(ClanSnapshot::tag)
                            .orElse("#" + blockedInvite.inviterClanId());
                    api.scheduler().runSync(() -> {
                        messages.send(player, "coalition.invite.blocked", Map.of("inviter_tag", inviterTag));
                        inviteChatPresenter.notifyInviterBlocked(blockedInvite);
                    });
                    return true;
                }));
            });
        });
    }

    public CompletableFuture<Boolean> leave(Player player) {
        return resolveLeaderClan(player).thenCompose(leaderClanOptional -> {
            if (leaderClanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            long clanId = leaderClanOptional.get().id();
            if (membershipCache.coalitionSize(clanId) <= 1) {
                api.scheduler().runSync(() -> messages.send(player, "coalition.error.not-in-coalition"));
                return CompletableFuture.completedFuture(false);
            }
            return api.scheduler().supplyAsync(() -> {
                repository.removeMember(clanId);
                membershipCache.removeClan(clanId);
                return true;
            }).thenApply(left -> {
                if (left) {
                    api.scheduler().runSync(() -> {
                        messages.send(player, "coalition.leave.success");
                        bossBarService.refreshCoalition(clanId);
                    });
                }
                return left;
            });
        });
    }

    public CompletableFuture<List<CoalitionAllySnapshot>> listMembersForLeader(Player player) {
        return resolveLeaderClan(player).thenCompose(leaderOptional -> {
            if (leaderOptional.isEmpty()) {
                return CompletableFuture.completedFuture(List.of());
            }
            return alliesForInfo(leaderOptional.get().id()).thenApply(allies -> {
                List<CoalitionAllySnapshot> members = new ArrayList<>(allies);
                ClanSnapshot self = leaderOptional.get();
                members.add(0, new CoalitionAllySnapshot(self.id(), self.tag(), self.name(), self.leaderId()));
                return members;
            });
        });
    }

    private boolean canInviteToCoalition(long inviterClanId, long targetClanId) {
        if (repository.isInviteBlocked(targetClanId, inviterClanId)) {
            return false;
        }
        if (repository.findCoalitionIdByClan(targetClanId).isPresent()) {
            return false;
        }
        Optional<Long> inviterCoalition = repository.findCoalitionIdByClan(inviterClanId);
        if (inviterCoalition.isPresent() && repository.countMembers(inviterCoalition.get()) >= config.maxMembers()) {
            return false;
        }
        return repository.listPendingForTarget(targetClanId).isEmpty();
    }

    private long prepareInvite(ClanSnapshot inviterClan, ClanSnapshot targetClan, Player player) {
        if (repository.isInviteBlocked(targetClan.id(), inviterClan.id())) {
            return -2L;
        }
        long coalitionId = repository.findCoalitionIdByClan(inviterClan.id()).orElseGet(() -> {
            long created = repository.createCoalition(System.currentTimeMillis());
            repository.addMember(created, inviterClan.id(), System.currentTimeMillis());
            membershipCache.putMember(created, inviterClan.id());
            return created;
        });
        if (repository.countMembers(coalitionId) >= config.maxMembers()) {
            return 0L;
        }
        if (!repository.listPendingForTarget(targetClan.id()).isEmpty()) {
            return -1L;
        }
        return repository.createInvite(
                coalitionId,
                inviterClan.id(),
                targetClan.id(),
                player.getUniqueId(),
                System.currentTimeMillis(),
                CoalitionInviteStatuses.PENDING
        );
    }

    private boolean finalizeAccept(CoalitionInviteRecord invite, long targetClanId) {
        if (repository.findCoalitionIdByClan(targetClanId).isPresent()) {
            return false;
        }
        if (repository.countMembers(invite.coalitionId()) >= config.maxMembers()) {
            return false;
        }
        repository.updateInviteStatus(invite.id(), CoalitionInviteStatuses.ACCEPTED);
        repository.addMember(invite.coalitionId(), targetClanId, System.currentTimeMillis());
        membershipCache.putMember(invite.coalitionId(), targetClanId);
        return true;
    }

    private CompletableFuture<Optional<ClanSnapshot>> resolveLeaderClan(Player player) {
        return api.findClanByPlayer(player.getUniqueId()).thenApply(clanOptional -> {
            if (clanOptional.isEmpty()) {
                api.scheduler().runSync(() -> messages.send(player, "coalition.error.not-in-clan"));
                return Optional.empty();
            }
            ClanSnapshot clan = clanOptional.get();
            if (!clan.leaderId().equals(player.getUniqueId())) {
                api.scheduler().runSync(() -> messages.send(player, "coalition.error.not-leader"));
                return Optional.empty();
            }
            return Optional.of(clan);
        });
    }
}
