package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanListPage;
import bm.b0b0b0.SoulPact.core.config.GuiListConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Collections;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanListMenu implements InventoryHolder {

    private final GuiListConfig guiListConfig;
    private final ClanListPage listPage;
    private final Map<Integer, Long> entryClanIds;
    private final Inventory inventory;

    public ClanListMenu(
            GuiListConfig guiListConfig,
            ClanListMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanListPage listPage
    ) {
        this.guiListConfig = guiListConfig;
        this.listPage = listPage;
        this.inventory = Bukkit.createInventory(
                this,
                guiListConfig.size(),
                messageService.component(player, "clan.gui.list.title", java.util.Map.of(
                        "page", String.valueOf(listPage.totalPages() == 0 ? 0 : listPage.page() + 1),
                        "pages", String.valueOf(Math.max(listPage.totalPages(), 1)),
                        "total", String.valueOf(listPage.totalClans())
                ))
        );
        this.entryClanIds = Collections.unmodifiableMap(populator.populate(inventory, player, listPage));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiListConfig config() {
        return guiListConfig;
    }

    public ClanListPage listPage() {
        return listPage;
    }

    public int slotPrevious() {
        return guiListConfig.previousSlot();
    }

    public int slotBack() {
        return guiListConfig.backSlot();
    }

    public int slotNext() {
        return guiListConfig.nextSlot();
    }

    public Long clanIdAtSlot(int slot) {
        return entryClanIds.get(slot);
    }
}
