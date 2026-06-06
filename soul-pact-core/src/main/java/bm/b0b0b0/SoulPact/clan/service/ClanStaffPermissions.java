package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.model.ClanPermissionKeys;
import bm.b0b0b0.SoulPact.clan.model.ClanRolePermissionMap;
import bm.b0b0b0.SoulPact.clan.role.RoleTheme;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ClanStaffPermissions {

    public static final String LEADER_ROLE = "leader";

    private ClanStaffPermissions() {
    }

    public static Optional<ClanMember> findMember(List<ClanMember> members, UUID playerId) {
        return members.stream().filter(member -> member.playerId().equals(playerId)).findFirst();
    }

    public static boolean isLeader(Clan clan, UUID playerId) {
        return clan.leaderId().equals(playerId);
    }

    public static boolean canReviewRequests(
            Clan clan,
            List<ClanMember> members,
            UUID playerId,
            ClanRolePermissionMap permissions
    ) {
        if (isLeader(clan, playerId)) {
            return true;
        }
        return findMember(members, playerId)
                .map(member -> permissions.isEnabled(
                        member.role(),
                        ClanPermissionKeys.ACCEPT,
                        false
                ))
                .orElse(false);
    }

    public static boolean canManageRecruitmentSettings(Clan clan, UUID playerId) {
        return isLeader(clan, playerId);
    }

    public static boolean canAssignRoles(Clan clan, UUID playerId) {
        return isLeader(clan, playerId);
    }

    public static boolean canKick(
            Clan clan,
            List<ClanMember> members,
            UUID actorId,
            ClanMember target,
            ClanRolePermissionMap permissions,
            RoleTheme roleTheme
    ) {
        if (actorId.equals(target.playerId())) {
            return false;
        }
        if (isLeader(clan, target.playerId()) || LEADER_ROLE.equals(target.role())) {
            return false;
        }
        if (isLeader(clan, actorId)) {
            return isLowerRank(roleTheme, LEADER_ROLE, target.role());
        }
        Optional<ClanMember> actorOptional = findMember(members, actorId);
        if (actorOptional.isEmpty()) {
            return false;
        }
        ClanMember actor = actorOptional.get();
        if (!permissions.isEnabled(actor.role(), ClanPermissionKeys.KICK, false)) {
            return false;
        }
        return isLowerRank(roleTheme, actor.role(), target.role());
    }

    public static boolean isLowerRank(RoleTheme roleTheme, String actorRole, String targetRole) {
        int actorIndex = roleTheme.order().indexOf(actorRole);
        int targetIndex = roleTheme.order().indexOf(targetRole);
        if (actorIndex < 0 || targetIndex < 0) {
            return false;
        }
        return targetIndex > actorIndex;
    }
}
