package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.model.ClanMemberManagementAction;
import bm.b0b0b0.SoulPact.clan.model.ClanRolePermissionMap;
import bm.b0b0b0.SoulPact.clan.role.RoleTheme;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ClanMemberManagementPlanner {

    private static final String LEADER_ROLE = "leader";

    private final RoleThemeService roleThemeService;

    public ClanMemberManagementPlanner(RoleThemeService roleThemeService) {
        this.roleThemeService = roleThemeService;
    }

    public List<ClanMemberManagementAction> plan(
            Clan clan,
            ClanMember target,
            UUID viewerId,
            List<ClanMember> members,
            ClanRolePermissionMap permissions
    ) {
        if (target.playerId().equals(viewerId)) {
            return List.of();
        }
        RoleTheme theme = roleThemeService.theme();
        boolean leader = ClanStaffPermissions.isLeader(clan, viewerId);
        if (!leader && !ClanStaffPermissions.findMember(members, viewerId).isPresent()) {
            return List.of();
        }
        List<ClanMemberManagementAction> actions = new ArrayList<>();
        if (leader) {
            actions.add(new ClanMemberManagementAction(ClanMemberManagementAction.Kind.TRANSFER, null));
            for (String roleKey : theme.order()) {
                if (LEADER_ROLE.equals(roleKey)) {
                    continue;
                }
                if (roleKey.equals(target.role())) {
                    continue;
                }
                actions.add(new ClanMemberManagementAction(ClanMemberManagementAction.Kind.SET_ROLE, roleKey));
            }
        }
        if (ClanStaffPermissions.canKick(clan, members, viewerId, target, permissions, theme)) {
            actions.add(new ClanMemberManagementAction(ClanMemberManagementAction.Kind.KICK, null));
        }
        return List.copyOf(actions);
    }
}
