package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.model.ClanJoinRequest;
import bm.b0b0b0.SoulPact.clan.service.ClanPlayerNames;
import bm.b0b0b0.SoulPact.clan.service.ClanRequestsSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiRequestsConfig;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanRequestsMenuPopulator {

    private final GuiRequestsConfig guiRequestsConfig;
    private final GuiItemBuilder guiItemBuilder;

    public ClanRequestsMenuPopulator(GuiRequestsConfig guiRequestsConfig, GuiItemBuilder guiItemBuilder) {
        this.guiRequestsConfig = guiRequestsConfig;
        this.guiItemBuilder = guiItemBuilder;
    }

    public Map<Integer, Long> populate(Inventory inventory, Player player, ClanRequestsSnapshot snapshot) {
        fillBackground(inventory, player);
        placeToolbar(inventory, player, snapshot);
        Map<Integer, Long> requestIds = new HashMap<>();
        if (snapshot.requests().isEmpty()) {
            inventory.setItem(22, guiItemBuilder.build(
                    player,
                    guiRequestsConfig.emptyMaterial(),
                    "clan.gui.requests.item.empty.name",
                    "clan.gui.requests.item.empty.lore"
            ));
            inventory.setItem(guiRequestsConfig.backSlot(), buildBack(player));
            return requestIds;
        }
        int index = 0;
        for (ClanJoinRequest request : snapshot.requests()) {
            if (index >= guiRequestsConfig.contentSize()) {
                break;
            }
            int slot = guiRequestsConfig.contentSlot(index);
            inventory.setItem(slot, buildRequestHead(player, request));
            requestIds.put(slot, request.id());
            index++;
        }
        inventory.setItem(guiRequestsConfig.backSlot(), buildBack(player));
        return requestIds;
    }

    private void placeToolbar(Inventory inventory, Player player, ClanRequestsSnapshot snapshot) {
        if (!snapshot.requests().isEmpty()) {
            inventory.setItem(guiRequestsConfig.acceptAllSlot(), guiItemBuilder.build(
                    player,
                    guiRequestsConfig.acceptAllMaterial(),
                    "clan.gui.requests.item.accept-all.name",
                    "clan.gui.requests.item.accept-all.lore",
                    Map.of("count", String.valueOf(snapshot.requests().size()))
            ));
            inventory.setItem(guiRequestsConfig.denyAllSlot(), guiItemBuilder.build(
                    player,
                    guiRequestsConfig.denyAllMaterial(),
                    "clan.gui.requests.item.deny-all.name",
                    "clan.gui.requests.item.deny-all.lore",
                    Map.of("count", String.valueOf(snapshot.requests().size()))
            ));
            if (snapshot.leaderControls()) {
                inventory.setItem(guiRequestsConfig.blockAllSlot(), guiItemBuilder.build(
                        player,
                        guiRequestsConfig.blockAllMaterial(),
                        "clan.gui.requests.item.block-all.name",
                        "clan.gui.requests.item.block-all.lore",
                        Map.of("count", String.valueOf(snapshot.requests().size()))
                ));
            }
        }
        if (snapshot.leaderControls()) {
            boolean open = snapshot.clan().joinRequestsOpen();
            inventory.setItem(guiRequestsConfig.toggleSlot(), guiItemBuilder.build(
                    player,
                    guiRequestsConfig.toggleOpenMaterial(open),
                    open ? "clan.gui.requests.item.toggle-open.name" : "clan.gui.requests.item.toggle-closed.name",
                    open ? "clan.gui.requests.item.toggle-open.lore" : "clan.gui.requests.item.toggle-closed.lore"
            ));
        }
    }

    private ItemStack buildRequestHead(Player player, ClanJoinRequest request) {
        String playerName = ClanPlayerNames.displayName(request.playerId());
        return guiItemBuilder.buildPlayerHead(
                player,
                request.playerId(),
                playerName,
                "clan.gui.requests.item.entry.name",
                "clan.gui.requests.item.entry.lore",
                Map.of("player", playerName)
        );
    }

    private ItemStack buildBack(Player player) {
        return guiItemBuilder.build(
                player,
                guiRequestsConfig.backMaterial(),
                "clan.gui.requests.item.back.name",
                "clan.gui.requests.item.back.lore"
        );
    }

    private void fillBackground(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                guiRequestsConfig.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }
}
