package bm.b0b0b0.SoulPact.gladiator.model;

import bm.b0b0b0.SoulPact.gladiator.util.LocationCodec;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Location;

public record Arena(
        String name,
        boolean enabled,
        String icon,
        String tag,
        String description,
        long holderClanId,
        String holderClanTag,
        String region,
        Map<ArenaPoint, String> points
) {

    public Arena {
        points = Map.copyOf(points);
    }

    public static Arena create(String name) {
        return new Arena(name, true, "", "", "", 0L, "", "", new EnumMap<>(ArenaPoint.class));
    }

    public Optional<Location> location(ArenaPoint point) {
        return LocationCodec.decode(points.getOrDefault(point, ""));
    }

    public Optional<ArenaRegion> regionBounds() {
        return ArenaRegion.decode(region);
    }

    public boolean hasRequiredPoints() {
        return hasPoint(ArenaPoint.SPAWN) && hasPoint(ArenaPoint.LOBBY) && hasPoint(ArenaPoint.EXIT);
    }

    public boolean hasPoint(ArenaPoint point) {
        return !points.getOrDefault(point, "").isBlank();
    }

    public Arena withEnabled(boolean newEnabled) {
        return new Arena(name, newEnabled, icon, tag, description, holderClanId, holderClanTag, region, points);
    }

    public Arena withIcon(String newIcon) {
        return new Arena(name, enabled, newIcon, tag, description, holderClanId, holderClanTag, region, points);
    }

    public Arena withTag(String newTag) {
        return new Arena(name, enabled, icon, newTag, description, holderClanId, holderClanTag, region, points);
    }

    public Arena withDescription(String newDescription) {
        return new Arena(name, enabled, icon, tag, newDescription, holderClanId, holderClanTag, region, points);
    }

    public Arena withHolder(long clanId, String clanTag) {
        return new Arena(name, enabled, icon, tag, description, clanId, clanTag, region, points);
    }

    public Arena withRegion(String newRegion) {
        return new Arena(name, enabled, icon, tag, description, holderClanId, holderClanTag, newRegion, points);
    }

    public Arena withPoint(ArenaPoint point, String encodedLocation) {
        Map<ArenaPoint, String> newPoints = new EnumMap<>(ArenaPoint.class);
        newPoints.putAll(points);
        newPoints.put(point, encodedLocation);
        return new Arena(name, enabled, icon, tag, description, holderClanId, holderClanTag, region, newPoints);
    }
}
