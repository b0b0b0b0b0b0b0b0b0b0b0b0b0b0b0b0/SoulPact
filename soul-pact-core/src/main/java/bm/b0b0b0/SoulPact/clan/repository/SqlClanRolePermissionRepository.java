package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanRolePermissionMap;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class SqlClanRolePermissionRepository implements ClanRolePermissionRepository {

    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public SqlClanRolePermissionRepository(
            DataSourceProvider dataSourceProvider,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    @Override
    public CompletableFuture<ClanRolePermissionMap> findByClanId(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryByClanId(clanId));
    }

    @Override
    public CompletableFuture<Void> upsert(long clanId, String role, String permission, boolean enabled) {
        return asyncDatabaseExecutor.run(() -> insertOrUpdate(clanId, role, permission, enabled));
    }

    @Override
    public CompletableFuture<Void> seedDefaults(long clanId, ClanRolePermissionMap defaults) {
        return asyncDatabaseExecutor.run(() -> {
            for (Map.Entry<String, Map<String, Boolean>> roleEntry : defaults.byRole().entrySet()) {
                for (Map.Entry<String, Boolean> permissionEntry : roleEntry.getValue().entrySet()) {
                    insertOrUpdate(clanId, roleEntry.getKey(), permissionEntry.getKey(), permissionEntry.getValue());
                }
            }
        });
    }

    private ClanRolePermissionMap queryByClanId(long clanId) {
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
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to load clan role permissions", exception);
        }
        return new ClanRolePermissionMap(byRole);
    }

    private void insertOrUpdate(long clanId, String role, String permission, boolean enabled) {
        String sql = """
                INSERT INTO clan_role_permissions (clan_id, role, permission_key, enabled)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(clan_id, role, permission_key) DO UPDATE SET enabled = excluded.enabled
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, role);
            statement.setString(3, permission);
            statement.setInt(4, enabled ? 1 : 0);
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to save clan role permission", exception);
        }
    }
}
