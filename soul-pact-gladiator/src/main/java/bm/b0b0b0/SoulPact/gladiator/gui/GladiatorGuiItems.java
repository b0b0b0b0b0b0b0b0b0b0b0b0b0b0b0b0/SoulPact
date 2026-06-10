package bm.b0b0b0.SoulPact.gladiator.gui;

import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorTextParser;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GladiatorGuiItems {

    private GladiatorGuiItems() {
    }

    public static ItemStack named(Material material, String name, List<String> loreLines) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(GladiatorTextParser.parse(name));
        if (loreLines != null && !loreLines.isEmpty()) {
            itemMeta.lore(loreLines.stream().map(GladiatorTextParser::parse).toList());
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack build(
            GladiatorMessages messages,
            Player player,
            Material material,
            String nameKey,
            String loreKey,
            Map<String, String> placeholders
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
        itemMeta.displayName(GladiatorTextParser.parse(" "));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
