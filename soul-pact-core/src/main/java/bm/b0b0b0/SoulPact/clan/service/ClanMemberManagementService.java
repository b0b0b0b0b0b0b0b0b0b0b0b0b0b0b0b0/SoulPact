package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import bm.b0b0b0.SoulPact.core.module.ClanExtensionMembershipNotifier;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanMemberManagementService {

    private static final String LEADER_ROLE = "leader";
    private static final String FORMER_LEADER_ROLE = "deputy";

    private final ClanRepository clanRepository;
    private final RoleThemeService roleThemeService;
    private final ClanCreateEconomy clanCreateEconomy;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;
    private final ClanExtensionMembershipNotifier extensionMembershipNotifier;

    public ClanMemberManagementService(
            ClanRepository clanRepository,
            RoleThemeService roleThemeService,
            ClanCreateEconomy clanCreateEconomy,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor,
            ClanExtensionMembershipNotifier extensionMembershipNotifier
    ) {
        this.clanRepository = clanRepository;
        this.roleThemeService = roleThemeService;
        this.clanCreateEconomy = clanCreateEconomy;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
        this.extensionMembershipNotifier = extensionMembershipNotifier;
    }

    public CompletableFuture<Void> setRole(Player leader, long clanId, UUID targetId, String roleKey) {
        if (LEADER_ROLE.equals(roleKey) || roleThemeService.theme().definition(roleKey) == null) {
            notify(leader, "clan.role.invalid");
            return CompletableFuture.completedFuture(null);
        }
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty() || !clanOptional.get().leaderId().equals(leader.getUniqueId())) {
                notify(leader, "clan.role.not-leader");
                return CompletableFuture.completedFuture(null);
            }
            if (leader.getUniqueId().equals(targetId)) {
                notify(leader, "clan.role.self");
                return CompletableFuture.completedFuture(null);
            }
            var clan = clanOptional.get();
            return clanRepository.findMembersByClanId(clan.id()).thenCompose(members -> {
                boolean targetInClan = members.stream().anyMatch(member -> member.playerId().equals(targetId));
                if (!targetInClan) {
                    notify(leader, "clan.role.target-not-found");
                    return CompletableFuture.completedFuture(null);
                }
                return clanRepository.updateMemberRole(clan.id(), targetId, roleKey).thenAccept(updated -> {
                    if (!updated) {
                        notify(leader, "clan.role.failed");
                        return;
                    }
                    RoleDefinition roleDefinition = roleThemeService.theme().definition(roleKey);
                    String roleTitle = roleDefinition == null ? roleKey : roleDefinition.title();
                    notify(leader, "clan.role.assigned", Map.of(
                            "player", ClanPlayerNames.displayName(targetId),
                            "role", roleTitle
                    ));
                });
            });
        });
    }

    public CompletableFuture<Void> transferLeadership(Player leader, long clanId, UUID targetId) {
        ClanCreateEconomy.ChargeResult chargeResult = clanCreateEconomy.chargeCreate(leader);
        if (chargeResult == ClanCreateEconomy.ChargeResult.INSUFFICIENT_FUNDS) {
            notify(leader, "clan.leadership.not-enough-money", Map.of(
                    "amount", String.valueOf(clanCreateEconomy.createCostAmount())
            ));
            return CompletableFuture.completedFuture(null);
        }
        if (chargeResult == ClanCreateEconomy.ChargeResult.FAILED) {
            notify(leader, "clan.leadership.payment-failed");
            return CompletableFuture.completedFuture(null);
        }
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty() || !clanOptional.get().leaderId().equals(leader.getUniqueId())) {
                notify(leader, "clan.leadership.not-leader");
                return CompletableFuture.completedFuture(null);
            }
            if (leader.getUniqueId().equals(targetId)) {
                notify(leader, "clan.leadership.self");
                return CompletableFuture.completedFuture(null);
            }
            var clan = clanOptional.get();
                return clanRepository.findMembersByClanId(clan.id()).thenCompose(members -> {
                boolean targetInClan = members.stream().anyMatch(member -> member.playerId().equals(targetId));
                if (!targetInClan) {
                    notify(leader, "clan.leadership.target-not-found");
                    return CompletableFuture.completedFuture(null);
                }
                return clanRepository.transferLeadership(clan.id(), leader.getUniqueId(), targetId, FORMER_LEADER_ROLE)
                        .thenAccept(updated -> {
                            if (!updated) {
                                notify(leader, "clan.leadership.failed");
                                return;
                            }
                            extensionMembershipNotifier.leadershipTransferred(
                                    clan.id(),
                                    leader.getUniqueId(),
                                    targetId
                            );
                            notify(leader, "clan.leadership.transferred", Map.of(
                                    "player", ClanPlayerNames.displayName(targetId),
                                    "tag", clan.tag()
                            ));
                        });
            });
        });
    }

    private void notify(Player player, String key) {
        asyncDatabaseExecutor.runSync(() -> messageService.send(player, key));
    }

    private void notify(Player player, String key, Map<String, String> placeholders) {
        asyncDatabaseExecutor.runSync(() -> messageService.send(player, key, placeholders));
    }
}
