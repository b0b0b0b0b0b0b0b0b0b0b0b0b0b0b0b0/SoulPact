package bm.b0b0b0.SoulPact.clan.model;

import java.util.UUID;

public final class ClanMembershipHistoryEntry {

    private final long id;
    private final UUID playerId;
    private final long clanId;
    private final String clanTag;
    private final String clanName;
    private final String role;
    private final long joinedAt;
    private final long leftAt;
    private final String reason;

    public ClanMembershipHistoryEntry(
            long id,
            UUID playerId,
            long clanId,
            String clanTag,
            String clanName,
            String role,
            long joinedAt,
            long leftAt,
            String reason
    ) {
        this.id = id;
        this.playerId = playerId;
        this.clanId = clanId;
        this.clanTag = clanTag;
        this.clanName = clanName;
        this.role = role;
        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
        this.reason = reason;
    }

    public long id() {
        return id;
    }

    public UUID playerId() {
        return playerId;
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

    public String role() {
        return role;
    }

    public long joinedAt() {
        return joinedAt;
    }

    public long leftAt() {
        return leftAt;
    }

    public String reason() {
        return reason;
    }
}
