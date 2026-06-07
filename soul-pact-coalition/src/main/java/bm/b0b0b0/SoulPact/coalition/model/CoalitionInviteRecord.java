package bm.b0b0b0.SoulPact.coalition.model;

import java.util.UUID;

public record CoalitionInviteRecord(
        long id,
        long coalitionId,
        long inviterClanId,
        long targetClanId,
        UUID invitedBy,
        long createdAt,
        String status
) {
}
