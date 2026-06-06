package bm.b0b0b0.SoulPact.clan.standard;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.World;

public final class ClanStandardPresence {

    private final Map<Long, StandardTrack> tracks = new ConcurrentHashMap<>();

    public boolean isTracked(long clanId) {
        return tracks.containsKey(clanId);
    }

    public Optional<StandardTrack> track(long clanId) {
        return Optional.ofNullable(tracks.get(clanId));
    }

    public void trackInventory(long clanId, UUID playerId) {
        tracks.put(clanId, new InventoryTrack(playerId));
    }

    public void trackEntity(long clanId, UUID entityId) {
        tracks.put(clanId, new EntityTrack(entityId));
    }

    public void trackBlock(long clanId, Location location) {
        tracks.put(clanId, new BlockTrack(
                location.getWorld().getUID(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        ));
    }

    public void clear(long clanId) {
        tracks.remove(clanId);
    }

    public sealed interface StandardTrack permits InventoryTrack, EntityTrack, BlockTrack {
    }

    public record InventoryTrack(UUID playerId) implements StandardTrack {
    }

    public record EntityTrack(UUID entityId) implements StandardTrack {
    }

    public record BlockTrack(UUID worldId, int x, int y, int z) implements StandardTrack {
        public boolean matches(World world, int blockX, int blockY, int blockZ) {
            return world.getUID().equals(worldId) && x == blockX && y == blockY && z == blockZ;
        }
    }
}
