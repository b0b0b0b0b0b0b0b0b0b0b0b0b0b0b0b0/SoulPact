package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.model.ClanPermissionKeys;
import bm.b0b0b0.SoulPact.clan.model.ClanRolePermissionMap;
import bm.b0b0b0.SoulPact.clan.service.ClanRoleSettingsSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiClanRoleSettingsConfig;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanRoleSettingsMenuPopulator {

    private final GuiClanRoleSettingsConfig config;
    private final GuiItemBuilder guiItemBuilder;

    public ClanRoleSettingsMenuPopulator(GuiClanRoleSettingsConfig config, GuiItemBuilder guiItemBuilder) {
        this.config = config;
        this.guiItemBuilder = guiItemBuilder;
    }

    public Map<Integer, String> populate(Inventory inventory, Player player, ClanRoleSettingsSnapshot snapshot) {
        fillBackground(inventory, player);
        Map<Integer, String> permissionSlots = new HashMap<>();
        for (String permission : ClanRolePermissionMap.PERMISSION_ORDER) {
            int slot = config.slotForPermission(permission);
            if (slot < 0) {
                continue;
            }
            boolean enabled = snapshot.isEnabled(permission);
            inventory.setItem(slot, buildToggleItem(player, permission, enabled));
            permissionSlots.put(slot, permission);
        }
        inventory.setItem(config.backSlot(), guiItemBuilder.build(
                player,
                config.backMaterial(),
                "clan.gui.settings.role.item.back.name",
                "clan.gui.settings.role.item.back.lore"
        ));
        return permissionSlots;
    }

    private ItemStack buildToggleItem(Player player, String permission, boolean enabled) {
        String state = enabled ? "enabled" : "disabled";
        return guiItemBuilder.build(
                player,
                enabled ? config.toggleOnMaterial() : config.toggleOffMaterial(),
                "clan.gui.settings.role.permission." + permission + "." + state + ".name",
                "clan.gui.settings.role.permission." + permission + "." + state + ".lore"
        );
    }

    private void fillBackground(Inventory inventory, Player player) {
        ItemStack filler = guiItemBuilder.filler(
                player,
                config.fillerMaterial(),
                "clan.gui.hub.item.filler.name"
        );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }
}
