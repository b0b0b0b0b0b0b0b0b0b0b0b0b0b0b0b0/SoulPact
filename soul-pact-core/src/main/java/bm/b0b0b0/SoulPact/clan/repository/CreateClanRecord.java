package bm.b0b0b0.SoulPact.clan.repository;

import java.util.UUID;

public record CreateClanRecord(
        String tag,
        String name,
        UUID leaderUuid,
        int maxSlots,
        long createdAt
) {
}
