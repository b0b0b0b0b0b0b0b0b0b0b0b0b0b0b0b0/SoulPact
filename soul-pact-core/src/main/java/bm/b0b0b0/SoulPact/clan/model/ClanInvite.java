package bm.b0b0b0.SoulPact.clan.model;

import java.util.UUID;

public final class ClanInvite {

    private final long id;
    private final long clanId;
    private final UUID playerId;
    private final UUID inviterId;
    private final long createdAt;

    public ClanInvite(long id, long clanId, UUID playerId, UUID inviterId, long createdAt) {
        this.id = id;
        this.clanId = clanId;
        this.playerId = playerId;
        this.inviterId = inviterId;
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

    public UUID inviterId() {
        return inviterId;
    }

    public long createdAt() {
        return createdAt;
    }
}
