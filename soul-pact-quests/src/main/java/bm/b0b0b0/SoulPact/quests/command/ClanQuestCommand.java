package bm.b0b0b0.SoulPact.quests.command;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.quests.gui.QuestsGuiService;
import bm.b0b0b0.SoulPact.quests.message.QuestsMessages;
import bm.b0b0b0.SoulPact.quests.service.ActiveQuestState;
import bm.b0b0b0.SoulPact.quests.service.ClanQuestService;
import bm.b0b0b0.SoulPact.quests.service.QuestCatalog;
import bm.b0b0b0.SoulPact.quests.util.QuestTimeFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public final class ClanQuestCommand implements TabExecutor {

    private static final String SUB_LIST = "list";
    private static final String SUB_STATUS = "status";
    private static final String SUB_START = "start";
    private static final String SUB_ABANDON = "abandon";

    private final SoulPactApi api;
    private final QuestsGuiService guiService;
    private final ClanQuestService questService;
    private final QuestCatalog catalog;
    private final QuestsMessages messages;

    public ClanQuestCommand(
            SoulPactApi api,
            QuestsGuiService guiService,
            ClanQuestService questService,
            QuestCatalog catalog,
            QuestsMessages messages
    ) {
        this.api = api;
        this.guiService = guiService;
        this.questService = questService;
        this.catalog = catalog;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (args.length == 0) {
            guiService.open(player);
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case SUB_LIST -> guiService.open(player);
            case SUB_STATUS -> sendStatus(player);
            case SUB_START -> {
                if (args.length < 2) {
                    messages.send(player, "quests.usage-start");
                    return true;
                }
                guiService.start(player, args[1]);
            }
            case SUB_ABANDON -> guiService.abandon(player);
            default -> messages.send(player, "quests.usage");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return filterPrefix(List.of(SUB_LIST, SUB_STATUS, SUB_START, SUB_ABANDON), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase(SUB_START)) {
            return filterPrefix(catalog.all().stream().map(definition -> definition.id()).toList(), args[1]);
        }
        return List.of();
    }

    private void sendStatus(Player player) {
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (clanOptional.isEmpty()) {
                messages.send(player, "quests.not-in-clan");
                return;
            }
            Optional<ActiveQuestState> stateOptional = questService.activeState(clanOptional.get().id());
            if (stateOptional.isEmpty()) {
                messages.send(player, "quests.no-active-quest");
                return;
            }
            ActiveQuestState state = stateOptional.get();
            messages.send(player, "quests.status", Map.of(
                    "quest", messages.resolve(player, "quests.quest." + state.definition().id() + ".name"),
                    "progress", String.valueOf(Math.min(state.progress(), state.definition().targetAmount())),
                    "target", String.valueOf(state.definition().targetAmount()),
                    "time", state.expiresAt() > 0
                            ? QuestTimeFormat.remaining(messages, state.expiresAt())
                            : messages.resolve(player, "quests.time.unlimited")
            ));
        }));
    }

    private List<String> filterPrefix(List<String> options, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        return options.stream().filter(option -> option.toLowerCase(Locale.ROOT).startsWith(lower)).toList();
    }
}
