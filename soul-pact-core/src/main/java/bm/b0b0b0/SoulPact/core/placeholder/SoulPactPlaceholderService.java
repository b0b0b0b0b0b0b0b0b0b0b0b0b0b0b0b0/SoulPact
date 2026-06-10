package bm.b0b0b0.SoulPact.core.placeholder;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public final class SoulPactPlaceholderService implements ClanPlaceholderInvalidator {

    private SoulPactPlaceholderResolver resolver;
    private final ClanPlaceholderDataLoader dataLoader;
    private final ClanPlaceholderSnapshotFactory snapshotFactory;
    private final int cacheMillis;
    private final Map<UUID, CachedPlayerSnapshot> playerCache = new ConcurrentHashMap<>();
    private final Map<Long, CachedClanBundle> clanCache = new ConcurrentHashMap<>();

    public SoulPactPlaceholderService(
            SoulPactPlaceholderResolver resolver,
            ClanPlaceholderDataLoader dataLoader,
            ClanPlaceholderSnapshotFactory snapshotFactory,
            int cacheMillis
    ) {
        this.resolver = resolver;
        this.dataLoader = dataLoader;
        this.snapshotFactory = snapshotFactory;
        this.cacheMillis = cacheMillis;
    }

    public void bindResolver(SoulPactPlaceholderResolver resolver) {
        this.resolver = resolver;
    }

    public String resolve(Player player, String params) {
        if (player == null) {
            return "";
        }
        return resolver.resolve(player, params);
    }

    public ClanPlaceholderSnapshot load(Player player) {
        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (cacheMillis > 0) {
            CachedPlayerSnapshot cachedPlayer = playerCache.get(playerId);
            if (cachedPlayer != null && cachedPlayer.expiresAt() > now) {
                return snapshotFactory.refreshOnlinePresence(cachedPlayer.snapshot());
            }
        }
        ClanPlaceholderMembershipRow membership = dataLoader.loadMembership(playerId);
        if (!membership.present()) {
            ClanPlaceholderSnapshot empty = ClanPlaceholderSnapshot.empty();
            storePlayerSnapshot(playerId, empty, now);
            return empty;
        }
        ClanPlaceholderClanBundle bundle = resolveClanBundle(membership.clanId(), now);
        if (bundle == null) {
            ClanPlaceholderSnapshot empty = ClanPlaceholderSnapshot.empty();
            storePlayerSnapshot(playerId, empty, now);
            return empty;
        }
        ClanPlaceholderSnapshot snapshot = snapshotFactory.build(playerId, bundle, membership);
        storePlayerSnapshot(playerId, snapshot, now);
        return snapshotFactory.refreshOnlinePresence(snapshot);
    }

    @Override
    public void invalidatePlayer(UUID playerId) {
        playerCache.remove(playerId);
    }

    @Override
    public void invalidateClan(long clanId) {
        clanCache.remove(clanId);
        Iterator<Map.Entry<UUID, CachedPlayerSnapshot>> iterator = playerCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, CachedPlayerSnapshot> entry = iterator.next();
            if (entry.getValue().snapshot().clanId() == clanId) {
                iterator.remove();
            }
        }
    }

    @Override
    public void invalidateAll() {
        playerCache.clear();
        clanCache.clear();
    }

    private ClanPlaceholderClanBundle resolveClanBundle(long clanId, long now) {
        if (cacheMillis > 0) {
            CachedClanBundle cachedClan = clanCache.get(clanId);
            if (cachedClan != null && cachedClan.expiresAt() > now) {
                return cachedClan.bundle();
            }
        }
        ClanPlaceholderClanBundle bundle = dataLoader.loadClanBundle(clanId);
        if (bundle != null && cacheMillis > 0) {
            clanCache.put(clanId, new CachedClanBundle(bundle, now + cacheMillis));
        }
        return bundle;
    }

    private void storePlayerSnapshot(UUID playerId, ClanPlaceholderSnapshot snapshot, long now) {
        if (cacheMillis <= 0) {
            return;
        }
        playerCache.put(playerId, new CachedPlayerSnapshot(snapshot, now + cacheMillis));
    }

    private record CachedPlayerSnapshot(ClanPlaceholderSnapshot snapshot, long expiresAt) {
    }

    private record CachedClanBundle(ClanPlaceholderClanBundle bundle, long expiresAt) {
    }
}
