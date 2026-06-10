package bm.b0b0b0.SoulPact.quests.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.quests.model.ClanQuestRecord;
import bm.b0b0b0.SoulPact.quests.model.QuestStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class SqlClanQuestRepository implements ClanQuestRepository {

    private final SoulPactApi api;

    public SqlClanQuestRepository(SoulPactApi api) {
        this.api = api;
    }

    @Override
    public List<ClanQuestRecord> findAllActive() {
        String sql = selectColumns() + " WHERE status = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, QuestStatus.ACTIVE.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ClanQuestRecord> records = new ArrayList<>();
                while (resultSet.next()) {
                    records.add(readRecord(resultSet));
                }
                return records;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query active clan quests", exception);
        }
    }

    @Override
    public Map<String, ClanQuestRecord> findByClan(long clanId) {
        String sql = selectColumns() + " WHERE clan_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                Map<String, ClanQuestRecord> records = new LinkedHashMap<>();
                while (resultSet.next()) {
                    ClanQuestRecord record = readRecord(resultSet);
                    records.put(record.questId(), record);
                }
                return records;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan quests", exception);
        }
    }

    @Override
    public Optional<ClanQuestRecord> findActive(long clanId) {
        String sql = selectColumns() + " WHERE clan_id = ? AND status = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, QuestStatus.ACTIVE.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(readRecord(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query active clan quest", exception);
        }
    }

    @Override
    public boolean insertActive(ClanQuestRecord record) {
        String sql = """
                INSERT INTO clan_quests(clan_id, quest_id, status, progress, started_at, expires_at, completed_at, started_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, record.clanId());
            statement.setString(2, record.questId());
            statement.setString(3, record.status().name());
            statement.setInt(4, record.progress());
            statement.setLong(5, record.startedAt());
            statement.setLong(6, record.expiresAt());
            statement.setLong(7, record.completedAt());
            statement.setString(8, record.startedBy().toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            return false;
        }
    }

    @Override
    public boolean reactivate(ClanQuestRecord record) {
        String sql = """
                UPDATE clan_quests
                SET status = ?, progress = ?, started_at = ?, expires_at = ?, completed_at = 0, started_by = ?
                WHERE clan_id = ? AND quest_id = ?
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, QuestStatus.ACTIVE.name());
            statement.setInt(2, record.progress());
            statement.setLong(3, record.startedAt());
            statement.setLong(4, record.expiresAt());
            statement.setString(5, record.startedBy().toString());
            statement.setLong(6, record.clanId());
            statement.setString(7, record.questId());
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to reactivate clan quest", exception);
        }
    }

    @Override
    public void updateProgress(long clanId, String questId, int progress) {
        String sql = "UPDATE clan_quests SET progress = ? WHERE clan_id = ? AND quest_id = ? AND status = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, progress);
            statement.setLong(2, clanId);
            statement.setString(3, questId);
            statement.setString(4, QuestStatus.ACTIVE.name());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to update clan quest progress", exception);
        }
    }

    @Override
    public void markCompleted(long clanId, String questId, int progress, long completedAt) {
        String sql = "UPDATE clan_quests SET status = ?, progress = ?, completed_at = ? WHERE clan_id = ? AND quest_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, QuestStatus.COMPLETED.name());
            statement.setInt(2, progress);
            statement.setLong(3, completedAt);
            statement.setLong(4, clanId);
            statement.setString(5, questId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to complete clan quest", exception);
        }
    }

    @Override
    public boolean deleteActive(long clanId, String questId) {
        String sql = "DELETE FROM clan_quests WHERE clan_id = ? AND quest_id = ? AND status = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, questId);
            statement.setString(3, QuestStatus.ACTIVE.name());
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to delete active clan quest", exception);
        }
    }

    private String selectColumns() {
        return "SELECT clan_id, quest_id, status, progress, started_at, expires_at, completed_at, started_by FROM clan_quests";
    }

    private ClanQuestRecord readRecord(ResultSet resultSet) throws SQLException {
        return new ClanQuestRecord(
                resultSet.getLong("clan_id"),
                resultSet.getString("quest_id"),
                QuestStatus.valueOf(resultSet.getString("status")),
                resultSet.getInt("progress"),
                resultSet.getLong("started_at"),
                resultSet.getLong("expires_at"),
                resultSet.getLong("completed_at"),
                UUID.fromString(resultSet.getString("started_by"))
        );
    }
}
