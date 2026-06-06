package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.clan.service.ClanSettingsSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiClanSettingsConfig;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanSettingsMenuPopulator {

    private final GuiClanSettingsConfig config;
    private final GuiItemBuilder guiItemBuilder;
    private final RoleThemeService roleThemeService;

    public ClanSettingsMenuPopulator(
            GuiClanSettingsConfig config,
            GuiItemBuilder guiItemBuilder,
            RoleThemeService roleThemeService
    ) {
        this.config = config;
        this.guiItemBuilder = guiItemBuilder;
        this.roleThemeService = roleThemeService;
    }

    public Map<Integer, String> populate(Inventory inventory, Player player, ClanSettingsSnapshot snapshot) {
        fillBackground(inventory, player);
        Map<Integer, String> roleSlots = new HashMap<>();
        int index = 0;
        for (String roleKey : snapshot.roleKeys()) {
            if (index >= config.contentSize()) {
                break;
            }
            RoleDefinition roleDefinition = roleThemeService.theme().definition(roleKey);
            String roleTitle = roleDefinition == null ? roleKey : roleDefinition.title();
            int slot = config.contentSlot(index);
            inventory.setItem(slot, guiItemBuilder.build(
                    player,
                    config.roleMaterial(),
                    "clan.gui.settings.item.role.name",
                    "clan.gui.settings.item.role.lore",
                    Map.of("role", roleTitle)
            ));
            roleSlots.put(slot, roleKey);
            index++;
        }
        inventory.setItem(config.backSlot(), guiItemBuilder.build(
                player,
                config.backMaterial(),
                "clan.gui.settings.item.back.name",
                "clan.gui.settings.item.back.lore"
        ));
        return roleSlots;
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
