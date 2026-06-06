package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.entity.Player;

public final class ClanCreateChatPrompt {

    private static final String CREATE_COMMAND_PREFIX = "/clan create ";
    private static final String REOPEN_HUB_COMMAND = "/clan";

    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanCreateChatPrompt(MessageService messageService, AsyncDatabaseExecutor asyncDatabaseExecutor) {
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public void open(Player player) {
        player.closeInventory();
        asyncDatabaseExecutor.runSync(() ->
                messageService.sendCreatePrompt(player, CREATE_COMMAND_PREFIX, REOPEN_HUB_COMMAND)
        );
    }
}
