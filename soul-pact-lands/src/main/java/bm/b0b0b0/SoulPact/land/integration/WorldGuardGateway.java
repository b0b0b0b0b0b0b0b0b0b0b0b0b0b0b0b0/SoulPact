package bm.b0b0b0.SoulPact.land.integration;

import bm.b0b0b0.SoulPact.land.model.BaseBounds;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;

public final class WorldGuardGateway {

    public boolean available() {
        return Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    public boolean hasConflict(World world, BaseBounds bounds, int buffer, String ignoredRegionId) {
        RegionManager manager = manager(world);
        if (manager == null) {
            return true;
        }
        ProtectedCuboidRegion expanded = expandHorizontally(toRegion("probe", bounds), buffer);
        for (ProtectedRegion region : manager.getRegions().values()) {
            if (region.getId().equals(ignoredRegionId)) {
                continue;
            }
            if (intersects(expanded, region)) {
                return true;
            }
        }
        return false;
    }

    private static boolean intersects(ProtectedRegion first, ProtectedRegion second) {
        BlockVector3 minFirst = first.getMinimumPoint();
        BlockVector3 maxFirst = first.getMaximumPoint();
        BlockVector3 minSecond = second.getMinimumPoint();
        BlockVector3 maxSecond = second.getMaximumPoint();
        return minFirst.x() <= maxSecond.x() && maxFirst.x() >= minSecond.x()
                && minFirst.y() <= maxSecond.y() && maxFirst.y() >= minSecond.y()
                && minFirst.z() <= maxSecond.z() && maxFirst.z() >= minSecond.z();
    }

    public void createRegion(
            World world,
            String regionId,
            BaseBounds bounds,
            UUID ownerId,
            Collection<UUID> memberIds,
            boolean pvpEnabled,
            boolean mobSpawnEnabled
    ) {
        RegionManager manager = requireManager(world);
        ProtectedCuboidRegion region = toRegion(regionId, bounds);
        region.setPriority(20);
        applyAccess(region, ownerId, memberIds);
        region.setFlag(Flags.PVP, pvpEnabled ? StateFlag.State.ALLOW : StateFlag.State.DENY);
        region.setFlag(Flags.MOB_SPAWNING, mobSpawnEnabled ? StateFlag.State.ALLOW : StateFlag.State.DENY);
        manager.addRegion(region);
    }

    public void removeRegion(World world, String regionId) {
        RegionManager manager = manager(world);
        if (manager == null) {
            return;
        }
        ProtectedRegion region = manager.getRegion(regionId);
        if (region != null) {
            clearAccess(region);
        }
        manager.removeRegion(regionId);
    }

    public void addMember(World world, String regionId, UUID playerId) {
        ProtectedRegion region = region(world, regionId);
        if (region == null) {
            return;
        }
        region.getMembers().addPlayer(playerId);
    }

    public void removeMember(World world, String regionId, UUID playerId) {
        ProtectedRegion region = region(world, regionId);
        if (region == null) {
            return;
        }
        region.getMembers().removePlayer(playerId);
    }

    public void transferOwnership(World world, String regionId, UUID previousOwnerId, UUID newOwnerId) {
        ProtectedRegion region = region(world, regionId);
        if (region == null) {
            return;
        }
        DefaultDomain owners = region.getOwners();
        DefaultDomain members = region.getMembers();
        owners.removePlayer(previousOwnerId);
        members.removePlayer(newOwnerId);
        members.addPlayer(previousOwnerId);
        owners.addPlayer(newOwnerId);
    }

    public void applyFlags(World world, String regionId, boolean pvpEnabled, boolean mobSpawnEnabled) {
        ProtectedRegion region = region(world, regionId);
        if (region == null) {
            return;
        }
        region.setFlag(Flags.PVP, pvpEnabled ? StateFlag.State.ALLOW : StateFlag.State.DENY);
        region.setFlag(Flags.MOB_SPAWNING, mobSpawnEnabled ? StateFlag.State.ALLOW : StateFlag.State.DENY);
    }

    public void forcePvpAllow(World world, String regionId) {
        ProtectedRegion region = region(world, regionId);
        if (region == null) {
            return;
        }
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
    }

    public void restorePvp(World world, String regionId, boolean pvpEnabled) {
        ProtectedRegion region = region(world, regionId);
        if (region == null) {
            return;
        }
        region.setFlag(Flags.PVP, pvpEnabled ? StateFlag.State.ALLOW : StateFlag.State.DENY);
    }

    public void resizeRegion(World world, String regionId, BaseBounds newBounds) {
        RegionManager manager = requireManager(world);
        ProtectedRegion existing = manager.getRegion(regionId);
        if (existing == null) {
            throw new IllegalStateException("WorldGuard region missing: " + regionId);
        }
        ProtectedCuboidRegion region = toRegion(regionId, newBounds);
        region.setPriority(existing.getPriority());
        region.getOwners().addAll(existing.getOwners());
        region.getMembers().addAll(existing.getMembers());
        region.setFlag(Flags.PVP, existing.getFlag(Flags.PVP));
        region.setFlag(Flags.MOB_SPAWNING, existing.getFlag(Flags.MOB_SPAWNING));
        manager.addRegion(region);
    }

    private RegionManager manager(World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(world));
    }

    private RegionManager requireManager(World world) {
        RegionManager manager = manager(world);
        if (manager == null) {
            throw new IllegalStateException("WorldGuard region manager missing for world " + world.getName());
        }
        return manager;
    }

    private ProtectedRegion region(World world, String regionId) {
        RegionManager manager = manager(world);
        if (manager == null) {
            return null;
        }
        return manager.getRegion(regionId);
    }

    private ProtectedCuboidRegion toRegion(String id, BaseBounds bounds) {
        BlockVector3 min = BlockVector3.at(bounds.minX(), bounds.minY(), bounds.minZ());
        BlockVector3 max = BlockVector3.at(bounds.maxX(), bounds.maxY(), bounds.maxZ());
        return new ProtectedCuboidRegion(id, min, max);
    }

    private ProtectedCuboidRegion expandHorizontally(ProtectedCuboidRegion region, int buffer) {
        BlockVector3 min = region.getMinimumPoint().subtract(buffer, 0, buffer);
        BlockVector3 max = region.getMaximumPoint().add(buffer, 0, buffer);
        return new ProtectedCuboidRegion(region.getId() + "-buffer", min, max);
    }

    private void applyAccess(ProtectedRegion region, UUID ownerId, Collection<UUID> memberIds) {
        region.getOwners().addPlayer(ownerId);
        DefaultDomain members = region.getMembers();
        Set<UUID> uniqueIds = new HashSet<>(memberIds);
        uniqueIds.remove(ownerId);
        uniqueIds.forEach(members::addPlayer);
    }

    private void clearAccess(ProtectedRegion region) {
        clearDomain(region.getOwners());
        clearDomain(region.getMembers());
    }

    private void clearDomain(DefaultDomain domain) {
        Set<UUID> existing = new HashSet<>(domain.getUniqueIds());
        existing.forEach(domain::removePlayer);
    }
}
