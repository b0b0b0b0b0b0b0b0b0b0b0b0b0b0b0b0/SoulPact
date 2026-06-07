package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.land.config.BorderColorPalette;
import bm.b0b0b0.SoulPact.land.config.LandConfig;
import bm.b0b0b0.SoulPact.land.model.BaseBounds;
import bm.b0b0b0.SoulPact.land.repository.ClanBaseRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class BaseBorderService {

    private static final int ANCHOR_SCAN_BELOW = 8;

    private final LandConfig config;
    private final BorderBlockIndex borderBlockIndex;

    public BaseBorderService(LandConfig config, BorderBlockIndex borderBlockIndex) {
        this.config = config;
        this.borderBlockIndex = borderBlockIndex;
    }

    public List<ClanBaseRepository.BorderBlock> placeBorder(World world, BaseBounds bounds, int flagY, Material material) {
        Set<String> seen = new HashSet<>();
        List<ClanBaseRepository.BorderBlock> placed = new ArrayList<>();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            addBorderBlock(world, x, bounds.minZ(), flagY, material, seen, placed);
            addBorderBlock(world, x, bounds.maxZ(), flagY, material, seen, placed);
        }
        for (int z = bounds.minZ() + 1; z < bounds.maxZ(); z++) {
            addBorderBlock(world, bounds.minX(), z, flagY, material, seen, placed);
            addBorderBlock(world, bounds.maxX(), z, flagY, material, seen, placed);
        }
        return placed;
    }

    public void restoreBorder(World world, List<ClanBaseRepository.BorderBlock> blocks) {
        BorderColorPalette palette = config.borderColors();
        for (ClanBaseRepository.BorderBlock block : blocks) {
            Block target = world.getBlockAt(block.x(), block.y(), block.z());
            Material current = target.getType();
            if (!palette.isBorderMaterial(current) && !current.isAir()) {
                continue;
            }
            Material original = parseMaterial(block.originalMaterial());
            target.setType(original == null ? Material.AIR : original, false);
        }
    }

    public void applyBorderColor(World world, List<ClanBaseRepository.BorderBlock> blocks, Material newMaterial) {
        BorderColorPalette palette = config.borderColors();
        for (ClanBaseRepository.BorderBlock block : blocks) {
            Block target = world.getBlockAt(block.x(), block.y(), block.z());
            Material current = target.getType();
            if (!palette.isBorderMaterial(current) && !current.isAir()) {
                continue;
            }
            target.setType(newMaterial, false);
        }
    }

    public void registerBorder(long baseId, String world, List<ClanBaseRepository.BorderBlock> blocks) {
        borderBlockIndex.register(baseId, world, blocks);
    }

    public void unregisterBorder(long baseId, String world, List<ClanBaseRepository.BorderBlock> blocks) {
        borderBlockIndex.unregister(baseId, world, blocks);
    }

    public BorderExpansionDelta expandBorder(
            World world,
            BaseBounds newBounds,
            int flagY,
            Material material,
            List<ClanBaseRepository.BorderBlock> knownBlocks
    ) {
        List<ClanBaseRepository.BorderBlock> removed = new ArrayList<>(knownBlocks);
        restoreBorder(world, knownBlocks);
        knownBlocks.clear();
        List<ClanBaseRepository.BorderBlock> added = placeBorder(world, newBounds, flagY, material);
        knownBlocks.addAll(added);
        return new BorderExpansionDelta(removed, added);
    }

    private void addBorderBlock(
            World world,
            int x,
            int z,
            int flagY,
            Material material,
            Set<String> seen,
            List<ClanBaseRepository.BorderBlock> placed
    ) {
        String key = x + ":" + z;
        if (!seen.add(key)) {
            return;
        }
        OptionalInt borderY = resolveBorderY(world, x, z, flagY);
        if (borderY.isEmpty()) {
            return;
        }
        Block border = world.getBlockAt(x, borderY.getAsInt(), z);
        if (border.isLiquid()) {
            return;
        }
        if (border.getType().name().endsWith("_BANNER")) {
            return;
        }
        String originalMaterial = border.getType().name();
        border.setType(material, false);
        placed.add(new ClanBaseRepository.BorderBlock(x, borderY.getAsInt(), z, originalMaterial));
    }

    private OptionalInt resolveBorderY(World world, int x, int z, int flagY) {
        int groundY = findGroundY(world, x, z, flagY);
        if (groundY < world.getMinHeight()) {
            return OptionalInt.empty();
        }
        int borderY = Math.min(groundY, flagY);
        return OptionalInt.of(borderY);
    }

    private int findGroundY(World world, int x, int z, int anchorY) {
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();
        int searchTop = Math.min(anchorY, maxY);
        int searchBottom = Math.max(anchorY - ANCHOR_SCAN_BELOW, minY);
        for (int y = searchTop; y >= searchBottom; y--) {
            if (isSurfaceBlock(world.getBlockAt(x, y, z).getType())) {
                return y;
            }
        }
        return world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
    }

    private boolean isSurfaceBlock(Material type) {
        if (type.isAir() || !type.isSolid()) {
            return false;
        }
        if (type.name().endsWith("_LEAVES")) {
            return false;
        }
        return !type.name().endsWith("_BANNER");
    }

    private Material parseMaterial(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Material.AIR;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? Material.AIR : material;
    }
}
