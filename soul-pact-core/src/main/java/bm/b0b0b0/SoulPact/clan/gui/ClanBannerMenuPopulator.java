package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.banner.ClanBannerEditor;
import bm.b0b0b0.SoulPact.clan.banner.ClanBannerPatternCatalog;
import bm.b0b0b0.SoulPact.core.config.GuiClanBannerConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.List;
import java.util.Map;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ClanBannerMenuPopulator {

    private final GuiClanBannerConfig config;
    private final GuiItemBuilder guiItemBuilder;
    private final MessageService messageService;

    public ClanBannerMenuPopulator(
            GuiClanBannerConfig config,
            GuiItemBuilder guiItemBuilder,
            MessageService messageService
    ) {
        this.config = config;
        this.guiItemBuilder = guiItemBuilder;
        this.messageService = messageService;
    }

    public void populate(Inventory inventory, Player player, ClanBannerMenu menu) {
        fillBackground(inventory, player);
        Map<String, String> previewPlaceholders = Map.of(
                "layers", String.valueOf(ClanBannerEditor.layerCount(menu.workingBanner()))
        );
        inventory.setItem(
                config.previewSlot(),
                guiItemBuilder.buildFromStack(
                        player,
                        menu.workingBanner(),
                        "clan.gui.banner.item.preview.name",
                        "clan.gui.banner.item.preview.lore",
                        previewPlaceholders
                )
        );
        if (menu.clanLeader() && menu.standardOut()) {
            populateStandardStorageMode(inventory, player, menu);
            inventory.setItem(config.backSlot(), guiItemBuilder.build(
                    player,
                    config.backMaterial(),
                    "clan.gui.banner.item.back.name",
                    "clan.gui.banner.item.back.lore"
            ));
            return;
        }
        populateEditorControls(inventory, player, menu);
        if (menu.clanLeader()) {
            inventory.setItem(config.takeStandardSlot(), guiItemBuilder.build(
                    player,
                    config.takeStandardMaterial(),
                    "clan.gui.banner.item.take-standard.name",
                    "clan.gui.banner.item.take-standard.lore"
            ));
        }
        inventory.setItem(config.backSlot(), guiItemBuilder.build(
                player,
                config.backMaterial(),
                "clan.gui.banner.item.back.name",
                "clan.gui.banner.item.back.lore"
        ));
    }

    private void populateStandardStorageMode(Inventory inventory, Player player, ClanBannerMenu menu) {
        inventory.setItem(config.saveSlot(), guiItemBuilder.build(
                player,
                config.editLockedMaterial(),
                "clan.gui.banner.item.edit-locked.name",
                "clan.gui.banner.item.edit-locked.lore"
        ));
        if (menu.canDepositStandard()) {
            inventory.setItem(config.takeStandardSlot(), guiItemBuilder.build(
                    player,
                    config.depositStandardMaterial(),
                    "clan.gui.banner.item.deposit-standard.name",
                    "clan.gui.banner.item.deposit-standard.lore"
            ));
            return;
        }
        inventory.setItem(config.takeStandardSlot(), guiItemBuilder.build(
                player,
                config.standardAwayMaterial(),
                "clan.gui.banner.item.standard-away.name",
                "clan.gui.banner.item.standard-away.lore"
        ));
    }

    private void populateEditorControls(Inventory inventory, Player player, ClanBannerMenu menu) {
        List<ClanBannerPatternCatalog.PatternOption> patternOptions = ClanBannerPatternCatalog.patternOptions();
        for (int index = 0; index < patternOptions.size(); index++) {
            ClanBannerPatternCatalog.PatternOption option = patternOptions.get(index);
            ItemStack preview = ClanBannerPatternCatalog.previewPattern(option, menu.patternColor());
            inventory.setItem(
                    config.patternSlot(index),
                    guiItemBuilder.buildFromStack(
                            player,
                            preview,
                            "clan.gui.banner.item.pattern." + option.id() + ".name",
                            "clan.gui.banner.item.pattern." + option.id() + ".lore"
                    )
            );
        }
        List<Material> baseColors = ClanBannerPatternCatalog.baseColors();
        for (int index = 0; index < baseColors.size(); index++) {
            inventory.setItem(config.baseColorSlot(index), new ItemStack(baseColors.get(index)));
        }
        inventory.setItem(config.clearPatternsSlot(), guiItemBuilder.build(
                player,
                config.clearPatternsMaterial(),
                "clan.gui.banner.item.clear.name",
                "clan.gui.banner.item.clear.lore"
        ));
        inventory.setItem(config.patternColorSlot(), buildPatternColorItem(player, menu.patternColor()));
        inventory.setItem(config.fromHandSlot(), guiItemBuilder.build(
                player,
                config.fromHandMaterial(),
                "clan.gui.banner.item.from-hand.name",
                "clan.gui.banner.item.from-hand.lore"
        ));
        inventory.setItem(config.undoPatternSlot(), guiItemBuilder.build(
                player,
                config.undoPatternMaterial(),
                "clan.gui.banner.item.undo.name",
                "clan.gui.banner.item.undo.lore"
        ));
        if (menu.clanLeader()) {
            inventory.setItem(config.saveSlot(), guiItemBuilder.build(
                    player,
                    config.saveMaterial(),
                    "clan.gui.banner.item.save.name",
                    "clan.gui.banner.item.save.lore"
            ));
        }
    }

    private ItemStack buildPatternColorItem(Player player, DyeColor dyeColor) {
        Material dyeMaterial = resolveDyeMaterial(dyeColor);
        return guiItemBuilder.build(
                player,
                dyeMaterial,
                "clan.gui.banner.item.pattern-color.name",
                "clan.gui.banner.item.pattern-color.lore",
                Map.of("color", resolveColorLabel(player, dyeColor))
        );
    }

    private Material resolveDyeMaterial(DyeColor dyeColor) {
        Material material = Material.matchMaterial(dyeColor.name() + "_DYE");
        return material == null ? config.patternColorMaterial() : material;
    }

    private String resolveColorLabel(Player player, DyeColor dyeColor) {
        return messageService.resolve(player, "clan.gui.banner.color." + dyeColor.name().toLowerCase());
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
