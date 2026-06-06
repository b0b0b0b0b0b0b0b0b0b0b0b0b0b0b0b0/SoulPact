package bm.b0b0b0.SoulPact.clan.standard;

import bm.b0b0b0.SoulPact.core.message.AdventureTextParser;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.List;
import java.util.Map;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ClanStandardItem {

    private final ClanStandardKeys keys;
    private final MessageService messageService;

    public ClanStandardItem(ClanStandardKeys keys, MessageService messageService) {
        this.keys = keys;
        this.messageService = messageService;
    }

    public ItemStack create(Player player, ItemStack bannerDesign, long clanId, String clanTag) {
        ItemStack itemStack = bannerDesign.clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        markContainer(itemMeta.getPersistentDataContainer(), clanId, clanTag);
        itemMeta.displayName(messageService.component(
                player,
                "clan.standard.item.name",
                Map.of("tag", clanTag, "id", String.valueOf(clanId))
        ));
        List<String> loreLines = messageService.resolveList(
                player,
                "clan.standard.item.lore",
                Map.of("tag", clanTag, "id", String.valueOf(clanId))
        );
        itemMeta.lore(loreLines.stream().map(AdventureTextParser::parse).toList());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack refreshAppearance(Player player, ItemStack existing, ItemStack bannerDesign, String clanTag) {
        Long clanId = readClanIdFromItem(existing);
        if (clanId == null) {
            return existing;
        }
        ItemStack updated = create(player, bannerDesign, clanId, clanTag);
        updated.setAmount(existing.getAmount());
        return updated;
    }

    public boolean isStandard(ItemStack itemStack) {
        return readClanIdFromItem(itemStack) != null;
    }

    public Long readClanIdFromItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir() || !itemStack.getType().name().endsWith("_BANNER")) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        return itemMeta.getPersistentDataContainer().get(keys.clanId(), PersistentDataType.LONG);
    }

    public String readClanTagFromItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        return itemMeta.getPersistentDataContainer().get(keys.clanTag(), PersistentDataType.STRING);
    }

    public void markBlock(TileState tileState, long clanId, String clanTag) {
        markContainer(tileState.getPersistentDataContainer(), clanId, clanTag);
    }

    public Long readClanIdFromBlock(BlockState blockState) {
        if (!(blockState instanceof TileState tileState)) {
            return null;
        }
        return tileState.getPersistentDataContainer().get(keys.clanId(), PersistentDataType.LONG);
    }

    private void markContainer(PersistentDataContainer container, long clanId, String clanTag) {
        container.set(keys.clanId(), PersistentDataType.LONG, clanId);
        container.set(keys.clanTag(), PersistentDataType.STRING, clanTag);
    }
}
