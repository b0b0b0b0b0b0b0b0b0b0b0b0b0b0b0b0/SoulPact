package bm.b0b0b0.SoulPact.leaderboard.repository;

import bm.b0b0b0.SoulPact.leaderboard.model.BoardStatistic;
import bm.b0b0b0.SoulPact.leaderboard.model.ClanStanding;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import javax.sql.DataSource;

public final class ClanStandingQuery {

    private static final String BASE_SQL = """
            SELECT c.id, c.tag, c.name, c.leader_uuid, c.points, c.wars_won,
                   COUNT(m.player_uuid) AS members,
                   COALESCE(SUM(m.kills), 0) AS kills,
                   COALESCE(SUM(m.deaths), 0) AS deaths
            FROM clans c
            LEFT JOIN clan_members m ON m.clan_id = c.id
            GROUP BY c.id, c.tag, c.name, c.leader_uuid, c.points, c.wars_won
            """;

    private final DataSource dataSource;
    private final Logger logger;
    private volatile boolean treasuryMissingLogged;

    public ClanStandingQuery(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }

    public List<ClanStanding> top(BoardStatistic statistic, int limit) {
        List<ClanRow> rows = loadRows();
        Map<Long, Double> bankBalances = statistic == BoardStatistic.BANK ? loadBankBalances() : Map.of();
        List<ClanStanding> standings = new ArrayList<>(rows.size());
        for (ClanRow row : rows) {
            standings.add(new ClanStanding(row.id(), row.tag(), row.name(), row.leaderId(), value(statistic, row, bankBalances)));
        }
        standings.sort(Comparator.comparingDouble(ClanStanding::value).reversed());
        return standings.size() > limit ? List.copyOf(standings.subList(0, limit)) : List.copyOf(standings);
    }

    private double value(BoardStatistic statistic, ClanRow row, Map<Long, Double> bankBalances) {
        return switch (statistic) {
            case POINTS -> row.points();
            case MEMBERS -> row.members();
            case KILLS -> row.kills();
            case DEATHS -> row.deaths();
            case KDR -> row.deaths() <= 0 ? row.kills() : (double) row.kills() / row.deaths();
            case WARS -> row.warsWon();
            case BANK -> bankBalances.getOrDefault(row.id(), 0.0D);
        };
    }

    private List<ClanRow> loadRows() {
        List<ClanRow> rows = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(BASE_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rows.add(new ClanRow(
                        resultSet.getLong("id"),
                        resultSet.getString("tag"),
                        resultSet.getString("name"),
                        UUID.fromString(resultSet.getString("leader_uuid")),
                        resultSet.getInt("points"),
                        resultSet.getInt("wars_won"),
                        resultSet.getInt("members"),
                        resultSet.getLong("kills"),
                        resultSet.getLong("deaths")
                ));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load clan standings", exception);
        }
        return rows;
    }

    private Map<Long, Double> loadBankBalances() {
        Map<Long, Double> balances = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT clan_id, balance FROM clan_treasury");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                balances.put(resultSet.getLong("clan_id"), resultSet.getDouble("balance"));
            }
        } catch (SQLException exception) {
            if (!treasuryMissingLogged) {
                treasuryMissingLogged = true;
                logger.warning("BANK leaderboard unavailable (install SoulPact-Bank): " + exception.getMessage());
            }
        }
        return balances;
    }

    private record ClanRow(
            long id,
            String tag,
            String name,
            UUID leaderId,
            int points,
            int warsWon,
            int members,
            long kills,
            long deaths
    ) {
    }
}
