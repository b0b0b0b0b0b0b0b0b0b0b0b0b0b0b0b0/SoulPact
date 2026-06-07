package bm.b0b0b0.SoulPact.clan.model;

import java.util.UUID;

public final class Clan {

    private final long id;
    private final String tag;
    private final String name;
    private final String description;
    private final UUID leaderId;
    private final int points;
    private final int warsWon;
    private final int maxSlots;
    private final boolean verified;
    private final boolean friendlyFire;
    private final boolean joinRequestsOpen;
    private final long createdAt;

    public Clan(
            long id,
            String tag,
            String name,
            String description,
            UUID leaderId,
            int points,
            int warsWon,
            int maxSlots,
            boolean verified,
            boolean friendlyFire,
            boolean joinRequestsOpen,
            long createdAt
    ) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.leaderId = leaderId;
        this.points = points;
        this.warsWon = warsWon;
        this.maxSlots = maxSlots;
        this.verified = verified;
        this.friendlyFire = friendlyFire;
        this.joinRequestsOpen = joinRequestsOpen;
        this.createdAt = createdAt;
    }

    public long id() {
        return id;
    }

    public String tag() {
        return tag;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public UUID leaderId() {
        return leaderId;
    }

    public int points() {
        return points;
    }

    public int warsWon() {
        return warsWon;
    }

    public int maxSlots() {
        return maxSlots;
    }

    public boolean verified() {
        return verified;
    }

    public boolean friendlyFire() {
        return friendlyFire;
    }

    public boolean joinRequestsOpen() {
        return joinRequestsOpen;
    }

    public long createdAt() {
        return createdAt;
    }
}
