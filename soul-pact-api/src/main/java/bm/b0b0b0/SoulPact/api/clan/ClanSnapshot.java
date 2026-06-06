package bm.b0b0b0.SoulPact.api.clan;

import java.util.UUID;

public record ClanSnapshot(
        long id,
        String tag,
        String name,
        String description,
        UUID leaderId,
        int points,
        int maxSlots,
        boolean verified,
        boolean friendlyFire,
        long createdAt
) {
}
