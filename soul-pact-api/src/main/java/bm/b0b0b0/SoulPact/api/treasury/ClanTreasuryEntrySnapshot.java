package bm.b0b0b0.SoulPact.api.treasury;

import java.util.UUID;

public record ClanTreasuryEntrySnapshot(
        long id,
        long clanId,
        UUID actorId,
        String entryType,
        double amount,
        double balanceAfter,
        String note,
        long createdAt
) {
}
