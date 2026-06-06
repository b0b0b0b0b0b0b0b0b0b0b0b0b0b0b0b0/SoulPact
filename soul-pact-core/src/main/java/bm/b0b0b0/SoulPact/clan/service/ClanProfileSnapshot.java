package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import java.util.List;

public final class ClanProfileSnapshot {

    private final Clan clan;
    private final List<ClanMember> members;
    private final int pendingRequestCount;
    private final boolean requestsView;

    public ClanProfileSnapshot(
            Clan clan,
            List<ClanMember> members,
            int pendingRequestCount,
            boolean requestsView
    ) {
        this.clan = clan;
        this.members = List.copyOf(members);
        this.pendingRequestCount = pendingRequestCount;
        this.requestsView = requestsView;
    }

    public Clan clan() {
        return clan;
    }

    public List<ClanMember> members() {
        return members;
    }

    public int pendingRequestCount() {
        return pendingRequestCount;
    }

    public boolean requestsView() {
        return requestsView;
    }
}
