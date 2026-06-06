package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanSettingsSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiClanSettingsConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Collections;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanSettingsMenu implements InventoryHolder {

    private final GuiClanSettingsConfig config;
    private final ClanSettingsSnapshot snapshot;
    private final Map<Integer, String> roleSlots;
    private final Inventory inventory;

    public ClanSettingsMenu(
            GuiClanSettingsConfig config,
            ClanSettingsMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanSettingsSnapshot snapshot
    ) {
        this.config = config;
        this.snapshot = snapshot;
        this.inventory = Bukkit.createInventory(
                this,
                config.size(),
                messageService.component(player, "clan.gui.settings.title", Map.of(
                        "tag", snapshot.clan().tag()
                ))
        );
        this.roleSlots = Collections.unmodifiableMap(populator.populate(inventory, player, snapshot));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiClanSettingsConfig config() {
        return config;
    }

    public ClanSettingsSnapshot snapshot() {
        return snapshot;
    }

    public String roleAtSlot(int slot) {
        return roleSlots.get(slot);
    }
}
