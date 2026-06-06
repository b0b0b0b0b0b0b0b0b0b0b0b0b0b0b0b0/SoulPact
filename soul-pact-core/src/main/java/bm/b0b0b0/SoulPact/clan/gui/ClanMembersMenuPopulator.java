package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.clan.service.ClanMembersPage;
import bm.b0b0b0.SoulPact.clan.service.ClanMembersSnapshot;
import bm.b0b0b0.SoulPact.clan.service.ClanMembersSlotLayout;
import bm.b0b0b0.SoulPact.clan.service.ClanPlayerNames;
import bm.b0b0b0.SoulPact.core.config.GuiMembersConfig;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanMembersMenuPopulator {

    private final GuiMembersConfig guiMembersConfig;
    private final GuiItemBuilder guiItemBuilder;
    private final ClanMembersSlotLayout slotLayout;
    private final RoleThemeService roleThemeService;

    public ClanMembersMenuPopulator(
            GuiMembersConfig guiMembersConfig,
            GuiItemBuilder guiItemBuilder,
            ClanMembersSlotLayout slotLayout,
            RoleThemeService roleThemeService
    ) {
        this.guiMembersConfig = guiMembersConfig;
        this.guiItemBuilder = guiItemBuilder;
        this.slotLayout = slotLayout;
        this.roleThemeService = roleThemeService;
    }

    public ClanMembersPage resolvePage(ClanMembersSnapshot snapshot, int page) {
        return slotLayout.assignPage(guiMembersConfig, snapshot.members(), page);
    }

    public void populate(Inventory inventory, Player player, ClanMembersPage membersPage) {
        fillBackground(inventory, player);
        for (Map.Entry<Integer, ClanMember> entry : membersPage.slotMembers().entrySet()) {
            ClanMember member = entry.getValue();
            String playerName = resolveDisplayName(member);
            RoleDefinition roleDefinition = roleThemeService.theme().definition(member.role());
            String roleTitle = roleDefinition == null ? member.role() : roleDefinition.title();
            inventory.setItem(entry.getKey(), guiItemBuilder.buildPlayerHead(
                    player,
                    member.playerId(),
                    playerName,
                    "clan.gui.members.item.entry.name",
                    "clan.gui.members.item.entry.lore",
                    Map.of(
                            "player", playerName,
                            "role", roleTitle
                    )
            ));
        }
        placeNavigation(inventory, player, membersPage);
    }

    private void placeNavigation(Inventory inventory, Player player, ClanMembersPage membersPage) {
        inventory.setItem(guiMembersConfig.previousSlot(), buildPageArrow(
                player,
                membersPage.hasPrevious(),
                "clan.gui.members.item.previous.name",
                "clan.gui.members.item.previous.lore"
        ));
        inventory.setItem(guiMembersConfig.backSlot(), guiItemBuilder.build(
                player,
                guiMembersConfig.backMaterial(),
                "clan.gui.members.item.back.name",
                "clan.gui.members.item.back.lore"
        ));
        inventory.setItem(guiMembersConfig.nextSlot(), buildPageArrow(
                player,
                membersPage.hasNext(),
                "clan.gui.members.item.next.name",
                "clan.gui.members.item.next.lore"
        ));
    }

    private ItemStack buildPageArrow(Player player, boolean enabled, String nameKey, String loreKey) {
        return guiItemBuilder.build(
                player,
                enabled ? guiMembersConfig.pageArrowMaterial() : guiMembersConfig.pageArrowDisabledMaterial(),
                nameKey,
                loreKey
        );
    }

    private static String resolveDisplayName(ClanMember member) {
        if (member.nickname() != null && !member.nickname().isBlank()) {
            return member.nickname();
        }
        return ClanPlayerNames.displayName(member.playerId());
    }

    private void fillBackground(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                guiMembersConfig.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }
}
