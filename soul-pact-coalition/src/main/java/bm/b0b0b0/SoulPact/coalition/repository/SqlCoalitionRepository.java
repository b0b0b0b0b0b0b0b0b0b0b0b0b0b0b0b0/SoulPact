package bm.b0b0b0.SoulPact.coalition.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.coalition.model.CoalitionInviteRecord;
import bm.b0b0b0.SoulPact.coalition.model.CoalitionInviteStatuses;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class SqlCoalitionRepository implements CoalitionRepository {

    private final SoulPactApi api;

    public SqlCoalitionRepository(SoulPactApi api) {
        this.api = api;
    }

    @Override
    public List<Long> listAllMemberClanIds() {
        String sql = "SELECT clan_id FROM coalition_members";
        List<Long> clanIds = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                clanIds.add(resultSet.getLong("clan_id"));
            }
            return clanIds;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to list coalition members", exception);
        }
    }

    @Override
    public Optional<Long> findCoalitionIdByClan(long clanId) {
        String sql = "SELECT coalition_id FROM coalition_members WHERE clan_id = ? LIMIT 1";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(resultSet.getLong("coalition_id"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to find coalition for clan", exception);
        }
    }

    @Override
    public List<Long> listMemberClanIds(long coalitionId) {
        String sql = "SELECT clan_id FROM coalition_members WHERE coalition_id = ? ORDER BY joined_at ASC";
        List<Long> clanIds = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, coalitionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clanIds.add(resultSet.getLong("clan_id"));
                }
            }
            return clanIds;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to list coalition member clans", exception);
        }
    }

    @Override
    public long createCoalition(long createdAt) {
        String sql = "INSERT INTO coalitions(created_at) VALUES(?)";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, createdAt);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("Coalition insert returned no key");
                }
                return keys.getLong(1);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to create coalition", exception);
        }
    }

    @Override
    public void addMember(long coalitionId, long clanId, long joinedAt) {
        String sql = "INSERT INTO coalition_members(coalition_id, clan_id, joined_at) VALUES(?, ?, ?)";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, coalitionId);
            statement.setLong(2, clanId);
            statement.setLong(3, joinedAt);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to add coalition member", exception);
        }
    }

    @Override
    public void removeMember(long clanId) {
        String sql = "DELETE FROM coalition_members WHERE clan_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to remove coalition member", exception);
        }
    }

    @Override
    public int countMembers(long coalitionId) {
        String sql = "SELECT COUNT(*) AS total FROM coalition_members WHERE coalition_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, coalitionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total");
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to count coalition members", exception);
        }
    }

    @Override
    public Optional<CoalitionInviteRecord> findPendingInvite(long inviteId) {
        return findInvite(inviteId, CoalitionInviteStatuses.PENDING);
    }

    @Override
    public Optional<CoalitionInviteRecord> findPendingInviteForTarget(long targetClanId, long inviteId) {
        String sql = """
                SELECT id, coalition_id, inviter_clan_id, target_clan_id, invited_by_uuid, created_at, status
                FROM coalition_invites
                WHERE id = ? AND target_clan_id = ? AND status = ?
                LIMIT 1
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, inviteId);
            statement.setLong(2, targetClanId);
            statement.setString(3, CoalitionInviteStatuses.PENDING);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapInvite(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to find coalition invite", exception);
        }
    }

    @Override
    public List<CoalitionInviteRecord> listPendingForTarget(long targetClanId) {
        String sql = """
                SELECT id, coalition_id, inviter_clan_id, target_clan_id, invited_by_uuid, created_at, status
                FROM coalition_invites
                WHERE target_clan_id = ? AND status = ?
                ORDER BY created_at ASC
                """;
        List<CoalitionInviteRecord> records = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, targetClanId);
            statement.setString(2, CoalitionInviteStatuses.PENDING);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(mapInvite(resultSet));
                }
            }
            return records;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to list coalition invites", exception);
        }
    }

    @Override
    public long createInvite(
            long coalitionId,
            long inviterClanId,
            long targetClanId,
            UUID invitedBy,
            long createdAt,
            String status
    ) {
        String sql = """
                INSERT INTO coalition_invites(
                    coalition_id, inviter_clan_id, target_clan_id, invited_by_uuid, created_at, status
                ) VALUES(?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, coalitionId);
            statement.setLong(2, inviterClanId);
            statement.setLong(3, targetClanId);
            statement.setString(4, invitedBy.toString());
            statement.setLong(5, createdAt);
            statement.setString(6, status);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("Invite insert returned no key");
                }
                return keys.getLong(1);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to create coalition invite", exception);
        }
    }

    @Override
    public void updateInviteStatus(long inviteId, String status) {
        String sql = "UPDATE coalition_invites SET status = ? WHERE id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setLong(2, inviteId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to update coalition invite", exception);
        }
    }

    @Override
    public void deleteInvitesForClan(long clanId) {
        String sql = "DELETE FROM coalition_invites WHERE inviter_clan_id = ? OR target_clan_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setLong(2, clanId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to delete coalition invites", exception);
        }
    }

    private Optional<CoalitionInviteRecord> findInvite(long inviteId, String status) {
        String sql = """
                SELECT id, coalition_id, inviter_clan_id, target_clan_id, invited_by_uuid, created_at, status
                FROM coalition_invites
                WHERE id = ? AND status = ?
                LIMIT 1
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, inviteId);
            statement.setString(2, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapInvite(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query coalition invite", exception);
        }
    }

    private CoalitionInviteRecord mapInvite(ResultSet resultSet) throws SQLException {
        return new CoalitionInviteRecord(
                resultSet.getLong("id"),
                resultSet.getLong("coalition_id"),
                resultSet.getLong("inviter_clan_id"),
                resultSet.getLong("target_clan_id"),
                UUID.fromString(resultSet.getString("invited_by_uuid")),
                resultSet.getLong("created_at"),
                resultSet.getString("status")
        );
    }
}
