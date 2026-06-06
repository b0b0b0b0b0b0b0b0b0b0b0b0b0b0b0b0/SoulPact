package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanListEntry;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.repository.CreateClanRecord;
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

public final class SqlClanRepository implements ClanRepository {

    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public SqlClanRepository(DataSourceProvider dataSourceProvider, AsyncDatabaseExecutor asyncDatabaseExecutor) {
        this.dataSourceProvider = dataSourceProvider;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    @Override
    public CompletableFuture<Optional<Clan>> findByTag(String tag) {
        return asyncDatabaseExecutor.supply(() -> queryByTag(tag));
    }

    @Override
    public CompletableFuture<Optional<Clan>> findById(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryById(clanId));
    }

    @Override
    public CompletableFuture<Optional<Clan>> findByPlayerId(UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> queryByPlayerId(playerId));
    }

    @Override
    public CompletableFuture<Integer> countClans() {
        return asyncDatabaseExecutor.supply(this::queryCount);
    }

    @Override
    public CompletableFuture<List<Clan>> findAll(int limit) {
        return asyncDatabaseExecutor.supply(() -> queryAll(limit));
    }

    @Override
    public CompletableFuture<List<ClanListEntry>> findPageEntries(int offset, int limit) {
        return asyncDatabaseExecutor.supply(() -> queryPageEntries(offset, limit));
    }

    @Override
    public CompletableFuture<Clan> create(CreateClanRecord record) {
        return asyncDatabaseExecutor.supply(() -> insertClan(record));
    }

    @Override
    public CompletableFuture<Integer> countMembers(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryMemberCount(clanId));
    }

    @Override
    public CompletableFuture<Boolean> removeMember(long clanId, UUID playerId) {
        return asyncDatabaseExecutor.supply(() -> deleteMember(clanId, playerId));
    }

    @Override
    public CompletableFuture<Boolean> addMember(long clanId, UUID playerId, String role, long joinedAt) {
        return asyncDatabaseExecutor.supply(() -> insertMember(clanId, playerId, role, joinedAt));
    }

    @Override
    public CompletableFuture<Boolean> deleteClan(long clanId) {
        return asyncDatabaseExecutor.supply(() -> deleteClanById(clanId));
    }

    @Override
    public CompletableFuture<List<ClanMember>> findMembersByClanId(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryMembersByClanId(clanId));
    }

    private Clan insertClan(CreateClanRecord record) {
        String insertClanSql = """
                INSERT INTO clans (tag, name, description, leader_uuid, points, max_slots, verified, friendly_fire, join_requests_open, created_at)
                VALUES (?, ?, '', ?, 0, ?, 0, 0, 1, ?)
                """;
        String insertMemberSql = """
                INSERT INTO clan_members (clan_id, player_uuid, role, nickname, kills, deaths, joined_at)
                VALUES (?, ?, 'leader', NULL, 0, 0, ?)
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement clanStatement = connection.prepareStatement(insertClanSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                clanStatement.setString(1, record.tag());
                clanStatement.setString(2, record.name());
                clanStatement.setString(3, record.leaderUuid().toString());
                clanStatement.setInt(4, record.maxSlots());
                clanStatement.setLong(5, record.createdAt());
                clanStatement.executeUpdate();
                try (ResultSet generatedKeys = clanStatement.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new IllegalStateException("Failed to read created clan id");
                    }
                    long clanId = generatedKeys.getLong(1);
                    try (PreparedStatement memberStatement = connection.prepareStatement(insertMemberSql)) {
                        memberStatement.setLong(1, clanId);
                        memberStatement.setString(2, record.leaderUuid().toString());
                        memberStatement.setLong(3, record.createdAt());
                        memberStatement.executeUpdate();
                    }
                    connection.commit();
                    return new Clan(
                            clanId,
                            record.tag(),
                            record.name(),
                            "",
                            record.leaderUuid(),
                            0,
                            record.maxSlots(),
                            false,
                            false,
                            true,
                            record.createdAt()
                    );
                }
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create clan", exception);
        }
    }

    private int queryCount() {
        String sql = "SELECT COUNT(*) AS total FROM clans";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return 0;
            }
            return resultSet.getInt("total");
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to count clans", exception);
        }
    }

    private List<Clan> queryAll(int limit) {
        String sql = """
                SELECT id, tag, name, description, leader_uuid, points, max_slots, verified, friendly_fire, join_requests_open, created_at
                FROM clans
                ORDER BY tag ASC
                LIMIT ?
                """;
        List<Clan> clans = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clans.add(mapRow(resultSet));
                }
            }
            return clans;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to list clans", exception);
        }
    }

    private List<ClanListEntry> queryPageEntries(int offset, int limit) {
        String sql = """
                SELECT c.id, c.tag, c.name, c.description, c.leader_uuid, c.points, c.max_slots,
                       c.verified, c.friendly_fire, c.join_requests_open, c.created_at, COUNT(m.player_uuid) AS member_count
                FROM clans c
                LEFT JOIN clan_members m ON m.clan_id = c.id
                GROUP BY c.id
                ORDER BY c.tag ASC
                LIMIT ? OFFSET ?
                """;
        List<ClanListEntry> entries = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entries.add(new ClanListEntry(
                            mapRow(resultSet),
                            resultSet.getInt("member_count")
                    ));
                }
            }
            return entries;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to list clans page", exception);
        }
    }

    private Optional<Clan> queryById(long clanId) {
        String sql = """
                SELECT id, tag, name, description, leader_uuid, points, max_slots, verified, friendly_fire, join_requests_open, created_at
                FROM clans
                WHERE id = ?
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(resultSet));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query clan by id", exception);
        }
    }

    private Optional<Clan> queryByTag(String tag) {
        String sql = """
                SELECT id, tag, name, description, leader_uuid, points, max_slots, verified, friendly_fire, join_requests_open, created_at
                FROM clans
                WHERE UPPER(tag) = UPPER(?)
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tag);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(resultSet));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query clan by tag", exception);
        }
    }

    private Optional<Clan> queryByPlayerId(UUID playerId) {
        String sql = """
                SELECT c.id, c.tag, c.name, c.description, c.leader_uuid, c.points, c.max_slots, c.verified, c.friendly_fire, c.join_requests_open, c.created_at
                FROM clans c
                INNER JOIN clan_members m ON m.clan_id = c.id
                WHERE m.player_uuid = ?
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(resultSet));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query clan by player", exception);
        }
    }

    private int queryMemberCount(long clanId) {
        String sql = "SELECT COUNT(*) AS total FROM clan_members WHERE clan_id = ?";
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
            throw new IllegalStateException("Failed to count clan members", exception);
        }
    }

    private boolean insertMember(long clanId, UUID playerId, String role, long joinedAt) {
        String sql = """
                INSERT INTO clan_members (clan_id, player_uuid, role, nickname, kills, deaths, joined_at)
                VALUES (?, ?, ?, NULL, 0, 0, ?)
                """;
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            statement.setString(3, role);
            statement.setLong(4, joinedAt);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to add clan member", exception);
        }
    }

    private boolean deleteMember(long clanId, UUID playerId) {
        String sql = "DELETE FROM clan_members WHERE clan_id = ? AND player_uuid = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setString(2, playerId.toString());
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to remove clan member", exception);
        }
    }

    private boolean deleteClanById(long clanId) {
        String sql = "DELETE FROM clans WHERE id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to delete clan", exception);
        }
    }

    private List<ClanMember> queryMembersByClanId(long clanId) {
        String sql = """
                SELECT player_uuid, role, nickname, kills, deaths, joined_at
                FROM clan_members
                WHERE clan_id = ?
                ORDER BY joined_at ASC
                """;
        List<ClanMember> members = new ArrayList<>();
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    members.add(mapMemberRow(resultSet));
                }
            }
            return members;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query clan members", exception);
        }
    }

    private static ClanMember mapMemberRow(ResultSet resultSet) throws Exception {
        return new ClanMember(
                UUID.fromString(resultSet.getString("player_uuid")),
                resultSet.getString("role"),
                resultSet.getString("nickname"),
                resultSet.getInt("kills"),
                resultSet.getInt("deaths"),
                resultSet.getLong("joined_at")
        );
    }

    @Override
    public CompletableFuture<Boolean> updateJoinRequestsOpen(long clanId, boolean open) {
        return asyncDatabaseExecutor.supply(() -> updateJoinRequestsOpenSync(clanId, open));
    }

    @Override
    public CompletableFuture<Boolean> updateMemberRole(long clanId, UUID playerId, String role) {
        return asyncDatabaseExecutor.supply(() -> updateMemberRoleSync(clanId, playerId, role));
    }

    @Override
    public CompletableFuture<Boolean> transferLeadership(
            long clanId,
            UUID currentLeaderId,
            UUID newLeaderId,
            String formerLeaderRole
    ) {
        return asyncDatabaseExecutor.supply(() ->
                transferLeadershipSync(clanId, currentLeaderId, newLeaderId, formerLeaderRole)
        );
    }

    private boolean updateMemberRoleSync(long clanId, UUID playerId, String role) {
        String sql = "UPDATE clan_members SET role = ? WHERE clan_id = ? AND player_uuid = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, role);
            statement.setLong(2, clanId);
            statement.setString(3, playerId.toString());
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to update clan member role", exception);
        }
    }

    private boolean transferLeadershipSync(
            long clanId,
            UUID currentLeaderId,
            UUID newLeaderId,
            String formerLeaderRole
    ) {
        String updateClanSql = "UPDATE clans SET leader_uuid = ? WHERE id = ? AND leader_uuid = ?";
        String updateFormerLeaderSql = "UPDATE clan_members SET role = ? WHERE clan_id = ? AND player_uuid = ?";
        String updateNewLeaderSql = "UPDATE clan_members SET role = 'leader' WHERE clan_id = ? AND player_uuid = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement clanStatement = connection.prepareStatement(updateClanSql)) {
                    clanStatement.setString(1, newLeaderId.toString());
                    clanStatement.setLong(2, clanId);
                    clanStatement.setString(3, currentLeaderId.toString());
                    if (clanStatement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }
                try (PreparedStatement formerLeaderStatement = connection.prepareStatement(updateFormerLeaderSql)) {
                    formerLeaderStatement.setString(1, formerLeaderRole);
                    formerLeaderStatement.setLong(2, clanId);
                    formerLeaderStatement.setString(3, currentLeaderId.toString());
                    if (formerLeaderStatement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }
                try (PreparedStatement newLeaderStatement = connection.prepareStatement(updateNewLeaderSql)) {
                    newLeaderStatement.setLong(1, clanId);
                    newLeaderStatement.setString(2, newLeaderId.toString());
                    if (newLeaderStatement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }
                connection.commit();
                return true;
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to transfer clan leadership", exception);
        }
    }

    private boolean updateJoinRequestsOpenSync(long clanId, boolean open) {
        String sql = "UPDATE clans SET join_requests_open = ? WHERE id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, open ? 1 : 0);
            statement.setLong(2, clanId);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to update join requests flag", exception);
        }
    }

    private static Clan mapRow(ResultSet resultSet) throws Exception {
        return new Clan(
                resultSet.getLong("id"),
                resultSet.getString("tag"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                UUID.fromString(resultSet.getString("leader_uuid")),
                resultSet.getInt("points"),
                resultSet.getInt("max_slots"),
                resultSet.getInt("verified") == 1,
                resultSet.getInt("friendly_fire") == 1,
                readJoinRequestsOpen(resultSet),
                resultSet.getLong("created_at")
        );
    }

    private static boolean readJoinRequestsOpen(ResultSet resultSet) throws Exception {
        try {
            return resultSet.getInt("join_requests_open") == 1;
        } catch (Exception ignored) {
            return true;
        }
    }
}
