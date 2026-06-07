package bm.b0b0b0.SoulPact.land.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SqlClanMemberUuidRepository {

    private final SoulPactApi api;

    public SqlClanMemberUuidRepository(SoulPactApi api) {
        this.api = api;
    }

    public List<UUID> findMemberIds(long clanId) {
        String sql = "SELECT player_uuid FROM clan_members WHERE clan_id = ?";
        List<UUID> members = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    members.add(UUID.fromString(resultSet.getString("player_uuid")));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan member ids", exception);
        }
        return members;
    }
}
