package bm.b0b0b0.SoulPact.quests.model;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public record QuestDefinition(
        String id,
        QuestType type,
        QuestMission mission,
        Set<String> filters,
        int targetAmount,
        int rewardPoints,
        double rewardTreasury,
        List<String> rewardCommands
) {

    public boolean matchesFilter(String value) {
        if (filters.isEmpty()) {
            return true;
        }
        return value != null && filters.contains(value.toUpperCase(Locale.ROOT));
    }
}
