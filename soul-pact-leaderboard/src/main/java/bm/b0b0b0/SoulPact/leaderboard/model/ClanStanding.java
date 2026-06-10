package bm.b0b0b0.SoulPact.leaderboard.model;

import java.util.UUID;

public record ClanStanding(
        long clanId,
        String tag,
        String name,
        UUID leaderId,
        double value
) {
}
