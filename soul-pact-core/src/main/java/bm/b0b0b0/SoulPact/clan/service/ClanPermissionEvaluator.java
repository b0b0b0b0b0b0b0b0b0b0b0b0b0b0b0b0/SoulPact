package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanRolePermissionMap;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClanPermissionEvaluator {

    private final DataSourceProvider dataSourceProvider;
    private final ClanRolePermissionService rolePermissionService;

    public ClanPermissionEvaluator(
            DataSourceProvider dataSourceProvider,
            ClanRolePermissionService rolePermissionService
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.rolePermissionService = rolePermissionService;
    }

    public boolean hasPermission(long clanId, UUID playerId, String permissionKey) {
        OptionalLeader leaderOptional = queryLeader(clanId);
        if (leaderOptional.isEmpty()) {
            return false;
        }
        if (leaderOptional.leaderId.equals(playerId)) {
            return true;
        }
        OptionalMemberRole memberRole = queryMemberRole(clanId, playerId);
        if (memberRole.isEmpty()) {
            return false;
        }
        if (ClanStaffPermissions.LEADER_ROLE.equals(memberRole.role)) {
            return true;
        }
        ClanRolePermissionMap permissions = queryPermissions(clanId);
        return permissions.isEnabled(
                memberRole.role,
                permissionKey,
                rolePermissionService.defaultFor(memberRole.role, permissionKey)
        );
    }

    private OptionalLeader queryLeader(long clanId) {
        String sql = "SELECT leader_uuid FROM clans WHERE id = ? LIMIT 1";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return OptionalLeader.empty();
                }
                return OptionalLeader.of(UUID.fromString(resultSet.getString("leader_uuid")));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan leader " + clanId, exception);
        }
    }

    private OptionalMemberRole queryMemberRole(long clanId, UUID playerId) {
        String sql = "SELECT role FROM clan_members WHERE clan_id = ? AND player_uuid = ? LIMIT 1";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return OptionalMemberRole.empty();
                }
                return OptionalMemberRole.of(resultSet.getString("role"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan member " + playerId, exception);
        }
    }

    private ClanRolePermissionMap queryPermissions(long clanId) {
        String sql = """
                SELECT role, permission_key, enabled
                FROM clan_role_permissions
                WHERE clan_id = ?
                """;
        Map<String, Map<String, Boolean>> byRole = new HashMap<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String role = resultSet.getString("role");
                    String permission = resultSet.getString("permission_key");
                    boolean enabled = resultSet.getInt("enabled") == 1;
                    byRole.computeIfAbsent(role, ignored -> new HashMap<>()).put(permission, enabled);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan permissions " + clanId, exception);
        }
        if (byRole.isEmpty()) {
            return ClanRolePermissionMap.empty();
        }
        return new ClanRolePermissionMap(byRole);
    }

    private record OptionalLeader(UUID leaderId) {
        static OptionalLeader empty() {
            return new OptionalLeader(null);
        }

        static OptionalLeader of(UUID leaderId) {
            return new OptionalLeader(leaderId);
        }

        boolean isEmpty() {
            return leaderId == null;
        }
    }

    private record OptionalMemberRole(String role) {
        static OptionalMemberRole empty() {
            return new OptionalMemberRole(null);
        }

        static OptionalMemberRole of(String role) {
            return new OptionalMemberRole(role);
        }

        boolean isEmpty() {
            return role == null;
        }
    }
}
