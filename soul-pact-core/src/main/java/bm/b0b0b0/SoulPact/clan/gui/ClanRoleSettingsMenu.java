package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanRoleSettingsSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiClanRoleSettingsConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Collections;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanRoleSettingsMenu implements InventoryHolder {

    private final GuiClanRoleSettingsConfig config;
    private final ClanRoleSettingsSnapshot snapshot;
    private final Map<Integer, String> permissionSlots;
    private final Inventory inventory;

    public ClanRoleSettingsMenu(
            GuiClanRoleSettingsConfig config,
            ClanRoleSettingsMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanRoleSettingsSnapshot snapshot
    ) {
        this.config = config;
        this.snapshot = snapshot;
        this.inventory = Bukkit.createInventory(
                this,
                config.size(),
                messageService.component(player, "clan.gui.settings.role.title", Map.of(
                        "role", snapshot.roleTitle()
                ))
        );
        this.permissionSlots = Collections.unmodifiableMap(populator.populate(inventory, player, snapshot));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiClanRoleSettingsConfig config() {
        return config;
    }

    public ClanRoleSettingsSnapshot snapshot() {
        return snapshot;
    }

    public String permissionAtSlot(int slot) {
        return permissionSlots.get(slot);
    }
}
