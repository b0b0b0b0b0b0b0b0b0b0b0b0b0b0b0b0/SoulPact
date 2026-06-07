package bm.b0b0b0.SoulPact.api.clan;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SoulPactClanStandard {

    boolean isStandard(ItemStack itemStack);

    Long readClanId(ItemStack itemStack);

    String readClanTag(ItemStack itemStack);

    UUID readStandardUid(ItemStack itemStack);

    Long readClanIdFromBlock(BlockState blockState);

    String readClanTagFromBlock(BlockState blockState);

    UUID readStandardUidFromBlock(BlockState blockState);

    void stampBlock(BlockState blockState, long clanId, String clanTag, UUID standardUid);

    void trackInventory(long clanId, UUID playerId);

    void trackDeployedBlock(long clanId, Location location);

    void clearDeployed(long clanId);

    void restoreToPlayer(Player player, long clanId, String clanTag);
}
