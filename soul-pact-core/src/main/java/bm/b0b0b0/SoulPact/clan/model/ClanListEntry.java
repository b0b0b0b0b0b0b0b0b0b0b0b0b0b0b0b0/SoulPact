package bm.b0b0b0.SoulPact.clan.model;

public final class ClanListEntry {

    private final Clan clan;
    private final int memberCount;
    private final String treasuryLine;
    private final String coalitionLine;

    public ClanListEntry(Clan clan, int memberCount, String treasuryLine) {
        this(clan, memberCount, treasuryLine, "");
    }

    public ClanListEntry(Clan clan, int memberCount, String treasuryLine, String coalitionLine) {
        this.clan = clan;
        this.memberCount = memberCount;
        this.treasuryLine = treasuryLine == null ? "" : treasuryLine;
        this.coalitionLine = coalitionLine == null ? "" : coalitionLine;
    }

    public Clan clan() {
        return clan;
    }

    public int memberCount() {
        return memberCount;
    }

    public String treasuryLine() {
        return treasuryLine;
    }

    public String coalitionLine() {
        return coalitionLine;
    }
}
