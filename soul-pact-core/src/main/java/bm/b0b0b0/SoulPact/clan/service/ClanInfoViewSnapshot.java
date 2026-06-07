package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.coalition.CoalitionAllySnapshot;
import bm.b0b0b0.SoulPact.clan.model.Clan;
import java.util.List;

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
    private final String treasuryLine;
    private final boolean showDeclareWar;
    private final String coalitionLine;
    private final List<CoalitionAllySnapshot> allies;

    public ClanInfoViewSnapshot(Clan clan, int memberCount, ViewerRole viewerRole) {
        this(clan, memberCount, viewerRole, "", false, "", List.of());
    }

    public ClanInfoViewSnapshot(
            Clan clan,
            int memberCount,
            ViewerRole viewerRole,
            String treasuryLine,
            boolean showDeclareWar
    ) {
        this(clan, memberCount, viewerRole, treasuryLine, showDeclareWar, "", List.of());
    }

    public ClanInfoViewSnapshot(
            Clan clan,
            int memberCount,
            ViewerRole viewerRole,
            String treasuryLine,
            boolean showDeclareWar,
            String coalitionLine,
            List<CoalitionAllySnapshot> allies
    ) {
        this.clan = clan;
        this.memberCount = memberCount;
        this.viewerRole = viewerRole;
        this.treasuryLine = treasuryLine == null ? "" : treasuryLine;
        this.showDeclareWar = showDeclareWar;
        this.coalitionLine = coalitionLine == null ? "" : coalitionLine;
        this.allies = allies == null ? List.of() : List.copyOf(allies);
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

    public String treasuryLine() {
        return treasuryLine;
    }

    public boolean showDeclareWar() {
        return showDeclareWar;
    }

    public String coalitionLine() {
        return coalitionLine;
    }

    public List<CoalitionAllySnapshot> allies() {
        return allies;
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
