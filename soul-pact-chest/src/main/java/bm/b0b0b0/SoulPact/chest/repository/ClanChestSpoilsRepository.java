package bm.b0b0b0.SoulPact.chest.repository;

import java.util.Map;
import org.bukkit.inventory.ItemStack;

public interface ClanChestSpoilsRepository {

    long createBatch(long ownerClanId, long sourceClanId, long capturedAt);

    void insertBatchItems(long spoilsId, Map<Integer, ItemStack> items);

    void reassignOwner(long fromClanId, long toClanId);

    void clearChestItems(long clanId);
}
