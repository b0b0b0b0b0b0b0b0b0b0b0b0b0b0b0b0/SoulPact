package bm.b0b0b0.SoulPact.clan.model;

import java.util.UUID;

public final class ClanMembershipNotification {

    private final long id;
    private final UUID playerId;
    private final String kind;
    private final long clanId;
    private final String clanTag;
    private final String clanName;
    private final long createdAt;

    public ClanMembershipNotification(
            long id,
            UUID playerId,
            String kind,
            long clanId,
            String clanTag,
            String clanName,
            long createdAt
    ) {
        this.id = id;
        this.playerId = playerId;
        this.kind = kind;
        this.clanId = clanId;
        this.clanTag = clanTag;
        this.clanName = clanName;
        this.createdAt = createdAt;
    }

    public long id() {
        return id;
    }

    public UUID playerId() {
        return playerId;
    }

    public String kind() {
        return kind;
    }

    public long clanId() {
        return clanId;
    }

    public String clanTag() {
        return clanTag;
    }

    public String clanName() {
        return clanName;
    }

    public long createdAt() {
        return createdAt;
    }
}
