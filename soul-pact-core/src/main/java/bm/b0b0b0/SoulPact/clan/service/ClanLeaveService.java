package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.event.ClanMemberLeaveEvent;
import bm.b0b0b0.SoulPact.api.event.SoulPactEvents;
import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import bm.b0b0b0.SoulPact.core.module.ClanExtensionMembershipNotifier;
import bm.b0b0b0.SoulPact.core.placeholder.ClanPlaceholderInvalidatorRegistry;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Player;

public final class ClanLeaveService {

    private final ClanRepository clanRepository;
    private final ClanMembershipHistoryService membershipHistoryService;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;
    private final ClanExtensionMembershipNotifier extensionMembershipNotifier;

    public ClanLeaveService(
            ClanRepository clanRepository,
            ClanMembershipHistoryService membershipHistoryService,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor,
            ClanExtensionMembershipNotifier extensionMembershipNotifier
    ) {
        this.clanRepository = clanRepository;
        this.membershipHistoryService = membershipHistoryService;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
        this.extensionMembershipNotifier = extensionMembershipNotifier;
    }

    public void leave(Player player) {
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.leave.not-in-clan"));
                return java.util.concurrent.CompletableFuture.completedFuture(false);
            }
            var clan = clanOptional.get();
            if (clan.leaderId().equals(player.getUniqueId())) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.leave.leader"));
                return java.util.concurrent.CompletableFuture.completedFuture(false);
            }
            return clanRepository.findMembersByClanId(clan.id()).thenCompose(members ->
                    findMember(members, player.getUniqueId()).map(member ->
                            membershipHistoryService.recordLeave(clan, member, System.currentTimeMillis())
                                    .thenCompose(ignored -> clanRepository.removeMember(clan.id(), player.getUniqueId()))
                    ).orElse(java.util.concurrent.CompletableFuture.completedFuture(false))
            ).thenApply(removed -> {
                asyncDatabaseExecutor.runSync(() -> {
                    if (removed) {
                        SoulPactEvents.fire(new ClanMemberLeaveEvent(
                                clan.id(),
                                clan.tag(),
                                player.getUniqueId(),
                                player.getName(),
                                ClanMemberLeaveEvent.Reason.LEAVE
                        ));
                    }
                    if (!player.isOnline()) {
                        return;
                    }
                    if (!removed) {
                        messageService.send(player, "clan.leave.failed");
                        return;
                    }
                    extensionMembershipNotifier.memberLeft(clan.id(), player.getUniqueId());
                    ClanPlaceholderInvalidatorRegistry.invalidateClan(clan.id());
                    ClanPlaceholderInvalidatorRegistry.invalidatePlayer(player.getUniqueId());
                    messageService.send(player, "clan.leave.success", Map.of("tag", clan.tag()));
                });
                return removed;
            });
        });
    }

    private static Optional<ClanMember> findMember(java.util.List<ClanMember> members, java.util.UUID playerId) {
        return members.stream().filter(member -> member.playerId().equals(playerId)).findFirst();
    }
}
