package bm.b0b0b0.SoulPact.leaderboard.model;

import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record Board(
        long id,
        BoardStatistic statistic,
        int rankPosition,
        BoardKind kind,
        String world,
        double x,
        double y,
        double z,
        float yaw
) {

    public Optional<Location> location() {
        World resolved = Bukkit.getWorld(world);
        if (resolved == null) {
            return Optional.empty();
        }
        return Optional.of(new Location(resolved, x, y, z, yaw, 0F));
    }
}
