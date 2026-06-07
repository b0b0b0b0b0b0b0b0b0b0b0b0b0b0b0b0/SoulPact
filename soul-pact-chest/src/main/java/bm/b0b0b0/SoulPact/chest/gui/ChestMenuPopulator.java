package bm.b0b0b0.SoulPact.chest.gui;

import bm.b0b0b0.SoulPact.chest.config.ChestConfig;
import bm.b0b0b0.SoulPact.chest.message.ChestGuiItems;
import bm.b0b0b0.SoulPact.chest.message.ChestMessages;
import bm.b0b0b0.SoulPact.chest.service.ChestGuiLayout;
import bm.b0b0b0.SoulPact.chest.util.MoneyFormat;
import java.util.Map;
import java.util.OptionalInt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ChestMenuPopulator {

    private final ChestConfig config;
    private final ChestMessages messages;
    private final ChestGuiLayout layout;

    public ChestMenuPopulator(ChestConfig config, ChestMessages messages, ChestGuiLayout layout) {
        this.config = config;
        this.messages = messages;
        this.layout = layout;
    }

    public void populate(Inventory inventory, Player player, ChestMenuSnapshot snapshot) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, ChestGuiItems.filler(config.fillerMaterial()));
        }
        populatePageTabs(inventory, player, snapshot);
        populateControls(inventory, player, snapshot);
        populateStorage(inventory, player, snapshot);
        inventory.setItem(config.backSlot(), ChestGuiItems.build(
                messages,
                player,
                config.backMaterial(),
                "chest.gui.item.back.name",
                "chest.gui.item.back.lore",
                Map.of()
        ));
        if (snapshot.page() > 0) {
            inventory.setItem(config.prevPageSlot(), ChestGuiItems.build(
                    messages,
                    player,
                    config.prevMaterial(),
                    "chest.gui.item.prev.name",
                    "chest.gui.item.prev.lore",
                    Map.of("page", String.valueOf(snapshot.page()))
            ));
        }
        if (snapshot.page() < config.pages() - 1) {
            inventory.setItem(config.nextPageSlot(), ChestGuiItems.build(
                    messages,
                    player,
                    config.nextMaterial(),
                    "chest.gui.item.next.name",
                    "chest.gui.item.next.lore",
                    Map.of("page", String.valueOf(snapshot.page() + 2))
            ));
        }
    }

    private void populatePageTabs(Inventory inventory, Player player, ChestMenuSnapshot snapshot) {
        for (int page = 0; page < config.pages(); page++) {
            int slot = config.pageTabSlot(page);
            if (slot < 0) {
                continue;
            }
            boolean active = page == snapshot.page();
            inventory.setItem(slot, ChestGuiItems.build(
                    messages,
                    player,
                    active ? config.pageActiveMaterial() : config.pageInactiveMaterial(),
                    active ? "chest.gui.item.page-active.name" : "chest.gui.item.page.name",
                    active ? "chest.gui.item.page-active.lore" : "chest.gui.item.page.lore",
                    Map.of(
                            "page", String.valueOf(page + 1),
                            "unlocked", String.valueOf(unlockedOnPage(snapshot, page)),
                            "total", String.valueOf(config.cellsPerPage())
                    )
            ));
        }
    }

    private void populateControls(Inventory inventory, Player player, ChestMenuSnapshot snapshot) {
        if (snapshot.leader() && snapshot.unlockedCells() < snapshot.maxCells()) {
            inventory.setItem(config.buyCellSlot(), ChestGuiItems.build(
                    messages,
                    player,
                    config.buyMaterial(),
                    "chest.gui.item.buy.name",
                    "chest.gui.item.buy.lore",
                    Map.of(
                            "cell", String.valueOf(snapshot.unlockedCells() + 1),
                            "cost", MoneyFormat.format(snapshot.nextCellCost()),
                            "source", messages.resolve(
                                    player,
                                    snapshot.bankAvailable()
                                            ? "chest.payment.source.treasury"
                                            : "chest.payment.source.leader"
                            )
                    )
            ));
        }
        if (snapshot.bankAvailable()) {
            inventory.setItem(config.bankLinkSlot(), ChestGuiItems.build(
                    messages,
                    player,
                    config.bankMaterial(),
                    "chest.gui.item.bank.name",
                    "chest.gui.item.bank.lore",
                    Map.of("tag", snapshot.clan().tag())
            ));
        }
    }

    private void populateStorage(Inventory inventory, Player player, ChestMenuSnapshot snapshot) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            OptionalInt cellIndex = layout.cellIndex(snapshot.page(), slot);
            if (cellIndex.isEmpty()) {
                continue;
            }
            int index = cellIndex.getAsInt();
            if (layout.isUnlocked(index, snapshot.unlockedCells())) {
                ItemStack stored = snapshot.items().get(index);
                inventory.setItem(slot, stored == null ? null : stored.clone());
                continue;
            }
            inventory.setItem(slot, ChestGuiItems.build(
                    messages,
                    player,
                    config.barrierMaterial(),
                    "chest.gui.item.locked.name",
                    "chest.gui.item.locked.lore",
                    Map.of(
                            "cell", String.valueOf(index + 1),
                            "cost", MoneyFormat.format(config.pricing().costForCell(index))
                    )
            ));
        }
    }

    private int unlockedOnPage(ChestMenuSnapshot snapshot, int page) {
        int pageStart = page * config.cellsPerPage();
        int pageEnd = pageStart + config.cellsPerPage();
        return Math.max(0, Math.min(snapshot.unlockedCells(), pageEnd) - pageStart);
    }
}
