package bm.b0b0b0.SoulPact.clan.model;

public final class ClanListEntry {

    private final Clan clan;
    private final int memberCount;

    public ClanListEntry(Clan clan, int memberCount) {
        this.clan = clan;
        this.memberCount = memberCount;
    }

    public Clan clan() {
        return clan;
    }

    public int memberCount() {
        return memberCount;
    }
}
