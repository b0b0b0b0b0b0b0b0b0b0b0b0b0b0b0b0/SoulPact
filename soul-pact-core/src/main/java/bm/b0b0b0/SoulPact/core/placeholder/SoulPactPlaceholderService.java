package bm.b0b0b0.SoulPact.core.placeholder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public final class SoulPactPlaceholderService {

    private final SoulPactPlaceholderResolver resolver;
    private final ClanPlaceholderSnapshotLoader snapshotLoader;
    private final int cacheMillis;
    private final Map<UUID, CachedSnapshot> cache = new ConcurrentHashMap<>();

    public SoulPactPlaceholderService(
            SoulPactPlaceholderResolver resolver,
            ClanPlaceholderSnapshotLoader snapshotLoader,
            int cacheMillis
    ) {
        this.resolver = resolver;
        this.snapshotLoader = snapshotLoader;
        this.cacheMillis = cacheMillis;
    }

    public String resolve(Player player, String params) {
        if (player == null) {
            return "";
        }
        return resolver.resolve(player, params);
    }

    public void invalidate(UUID playerId) {
        cache.remove(playerId);
    }

    public void invalidateAll() {
        cache.clear();
    }

    ClanPlaceholderSnapshot snapshotFor(Player player) {
        if (cacheMillis <= 0) {
            return snapshotLoader.load(player);
        }
        CachedSnapshot cached = cache.get(player.getUniqueId());
        long now = System.currentTimeMillis();
        if (cached != null && cached.expiresAt() > now) {
            return cached.snapshot();
        }
        ClanPlaceholderSnapshot snapshot = snapshotLoader.load(player);
        cache.put(player.getUniqueId(), new CachedSnapshot(snapshot, now + cacheMillis));
        return snapshot;
    }

    private record CachedSnapshot(ClanPlaceholderSnapshot snapshot, long expiresAt) {
    }
}
