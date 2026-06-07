package bm.b0b0b0.SoulPact.api.clan;

import java.util.UUID;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SoulPactClanStandard {

    boolean isStandard(ItemStack itemStack);

    Long readClanId(ItemStack itemStack);

    String readClanTag(ItemStack itemStack);

    Long readClanIdFromBlock(BlockState blockState);

    String readClanTagFromBlock(BlockState blockState);

    void trackInventory(long clanId, UUID playerId);

    void restoreToPlayer(Player player, long clanId, String clanTag);
}
