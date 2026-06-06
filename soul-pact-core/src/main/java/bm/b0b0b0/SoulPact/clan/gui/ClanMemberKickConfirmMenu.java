package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.core.config.GuiMemberKickConfirmConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanMemberKickConfirmMenu implements InventoryHolder {

    private final GuiMemberKickConfirmConfig config;
    private final ClanMembersNav navigation;
    private final long clanId;
    private final UUID targetId;
    private final String targetName;
    private final Inventory inventory;

    public ClanMemberKickConfirmMenu(
            GuiMemberKickConfirmConfig config,
            ClanMemberKickConfirmMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanMembersNav navigation,
            long clanId,
            UUID targetId,
            String targetName
    ) {
        this.config = config;
        this.navigation = navigation;
        this.clanId = clanId;
        this.targetId = targetId;
        this.targetName = targetName;
        this.inventory = Bukkit.createInventory(
                this,
                config.size(),
                messageService.component(player, "clan.gui.members.kick-confirm.title", Map.of(
                        "player", targetName
                ))
        );
        populator.populate(inventory, player);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiMemberKickConfirmConfig config() {
        return config;
    }

    public ClanMembersNav navigation() {
        return navigation;
    }

    public long clanId() {
        return clanId;
    }

    public UUID targetId() {
        return targetId;
    }

    public String targetName() {
        return targetName;
    }
}
