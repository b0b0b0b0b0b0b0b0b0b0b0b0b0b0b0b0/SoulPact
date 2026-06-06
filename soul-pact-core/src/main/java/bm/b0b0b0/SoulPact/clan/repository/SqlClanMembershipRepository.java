package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanInvite;
import bm.b0b0b0.SoulPact.clan.model.ClanJoinRequest;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SqlClanMembershipRepository implements ClanMembershipRepository {

    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public SqlClanMembershipRepository(
            DataSourceProvider dataSourceProvider,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    @Override
    public CompletableFuture<Optional<ClanInvite>> findInviteById(long inviteId) {
        return asyncDatabaseExecutor.supply(() -> queryInviteById(inviteId));
    }

    @Override
    public CompletableFuture<List<ClanInvite>> findInvitesByPlayerId(UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> queryInvitesByPlayerId(playerId));
    }

    @Override
    public CompletableFuture<Optional<ClanInvite>> findInvite(long clanId, UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> queryInvite(clanId, playerId));
    }

    @Override
    public CompletableFuture<ClanInvite> createInvite(long clanId, UUID playerId, UUID inviterId, long createdAt) {
        return asyncDatabaseExecutor.supply(() -> insertInvite(clanId, playerId, inviterId, createdAt));
    }

    @Override
    public CompletableFuture<Boolean> deleteInvite(long inviteId) {
        return asyncDatabaseExecutor.supply(() -> deleteInviteById(inviteId));
    }

    @Override
    public CompletableFuture<Optional<ClanJoinRequest>> findJoinRequestById(long requestId) {
        return asyncDatabaseExecutor.supply(() -> queryJoinRequestById(requestId));
    }

    @Override
    public CompletableFuture<List<ClanJoinRequest>> findJoinRequestsByClanId(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryJoinRequestsByClanId(clanId));
    }

    @Override
    public CompletableFuture<List<ClanJoinRequest>> findJoinRequestsByLeaderId(UUID leaderId) {
        return asyncDatabaseExecutor.supply(() -> queryJoinRequestsByLeaderId(leaderId));
    }

    @Override
    public CompletableFuture<Optional<ClanJoinRequest>> findJoinRequest(long clanId, UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> queryJoinRequest(clanId, playerId));
    }

    @Override
    public CompletableFuture<ClanJoinRequest> createJoinRequest(long clanId, UUID playerId, long createdAt) {
        return asyncDatabaseExecutor.supply(() -> insertJoinRequest(clanId, playerId, createdAt));
    }

    @Override
    public CompletableFuture<Boolean> deleteJoinRequest(long requestId) {
        return asyncDatabaseExecutor.supply(() -> deleteJoinRequestById(requestId));
    }

    @Override
    public CompletableFuture<Integer> deleteInvitesByPlayerId(UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> deleteInvitesForPlayer(playerId));
    }

    @Override
    public CompletableFuture<Integer> deleteJoinRequestsByPlayerId(UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> deleteJoinRequestsForPlayer(playerId));
    }

    @Override
    public CompletableFuture<Boolean> isJoinBlocked(long clanId, UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> queryJoinBlocked(clanId, playerId));
    }

    @Override
    public CompletableFuture<Boolean> createJoinBlock(long clanId, UUID playerId, long blockedAt) {
        return asyncDatabaseExecutor.supply(() -> insertJoinBlock(clanId, playerId, blockedAt));
    }

    @Override
    public CompletableFuture<Integer> countJoinRequestsByClanId(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryJoinRequestCount(clanId));
    }

    @Override
    public CompletableFuture<List<ClanJoinRequest>> deleteJoinRequestsByClanId(long clanId) {
        return asyncDatabaseExecutor.supply(() -> deleteJoinRequestsForClan(clanId));
    }

    private int queryJoinRequestCount(long clanId) {
        String sql = "SELECT COUNT(*) AS total FROM clan_join_requests WHERE clan_id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0;
                }
                return resultSet.getInt("total");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to count join requests", exception);
        }
    }

    private Optional<ClanInvite> queryInviteById(long inviteId) {
        String sql = """
                SELECT id, clan_id, player_uuid, inviter_uuid, created_at
                FROM clan_invites
                WHERE id = ?
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, inviteId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapInviteRow(resultSet));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query invite", exception);
        }
    }

    private List<ClanInvite> queryInvitesByPlayerId(UUID playerId) {
        String sql = """
                SELECT id, clan_id, player_uuid, inviter_uuid, created_at
                FROM clan_invites
                WHERE player_uuid = ?
                ORDER BY created_at ASC
                """;
        List<ClanInvite> invites = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    invites.add(mapInviteRow(resultSet));
                }
            }
            return invites;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query invites by player", exception);
        }
    }

    private Optional<ClanInvite> queryInvite(long clanId, UUID playerId) {
        String sql = """
                SELECT id, clan_id, player_uuid, inviter_uuid, created_at
                FROM clan_invites
                WHERE clan_id = ? AND player_uuid = ?
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapInviteRow(resultSet));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query invite", exception);
        }
    }

    private ClanInvite insertInvite(long clanId, UUID playerId, UUID inviterId, long createdAt) {
        String sql = """
                INSERT INTO clan_invites (clan_id, player_uuid, inviter_uuid, created_at)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            statement.setString(3, inviterId.toString());
            statement.setLong(4, createdAt);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new IllegalStateException("Failed to read invite id");
                }
                return new ClanInvite(
                        generatedKeys.getLong(1),
                        clanId,
                        playerId,
                        inviterId,
                        createdAt
                );
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create invite", exception);
        }
    }

    private boolean deleteInviteById(long inviteId) {
        String sql = "DELETE FROM clan_invites WHERE id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, inviteId);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to delete invite", exception);
        }
    }

    private Optional<ClanJoinRequest> queryJoinRequestById(long requestId) {
        String sql = """
                SELECT id, clan_id, player_uuid, created_at
                FROM clan_join_requests
                WHERE id = ?
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, requestId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapJoinRequestRow(resultSet));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query join request", exception);
        }
    }

    private List<ClanJoinRequest> queryJoinRequestsByClanId(long clanId) {
        String sql = """
                SELECT id, clan_id, player_uuid, created_at
                FROM clan_join_requests
                WHERE clan_id = ?
                ORDER BY created_at ASC
                """;
        List<ClanJoinRequest> requests = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapJoinRequestRow(resultSet));
                }
            }
            return requests;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query join requests by clan", exception);
        }
    }

    private List<ClanJoinRequest> queryJoinRequestsByLeaderId(UUID leaderId) {
        String sql = """
                SELECT r.id, r.clan_id, r.player_uuid, r.created_at
                FROM clan_join_requests r
                INNER JOIN clans c ON c.id = r.clan_id
                WHERE c.leader_uuid = ?
                ORDER BY r.created_at ASC
                """;
        List<ClanJoinRequest> requests = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, leaderId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapJoinRequestRow(resultSet));
                }
            }
            return requests;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query join requests by leader", exception);
        }
    }

    private Optional<ClanJoinRequest> queryJoinRequest(long clanId, UUID playerId) {
        String sql = """
                SELECT id, clan_id, player_uuid, created_at
                FROM clan_join_requests
                WHERE clan_id = ? AND player_uuid = ?
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapJoinRequestRow(resultSet));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query join request", exception);
        }
    }

    private ClanJoinRequest insertJoinRequest(long clanId, UUID playerId, long createdAt) {
        String sql = """
                INSERT INTO clan_join_requests (clan_id, player_uuid, created_at)
                VALUES (?, ?, ?)
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            statement.setLong(3, createdAt);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new IllegalStateException("Failed to read join request id");
                }
                return new ClanJoinRequest(
                        generatedKeys.getLong(1),
                        clanId,
                        playerId,
                        createdAt
                );
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create join request", exception);
        }
    }

    private boolean deleteJoinRequestById(long requestId) {
        String sql = "DELETE FROM clan_join_requests WHERE id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, requestId);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to delete join request", exception);
        }
    }

    private int deleteInvitesForPlayer(UUID playerId) {
        String sql = "DELETE FROM clan_invites WHERE player_uuid = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            return statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to delete invites by player", exception);
        }
    }

    private int deleteJoinRequestsForPlayer(UUID playerId) {
        String sql = "DELETE FROM clan_join_requests WHERE player_uuid = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            return statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to delete join requests by player", exception);
        }
    }

    private boolean queryJoinBlocked(long clanId, UUID playerId) {
        String sql = """
                SELECT 1
                FROM clan_join_blocks
                WHERE clan_id = ? AND player_uuid = ?
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query join block", exception);
        }
    }

    private boolean insertJoinBlock(long clanId, UUID playerId, long blockedAt) {
        String sql = """
                INSERT INTO clan_join_blocks (clan_id, player_uuid, blocked_at)
                VALUES (?, ?, ?)
                ON CONFLICT(clan_id, player_uuid) DO UPDATE SET blocked_at = excluded.blocked_at
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            statement.setLong(3, blockedAt);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create join block", exception);
        }
    }

    private List<ClanJoinRequest> deleteJoinRequestsForClan(long clanId) {
        List<ClanJoinRequest> requests = queryJoinRequestsByClanId(clanId);
        String sql = "DELETE FROM clan_join_requests WHERE clan_id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.executeUpdate();
            return requests;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to delete join requests by clan", exception);
        }
    }

    private static ClanInvite mapInviteRow(ResultSet resultSet) throws Exception {
        return new ClanInvite(
                resultSet.getLong("id"),
                resultSet.getLong("clan_id"),
                UUID.fromString(resultSet.getString("player_uuid")),
                UUID.fromString(resultSet.getString("inviter_uuid")),
                resultSet.getLong("created_at")
        );
    }

    private static ClanJoinRequest mapJoinRequestRow(ResultSet resultSet) throws Exception {
        return new ClanJoinRequest(
                resultSet.getLong("id"),
                resultSet.getLong("clan_id"),
                UUID.fromString(resultSet.getString("player_uuid")),
                resultSet.getLong("created_at")
        );
    }
}
