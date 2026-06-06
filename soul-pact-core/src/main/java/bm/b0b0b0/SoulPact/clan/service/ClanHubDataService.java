package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.module.ExtensionRegistryImpl;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanHubDataService {

    private final ClanRepository clanRepository;
    private final ClanEconomyMessages clanEconomyMessages;
    private final MessageService messageService;
    private final ExtensionRegistryImpl extensionRegistry;

    public ClanHubDataService(
            ClanRepository clanRepository,
            ClanEconomyMessages clanEconomyMessages,
            MessageService messageService,
            ExtensionRegistryImpl extensionRegistry
    ) {
        this.clanRepository = clanRepository;
        this.clanEconomyMessages = clanEconomyMessages;
        this.messageService = messageService;
        this.extensionRegistry = extensionRegistry;
    }

    public CompletableFuture<ClanHubSnapshot> loadSnapshot(Player player) {
        CompletableFuture<Integer> totalFuture = clanRepository.countClans();
        CompletableFuture<java.util.Optional<bm.b0b0b0.SoulPact.clan.model.Clan>> playerClanFuture =
                clanRepository.findByPlayerId(player.getUniqueId());
        return totalFuture.thenCombine(playerClanFuture, (total, playerClan) ->
                buildSnapshot(
                        player,
                        total,
                        playerClan.map(clan -> clan.tag()).orElse(null),
                        playerClan.isPresent(),
                        playerClan.map(clan -> clan.leaderId().equals(player.getUniqueId())).orElse(false)
                )
        );
    }

    private ClanHubSnapshot buildSnapshot(
            Player player,
            int totalClans,
            String playerClanTag,
            boolean inClan,
            boolean clanLeader
    ) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("total_clans", String.valueOf(totalClans));
        placeholders.put("your_clan", playerClanTag == null
                ? messageService.resolve(player, "clan.gui.hub.value.no-clan")
                : playerClanTag);
        placeholders.put("create_cost", clanEconomyMessages.createCostLine(player));
        placeholders.put("extension_count", String.valueOf(extensionRegistry.all().size()));
        return new ClanHubSnapshot(Map.copyOf(placeholders), inClan, clanLeader);
    }
}
