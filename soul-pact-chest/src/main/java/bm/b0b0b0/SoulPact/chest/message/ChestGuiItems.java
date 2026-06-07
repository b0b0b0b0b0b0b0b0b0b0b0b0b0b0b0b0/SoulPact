package bm.b0b0b0.SoulPact.chest.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ChestGuiItems {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private ChestGuiItems() {
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
            ChestMessages messages,
            org.bukkit.entity.Player player,
            Material material,
            String nameKey,
            String loreKey,
            java.util.Map<String, String> placeholders
    ) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }
        itemMeta.displayName(parse(messages.resolve(player, nameKey, placeholders)));
        itemMeta.lore(messages.resolveList(player, loreKey, placeholders).stream().map(ChestGuiItems::parse).toList());
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
