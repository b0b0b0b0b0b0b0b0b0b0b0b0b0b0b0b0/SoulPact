package bm.b0b0b0.SoulPact.api.coalition;

import java.util.UUID;

public record CoalitionAllySnapshot(
        long clanId,
        String tag,
        String name,
        UUID leaderId
) {
}
