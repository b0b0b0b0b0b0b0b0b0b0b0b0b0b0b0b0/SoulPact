package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.gui.ClanGuiOpenService;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.config.ClanStandardConfig;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.entity.Player;

public final class ClanStandardHubOpenService {

    private final ClanStandardConfig config;
    private final ClanRepository clanRepository;
    private final ClanGuiOpenService guiOpenService;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanStandardHubOpenService(
            ClanStandardConfig config,
            ClanRepository clanRepository,
            ClanGuiOpenService guiOpenService,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.config = config;
        this.clanRepository = clanRepository;
        this.guiOpenService = guiOpenService;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public boolean enabled() {
        return config.openHubOnInteract();
    }

    public void openHubFromStandard(Player player, long standardClanId) {
        if (!config.openHubOnInteract()) {
            return;
        }
        if (!config.requireClanMember()) {
            asyncDatabaseExecutor.runSync(() -> guiOpenService.openHub(player));
            return;
        }
        clanRepository.findByPlayerId(player.getUniqueId()).thenAccept(clanOptional ->
                asyncDatabaseExecutor.runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    if (clanOptional.isEmpty() || clanOptional.get().id() != standardClanId) {
                        messageService.send(player, "clan.standard.interact.not-in-clan");
                        return;
                    }
                    guiOpenService.openHub(player);
                })
        );
    }
}
