package bm.b0b0b0.SoulPact.land.gui;

import bm.b0b0b0.SoulPact.land.message.LandMessages;
import bm.b0b0b0.SoulPact.land.message.LandTextParser;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class LandGuiItems {

    private LandGuiItems() {
    }

    public static ItemStack create(
            Player player,
            LandMessages messages,
            Material material,
            String nameKey,
            List<String> loreLines,
            Map<String, String> placeholders
    ) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(LandTextParser.parse(messages.resolve(player, nameKey, placeholders)));
        meta.lore(loreLines.stream().map(LandTextParser::parse).toList());
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack simple(
            Player player,
            LandMessages messages,
            Material material,
            String nameKey,
            String loreKey,
            Map<String, String> placeholders
    ) {
        return create(
                player,
                messages,
                material,
                nameKey,
                messages.resolveList(player, loreKey, placeholders),
                placeholders
        );
    }

    public static ItemStack filler(LandMessages messages, Material material) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(LandTextParser.parse(messages.resolveDefault("land.gui.filler.name")));
        stack.setItemMeta(meta);
        return stack;
    }
}
