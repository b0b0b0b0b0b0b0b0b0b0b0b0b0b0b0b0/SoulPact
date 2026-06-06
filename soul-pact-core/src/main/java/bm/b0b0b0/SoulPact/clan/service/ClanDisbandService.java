package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ClanDisbandService {

    private final ClanRepository clanRepository;
    private final ClanMembershipHistoryService membershipHistoryService;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanDisbandService(
            ClanRepository clanRepository,
            ClanMembershipHistoryService membershipHistoryService,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.clanRepository = clanRepository;
        this.membershipHistoryService = membershipHistoryService;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public void disband(Player player) {
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.disband.not-in-clan"));
                return java.util.concurrent.CompletableFuture.completedFuture(false);
            }
            var clan = clanOptional.get();
            if (!clan.leaderId().equals(player.getUniqueId())) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.disband.not-leader"));
                return java.util.concurrent.CompletableFuture.completedFuture(false);
            }
            long disbandedAt = System.currentTimeMillis();
            return clanRepository.findMembersByClanId(clan.id()).thenCompose(members ->
                    membershipHistoryService.recordDisband(clan, members, disbandedAt)
                            .thenCompose(ignored -> clanRepository.deleteClan(clan.id()))
            ).thenApply(deleted -> {
                asyncDatabaseExecutor.runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    if (!deleted) {
                        messageService.send(player, "clan.disband.failed");
                        return;
                    }
                    messageService.send(player, "clan.disband.success", Map.of(
                            "tag", clan.tag(),
                            "name", clan.name()
                    ));
                });
                return deleted;
            });
        });
    }
}
