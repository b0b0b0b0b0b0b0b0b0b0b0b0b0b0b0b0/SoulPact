package bm.b0b0b0.SoulPact.bank.gui;

import bm.b0b0b0.SoulPact.bank.message.BankMessages;
import bm.b0b0b0.SoulPact.bank.message.BankTextParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class BankGuiItems {

    private BankGuiItems() {
    }

    public static ItemStack build(
            BankMessages messages,
            Player player,
            Material material,
            String nameKey,
            String loreKey,
            Map<String, String> placeholders
    ) {
        return named(
                messages,
                player,
                material,
                messages.resolve(player, nameKey, placeholders),
                messages.resolveList(player, loreKey, placeholders)
        );
    }

    public static ItemStack named(BankMessages messages, Player player, Material material, String name, List<String> loreLines) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(BankTextParser.parse(name));
        if (loreLines != null && !loreLines.isEmpty()) {
            itemMeta.lore(loreLines.stream().map(BankTextParser::parse).toList());
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack filler(Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(BankTextParser.parse(" "));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static List<String> mergeLoreSections(List<String> baseLines, List<String> sectionLines) {
        List<String> merged = new ArrayList<>(baseLines);
        merged.addAll(sectionLines);
        return merged;
    }
}
