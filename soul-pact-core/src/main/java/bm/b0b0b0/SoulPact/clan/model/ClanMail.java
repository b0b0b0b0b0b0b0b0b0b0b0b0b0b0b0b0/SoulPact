package bm.b0b0b0.SoulPact.clan.model;

import java.util.UUID;

public record ClanMail(
        long id,
        long clanId,
        UUID senderId,
        String senderName,
        String message,
        long createdAt
) {
}
