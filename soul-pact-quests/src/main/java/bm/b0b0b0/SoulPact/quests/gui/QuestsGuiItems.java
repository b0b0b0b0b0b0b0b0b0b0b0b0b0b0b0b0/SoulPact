package bm.b0b0b0.SoulPact.quests.gui;

import bm.b0b0b0.SoulPact.quests.message.QuestsMessages;
import bm.b0b0b0.SoulPact.quests.message.QuestsTextParser;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class QuestsGuiItems {

    private QuestsGuiItems() {
    }

    public static ItemStack named(Material material, String name, List<String> loreLines) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(QuestsTextParser.parse(name));
        if (loreLines != null && !loreLines.isEmpty()) {
            itemMeta.lore(loreLines.stream().map(QuestsTextParser::parse).toList());
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack build(
            QuestsMessages messages,
            Player player,
            Material material,
            String nameKey,
            String loreKey,
            java.util.Map<String, String> placeholders
    ) {
        return named(
                material,
                messages.resolve(player, nameKey, placeholders),
                messages.resolveList(player, loreKey, placeholders)
        );
    }

    public static ItemStack filler(Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(QuestsTextParser.parse(" "));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
