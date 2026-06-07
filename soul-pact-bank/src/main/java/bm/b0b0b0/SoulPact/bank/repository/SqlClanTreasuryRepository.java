package bm.b0b0b0.SoulPact.bank.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryContributorSnapshot;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryEntrySnapshot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class SqlClanTreasuryRepository implements ClanTreasuryRepository {

    private final SoulPactApi api;

    public SqlClanTreasuryRepository(SoulPactApi api) {
        this.api = api;
    }

    @Override
    public Optional<TreasuryState> findState(long clanId) {
        String sql = "SELECT balance, locked FROM clan_treasury WHERE clan_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(new TreasuryState(
                        clanId,
                        resultSet.getDouble("balance"),
                        resultSet.getInt("locked") == 1
                ));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan treasury", exception);
        }
    }

    @Override
    public double ensureAccount(long clanId) {
        Optional<TreasuryState> existing = findState(clanId);
        if (existing.isPresent()) {
            return existing.get().balance();
        }
        String insertSql = "INSERT INTO clan_treasury(clan_id, balance, locked) VALUES(?, 0, 0)";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setLong(1, clanId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to ensure clan treasury account", exception);
        }
        return 0D;
    }

    @Override
    public boolean isLocked(long clanId) {
        return findState(clanId).map(TreasuryState::locked).orElse(false);
    }

    @Override
    public boolean setLocked(long clanId, boolean locked) {
        ensureAccount(clanId);
        String sql = "UPDATE clan_treasury SET locked = ? WHERE clan_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, locked ? 1 : 0);
            statement.setLong(2, clanId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to update clan treasury lock", exception);
        }
    }

    @Override
    public TreasuryMutationResult applyMutation(TreasuryMutation mutation) {
        ensureAccount(mutation.clanId());
        try (Connection connection = api.dataSource().getConnection()) {
            connection.setAutoCommit(false);
            try {
                double currentBalance = readBalanceForUpdate(connection, mutation.clanId());
                double nextBalance = currentBalance + mutation.amountDelta();
                if (nextBalance < 0D) {
                    connection.rollback();
                    return new TreasuryMutationResult(false, currentBalance);
                }
                updateBalance(connection, mutation.clanId(), nextBalance);
                insertLedger(connection, mutation, nextBalance);
                if (mutation.trackContribution() && mutation.amountDelta() > 0D) {
                    incrementContribution(connection, mutation.clanId(), mutation.actorId(), mutation.amountDelta());
                }
                connection.commit();
                return new TreasuryMutationResult(true, nextBalance);
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to apply clan treasury mutation", exception);
        }
    }

    @Override
    public List<ClanTreasuryEntrySnapshot> recentEntries(long clanId, int limit) {
        String sql = """
                SELECT id, clan_id, actor_uuid, entry_type, amount, balance_after, note, created_at
                FROM clan_treasury_ledger
                WHERE clan_id = ?
                ORDER BY created_at DESC, id DESC
                LIMIT ?
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setInt(2, Math.max(1, limit));
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ClanTreasuryEntrySnapshot> entries = new ArrayList<>();
                while (resultSet.next()) {
                    entries.add(new ClanTreasuryEntrySnapshot(
                            resultSet.getLong("id"),
                            resultSet.getLong("clan_id"),
                            UUID.fromString(resultSet.getString("actor_uuid")),
                            resultSet.getString("entry_type"),
                            resultSet.getDouble("amount"),
                            resultSet.getDouble("balance_after"),
                            resultSet.getString("note"),
                            resultSet.getLong("created_at")
                    ));
                }
                return entries;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan treasury ledger", exception);
        }
    }

    @Override
    public List<ClanTreasuryContributorSnapshot> topContributors(long clanId, int limit) {
        String sql = """
                SELECT player_uuid, total_deposited
                FROM clan_treasury_contributions
                WHERE clan_id = ?
                ORDER BY total_deposited DESC
                LIMIT ?
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setInt(2, Math.max(1, limit));
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ClanTreasuryContributorSnapshot> contributors = new ArrayList<>();
                while (resultSet.next()) {
                    contributors.add(new ClanTreasuryContributorSnapshot(
                            UUID.fromString(resultSet.getString("player_uuid")),
                            resultSet.getDouble("total_deposited")
                    ));
                }
                return contributors;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan treasury contributors", exception);
        }
    }

    private double readBalanceForUpdate(Connection connection, long clanId) throws SQLException {
        String sql = "SELECT balance FROM clan_treasury WHERE clan_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0D;
                }
                return resultSet.getDouble("balance");
            }
        }
    }

    private void updateBalance(Connection connection, long clanId, double balance) throws SQLException {
        String sql = "UPDATE clan_treasury SET balance = ? WHERE clan_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, balance);
            statement.setLong(2, clanId);
            statement.executeUpdate();
        }
    }

    private void insertLedger(Connection connection, TreasuryMutation mutation, double balanceAfter) throws SQLException {
        String sql = """
                INSERT INTO clan_treasury_ledger(
                    clan_id, actor_uuid, entry_type, amount, balance_after, note, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, mutation.clanId());
            statement.setString(2, mutation.actorId().toString());
            statement.setString(3, mutation.entryType());
            statement.setDouble(4, Math.abs(mutation.amountDelta()));
            statement.setDouble(5, balanceAfter);
            statement.setString(6, mutation.note());
            statement.setLong(7, mutation.createdAt());
            statement.executeUpdate();
        }
    }

    private void incrementContribution(Connection connection, long clanId, UUID playerId, double amount) throws SQLException {
        String selectSql = """
                SELECT total_deposited
                FROM clan_treasury_contributions
                WHERE clan_id = ? AND player_uuid = ?
                """;
        try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            selectStatement.setLong(1, clanId);
            selectStatement.setString(2, playerId.toString());
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    String updateSql = """
                            UPDATE clan_treasury_contributions
                            SET total_deposited = total_deposited + ?
                            WHERE clan_id = ? AND player_uuid = ?
                            """;
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                        updateStatement.setDouble(1, amount);
                        updateStatement.setLong(2, clanId);
                        updateStatement.setString(3, playerId.toString());
                        updateStatement.executeUpdate();
                    }
                    return;
                }
            }
        }
        String insertSql = """
                INSERT INTO clan_treasury_contributions(clan_id, player_uuid, total_deposited)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            insertStatement.setLong(1, clanId);
            insertStatement.setString(2, playerId.toString());
            insertStatement.setDouble(3, amount);
            insertStatement.executeUpdate();
        }
    }
}
