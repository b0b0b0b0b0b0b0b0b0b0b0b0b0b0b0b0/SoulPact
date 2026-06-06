package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanRequestsSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiRequestsConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Collections;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanRequestsMenu implements InventoryHolder {

    private final GuiRequestsConfig guiRequestsConfig;
    private final ClanRequestsSnapshot snapshot;
    private final Map<Integer, Long> requestIds;
    private final Inventory inventory;

    public ClanRequestsMenu(
            GuiRequestsConfig guiRequestsConfig,
            ClanRequestsMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanRequestsSnapshot snapshot
    ) {
        this.guiRequestsConfig = guiRequestsConfig;
        this.snapshot = snapshot;
        this.inventory = Bukkit.createInventory(
                this,
                guiRequestsConfig.size(),
                messageService.component(player, "clan.gui.requests.title", Map.of(
                        "tag", snapshot.clan().tag(),
                        "count", String.valueOf(snapshot.requests().size())
                ))
        );
        this.requestIds = Collections.unmodifiableMap(populator.populate(inventory, player, snapshot));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiRequestsConfig config() {
        return guiRequestsConfig;
    }

    public ClanRequestsSnapshot snapshot() {
        return snapshot;
    }

    public Long requestIdAtSlot(int slot) {
        return requestIds.get(slot);
    }
}
