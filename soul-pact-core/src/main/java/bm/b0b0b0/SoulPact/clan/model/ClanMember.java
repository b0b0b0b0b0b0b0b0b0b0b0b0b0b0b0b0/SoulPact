package bm.b0b0b0.SoulPact.clan.model;

import java.util.UUID;

public final class ClanMember {

    private final UUID playerId;
    private final String role;
    private final String nickname;
    private final int kills;
    private final int deaths;
    private final long joinedAt;

    public ClanMember(
            UUID playerId,
            String role,
            String nickname,
            int kills,
            int deaths,
            long joinedAt
    ) {
        this.playerId = playerId;
        this.role = role;
        this.nickname = nickname;
        this.kills = kills;
        this.deaths = deaths;
        this.joinedAt = joinedAt;
    }

    public UUID playerId() {
        return playerId;
    }

    public String role() {
        return role;
    }

    public String nickname() {
        return nickname;
    }

    public int kills() {
        return kills;
    }

    public int deaths() {
        return deaths;
    }

    public long joinedAt() {
        return joinedAt;
    }
}
