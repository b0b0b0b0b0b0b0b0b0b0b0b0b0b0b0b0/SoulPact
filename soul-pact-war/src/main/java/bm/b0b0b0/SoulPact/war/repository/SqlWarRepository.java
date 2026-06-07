package bm.b0b0b0.SoulPact.war.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarCaptureRecord;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import bm.b0b0b0.SoulPact.war.model.WarFlagSnapshot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class SqlWarRepository implements WarRepository {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_RESOLVED = "RESOLVED";
    private static final String STATUS_RANSOM = "RANSOM";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_FINISHED = "FINISHED";

    private final SoulPactApi api;

    public SqlWarRepository(SoulPactApi api) {
        this.api = api;
    }

    @Override
    public Optional<WarDeclarationRecord> findPendingDeclaration(long attackerClanId, long defenderClanId) {
        String sql = """
                SELECT id, attacker_clan_id, defender_clan_id, declared_by_uuid, created_at, status
                FROM clan_war_declarations
                WHERE attacker_clan_id = ? AND defender_clan_id = ? AND status = ?
                LIMIT 1
                """;
        return queryDeclaration(sql, attackerClanId, defenderClanId, STATUS_PENDING);
    }

    @Override
    public Optional<WarDeclarationRecord> findPendingForDefender(long defenderClanId) {
        String sql = """
                SELECT id, attacker_clan_id, defender_clan_id, declared_by_uuid, created_at, status
                FROM clan_war_declarations
                WHERE defender_clan_id = ? AND status = ?
                ORDER BY created_at ASC
                LIMIT 1
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, defenderClanId);
            statement.setString(2, STATUS_PENDING);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapDeclaration(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query war declaration", exception);
        }
    }

    @Override
    public List<WarDeclarationRecord> listPendingForDefender(long defenderClanId) {
        String sql = """
                SELECT id, attacker_clan_id, defender_clan_id, declared_by_uuid, created_at, status
                FROM clan_war_declarations
                WHERE defender_clan_id = ? AND status = ?
                ORDER BY created_at ASC
                """;
        List<WarDeclarationRecord> records = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, defenderClanId);
            statement.setString(2, STATUS_PENDING);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(mapDeclaration(resultSet));
                }
            }
            return records;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to list war declarations", exception);
        }
    }

    @Override
    public List<WarDeclarationRecord> listAllPendingDeclarations() {
        String sql = """
                SELECT id, attacker_clan_id, defender_clan_id, declared_by_uuid, created_at, status
                FROM clan_war_declarations
                WHERE status = ?
                ORDER BY created_at ASC
                """;
        List<WarDeclarationRecord> records = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, STATUS_PENDING);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(mapDeclaration(resultSet));
                }
            }
            return records;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to list pending war declarations", exception);
        }
    }

    @Override
    public long createDeclaration(long attackerClanId, long defenderClanId, UUID declaredBy, long createdAt) {
        String sql = """
                INSERT INTO clan_war_declarations(attacker_clan_id, defender_clan_id, declared_by_uuid, created_at, status)
                VALUES(?, ?, ?, ?, ?)
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, attackerClanId);
            statement.setLong(2, defenderClanId);
            statement.setString(3, declaredBy.toString());
            statement.setLong(4, createdAt);
            statement.setString(5, STATUS_PENDING);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("War declaration insert returned no key");
                }
                return keys.getLong(1);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to create war declaration", exception);
        }
    }

    @Override
    public void updateDeclarationStatus(long declarationId, String status) {
        String sql = "UPDATE clan_war_declarations SET status = ? WHERE id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setLong(2, declarationId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to update war declaration", exception);
        }
    }

    @Override
    public Optional<ActiveWarRecord> findActiveWar(long clanId) {
        String sql = """
                SELECT id, attacker_clan_id, defender_clan_id, started_at, status
                FROM clan_wars
                WHERE status = ? AND (attacker_clan_id = ? OR defender_clan_id = ?)
                LIMIT 1
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, STATUS_ACTIVE);
            statement.setLong(2, clanId);
            statement.setLong(3, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapWar(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query active war", exception);
        }
    }

    @Override
    public Optional<ActiveWarRecord> findActiveWarBetween(long clanA, long clanB) {
        String sql = """
                SELECT id, attacker_clan_id, defender_clan_id, started_at, status
                FROM clan_wars
                WHERE status = ?
                  AND ((attacker_clan_id = ? AND defender_clan_id = ?) OR (attacker_clan_id = ? AND defender_clan_id = ?))
                LIMIT 1
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, STATUS_ACTIVE);
            statement.setLong(2, clanA);
            statement.setLong(3, clanB);
            statement.setLong(4, clanB);
            statement.setLong(5, clanA);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapWar(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query war between clans", exception);
        }
    }

    @Override
    public List<ActiveWarRecord> listAllActiveWars() {
        String sql = """
                SELECT id, attacker_clan_id, defender_clan_id, started_at, status
                FROM clan_wars
                WHERE status = ?
                """;
        List<ActiveWarRecord> records = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, STATUS_ACTIVE);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(mapWar(resultSet));
                }
            }
            return records;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to list active wars", exception);
        }
    }

    @Override
    public long createActiveWar(
            long attackerClanId,
            long defenderClanId,
            long startedAt,
            WarFlagSnapshot attackerFlag,
            WarFlagSnapshot defenderFlag
    ) {
        String sql = """
                INSERT INTO clan_wars(
                    attacker_clan_id,
                    defender_clan_id,
                    started_at,
                    status,
                    attacker_flag_world,
                    attacker_flag_x,
                    attacker_flag_y,
                    attacker_flag_z,
                    defender_flag_world,
                    defender_flag_x,
                    defender_flag_y,
                    defender_flag_z
                )
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, attackerClanId);
            statement.setLong(2, defenderClanId);
            statement.setLong(3, startedAt);
            statement.setString(4, STATUS_ACTIVE);
            statement.setString(5, attackerFlag.world());
            statement.setInt(6, attackerFlag.x());
            statement.setInt(7, attackerFlag.y());
            statement.setInt(8, attackerFlag.z());
            statement.setString(9, defenderFlag.world());
            statement.setInt(10, defenderFlag.x());
            statement.setInt(11, defenderFlag.y());
            statement.setInt(12, defenderFlag.z());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("Active war insert returned no key");
                }
                return keys.getLong(1);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to create active war", exception);
        }
    }

    @Override
    public void finishWar(long warId, String status) {
        String sql = "UPDATE clan_wars SET status = ? WHERE id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setLong(2, warId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to finish war", exception);
        }
    }

    @Override
    public void upsertCapture(long warId, long holderClanId, long targetClanId, long capturedAt, long deadlineAt) {
        String sql = """
                INSERT INTO clan_war_captures(war_id, holder_clan_id, target_clan_id, captured_at, deadline_at)
                VALUES(?, ?, ?, ?, ?)
                ON CONFLICT(war_id) DO UPDATE SET
                    holder_clan_id = excluded.holder_clan_id,
                    target_clan_id = excluded.target_clan_id,
                    captured_at = excluded.captured_at,
                    deadline_at = excluded.deadline_at
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, warId);
            statement.setLong(2, holderClanId);
            statement.setLong(3, targetClanId);
            statement.setLong(4, capturedAt);
            statement.setLong(5, deadlineAt);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to upsert war capture", exception);
        }
    }

    @Override
    public List<WarCaptureRecord> listActiveCaptures() {
        String sql = """
                SELECT c.war_id, c.holder_clan_id, c.target_clan_id, c.captured_at, c.deadline_at
                FROM clan_war_captures c
                INNER JOIN clan_wars w ON w.id = c.war_id
                WHERE w.status = ?
                """;
        List<WarCaptureRecord> records = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, STATUS_ACTIVE);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(new WarCaptureRecord(
                            resultSet.getLong("war_id"),
                            resultSet.getLong("holder_clan_id"),
                            resultSet.getLong("target_clan_id"),
                            resultSet.getLong("captured_at"),
                            resultSet.getLong("deadline_at")
                    ));
                }
            }
            return records;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to list active war captures", exception);
        }
    }

    @Override
    public void clearCapture(long warId) {
        String sql = "DELETE FROM clan_war_captures WHERE war_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, warId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to clear war capture", exception);
        }
    }

    public String pendingStatus() {
        return STATUS_PENDING;
    }

    public String resolvedStatus() {
        return STATUS_RESOLVED;
    }

    public String ransomStatus() {
        return STATUS_RANSOM;
    }

    public String activeStatus() {
        return STATUS_ACTIVE;
    }

    public String finishedStatus() {
        return STATUS_FINISHED;
    }

    private Optional<WarDeclarationRecord> queryDeclaration(String sql, long attackerClanId, long defenderClanId, String status) {
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, attackerClanId);
            statement.setLong(2, defenderClanId);
            statement.setString(3, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapDeclaration(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query war declaration", exception);
        }
    }

    private WarDeclarationRecord mapDeclaration(ResultSet resultSet) throws SQLException {
        return new WarDeclarationRecord(
                resultSet.getLong("id"),
                resultSet.getLong("attacker_clan_id"),
                resultSet.getLong("defender_clan_id"),
                UUID.fromString(resultSet.getString("declared_by_uuid")),
                resultSet.getLong("created_at"),
                resultSet.getString("status")
        );
    }

    private ActiveWarRecord mapWar(ResultSet resultSet) throws SQLException {
        return new ActiveWarRecord(
                resultSet.getLong("id"),
                resultSet.getLong("attacker_clan_id"),
                resultSet.getLong("defender_clan_id"),
                resultSet.getLong("started_at"),
                resultSet.getString("status"),
                resultSet.getString("attacker_flag_world"),
                resultSet.getInt("attacker_flag_x"),
                resultSet.getInt("attacker_flag_y"),
                resultSet.getInt("attacker_flag_z"),
                resultSet.getString("defender_flag_world"),
                resultSet.getInt("defender_flag_x"),
                resultSet.getInt("defender_flag_y"),
                resultSet.getInt("defender_flag_z")
        );
    }
}
