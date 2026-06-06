package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.core.config.GuiMemberKickConfirmConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanMemberKickConfirmMenuPopulator {

    private final GuiMemberKickConfirmConfig config;
    private final GuiItemBuilder guiItemBuilder;

    public ClanMemberKickConfirmMenuPopulator(
            GuiMemberKickConfirmConfig config,
            GuiItemBuilder guiItemBuilder
    ) {
        this.config = config;
        this.guiItemBuilder = guiItemBuilder;
    }

    public void populate(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                config.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
        inventory.setItem(config.confirmSlot(), guiItemBuilder.build(
                player,
                config.confirmMaterial(),
                "clan.gui.members.kick-confirm.item.confirm.name",
                "clan.gui.members.kick-confirm.item.confirm.lore"
        ));
        inventory.setItem(config.denySlot(), guiItemBuilder.build(
                player,
                config.denyMaterial(),
                "clan.gui.members.kick-confirm.item.deny.name",
                "clan.gui.members.kick-confirm.item.deny.lore"
        ));
    }
}
