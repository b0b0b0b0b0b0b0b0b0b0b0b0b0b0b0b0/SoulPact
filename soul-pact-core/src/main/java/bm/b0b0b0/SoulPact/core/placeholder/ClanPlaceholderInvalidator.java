package bm.b0b0b0.SoulPact.core.placeholder;

import java.util.UUID;

public interface ClanPlaceholderInvalidator {

    void invalidatePlayer(UUID playerId);

    void invalidateClan(long clanId);

    void invalidateAll();
}
