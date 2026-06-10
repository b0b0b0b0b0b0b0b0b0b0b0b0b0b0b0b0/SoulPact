package bm.b0b0b0.SoulPact.clanholo.render;

import bm.b0b0b0.SoulPact.clanholo.config.ClanHoloConfig;
import bm.b0b0b0.SoulPact.clanholo.message.ClanHoloTextParser;
import bm.b0b0b0.SoulPact.clanholo.model.ClanHologram;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;

public final class ClanHologramRenderer {

    private final HologramEntityTag entityTag;
    private final Supplier<ClanHoloConfig> configSupplier;

    public ClanHologramRenderer(HologramEntityTag entityTag, Supplier<ClanHoloConfig> configSupplier) {
        this.entityTag = entityTag;
        this.configSupplier = configSupplier;
    }

    public void render(ClanHologram hologram, List<String> lines, String ownerLine) {
        Location anchor = hologramLocation(hologram);
        if (anchor.getWorld() == null) {
            return;
        }
        removeEntities(hologram);
        List<String> renderedLines = new ArrayList<>(lines);
        renderedLines.add(ownerLine);
        double spacing = configSupplier.get().lineSpacing();
        for (int index = 0; index < renderedLines.size(); index++) {
            Location lineLocation = anchor.clone().subtract(0, spacing * index, 0);
            spawnLine(hologram.id(), index, lineLocation, renderedLines.get(index));
        }
    }

    public void removeEntities(ClanHologram hologram) {
        Location anchor = hologramLocation(hologram);
        World world = anchor.getWorld();
        if (world == null) {
            return;
        }
        double radius = Math.max(4.0D, configSupplier.get().selectRadius());
        for (Entity entity : world.getNearbyEntities(anchor, radius, radius, radius)) {
            if (entity instanceof TextDisplay && entityTag.matches(entity, hologram.id())) {
                entity.remove();
            }
        }
    }

    private void spawnLine(long hologramId, int lineIndex, Location location, String text) {
        float scale = (float) configSupplier.get().displayScale();
        location.getWorld().spawn(location, TextDisplay.class, display -> {
            display.setBillboard(Display.Billboard.CENTER);
            display.setShadowed(true);
            display.setAlignment(TextDisplay.TextAlignment.CENTER);
            display.setPersistent(true);
            display.setSeeThrough(false);
            display.setTransformation(new org.bukkit.util.Transformation(
                    new Vector3f(),
                    display.getTransformation().getLeftRotation(),
                    new Vector3f(scale, scale, scale),
                    display.getTransformation().getRightRotation()
            ));
            display.text(parse(text));
            entityTag.apply(display, hologramId, lineIndex);
        });
    }

    private static Component parse(String text) {
        return ClanHoloTextParser.parse(text);
    }

    private static Location hologramLocation(ClanHologram hologram) {
        World world = org.bukkit.Bukkit.getWorld(hologram.world());
        if (world == null) {
            return new Location(null, hologram.x(), hologram.y(), hologram.z());
        }
        return new Location(world, hologram.x(), hologram.y(), hologram.z());
    }
}
