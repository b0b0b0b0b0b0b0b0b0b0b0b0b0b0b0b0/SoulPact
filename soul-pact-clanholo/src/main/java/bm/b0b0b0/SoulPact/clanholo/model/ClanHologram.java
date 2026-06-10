package bm.b0b0b0.SoulPact.clanholo.model;

import java.util.List;
import java.util.UUID;

public record ClanHologram(
        long id,
        long clanId,
        String name,
        String world,
        double x,
        double y,
        double z,
        UUID creatorId,
        String creatorName,
        String template,
        long createdAt,
        List<String> lines
) {
}
