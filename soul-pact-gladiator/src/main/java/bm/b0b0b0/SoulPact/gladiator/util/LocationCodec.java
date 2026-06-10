package bm.b0b0b0.SoulPact.gladiator.util;

import java.util.Locale;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationCodec {

    private static final String SEPARATOR = ";";

    private LocationCodec() {
    }

    public static String encode(Location location) {
        return String.join(
                SEPARATOR,
                location.getWorld().getName(),
                format(location.getX()),
                format(location.getY()),
                format(location.getZ()),
                format(location.getYaw()),
                format(location.getPitch())
        );
    }

    public static Optional<Location> decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return Optional.empty();
        }
        String[] parts = encoded.split(SEPARATOR);
        if (parts.length < 6) {
            return Optional.empty();
        }
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new Location(
                    world,
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]),
                    Float.parseFloat(parts[4]),
                    Float.parseFloat(parts[5])
            ));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
