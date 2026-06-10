package bm.b0b0b0.SoulPact.leaderboard.config;

import java.util.Map;

public final class LeaderboardConfig {

    private final String locale;
    private final String fallbackLocale;
    private final int updateIntervalSeconds;
    private final boolean eventUpdates;
    private final int eventDebounceSeconds;
    private final int topSize;
    private final boolean leaderHead;
    private final double hologramYOffset;
    private final double hologramScale;
    private final Map<Integer, StandEquipmentSet> equipmentByPosition;
    private final StandEquipmentSet equipmentDefault;
    private final String adminPermission;

    public LeaderboardConfig(
            String locale,
            String fallbackLocale,
            int updateIntervalSeconds,
            boolean eventUpdates,
            int eventDebounceSeconds,
            int topSize,
            boolean leaderHead,
            double hologramYOffset,
            double hologramScale,
            Map<Integer, StandEquipmentSet> equipmentByPosition,
            StandEquipmentSet equipmentDefault,
            String adminPermission
    ) {
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
        this.updateIntervalSeconds = updateIntervalSeconds;
        this.eventUpdates = eventUpdates;
        this.eventDebounceSeconds = eventDebounceSeconds;
        this.topSize = topSize;
        this.leaderHead = leaderHead;
        this.hologramYOffset = hologramYOffset;
        this.hologramScale = hologramScale;
        this.equipmentByPosition = Map.copyOf(equipmentByPosition);
        this.equipmentDefault = equipmentDefault;
        this.adminPermission = adminPermission;
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public int updateIntervalSeconds() {
        return updateIntervalSeconds;
    }

    public boolean eventUpdates() {
        return eventUpdates;
    }

    public int eventDebounceSeconds() {
        return eventDebounceSeconds;
    }

    public int topSize() {
        return topSize;
    }

    public boolean leaderHead() {
        return leaderHead;
    }

    public double hologramYOffset() {
        return hologramYOffset;
    }

    public double hologramScale() {
        return hologramScale;
    }

    public StandEquipmentSet equipmentFor(int position) {
        return equipmentByPosition.getOrDefault(position, equipmentDefault);
    }

    public String adminPermission() {
        return adminPermission;
    }
}
