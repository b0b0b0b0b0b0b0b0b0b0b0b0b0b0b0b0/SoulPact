package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanJoinRequest;
import java.util.List;

public final class ClanRequestsSnapshot {

    private final Clan clan;
    private final List<ClanJoinRequest> requests;
    private final boolean leaderControls;

    public ClanRequestsSnapshot(Clan clan, List<ClanJoinRequest> requests, boolean leaderControls) {
        this.clan = clan;
        this.requests = List.copyOf(requests);
        this.leaderControls = leaderControls;
    }

    public Clan clan() {
        return clan;
    }

    public List<ClanJoinRequest> requests() {
        return requests;
    }

    public boolean leaderControls() {
        return leaderControls;
    }
}
