package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.event.ClanDisbandEvent;
import bm.b0b0b0.SoulPact.api.event.SoulPactEvents;
import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardPresence;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import bm.b0b0b0.SoulPact.core.placeholder.ClanPlaceholderInvalidatorRegistry;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanDisbandService {

    private final ClanRepository clanRepository;
    private final ClanMembershipHistoryService membershipHistoryService;
    private final ClanStandardPresence standardPresence;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanDisbandService(
            ClanRepository clanRepository,
            ClanMembershipHistoryService membershipHistoryService,
            ClanStandardPresence standardPresence,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.clanRepository = clanRepository;
        this.membershipHistoryService = membershipHistoryService;
        this.standardPresence = standardPresence;
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
            return executeDisband(clan, player, false);
        });
    }

    public void disbandByStandardLoss(long clanId) {
        clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return java.util.concurrent.CompletableFuture.completedFuture(false);
            }
            return executeDisband(clanOptional.get(), null, true);
        });
    }

    public java.util.concurrent.CompletableFuture<Boolean> disbandByWarDefeat(long clanId) {
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return java.util.concurrent.CompletableFuture.completedFuture(false);
            }
            return executeDisband(clanOptional.get(), null, false);
        });
    }

    private java.util.concurrent.CompletableFuture<Boolean> executeDisband(Clan clan, Player initiator, boolean standardLoss) {
        long disbandedAt = System.currentTimeMillis();
        return clanRepository.findMembersByClanId(clan.id()).thenCompose(members -> {
            java.util.concurrent.CompletableFuture<Void> historyFuture = standardLoss
                    ? membershipHistoryService.recordStandardLoss(clan, members, disbandedAt)
                    : membershipHistoryService.recordDisband(clan, members, disbandedAt);
            return historyFuture.thenCompose(ignored -> clanRepository.deleteClan(clan.id()));
        }).thenApply(deleted -> {
            standardPresence.clear(clan.id());
            if (deleted) {
                ClanPlaceholderInvalidatorRegistry.invalidateClan(clan.id());
            }
            asyncDatabaseExecutor.runSync(() -> finishDisband(clan, initiator, standardLoss, deleted));
            return deleted;
        });
    }

    private void finishDisband(Clan clan, Player initiator, boolean standardLoss, boolean deleted) {
        if (deleted) {
            String actorName = initiator != null
                    ? initiator.getName()
                    : ClanPlayerNames.displayName(clan.leaderId());
            SoulPactEvents.fire(new ClanDisbandEvent(clan.id(), clan.tag(), clan.name(), actorName));
        }
        if (standardLoss) {
            broadcastStandardLoss(clan);
            return;
        }
        if (initiator == null || !initiator.isOnline()) {
            return;
        }
        if (!deleted) {
            messageService.send(initiator, "clan.disband.failed");
            return;
        }
        messageService.send(initiator, "clan.disband.success", Map.of(
                "tag", clan.tag(),
                "name", clan.name()
        ));
    }

    private void broadcastStandardLoss(Clan clan) {
        Map<String, String> placeholders = Map.of(
                "tag", clan.tag(),
                "name", clan.name(),
                "id", String.valueOf(clan.id())
        );
        for (Player online : Bukkit.getOnlinePlayers()) {
            messageService.send(online, "clan.standard.disband.broadcast", placeholders);
        }
    }
}
