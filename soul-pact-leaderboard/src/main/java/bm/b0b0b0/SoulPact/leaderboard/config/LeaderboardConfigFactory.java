package bm.b0b0b0.SoulPact.leaderboard.config;

import bm.b0b0b0.SoulPact.leaderboard.config.settings.LeaderboardSettings;
import java.util.HashMap;
import java.util.Map;

public final class LeaderboardConfigFactory {

    private LeaderboardConfigFactory() {
    }

    public static LeaderboardConfig from(LeaderboardSettings settings) {
        StandEquipmentSet fallback = StandEquipmentSet.parse(settings.standEquipmentDefault, null);
        Map<Integer, StandEquipmentSet> byPosition = new HashMap<>();
        for (Map.Entry<String, String> entry : settings.standEquipment.entrySet()) {
            try {
                byPosition.put(Integer.parseInt(entry.getKey().trim()), StandEquipmentSet.parse(entry.getValue(), fallback));
            } catch (NumberFormatException ignored) {
            }
        }
        return new LeaderboardConfig(
                settings.locale,
                settings.fallbackLocale,
                Math.max(30, settings.updateIntervalSeconds),
                settings.eventUpdates,
                Math.max(5, settings.eventDebounceSeconds),
                Math.max(3, settings.topSize),
                settings.leaderHead,
                settings.hologramYOffset,
                Math.max(0.1, settings.hologramScale),
                byPosition,
                fallback,
                settings.adminPermission
        );
    }
}
