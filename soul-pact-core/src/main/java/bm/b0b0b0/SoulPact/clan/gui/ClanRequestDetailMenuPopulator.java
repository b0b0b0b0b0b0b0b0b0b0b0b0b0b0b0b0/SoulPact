package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanRequestDetailSnapshot;
import bm.b0b0b0.SoulPact.clan.service.ClanRequestHistoryLoreBuilder;
import bm.b0b0b0.SoulPact.core.config.GuiRequestDetailConfig;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanRequestDetailMenuPopulator {

    private static final DateTimeFormatter REQUEST_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final GuiRequestDetailConfig guiRequestDetailConfig;
    private final GuiItemBuilder guiItemBuilder;
    private final ClanRequestHistoryLoreBuilder historyLoreBuilder;

    public ClanRequestDetailMenuPopulator(
            GuiRequestDetailConfig guiRequestDetailConfig,
            GuiItemBuilder guiItemBuilder,
            ClanRequestHistoryLoreBuilder historyLoreBuilder
    ) {
        this.guiRequestDetailConfig = guiRequestDetailConfig;
        this.guiItemBuilder = guiItemBuilder;
        this.historyLoreBuilder = historyLoreBuilder;
    }

    public void populate(Inventory inventory, Player player, ClanRequestDetailSnapshot snapshot) {
        fillBackground(inventory, player);
        List<String> lore = new ArrayList<>();
        lore.add(formatRequestDate(snapshot.request().createdAt()));
        lore.add("");
        lore.addAll(historyLoreBuilder.build(player, snapshot.history()));
        inventory.setItem(guiRequestDetailConfig.playerSlot(), guiItemBuilder.buildPlayerHeadNamed(
                player,
                snapshot.request().playerId(),
                snapshot.playerName(),
                "clan.gui.requests.detail.player.name",
                lore,
                Map.of("player", snapshot.playerName())
        ));
        inventory.setItem(guiRequestDetailConfig.acceptSlot(), guiItemBuilder.build(
                player,
                guiRequestDetailConfig.acceptMaterial(),
                "clan.gui.requests.detail.accept.name",
                "clan.gui.requests.detail.accept.lore"
        ));
        inventory.setItem(guiRequestDetailConfig.denySlot(), guiItemBuilder.build(
                player,
                guiRequestDetailConfig.denyMaterial(),
                "clan.gui.requests.detail.deny.name",
                "clan.gui.requests.detail.deny.lore"
        ));
        if (snapshot.leaderControls()) {
            inventory.setItem(guiRequestDetailConfig.blockSlot(), guiItemBuilder.build(
                    player,
                    guiRequestDetailConfig.blockMaterial(),
                    "clan.gui.requests.detail.block.name",
                    "clan.gui.requests.detail.block.lore"
            ));
        }
        inventory.setItem(guiRequestDetailConfig.backSlot(), guiItemBuilder.build(
                player,
                guiRequestDetailConfig.backMaterial(),
                "clan.gui.requests.detail.back.name",
                "clan.gui.requests.detail.back.lore"
        ));
    }

    private static String formatRequestDate(long createdAt) {
        return Instant.ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .format(REQUEST_DATE);
    }

    private void fillBackground(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                guiRequestDetailConfig.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }
}
