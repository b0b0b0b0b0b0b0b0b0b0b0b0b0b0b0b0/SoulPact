package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class BaseFlagIndex {

    private final Map<BlockKey, Long> baseIdByFlag = new ConcurrentHashMap<>();

    public void register(ClanBaseRecord base) {
        baseIdByFlag.put(new BlockKey(base.world(), base.flagX(), base.flagY(), base.flagZ()), base.id());
    }

    public void unregister(ClanBaseRecord base) {
        baseIdByFlag.remove(new BlockKey(base.world(), base.flagX(), base.flagY(), base.flagZ()));
    }

    public Optional<Long> find(String world, int x, int y, int z) {
        return Optional.ofNullable(baseIdByFlag.get(new BlockKey(world, x, y, z)));
    }

    public void clear() {
        baseIdByFlag.clear();
    }

    private record BlockKey(String world, int x, int y, int z) {
    }
}
