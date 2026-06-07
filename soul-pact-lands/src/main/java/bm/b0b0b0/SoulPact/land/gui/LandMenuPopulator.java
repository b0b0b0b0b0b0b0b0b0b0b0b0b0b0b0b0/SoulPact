package bm.b0b0b0.SoulPact.land.gui;

import bm.b0b0b0.SoulPact.land.config.LandConfig;
import bm.b0b0b0.SoulPact.land.message.LandMessages;
import bm.b0b0b0.SoulPact.land.model.BaseExpansionAxis;
import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import bm.b0b0b0.SoulPact.land.service.ClanBaseService;
import bm.b0b0b0.SoulPact.land.util.MoneyFormat;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class LandMenuPopulator {

    private final LandConfig config;
    private final LandMessages messages;
    private final ClanBaseService baseService;

    public LandMenuPopulator(LandConfig config, LandMessages messages, ClanBaseService baseService) {
        this.config = config;
        this.messages = messages;
        this.baseService = baseService;
    }

    public void populate(Inventory inventory, Player player, LandMenuSnapshot snapshot) {
        fillBackground(inventory);
        Map<String, String> placeholders = snapshot.base()
                .map(base -> Map.of(
                        "tag", snapshot.clan().tag(),
                        "region", base.regionName(),
                        "world", base.world(),
                        "x", String.valueOf(base.flagX()),
                        "y", String.valueOf(base.flagY()),
                        "z", String.valueOf(base.flagZ())
                ))
                .orElse(Map.of("tag", snapshot.clan().tag(), "region", "-", "world", "-", "x", "-", "y", "-", "z", "-"));
        inventory.setItem(config.infoSlot(), LandGuiItems.create(
                player,
                messages,
                Material.MAP,
                snapshot.base().isPresent() ? "land.gui.item.info-active.name" : "land.gui.item.info-empty.name",
                snapshot.base().isPresent()
                        ? messages.resolveList(player, "land.gui.item.info-active.lore", placeholders)
                        : messages.resolveList(player, "land.gui.item.info-empty.lore", placeholders),
                placeholders
        ));
        if (snapshot.leader() && snapshot.base().isPresent()) {
            var base = snapshot.base().get();
            populateExpansion(inventory, player, base, BaseExpansionAxis.NORTH, config.expandNorthSlot(), Material.LIGHT_BLUE_WOOL);
            populateExpansion(inventory, player, base, BaseExpansionAxis.WEST, config.expandWestSlot(), Material.RED_WOOL);
            populateExpansion(inventory, player, base, BaseExpansionAxis.EAST, config.expandEastSlot(), Material.LIME_WOOL);
            populateExpansion(inventory, player, base, BaseExpansionAxis.SOUTH, config.expandSouthSlot(), Material.ORANGE_WOOL);
            inventory.setItem(config.pvpSlot(), LandGuiItems.simple(
                    player,
                    messages,
                    base.pvpEnabled() ? Material.IRON_SWORD : Material.WOODEN_SWORD,
                    base.pvpEnabled() ? "land.gui.item.pvp-on.name" : "land.gui.item.pvp-off.name",
                    base.pvpEnabled() ? "land.gui.item.pvp-on.lore" : "land.gui.item.pvp-off.lore",
                    placeholders
            ));
            inventory.setItem(config.mobSpawnSlot(), LandGuiItems.simple(
                    player,
                    messages,
                    base.mobSpawnEnabled() ? Material.ZOMBIE_HEAD : Material.SKELETON_SKULL,
                    base.mobSpawnEnabled() ? "land.gui.item.mob-on.name" : "land.gui.item.mob-off.name",
                    base.mobSpawnEnabled() ? "land.gui.item.mob-on.lore" : "land.gui.item.mob-off.lore",
                    placeholders
            ));
            Material borderMaterial = config.borderColors().resolve(base.borderMaterial());
            Map<String, String> borderPlaceholders = new HashMap<>(placeholders);
            borderPlaceholders.put(
                    "color",
                    messages.resolve(player, "land.gui.border-colors." + config.borderColors().displayKey(borderMaterial))
            );
            inventory.setItem(config.borderColorSlot(), LandGuiItems.simple(
                    player,
                    messages,
                    borderMaterial,
                    "land.gui.item.border-color.name",
                    "land.gui.item.border-color.lore",
                    borderPlaceholders
            ));
        }
        inventory.setItem(config.backSlot(), LandGuiItems.simple(
                player,
                messages,
                Material.ARROW,
                "land.gui.item.back.name",
                "land.gui.item.back.lore",
                placeholders
        ));
    }

    private void populateExpansion(
            Inventory inventory,
            Player player,
            ClanBaseRecord base,
            BaseExpansionAxis axis,
            int slot,
            Material material
    ) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("direction", messages.resolve(player, "land.expansion.direction." + axis.messageKey()));
        placeholders.put("size", String.valueOf(axis.resolvedExtent(base, config.baseRadius())));
        placeholders.put("max", String.valueOf(config.expansion().maxExtent()));
        placeholders.put("step", String.valueOf(config.expansion().step()));
        placeholders.put("cost", MoneyFormat.format(baseService.expansionCost(base, axis)));
        placeholders.put(
                "source",
                messages.resolve(
                        player,
                        baseService.usesTreasuryPayments()
                                ? "land.expansion.source.treasury"
                                : "land.expansion.source.leader"
                )
        );
        inventory.setItem(slot, LandGuiItems.simple(
                player,
                messages,
                material,
                "land.gui.item.expand.name",
                "land.gui.item.expand.lore",
                placeholders
        ));
    }

    private void fillBackground(Inventory inventory) {
        ItemStack filler = LandGuiItems.filler(messages, Material.GRAY_STAINED_GLASS_PANE);
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }
}
