package bm.b0b0b0.SoulPact.chest.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.chest.ClanChestSpoilsApi;
import bm.b0b0b0.SoulPact.chest.repository.SqlClanChestSpoilsRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.inventory.ItemStack;

public final class ClanChestSpoilsService implements ClanChestSpoilsApi {

    private final SoulPactApi api;
    private final SqlClanChestSpoilsRepository spoilsRepository;

    public ClanChestSpoilsService(SoulPactApi api, SqlClanChestSpoilsRepository spoilsRepository) {
        this.api = api;
        this.spoilsRepository = spoilsRepository;
    }

    @Override
    public CompletableFuture<Boolean> transferWarSpoils(long defeatedClanId, long winnerClanId) {
        return api.scheduler().supplyAsync(() -> {
            long capturedAt = System.currentTimeMillis();
            Map<Integer, ItemStack> chestItems = spoilsRepository.loadChestItems(defeatedClanId);
            if (!chestItems.isEmpty()) {
                Map<Integer, ItemStack> normalized = normalizeItems(chestItems);
                long batchId = spoilsRepository.createBatch(winnerClanId, defeatedClanId, capturedAt);
                spoilsRepository.insertBatchItems(batchId, normalized);
                spoilsRepository.clearChestItems(defeatedClanId);
            }
            spoilsRepository.reassignOwner(defeatedClanId, winnerClanId);
            return true;
        });
    }

    private Map<Integer, ItemStack> normalizeItems(Map<Integer, ItemStack> items) {
        Map<Integer, ItemStack> normalized = new HashMap<>();
        int index = 0;
        for (ItemStack itemStack : items.values()) {
            if (itemStack == null || itemStack.getType().isAir()) {
                continue;
            }
            normalized.put(index++, itemStack);
        }
        return normalized;
    }
}
