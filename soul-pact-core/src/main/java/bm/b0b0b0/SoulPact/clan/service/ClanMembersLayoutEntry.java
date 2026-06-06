package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanMember;

public final class ClanMembersLayoutEntry {

    private final ClanMember member;

    private ClanMembersLayoutEntry(ClanMember member) {
        this.member = member;
    }

    public static ClanMembersLayoutEntry gap() {
        return new ClanMembersLayoutEntry(null);
    }

    public static ClanMembersLayoutEntry member(ClanMember member) {
        return new ClanMembersLayoutEntry(member);
    }

    public boolean isGap() {
        return member == null;
    }

    public ClanMember member() {
        return member;
    }
}
