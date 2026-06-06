package bm.b0b0b0.SoulPact.clan.message;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ClanListChatPresenter {

    private static final String BACK_COMMAND = "/clan";

    private final ClanRepository clanRepository;
    private final ClanConfig clanConfig;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanListChatPresenter(
            ClanRepository clanRepository,
            ClanConfig clanConfig,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.clanRepository = clanRepository;
        this.clanConfig = clanConfig;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public void show(Player player) {
        player.closeInventory();
        clanRepository.findAll(clanConfig.listChatLimit()).thenAccept(clans ->
                asyncDatabaseExecutor.runSync(() -> render(player, clans))
        );
    }

    private void render(Player player, List<Clan> clans) {
        if (!player.isOnline()) {
            return;
        }
        if (clans.isEmpty()) {
            messageService.send(player, "clan.list.empty");
            messageService.sendRunLine(player, "clan.list.back.label", BACK_COMMAND);
            return;
        }
        messageService.send(player, "clan.list.header", Map.of("count", String.valueOf(clans.size())));
        for (Clan clan : clans) {
            Map<String, String> placeholders = Map.of(
                    "tag", clan.tag(),
                    "name", clan.name()
            );
            messageService.sendSuggestLine(
                    player,
                    "clan.list.entry",
                    placeholders,
                    messageService.resolve(player, "clan.list.entry-suggest", placeholders)
            );
        }
        messageService.sendRunLine(player, "clan.list.back.label", BACK_COMMAND);
    }
}
