package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.model.ClanListEntry;
import bm.b0b0b0.SoulPact.clan.service.ClanListPage;
import bm.b0b0b0.SoulPact.clan.service.ClanProfilePlaceholders;
import bm.b0b0b0.SoulPact.core.config.GuiListConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanListMenuPopulator {

    private final GuiListConfig guiListConfig;
    private final GuiItemBuilder guiItemBuilder;
    private final MessageService messageService;

    public ClanListMenuPopulator(
            GuiListConfig guiListConfig,
            GuiItemBuilder guiItemBuilder,
            MessageService messageService
    ) {
        this.guiListConfig = guiListConfig;
        this.guiItemBuilder = guiItemBuilder;
        this.messageService = messageService;
    }

    public Map<Integer, Long> populate(Inventory inventory, Player player, ClanListPage listPage) {
        fillBackground(inventory, player);
        Map<Integer, Long> entryClanIds = new HashMap<>();
        if (listPage.totalClans() == 0) {
            inventory.setItem(22, guiItemBuilder.build(
                    player,
                    guiListConfig.emptyMaterial(),
                    "clan.gui.list.item.empty.name",
                    "clan.gui.list.item.empty.lore"
            ));
            placeNavigation(inventory, player, listPage);
            return entryClanIds;
        }
        int index = 0;
        for (ClanListEntry entry : listPage.entries()) {
            if (index >= guiListConfig.pageSize()) {
                break;
            }
            int slot = guiListConfig.contentSlot(index);
            inventory.setItem(slot, buildEntryItem(player, entry));
            entryClanIds.put(slot, entry.clan().id());
            index++;
        }
        placeNavigation(inventory, player, listPage);
        return entryClanIds;
    }

    private ItemStack buildEntryItem(Player player, ClanListEntry entry) {
        Map<String, String> placeholders = new HashMap<>(ClanProfilePlaceholders.forClan(
                player,
                entry.clan(),
                entry.memberCount(),
                messageService
        ));
        placeholders.put("leader", resolveLeaderName(entry.clan().leaderId()));
        String leaderName = resolveLeaderName(entry.clan().leaderId());
        return guiItemBuilder.buildPlayerHead(
                player,
                entry.clan().leaderId(),
                leaderName,
                "clan.gui.list.item.entry.name",
                "clan.gui.list.item.entry.lore",
                placeholders
        );
    }

    private void placeNavigation(Inventory inventory, Player player, ClanListPage listPage) {
        inventory.setItem(guiListConfig.previousSlot(), buildPageArrow(
                player,
                listPage.hasPrevious(),
                "clan.gui.list.item.previous.name",
                "clan.gui.list.item.previous.lore"
        ));
        inventory.setItem(guiListConfig.backSlot(), guiItemBuilder.build(
                player,
                guiListConfig.backMaterial(),
                "clan.gui.list.item.back.name",
                "clan.gui.list.item.back.lore"
        ));
        inventory.setItem(guiListConfig.nextSlot(), buildPageArrow(
                player,
                listPage.hasNext(),
                "clan.gui.list.item.next.name",
                "clan.gui.list.item.next.lore"
        ));
    }

    private ItemStack buildPageArrow(Player player, boolean enabled, String nameKey, String loreKey) {
        return guiItemBuilder.build(
                player,
                enabled ? guiListConfig.pageArrowMaterial() : guiListConfig.pageArrowDisabledMaterial(),
                nameKey,
                loreKey
        );
    }

    private void fillBackground(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                guiListConfig.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }

    private String resolveLeaderName(java.util.UUID leaderId) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(leaderId);
        String name = offlinePlayer.getName();
        return name == null || name.isBlank() ? leaderId.toString() : name;
    }
}
