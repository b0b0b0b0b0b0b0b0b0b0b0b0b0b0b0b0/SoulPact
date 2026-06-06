package bm.b0b0b0.SoulPact.clan.model;

public final class ClanMemberManagementAction {

    public enum Kind {
        SET_ROLE,
        TRANSFER,
        KICK
    }

    private final Kind kind;
    private final String roleKey;

    public ClanMemberManagementAction(Kind kind, String roleKey) {
        this.kind = kind;
        this.roleKey = roleKey;
    }

    public Kind kind() {
        return kind;
    }

    public String roleKey() {
        return roleKey;
    }
}
