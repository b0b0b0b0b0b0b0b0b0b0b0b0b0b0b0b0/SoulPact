package bm.b0b0b0.SoulPact.clan.message;

import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.List;
import org.bukkit.entity.Player;

public final class ClanHelpChatPresenter {

    private static final List<String> COMMAND_IDS = List.of("menu", "create", "list", "info", "leave", "disband");
    private static final String BACK_COMMAND = "/clan";

    private final MessageService messageService;

    public ClanHelpChatPresenter(MessageService messageService) {
        this.messageService = messageService;
    }

    public void show(Player player) {
        messageService.send(player, "clan.gui.hub.help.header");
        for (String commandId : COMMAND_IDS) {
            messageService.sendSuggestLine(
                    player,
                    "clan.gui.hub.help.commands." + commandId + ".label",
                    resolveSuggest(player, commandId)
            );
        }
        messageService.sendRunLine(player, "clan.gui.hub.help.back.label", BACK_COMMAND);
    }

    private String resolveSuggest(Player player, String commandId) {
        return messageService.resolve(player, "clan.gui.hub.help.commands." + commandId + ".suggest");
    }
}
