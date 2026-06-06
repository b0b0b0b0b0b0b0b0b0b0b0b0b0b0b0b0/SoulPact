package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanJoinRequest;
import bm.b0b0b0.SoulPact.clan.model.ClanMembershipHistoryEntry;
import java.util.List;

public final class ClanRequestDetailSnapshot {

    private final ClanJoinRequest request;
    private final String playerName;
    private final List<ClanMembershipHistoryEntry> history;
    private final boolean leaderControls;

    public ClanRequestDetailSnapshot(
            ClanJoinRequest request,
            String playerName,
            List<ClanMembershipHistoryEntry> history,
            boolean leaderControls
    ) {
        this.request = request;
        this.playerName = playerName;
        this.history = List.copyOf(history);
        this.leaderControls = leaderControls;
    }

    public ClanJoinRequest request() {
        return request;
    }

    public String playerName() {
        return playerName;
    }

    public List<ClanMembershipHistoryEntry> history() {
        return history;
    }

    public boolean leaderControls() {
        return leaderControls;
    }
}
