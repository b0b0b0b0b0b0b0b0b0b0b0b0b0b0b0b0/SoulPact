package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;

public final class ClanInfoViewSnapshot {

    public enum ViewerRole {
        NONE,
        FOREIGN,
        MEMBER,
        LEADER
    }

    private final Clan clan;
    private final int memberCount;
    private final ViewerRole viewerRole;

    public ClanInfoViewSnapshot(Clan clan, int memberCount, ViewerRole viewerRole) {
        this.clan = clan;
        this.memberCount = memberCount;
        this.viewerRole = viewerRole;
    }

    public Clan clan() {
        return clan;
    }

    public int memberCount() {
        return memberCount;
    }

    public ViewerRole viewerRole() {
        return viewerRole;
    }

    public boolean canJoin() {
        return viewerRole == ViewerRole.NONE && clan.joinRequestsOpen();
    }

    public boolean joinClosedForViewer() {
        return viewerRole == ViewerRole.NONE && !clan.joinRequestsOpen();
    }

    public boolean canLeave() {
        return viewerRole == ViewerRole.MEMBER;
    }
}
