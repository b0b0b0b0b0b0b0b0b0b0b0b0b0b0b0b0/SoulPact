package bm.b0b0b0.SoulPact.clanholo.gate;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class ClanBaseLocationGate {

    private static final String PREFIX = "sp-base-";

    public boolean available() {
        return Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    public Optional<Long> findClanIdAt(Location location) {
        if (!available() || location.getWorld() == null) {
            return Optional.empty();
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (manager == null) {
            return Optional.empty();
        }
        BlockVector3 vector = BukkitAdapter.asBlockVector(location);
        ApplicableRegionSet regions = manager.getApplicableRegions(vector);
        for (ProtectedRegion region : regions) {
            String regionId = region.getId();
            if (!regionId.startsWith(PREFIX)) {
                continue;
            }
            try {
                return Optional.of(Long.parseLong(regionId.substring(PREFIX.length())));
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.empty();
    }
}
