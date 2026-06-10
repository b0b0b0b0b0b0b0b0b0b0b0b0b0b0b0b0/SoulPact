package bm.b0b0b0.SoulPact.quests.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SqlClanPointsRepository {

    private final SoulPactApi api;

    public SqlClanPointsRepository(SoulPactApi api) {
        this.api = api;
    }

    public void addPoints(long clanId, int amount) {
        if (amount == 0) {
            return;
        }
        String sql = "UPDATE clans SET points = points + ? WHERE id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amount);
            statement.setLong(2, clanId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to add clan points for clan " + clanId, exception);
        }
    }
}
