package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.model.ClanMemberManagementAction;
import bm.b0b0b0.SoulPact.clan.model.ClanMembershipHistoryEntry;
import java.util.List;

public final class ClanMemberDetailSnapshot {

    private final Clan clan;
    private final ClanMember member;
    private final String playerName;
    private final String roleTitle;
    private final List<ClanMembershipHistoryEntry> history;
    private final List<ClanMemberManagementAction> managementActions;

    public ClanMemberDetailSnapshot(
            Clan clan,
            ClanMember member,
            String playerName,
            String roleTitle,
            List<ClanMembershipHistoryEntry> history,
            List<ClanMemberManagementAction> managementActions
    ) {
        this.clan = clan;
        this.member = member;
        this.playerName = playerName;
        this.roleTitle = roleTitle;
        this.history = List.copyOf(history);
        this.managementActions = List.copyOf(managementActions);
    }

    public Clan clan() {
        return clan;
    }

    public ClanMember member() {
        return member;
    }

    public String playerName() {
        return playerName;
    }

    public String roleTitle() {
        return roleTitle;
    }

    public List<ClanMembershipHistoryEntry> history() {
        return history;
    }

    public List<ClanMemberManagementAction> managementActions() {
        return managementActions;
    }
}
