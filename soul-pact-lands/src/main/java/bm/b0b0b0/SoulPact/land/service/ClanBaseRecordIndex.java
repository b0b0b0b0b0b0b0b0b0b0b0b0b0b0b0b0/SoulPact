package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ClanBaseRecordIndex {

    private final Map<BlockKey, ClanBaseRecord> baseByFlag = new ConcurrentHashMap<>();
    private final Map<Long, ClanBaseRecord> baseByClanId = new ConcurrentHashMap<>();

    public void register(ClanBaseRecord base) {
        baseByClanId.put(base.clanId(), base);
        baseByFlag.put(flagKey(base), base);
    }

    public void unregister(ClanBaseRecord base) {
        baseByClanId.remove(base.clanId());
        baseByFlag.remove(flagKey(base));
    }

    public Optional<ClanBaseRecord> findByFlag(String world, int x, int y, int z) {
        return Optional.ofNullable(baseByFlag.get(new BlockKey(world, x, y, z)));
    }

    public Optional<ClanBaseRecord> findByClanId(long clanId) {
        return Optional.ofNullable(baseByClanId.get(clanId));
    }

    public void clear() {
        baseByFlag.clear();
        baseByClanId.clear();
    }

    private static BlockKey flagKey(ClanBaseRecord base) {
        return new BlockKey(base.world(), base.flagX(), base.flagY(), base.flagZ());
    }

    private record BlockKey(String world, int x, int y, int z) {
    }
}
