package bm.b0b0b0.SoulPact.gladiator.model;

import java.util.Locale;
import java.util.Optional;
import org.bukkit.Location;

public record ArenaRegion(
        String worldName,
        double minX,
        double minY,
        double minZ,
        double maxX,
        double maxY,
        double maxZ
) {

    private static final String SEPARATOR = ";";

    public static ArenaRegion of(Location first, Location second) {
        return new ArenaRegion(
                first.getWorld().getName(),
                Math.min(first.getX(), second.getX()),
                Math.min(first.getY(), second.getY()),
                Math.min(first.getZ(), second.getZ()),
                Math.max(first.getX(), second.getX()),
                Math.max(first.getY(), second.getY()),
                Math.max(first.getZ(), second.getZ())
        );
    }

    public static Optional<ArenaRegion> decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return Optional.empty();
        }
        String[] parts = encoded.split(SEPARATOR);
        if (parts.length < 7) {
            return Optional.empty();
        }
        try {
            return Optional.of(new ArenaRegion(
                    parts[0],
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]),
                    Double.parseDouble(parts[4]),
                    Double.parseDouble(parts[5]),
                    Double.parseDouble(parts[6])
            ));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public String encode() {
        return String.join(
                SEPARATOR,
                worldName,
                format(minX),
                format(minY),
                format(minZ),
                format(maxX),
                format(maxY),
                format(maxZ)
        );
    }

    public boolean contains(Location location) {
        if (location.getWorld() == null || !location.getWorld().getName().equals(worldName)) {
            return false;
        }
        return location.getX() >= minX && location.getX() <= maxX
                && location.getY() >= minY && location.getY() <= maxY
                && location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
