package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.core.integration.PlayerHeadSkinApplier;
import bm.b0b0b0.SoulPact.core.message.AdventureTextParser;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class GuiItemBuilder {

    private final MessageService messageService;
    private final PlayerHeadSkinApplier playerHeadSkinApplier;

    public GuiItemBuilder(MessageService messageService, PlayerHeadSkinApplier playerHeadSkinApplier) {
        this.messageService = messageService;
        this.playerHeadSkinApplier = playerHeadSkinApplier;
    }

    public ItemStack build(Player player, Material material, String nameKey, String loreKey) {
        return build(player, material, nameKey, loreKey, Map.of());
    }

    public ItemStack build(Player player, Material material, String nameKey, String loreKey, Map<String, String> placeholders) {
        List<String> loreLines = messageService.resolveList(player, loreKey, placeholders);
        return buildNamed(player, material, nameKey, loreLines, placeholders);
    }

    public ItemStack buildNamed(Player player, Material material, String nameKey, List<String> loreLines) {
        return buildNamed(player, material, nameKey, loreLines, Map.of());
    }

    public ItemStack buildNamed(
            Player player,
            Material material,
            String nameKey,
            List<String> loreLines,
            Map<String, String> placeholders
    ) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(messageService.component(player, nameKey, placeholders));
        if (loreLines != null && !loreLines.isEmpty()) {
            itemMeta.lore(loreLines.stream().map(AdventureTextParser::parse).toList());
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack filler(Player player, Material material, String nameKey) {
        return buildNamed(player, material, nameKey, List.of(), Map.of());
    }

    public ItemStack buildPlayerHead(
            Player player,
            UUID ownerId,
            String ownerName,
            String nameKey,
            String loreKey,
            Map<String, String> placeholders
    ) {
        List<String> loreLines = messageService.resolveList(player, loreKey, placeholders);
        return buildPlayerHeadNamed(player, ownerId, ownerName, nameKey, loreLines, placeholders);
    }

    public ItemStack buildPlayerHeadNamed(
            Player player,
            UUID ownerId,
            String ownerName,
            String nameKey,
            List<String> loreLines,
            Map<String, String> placeholders
    ) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        playerHeadSkinApplier.apply(skullMeta, ownerId, ownerName);
        skullMeta.displayName(messageService.component(player, nameKey, placeholders));
        if (loreLines != null && !loreLines.isEmpty()) {
            skullMeta.lore(loreLines.stream().map(AdventureTextParser::parse).toList());
        }
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}
