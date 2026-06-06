package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import java.util.List;

public final class ClanMembersSnapshot {

    private final Clan clan;
    private final List<ClanMember> members;

    public ClanMembersSnapshot(Clan clan, List<ClanMember> members) {
        this.clan = clan;
        this.members = List.copyOf(members);
    }

    public Clan clan() {
        return clan;
    }

    public List<ClanMember> members() {
        return members;
    }
}
