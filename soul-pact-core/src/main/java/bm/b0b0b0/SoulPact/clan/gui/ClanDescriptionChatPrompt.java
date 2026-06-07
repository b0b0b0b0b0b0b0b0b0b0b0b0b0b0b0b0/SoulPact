package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ClanDescriptionChatPrompt {

    private static final String DESCRIPTION_COMMAND_PREFIX = "/clan description ";
    private static final String REOPEN_SETTINGS_COMMAND = "/clan settings";

    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanDescriptionChatPrompt(MessageService messageService, AsyncDatabaseExecutor asyncDatabaseExecutor) {
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public void open(Player player, int maxLength) {
        player.closeInventory();
        asyncDatabaseExecutor.runSync(() -> messageService.sendCommandPrompt(
                player,
                "clan.description.prompt-suggest",
                Map.of("max", String.valueOf(maxLength)),
                "clan.description.prompt-cancel",
                DESCRIPTION_COMMAND_PREFIX,
                REOPEN_SETTINGS_COMMAND
        ));
    }
}
