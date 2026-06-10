package bm.b0b0b0.SoulPact.quests.gui;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.quests.config.QuestsConfig;
import bm.b0b0b0.SoulPact.quests.message.QuestsMessages;
import bm.b0b0b0.SoulPact.quests.service.ClanQuestService;
import bm.b0b0b0.SoulPact.quests.service.QuestActionResult;
import java.util.Map;
import org.bukkit.entity.Player;

public final class QuestsGuiService {

    private final SoulPactApi api;
    private final QuestsConfig config;
    private final QuestsMessages messages;
    private final ClanQuestService questService;
    private final QuestsMenuPopulator populator;
    private final QuestsClickHandler clickHandler;

    public QuestsGuiService(
            SoulPactApi api,
            QuestsConfig config,
            QuestsMessages messages,
            ClanQuestService questService
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.questService = questService;
        this.populator = new QuestsMenuPopulator(config, messages);
        this.clickHandler = new QuestsClickHandler(this, new QuestsClanNavigation(api), config);
    }

    public QuestsClickHandler clickHandler() {
        return clickHandler;
    }

    public ClanQuestService questService() {
        return questService;
    }

    public void open(Player player) {
        questService.loadOverview(player).thenAccept(overviewOptional -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (overviewOptional.isEmpty()) {
                messages.send(player, "quests.not-in-clan");
                return;
            }
            QuestsMenu menu = new QuestsMenu(config, populator, messages, player, overviewOptional.get());
            player.openInventory(menu.getInventory());
        }));
    }

    public void start(Player player, String questId) {
        questService.start(player, questId).thenAccept(result -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            sendActionResult(player, result, questId);
            if (result == QuestActionResult.STARTED) {
                open(player);
            }
        }));
    }

    public void abandon(Player player) {
        questService.abandon(player).thenAccept(result -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            sendActionResult(player, result, "");
            if (result == QuestActionResult.ABANDONED) {
                open(player);
            }
        }));
    }

    public void sendActionResult(Player player, QuestActionResult result, String questId) {
        String key = switch (result) {
            case STARTED -> "quests.started";
            case ABANDONED -> "quests.abandoned";
            case NOT_IN_CLAN -> "quests.not-in-clan";
            case NO_MANAGE_PERMISSION -> "quests.no-manage-permission";
            case UNKNOWN_QUEST -> "quests.unknown-quest";
            case ALREADY_ACTIVE -> "quests.already-active";
            case ALREADY_COMPLETED -> "quests.already-completed";
            case ON_COOLDOWN -> "quests.on-cooldown";
            case NO_ACTIVE_QUEST -> "quests.no-active-quest";
            case FAILED -> "quests.failed";
        };
        messages.send(player, key, Map.of(
                "quest", messages.resolve(player, "quests.quest." + questId + ".name"),
                "id", questId
        ));
    }
}
