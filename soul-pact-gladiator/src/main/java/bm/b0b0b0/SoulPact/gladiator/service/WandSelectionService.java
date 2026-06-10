package bm.b0b0b0.SoulPact.gladiator.service;

import bm.b0b0b0.SoulPact.gladiator.model.ArenaRegion;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;

public final class WandSelectionService {

    private final ConcurrentHashMap<UUID, Selection> selections = new ConcurrentHashMap<>();

    public void setFirst(UUID playerId, Location location) {
        selections.merge(
                playerId,
                new Selection(location, null),
                (current, replacement) -> new Selection(location, current.second())
        );
    }

    public void setSecond(UUID playerId, Location location) {
        selections.merge(
                playerId,
                new Selection(null, location),
                (current, replacement) -> new Selection(current.first(), location)
        );
    }

    public Optional<ArenaRegion> region(UUID playerId) {
        Selection selection = selections.get(playerId);
        if (selection == null || selection.first() == null || selection.second() == null) {
            return Optional.empty();
        }
        if (!selection.first().getWorld().equals(selection.second().getWorld())) {
            return Optional.empty();
        }
        return Optional.of(ArenaRegion.of(selection.first(), selection.second()));
    }

    public void clear(UUID playerId) {
        selections.remove(playerId);
    }

    private record Selection(Location first, Location second) {
    }
}
