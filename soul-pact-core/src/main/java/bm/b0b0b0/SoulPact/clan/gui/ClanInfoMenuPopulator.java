package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanInfoViewSnapshot;
import bm.b0b0b0.SoulPact.clan.service.ClanPlayerNames;
import bm.b0b0b0.SoulPact.clan.service.ClanProfilePlaceholders;
import bm.b0b0b0.SoulPact.core.config.GuiInfoConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanInfoMenuPopulator {

    private final GuiInfoConfig guiInfoConfig;
    private final GuiItemBuilder guiItemBuilder;
    private final MessageService messageService;

    public ClanInfoMenuPopulator(
            GuiInfoConfig guiInfoConfig,
            GuiItemBuilder guiItemBuilder,
            MessageService messageService
    ) {
        this.guiInfoConfig = guiInfoConfig;
        this.guiItemBuilder = guiItemBuilder;
        this.messageService = messageService;
    }

    public void populate(Inventory inventory, Player player, ClanInfoViewSnapshot snapshot) {
        fillBackground(inventory, player);
        var placeholders = ClanProfilePlaceholders.forClan(
                player,
                snapshot.clan(),
                snapshot.memberCount(),
                messageService
        );
        inventory.setItem(guiInfoConfig.clanCardSlot(), guiItemBuilder.build(
                player,
                guiInfoConfig.clanCardMaterial(),
                "clan.gui.info.item.clan.name",
                "clan.gui.info.item.clan.lore",
                placeholders
        ));
        String leaderName = ClanPlayerNames.displayName(snapshot.clan().leaderId());
        inventory.setItem(guiInfoConfig.membersSlot(), guiItemBuilder.buildPlayerHead(
                player,
                snapshot.clan().leaderId(),
                leaderName,
                "clan.gui.info.item.members.name",
                "clan.gui.info.item.members.lore",
                placeholders
        ));
        if (snapshot.canJoin()) {
            inventory.setItem(guiInfoConfig.actionSlot(), guiItemBuilder.build(
                    player,
                    guiInfoConfig.joinMaterial(),
                    "clan.gui.info.item.join.name",
                    "clan.gui.info.item.join.lore",
                    placeholders
            ));
        } else if (snapshot.joinClosedForViewer()) {
            inventory.setItem(guiInfoConfig.actionSlot(), guiItemBuilder.build(
                    player,
                    guiInfoConfig.joinClosedMaterial(),
                    "clan.gui.info.item.join-closed.name",
                    "clan.gui.info.item.join-closed.lore",
                    placeholders
            ));
        } else if (snapshot.canLeave()) {
            inventory.setItem(guiInfoConfig.actionSlot(), guiItemBuilder.build(
                    player,
                    guiInfoConfig.leaveMaterial(),
                    "clan.gui.info.item.leave.name",
                    "clan.gui.info.item.leave.lore",
                    placeholders
            ));
        }
        inventory.setItem(guiInfoConfig.backSlot(), guiItemBuilder.build(
                player,
                guiInfoConfig.backMaterial(),
                "clan.gui.info.item.back.name",
                "clan.gui.info.item.back.lore"
        ));
    }

    private void fillBackground(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                guiInfoConfig.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }
}
