package bm.b0b0b0.SoulPact.leaderboard.repository;

import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import bm.b0b0b0.SoulPact.leaderboard.model.BoardKind;
import bm.b0b0b0.SoulPact.leaderboard.model.BoardStatistic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public final class SqlBoardRepository implements BoardRepository {

    private final DataSource dataSource;

    public SqlBoardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Board insert(Board board) {
        String sql = """
                INSERT INTO lb_boards(statistic, rank_position, kind, world, x, y, z, yaw)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, board.statistic().name());
            statement.setInt(2, board.rankPosition());
            statement.setString(3, board.kind().name());
            statement.setString(4, board.world());
            statement.setDouble(5, board.x());
            statement.setDouble(6, board.y());
            statement.setDouble(7, board.z());
            statement.setFloat(8, board.yaw());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                long id = keys.next() ? keys.getLong(1) : 0;
                return new Board(id, board.statistic(), board.rankPosition(), board.kind(),
                        board.world(), board.x(), board.y(), board.z(), board.yaw());
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to insert board", exception);
        }
    }

    @Override
    public boolean deleteById(long boardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM lb_boards WHERE id = ?")) {
            statement.setLong(1, boardId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to delete board " + boardId, exception);
        }
    }

    @Override
    public Optional<Board> findById(long boardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM lb_boards WHERE id = ?")) {
            statement.setLong(1, boardId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return mapRow(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to find board " + boardId, exception);
        }
    }

    @Override
    public List<Board> findAll() {
        List<Board> boards = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM lb_boards ORDER BY id");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                mapRow(resultSet).ifPresent(boards::add);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load boards", exception);
        }
        return boards;
    }

    private Optional<Board> mapRow(ResultSet resultSet) throws SQLException {
        Optional<BoardStatistic> statistic = BoardStatistic.parse(resultSet.getString("statistic"));
        Optional<BoardKind> kind = BoardKind.parse(resultSet.getString("kind"));
        if (statistic.isEmpty() || kind.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Board(
                resultSet.getLong("id"),
                statistic.get(),
                resultSet.getInt("rank_position"),
                kind.get(),
                resultSet.getString("world"),
                resultSet.getDouble("x"),
                resultSet.getDouble("y"),
                resultSet.getDouble("z"),
                resultSet.getFloat("yaw")
        ));
    }
}
