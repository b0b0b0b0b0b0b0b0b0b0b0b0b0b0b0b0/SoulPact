package bm.b0b0b0.SoulPact.api.clan;

import java.util.UUID;

public record ClanMemberSnapshot(
        long clanId,
        UUID playerId,
        String role,
        String nickname,
        int kills,
        int deaths,
        long joinedAt
) {
}
