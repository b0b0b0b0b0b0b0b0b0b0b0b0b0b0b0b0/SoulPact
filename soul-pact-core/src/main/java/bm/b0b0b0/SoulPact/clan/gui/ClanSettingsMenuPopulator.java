package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.clan.service.ClanSettingsSnapshot;
import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.config.GuiClanSettingsConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanSettingsMenuPopulator {

    private final GuiClanSettingsConfig config;
    private final GuiItemBuilder guiItemBuilder;
    private final RoleThemeService roleThemeService;
    private final MessageService messageService;
    private final ClanConfig clanConfig;

    public ClanSettingsMenuPopulator(
            GuiClanSettingsConfig config,
            GuiItemBuilder guiItemBuilder,
            RoleThemeService roleThemeService,
            MessageService messageService,
            ClanConfig clanConfig
    ) {
        this.config = config;
        this.guiItemBuilder = guiItemBuilder;
        this.roleThemeService = roleThemeService;
        this.messageService = messageService;
        this.clanConfig = clanConfig;
    }

    public Map<Integer, String> populate(Inventory inventory, Player player, ClanSettingsSnapshot snapshot) {
        fillBackground(inventory, player);
        Map<Integer, String> roleSlots = new HashMap<>();
        int slotIndex = 0;
        for (String roleKey : snapshot.roleKeys()) {
            int slot = nextRoleSlot(slotIndex);
            if (slot < 0) {
                break;
            }
            slotIndex = slot - config.contentStart() + 1;
            RoleDefinition roleDefinition = roleThemeService.theme().definition(roleKey);
            String roleTitle = roleDefinition == null ? roleKey : roleDefinition.title();
            inventory.setItem(slot, guiItemBuilder.build(
                    player,
                    config.roleMaterial(),
                    "clan.gui.settings.item.role.name",
                    "clan.gui.settings.item.role.lore",
                    Map.of("role", roleTitle)
            ));
            roleSlots.put(slot, roleKey);
        }
        inventory.setItem(config.descriptionSlot(), guiItemBuilder.build(
                player,
                config.descriptionMaterial(),
                "clan.gui.settings.item.description.name",
                "clan.gui.settings.item.description.lore",
                Map.of(
                        "description", resolveDescription(player, snapshot.clan().description()),
                        "max", String.valueOf(clanConfig.descriptionMaxLength())
                )
        ));
        if (snapshot.bannerItem() != null) {
            inventory.setItem(config.bannerSlot(), guiItemBuilder.buildFromStack(
                    player,
                    snapshot.bannerItem(),
                    "clan.gui.settings.item.banner.name",
                    "clan.gui.settings.item.banner.lore"
            ));
        }
        inventory.setItem(config.backSlot(), guiItemBuilder.build(
                player,
                config.backMaterial(),
                "clan.gui.settings.item.back.name",
                "clan.gui.settings.item.back.lore"
        ));
        return roleSlots;
    }

    private String resolveDescription(Player player, String description) {
        if (description == null || description.isBlank()) {
            return messageService.resolve(player, "clan.gui.profile.value-empty-description");
        }
        return description;
    }

    private int nextRoleSlot(int startIndex) {
        for (int index = startIndex; index < config.contentSize(); index++) {
            int slot = config.contentSlot(index);
            if (slot != config.bannerSlot() && slot != config.descriptionSlot()) {
                return slot;
            }
        }
        return -1;
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
