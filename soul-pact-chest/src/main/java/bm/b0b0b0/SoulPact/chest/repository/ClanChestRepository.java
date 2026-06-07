package bm.b0b0b0.SoulPact.chest.repository;

import java.util.Map;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public interface ClanChestRepository {

    int unlockedCells(long clanId);

    void setUnlockedCells(long clanId, int unlockedCells);

    Map<Integer, ItemStack> loadItems(long clanId);

    void saveItems(long clanId, Map<Integer, ItemStack> items);
}
