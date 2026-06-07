package bm.b0b0b0.SoulPact.war.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Map;

public final class WarGuiItems {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private WarGuiItems() {
    }

    public static ItemStack filler(Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.displayName(Component.empty());
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public static ItemStack build(
            WarMessages messages,
            Player player,
            Material material,
            String nameKey,
            String loreKey,
            Map<String, String> placeholders
    ) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }
        itemMeta.displayName(parse(messages.resolve(player, nameKey, placeholders)));
        List<String> loreLines = messages.resolveList(player, loreKey, placeholders);
        itemMeta.lore(loreLines.stream().map(WarGuiItems::parse).toList());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        if (input.indexOf('<') >= 0 && input.indexOf('>') > input.indexOf('<')) {
            return MINI_MESSAGE.deserialize(input);
        }
        return LEGACY.deserialize(input);
    }
}
