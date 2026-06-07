package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.land.repository.ClanBaseRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class BorderBlockIndex {

    private final Map<BlockKey, Long> baseByBlock = new ConcurrentHashMap<>();

    public void register(long baseId, String world, List<ClanBaseRepository.BorderBlock> blocks) {
        for (ClanBaseRepository.BorderBlock block : blocks) {
            baseByBlock.put(new BlockKey(world, block.x(), block.y(), block.z()), baseId);
        }
    }

    public void unregister(long baseId, String world, List<ClanBaseRepository.BorderBlock> blocks) {
        for (ClanBaseRepository.BorderBlock block : blocks) {
            baseByBlock.remove(new BlockKey(world, block.x(), block.y(), block.z()));
        }
    }

    public Optional<Long> find(String world, int x, int y, int z) {
        return Optional.ofNullable(baseByBlock.get(new BlockKey(world, x, y, z)));
    }

    public void clear() {
        baseByBlock.clear();
    }

    private record BlockKey(String world, int x, int y, int z) {
    }
}
