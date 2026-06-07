package bm.b0b0b0.SoulPact.chest.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public final class SqlClanChestSpoilsRepository implements ClanChestSpoilsRepository {

    private final SoulPactApi api;
    private final ClanChestRepository chestRepository;

    public SqlClanChestSpoilsRepository(SoulPactApi api, ClanChestRepository chestRepository) {
        this.api = api;
        this.chestRepository = chestRepository;
    }

    @Override
    public long createBatch(long ownerClanId, long sourceClanId, long capturedAt) {
        String sql = """
                INSERT INTO clan_chest_spoils(owner_clan_id, source_clan_id, captured_at)
                VALUES(?, ?, ?)
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, ownerClanId);
            statement.setLong(2, sourceClanId);
            statement.setLong(3, capturedAt);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("Spoils batch insert returned no key");
                }
                return keys.getLong(1);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to create spoils batch", exception);
        }
    }

    @Override
    public void insertBatchItems(long spoilsId, Map<Integer, ItemStack> items) {
        if (items.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO clan_chest_spoils_items(spoils_id, cell_index, item_data) VALUES(?, ?, ?)";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                ItemStack itemStack = entry.getValue();
                if (itemStack == null || itemStack.getType().isAir()) {
                    continue;
                }
                statement.setLong(1, spoilsId);
                statement.setInt(2, entry.getKey());
                statement.setBytes(3, itemStack.serializeAsBytes());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to insert spoils items", exception);
        }
    }

    @Override
    public void reassignOwner(long fromClanId, long toClanId) {
        String sql = "UPDATE clan_chest_spoils SET owner_clan_id = ? WHERE owner_clan_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, toClanId);
            statement.setLong(2, fromClanId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to reassign spoils owner", exception);
        }
    }

    @Override
    public void clearChestItems(long clanId) {
        chestRepository.saveItems(clanId, Map.of());
    }

    public Map<Integer, ItemStack> loadChestItems(long clanId) {
        return chestRepository.loadItems(clanId);
    }
}
