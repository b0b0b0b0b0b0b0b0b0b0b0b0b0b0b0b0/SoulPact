package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.message.ClanHelpChatPresenter;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.entity.Player;

public final class ClanHubClickHandler {

    private final MessageService messageService;
    private final ClanCreateChatPrompt createChatPrompt;
    private final ClanHelpChatPresenter helpChatPresenter;
    private final ClanGuiOpenService guiOpenService;

    public ClanHubClickHandler(
            MessageService messageService,
            ClanCreateChatPrompt createChatPrompt,
            ClanHelpChatPresenter helpChatPresenter,
            ClanGuiOpenService guiOpenService
    ) {
        this.messageService = messageService;
        this.createChatPrompt = createChatPrompt;
        this.helpChatPresenter = helpChatPresenter;
        this.guiOpenService = guiOpenService;
    }

    public void handle(ClanHubMenu menu, Player player, int slot) {
        if (slot == menu.slotCreate()) {
            createChatPrompt.open(player);
            return;
        }
        if (slot == menu.slotHelp()) {
            player.closeInventory();
            helpChatPresenter.show(player);
            return;
        }
        if (slot == menu.slotProfile()) {
            guiOpenService.openProfile(player);
            return;
        }
        if (slot == menu.slotOverview()) {
            guiOpenService.openList(player, 0);
            return;
        }
        if (slot == menu.slotSettings()) {
            guiOpenService.openSettings(player);
            return;
        }
        if (slot == menu.slotBanner()) {
            if (!menu.inClan()) {
                return;
            }
            if (!menu.clanLeader()) {
                messageService.send(player, "clan.banner.view-only");
                return;
            }
            guiOpenService.openBanner(player);
            return;
        }
        if (menu.inClan()) {
            var extension = menu.extensionAtSlot(slot);
            if (extension.isPresent()) {
                extension.get().openGui(player);
                return;
            }
        }
        messageService.send(player, "general.not-implemented");
    }
}
