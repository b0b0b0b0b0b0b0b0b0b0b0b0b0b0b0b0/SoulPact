package bm.b0b0b0.SoulPact.api.treasury;

import java.util.UUID;

public record ClanTreasuryContributorSnapshot(
        UUID playerId,
        double totalDeposited
) {
}
