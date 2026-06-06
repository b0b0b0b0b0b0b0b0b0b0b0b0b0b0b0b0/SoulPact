package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanEconomyMessages;
import bm.b0b0b0.SoulPact.clan.service.ClanPlayerNames;
import bm.b0b0b0.SoulPact.clan.service.ClanProfileMembersLoreBuilder;
import bm.b0b0b0.SoulPact.clan.service.ClanProfilePlaceholders;
import bm.b0b0b0.SoulPact.clan.service.ClanProfileSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiProfileConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanProfileMenuPopulator {

    private final GuiProfileConfig guiProfileConfig;
    private final GuiItemBuilder guiItemBuilder;
    private final ClanProfileMembersLoreBuilder membersLoreBuilder;
    private final MessageService messageService;
    private final ClanEconomyMessages clanEconomyMessages;

    public ClanProfileMenuPopulator(
            GuiProfileConfig guiProfileConfig,
            GuiItemBuilder guiItemBuilder,
            ClanProfileMembersLoreBuilder membersLoreBuilder,
            MessageService messageService,
            ClanEconomyMessages clanEconomyMessages
    ) {
        this.guiProfileConfig = guiProfileConfig;
        this.guiItemBuilder = guiItemBuilder;
        this.membersLoreBuilder = membersLoreBuilder;
        this.messageService = messageService;
        this.clanEconomyMessages = clanEconomyMessages;
    }

    public void populate(Inventory inventory, Player player, ClanProfileSnapshot snapshot) {
        fillBackground(inventory, player);
        inventory.setItem(guiProfileConfig.bannerSlot(), guiItemBuilder.buildFromStack(
                player,
                snapshot.bannerItem(),
                "clan.gui.profile.item.banner.name",
                "clan.gui.profile.item.banner.lore"
        ));
        var placeholders = ClanProfilePlaceholders.forClan(
                player,
                snapshot.clan(),
                snapshot.members().size(),
                messageService
        );
        inventory.setItem(guiProfileConfig.clanInfoSlot(), guiItemBuilder.build(
                player,
                guiProfileConfig.clanInfoMaterial(),
                "clan.gui.profile.item.clan.name",
                "clan.gui.profile.item.clan.lore",
                placeholders
        ));
        List<String> membersLore = membersLoreBuilder.build(player, snapshot.members(), snapshot.clan().maxSlots());
        String leaderName = ClanPlayerNames.displayName(snapshot.clan().leaderId());
        inventory.setItem(guiProfileConfig.membersSlot(), guiItemBuilder.buildPlayerHeadNamed(
                player,
                snapshot.clan().leaderId(),
                leaderName,
                "clan.gui.profile.item.members.name",
                membersLore,
                Map.of()
        ));
        if (snapshot.requestsView()) {
            inventory.setItem(guiProfileConfig.requestsSlot(), guiItemBuilder.build(
                    player,
                    guiProfileConfig.requestsMaterial(),
                    "clan.gui.profile.item.requests.name",
                    "clan.gui.profile.item.requests.lore",
                    Map.of("count", String.valueOf(snapshot.pendingRequestCount()))
            ));
        } else if (!snapshot.clan().leaderId().equals(player.getUniqueId())) {
            inventory.setItem(guiProfileConfig.leaveSlot(), guiItemBuilder.build(
                    player,
                    guiProfileConfig.leaveMaterial(),
                    "clan.gui.profile.item.leave.name",
                    "clan.gui.profile.item.leave.lore",
                    placeholders
            ));
        }
        inventory.setItem(guiProfileConfig.backSlot(), guiItemBuilder.build(
                player,
                guiProfileConfig.backMaterial(),
                "clan.gui.profile.item.back.name",
                "clan.gui.profile.item.back.lore"
        ));
    }

    public void populateEmpty(Inventory inventory, Player player) {
        fillBackground(inventory, player);
        Map<String, String> placeholders = Map.of(
                "create_cost", clanEconomyMessages.createCostLine(player)
        );
        inventory.setItem(guiProfileConfig.emptyCreateSlot(), guiItemBuilder.build(
                player,
                guiProfileConfig.createMaterial(),
                "clan.gui.profile.empty.create.name",
                "clan.gui.profile.empty.create.lore",
                placeholders
        ));
        inventory.setItem(guiProfileConfig.emptyListSlot(), guiItemBuilder.build(
                player,
                guiProfileConfig.emptyListMaterial(),
                "clan.gui.profile.empty.list.name",
                "clan.gui.profile.empty.list.lore"
        ));
        inventory.setItem(guiProfileConfig.emptyMessageSlot(), guiItemBuilder.build(
                player,
                guiProfileConfig.emptyMaterial(),
                "clan.gui.profile.empty.item.name",
                "clan.gui.profile.empty.item.lore"
        ));
        inventory.setItem(guiProfileConfig.backSlot(), guiItemBuilder.build(
                player,
                guiProfileConfig.backMaterial(),
                "clan.gui.profile.item.back.name",
                "clan.gui.profile.item.back.lore"
        ));
    }

    private void fillBackground(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                guiProfileConfig.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }
}
