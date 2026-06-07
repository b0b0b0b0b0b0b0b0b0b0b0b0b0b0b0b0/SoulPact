package bm.b0b0b0.SoulPact.chest.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public final class SqlClanChestRepository implements ClanChestRepository {

    private final SoulPactApi api;

    public SqlClanChestRepository(SoulPactApi api) {
        this.api = api;
    }

    @Override
    public int unlockedCells(long clanId) {
        String sql = "SELECT unlocked_cells FROM clan_chest_meta WHERE clan_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0;
                }
                return resultSet.getInt("unlocked_cells");
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan chest meta", exception);
        }
    }

    @Override
    public void setUnlockedCells(long clanId, int unlockedCells) {
        String sql = """
                INSERT INTO clan_chest_meta(clan_id, unlocked_cells)
                VALUES(?, ?)
                ON CONFLICT(clan_id) DO UPDATE SET unlocked_cells = excluded.unlocked_cells
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            statement.setInt(2, unlockedCells);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to update clan chest meta", exception);
        }
    }

    @Override
    public Map<Integer, ItemStack> loadItems(long clanId) {
        String sql = "SELECT cell_index, item_data FROM clan_chest_items WHERE clan_id = ?";
        Map<Integer, ItemStack> items = new HashMap<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    byte[] raw = resultSet.getBytes("item_data");
                    if (raw == null || raw.length == 0) {
                        continue;
                    }
                    ItemStack itemStack = ItemStack.deserializeBytes(raw);
                    if (itemStack == null || itemStack.getType().isAir()) {
                        continue;
                    }
                    items.put(resultSet.getInt("cell_index"), itemStack);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to load clan chest items", exception);
        }
        return items;
    }

    @Override
    public void saveItems(long clanId, Map<Integer, ItemStack> items) {
        String deleteSql = "DELETE FROM clan_chest_items WHERE clan_id = ?";
        String insertSql = "INSERT INTO clan_chest_items(clan_id, cell_index, item_data) VALUES(?, ?, ?)";
        try (Connection connection = api.dataSource().getConnection()) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                deleteStatement.setLong(1, clanId);
                deleteStatement.executeUpdate();
            }
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                    ItemStack itemStack = entry.getValue();
                    if (itemStack == null || itemStack.getType().isAir()) {
                        continue;
                    }
                    insertStatement.setLong(1, clanId);
                    insertStatement.setInt(2, entry.getKey());
                    insertStatement.setBytes(3, itemStack.serializeAsBytes());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to save clan chest items", exception);
        }
    }
}
