package bm.b0b0b0.SoulPact.core.placeholder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ClanPlaceholderClanBundle(
        long id,
        String tag,
        String name,
        String description,
        UUID leaderId,
        String leaderName,
        int points,
        int warsWon,
        int warsLost,
        int maxSlots,
        boolean verified,
        boolean friendlyFire,
        boolean joinOpen,
        long createdAt,
        String bannerData,
        double bankBalance,
        List<ClanPlaceholderMemberRow> members,
        Map<String, Integer> historyStats,
        List<String> allyTags
) {
}
