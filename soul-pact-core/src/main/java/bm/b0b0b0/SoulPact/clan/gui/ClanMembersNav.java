package bm.b0b0b0.SoulPact.clan.gui;

public final class ClanMembersNav {

    public enum BackTarget {
        PROFILE,
        INFO
    }

    private final BackTarget backTarget;
    private final long clanId;
    private final int infoListPage;
    private final int membersPage;

    public ClanMembersNav(BackTarget backTarget, long clanId, int infoListPage, int membersPage) {
        this.backTarget = backTarget;
        this.clanId = clanId;
        this.infoListPage = infoListPage;
        this.membersPage = membersPage;
    }

    public static ClanMembersNav fromProfile(long clanId) {
        return new ClanMembersNav(BackTarget.PROFILE, clanId, 0, 0);
    }

    public static ClanMembersNav fromInfo(long clanId, int listPage) {
        return new ClanMembersNav(BackTarget.INFO, clanId, listPage, 0);
    }

    public ClanMembersNav withMembersPage(int page) {
        return new ClanMembersNav(backTarget, clanId, infoListPage, page);
    }

    public BackTarget backTarget() {
        return backTarget;
    }

    public long clanId() {
        return clanId;
    }

    public int infoListPage() {
        return infoListPage;
    }

    public int membersPage() {
        return membersPage;
    }
}
