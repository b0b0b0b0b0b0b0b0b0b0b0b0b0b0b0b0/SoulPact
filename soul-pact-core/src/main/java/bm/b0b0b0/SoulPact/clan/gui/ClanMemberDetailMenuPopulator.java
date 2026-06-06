package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.model.ClanMemberManagementAction;
import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.clan.service.ClanEconomyMessages;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberDetailLoreBuilder;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberDetailSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiMemberDetailConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanMemberDetailMenuPopulator {

    private final GuiMemberDetailConfig guiMemberDetailConfig;
    private final GuiItemBuilder guiItemBuilder;
    private final ClanMemberDetailLoreBuilder detailLoreBuilder;
    private final RoleThemeService roleThemeService;
    private final ClanEconomyMessages clanEconomyMessages;

    public ClanMemberDetailMenuPopulator(
            GuiMemberDetailConfig guiMemberDetailConfig,
            GuiItemBuilder guiItemBuilder,
            ClanMemberDetailLoreBuilder detailLoreBuilder,
            RoleThemeService roleThemeService,
            ClanEconomyMessages clanEconomyMessages
    ) {
        this.guiMemberDetailConfig = guiMemberDetailConfig;
        this.guiItemBuilder = guiItemBuilder;
        this.detailLoreBuilder = detailLoreBuilder;
        this.roleThemeService = roleThemeService;
        this.clanEconomyMessages = clanEconomyMessages;
    }

    public Map<Integer, ClanMemberManagementAction> populate(
            Inventory inventory,
            Player player,
            ClanMemberDetailSnapshot snapshot
    ) {
        fillBackground(inventory, player);
        List<String> lore = detailLoreBuilder.build(player, snapshot);
        inventory.setItem(guiMemberDetailConfig.playerSlot(), guiItemBuilder.buildPlayerHeadNamed(
                player,
                snapshot.member().playerId(),
                snapshot.playerName(),
                "clan.gui.members.detail.player.name",
                lore,
                Map.of("player", snapshot.playerName())
        ));
        Map<Integer, ClanMemberManagementAction> actionSlots = new HashMap<>();
        int slotIndex = 0;
        for (ClanMemberManagementAction action : snapshot.managementActions()) {
            if (slotIndex >= guiMemberDetailConfig.contentSize()) {
                break;
            }
            int slot = guiMemberDetailConfig.contentSlot(slotIndex);
            inventory.setItem(slot, buildActionItem(player, action));
            actionSlots.put(slot, action);
            slotIndex++;
        }
        inventory.setItem(guiMemberDetailConfig.backSlot(), guiItemBuilder.build(
                player,
                guiMemberDetailConfig.backMaterial(),
                "clan.gui.members.detail.back.name",
                "clan.gui.members.detail.back.lore"
        ));
        return actionSlots;
    }

    private ItemStack buildActionItem(Player player, ClanMemberManagementAction action) {
        if (action.kind() == ClanMemberManagementAction.Kind.TRANSFER) {
            Map<String, String> placeholders = Map.of(
                    "cost", clanEconomyMessages.createCostLine(player)
            );
            return guiItemBuilder.build(
                    player,
                    guiMemberDetailConfig.transferMaterial(),
                    "clan.gui.members.detail.action.transfer.name",
                    "clan.gui.members.detail.action.transfer.lore",
                    placeholders
            );
        }
        if (action.kind() == ClanMemberManagementAction.Kind.KICK) {
            return guiItemBuilder.build(
                    player,
                    guiMemberDetailConfig.kickMaterial(),
                    "clan.gui.members.detail.action.kick.name",
                    "clan.gui.members.detail.action.kick.lore"
            );
        }
        RoleDefinition roleDefinition = roleThemeService.theme().definition(action.roleKey());
        String roleTitle = roleDefinition == null ? action.roleKey() : roleDefinition.title();
        return guiItemBuilder.build(
                player,
                guiMemberDetailConfig.assignRoleMaterial(),
                "clan.gui.members.detail.action.assign.name",
                "clan.gui.members.detail.action.assign.lore",
                Map.of("role", roleTitle)
        );
    }

    private void fillBackground(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                guiMemberDetailConfig.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }
}
