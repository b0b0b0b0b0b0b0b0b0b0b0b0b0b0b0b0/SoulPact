package bm.b0b0b0.SoulPact.clan.model;

import java.util.UUID;

public final class ClanJoinRequest {

    private final long id;
    private final long clanId;
    private final UUID playerId;
    private final long createdAt;

    public ClanJoinRequest(long id, long clanId, UUID playerId, long createdAt) {
        this.id = id;
        this.clanId = clanId;
        this.playerId = playerId;
        this.createdAt = createdAt;
    }

    public long id() {
        return id;
    }

    public long clanId() {
        return clanId;
    }

    public UUID playerId() {
        return playerId;
    }

    public long createdAt() {
        return createdAt;
    }
}
