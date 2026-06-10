package bm.b0b0b0.SoulPact.gladiator.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaPoint;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaSchedule;
import bm.b0b0b0.SoulPact.gladiator.model.ScheduleType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SqlArenaRepository implements ArenaRepository {

    private final SoulPactApi api;

    public SqlArenaRepository(SoulPactApi api) {
        this.api = api;
    }

    @Override
    public List<Arena> loadArenas() {
        String sql = """
                SELECT name, enabled, icon, tag, description, holder_clan_id, holder_clan_tag,
                       region, spawn_point, watch_point, exit_point, lobby_point
                FROM glad_arenas
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Arena> arenas = new ArrayList<>();
            while (resultSet.next()) {
                arenas.add(readArena(resultSet));
            }
            return arenas;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load gladiator arenas", exception);
        }
    }

    @Override
    public Map<String, List<String>> loadRewards() {
        String sql = "SELECT arena_name, command FROM glad_rewards ORDER BY id";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            Map<String, List<String>> rewards = new HashMap<>();
            while (resultSet.next()) {
                rewards.computeIfAbsent(resultSet.getString("arena_name"), key -> new ArrayList<>())
                        .add(resultSet.getString("command"));
            }
            return rewards;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load gladiator rewards", exception);
        }
    }

    @Override
    public Map<String, List<ArenaSchedule>> loadSchedules() {
        String sql = "SELECT id, arena_name, schedule_type, day_of_week, hour, minute FROM glad_schedules ORDER BY id";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            Map<String, List<ArenaSchedule>> schedules = new HashMap<>();
            while (resultSet.next()) {
                ArenaSchedule schedule = new ArenaSchedule(
                        resultSet.getLong("id"),
                        resultSet.getString("arena_name"),
                        ScheduleType.valueOf(resultSet.getString("schedule_type")),
                        resultSet.getInt("day_of_week"),
                        resultSet.getInt("hour"),
                        resultSet.getInt("minute")
                );
                schedules.computeIfAbsent(schedule.arenaName(), key -> new ArrayList<>()).add(schedule);
            }
            return schedules;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load gladiator schedules", exception);
        }
    }

    @Override
    public void upsertArena(Arena arena) {
        String deleteSql = "DELETE FROM glad_arenas WHERE name = ?";
        String insertSql = """
                INSERT INTO glad_arenas(name, enabled, icon, tag, description, holder_clan_id, holder_clan_tag,
                                        region, spawn_point, watch_point, exit_point, lobby_point)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = api.dataSource().getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                    deleteStatement.setString(1, arena.name());
                    deleteStatement.executeUpdate();
                }
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setString(1, arena.name());
                    insertStatement.setInt(2, arena.enabled() ? 1 : 0);
                    insertStatement.setString(3, arena.icon());
                    insertStatement.setString(4, arena.tag());
                    insertStatement.setString(5, arena.description());
                    insertStatement.setLong(6, arena.holderClanId());
                    insertStatement.setString(7, arena.holderClanTag());
                    insertStatement.setString(8, arena.region());
                    insertStatement.setString(9, arena.points().getOrDefault(ArenaPoint.SPAWN, ""));
                    insertStatement.setString(10, arena.points().getOrDefault(ArenaPoint.WATCH, ""));
                    insertStatement.setString(11, arena.points().getOrDefault(ArenaPoint.EXIT, ""));
                    insertStatement.setString(12, arena.points().getOrDefault(ArenaPoint.LOBBY, ""));
                    insertStatement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to save gladiator arena " + arena.name(), exception);
        }
    }

    @Override
    public void deleteArena(String arenaName) {
        try (Connection connection = api.dataSource().getConnection()) {
            for (String sql : List.of(
                    "DELETE FROM glad_rewards WHERE arena_name = ?",
                    "DELETE FROM glad_schedules WHERE arena_name = ?",
                    "DELETE FROM glad_arenas WHERE name = ?"
            )) {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, arenaName);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to delete gladiator arena " + arenaName, exception);
        }
    }

    @Override
    public long addReward(String arenaName, String command) {
        String sql = "INSERT INTO glad_rewards(arena_name, command) VALUES (?, ?)";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, arenaName);
            statement.setString(2, command);
            statement.executeUpdate();
            return readGeneratedKey(statement);
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to add gladiator reward", exception);
        }
    }

    @Override
    public void clearRewards(String arenaName) {
        String sql = "DELETE FROM glad_rewards WHERE arena_name = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, arenaName);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to clear gladiator rewards", exception);
        }
    }

    @Override
    public long addSchedule(String arenaName, ArenaSchedule schedule) {
        String sql = "INSERT INTO glad_schedules(arena_name, schedule_type, day_of_week, hour, minute) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, arenaName);
            statement.setString(2, schedule.type().name());
            statement.setInt(3, schedule.dayOfWeek());
            statement.setInt(4, schedule.hour());
            statement.setInt(5, schedule.minute());
            statement.executeUpdate();
            return readGeneratedKey(statement);
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to add gladiator schedule", exception);
        }
    }

    @Override
    public boolean deleteSchedule(long scheduleId) {
        String sql = "DELETE FROM glad_schedules WHERE id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, scheduleId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to delete gladiator schedule", exception);
        }
    }

    private Arena readArena(ResultSet resultSet) throws SQLException {
        Map<ArenaPoint, String> points = new EnumMap<>(ArenaPoint.class);
        points.put(ArenaPoint.SPAWN, resultSet.getString("spawn_point"));
        points.put(ArenaPoint.WATCH, resultSet.getString("watch_point"));
        points.put(ArenaPoint.EXIT, resultSet.getString("exit_point"));
        points.put(ArenaPoint.LOBBY, resultSet.getString("lobby_point"));
        return new Arena(
                resultSet.getString("name"),
                resultSet.getInt("enabled") == 1,
                resultSet.getString("icon"),
                resultSet.getString("tag"),
                resultSet.getString("description"),
                resultSet.getLong("holder_clan_id"),
                resultSet.getString("holder_clan_tag"),
                resultSet.getString("region"),
                points
        );
    }

    private long readGeneratedKey(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            return keys.next() ? keys.getLong(1) : 0L;
        }
    }
}
