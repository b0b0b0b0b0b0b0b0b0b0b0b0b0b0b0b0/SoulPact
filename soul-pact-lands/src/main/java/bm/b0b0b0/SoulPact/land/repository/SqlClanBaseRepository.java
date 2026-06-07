package bm.b0b0b0.SoulPact.land.repository;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SqlClanBaseRepository implements ClanBaseRepository {

    private final SoulPactApi api;

    public SqlClanBaseRepository(SoulPactApi api) {
        this.api = api;
    }

    @Override
    public Optional<ClanBaseRecord> findByClanId(long clanId) {
        String sql = """
                SELECT id, clan_id, region_name, world, flag_x, flag_y, flag_z, border_material,
                       extent_x_pos, extent_x_neg, extent_z_pos, extent_z_neg,
                       pvp_enabled, mob_spawn_enabled, created_at
                FROM clan_bases WHERE clan_id = ?
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan base", exception);
        }
    }

    @Override
    public Optional<ClanBaseRecord> findByFlag(String world, int x, int y, int z) {
        String sql = """
                SELECT id, clan_id, region_name, world, flag_x, flag_y, flag_z, border_material,
                       extent_x_pos, extent_x_neg, extent_z_pos, extent_z_neg,
                       pvp_enabled, mob_spawn_enabled, created_at
                FROM clan_bases WHERE world = ? AND flag_x = ? AND flag_y = ? AND flag_z = ?
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, world);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan base by flag", exception);
        }
    }

    @Override
    public List<ClanBaseRecord> findAll() {
        String sql = """
                SELECT id, clan_id, region_name, world, flag_x, flag_y, flag_z, border_material,
                       extent_x_pos, extent_x_neg, extent_z_pos, extent_z_neg,
                       pvp_enabled, mob_spawn_enabled, created_at
                FROM clan_bases
                """;
        List<ClanBaseRecord> records = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                records.add(mapRow(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan bases", exception);
        }
        return records;
    }

    @Override
    public List<ClanBaseRecord> findAllInWorld(String world) {
        String sql = """
                SELECT id, clan_id, region_name, world, flag_x, flag_y, flag_z, border_material,
                       extent_x_pos, extent_x_neg, extent_z_pos, extent_z_neg,
                       pvp_enabled, mob_spawn_enabled, created_at
                FROM clan_bases WHERE world = ?
                """;
        List<ClanBaseRecord> records = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, world);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(mapRow(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query clan bases in world", exception);
        }
        return records;
    }

    @Override
    public ClanBaseRecord insert(ClanBaseRecord record) {
        String sql = """
                INSERT INTO clan_bases(
                    clan_id, world, flag_x, flag_y, flag_z, region_name, border_material,
                    extent_x_pos, extent_x_neg, extent_z_pos, extent_z_neg,
                    pvp_enabled, mob_spawn_enabled, created_at
                )
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, record.clanId());
            statement.setString(2, record.world());
            statement.setInt(3, record.flagX());
            statement.setInt(4, record.flagY());
            statement.setInt(5, record.flagZ());
            statement.setString(6, record.regionName());
            statement.setString(7, record.borderMaterial());
            statement.setInt(8, record.extentXPos());
            statement.setInt(9, record.extentXNeg());
            statement.setInt(10, record.extentZPos());
            statement.setInt(11, record.extentZNeg());
            statement.setInt(12, record.pvpEnabled() ? 1 : 0);
            statement.setInt(13, record.mobSpawnEnabled() ? 1 : 0);
            statement.setLong(14, record.createdAt());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("Failed to read generated base id");
                }
                return new ClanBaseRecord(
                        keys.getLong(1),
                        record.clanId(),
                        record.regionName(),
                        record.world(),
                        record.flagX(),
                        record.flagY(),
                        record.flagZ(),
                        record.borderMaterial(),
                        record.extentXPos(),
                        record.extentXNeg(),
                        record.extentZPos(),
                        record.extentZNeg(),
                        record.pvpEnabled(),
                        record.mobSpawnEnabled(),
                        record.createdAt()
                );
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to insert clan base", exception);
        }
    }

    @Override
    public void delete(long baseId) {
        deleteBorderBlocks(baseId);
        String sql = "DELETE FROM clan_bases WHERE id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, baseId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to delete clan base", exception);
        }
    }

    @Override
    public void saveBorderBlocks(long baseId, String world, List<BorderBlock> blocks) {
        deleteBorderBlocks(baseId);
        String sql = """
                INSERT INTO clan_base_border_blocks(base_id, world, x, y, z, original_material)
                VALUES(?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (BorderBlock block : blocks) {
                statement.setLong(1, baseId);
                statement.setString(2, world);
                statement.setInt(3, block.x());
                statement.setInt(4, block.y());
                statement.setInt(5, block.z());
                statement.setString(6, block.originalMaterial());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to save border blocks", exception);
        }
    }

    @Override
    public List<BorderBlock> findBorderBlocks(long baseId) {
        String sql = "SELECT x, y, z, original_material FROM clan_base_border_blocks WHERE base_id = ?";
        List<BorderBlock> blocks = new ArrayList<>();
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, baseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    blocks.add(new BorderBlock(
                            resultSet.getInt("x"),
                            resultSet.getInt("y"),
                            resultSet.getInt("z"),
                            resultSet.getString("original_material")
                    ));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to query border blocks", exception);
        }
        return blocks;
    }

    @Override
    public void deleteBorderBlocks(long baseId) {
        String sql = "DELETE FROM clan_base_border_blocks WHERE base_id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, baseId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to delete border blocks", exception);
        }
    }

    @Override
    public void updatePvp(long baseId, boolean enabled) {
        updateToggle(baseId, "pvp_enabled", enabled);
    }

    @Override
    public void updateMobSpawn(long baseId, boolean enabled) {
        updateToggle(baseId, "mob_spawn_enabled", enabled);
    }

    @Override
    public void updateBorderMaterial(long baseId, String borderMaterial) {
        String sql = "UPDATE clan_bases SET border_material = ? WHERE id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, borderMaterial);
            statement.setLong(2, baseId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to update border material", exception);
        }
    }

    @Override
    public void updateExtents(long baseId, int extentXPos, int extentXNeg, int extentZPos, int extentZNeg) {
        String sql = """
                UPDATE clan_bases
                SET extent_x_pos = ?, extent_x_neg = ?, extent_z_pos = ?, extent_z_neg = ?
                WHERE id = ?
                """;
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, extentXPos);
            statement.setInt(2, extentXNeg);
            statement.setInt(3, extentZPos);
            statement.setInt(4, extentZNeg);
            statement.setLong(5, baseId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to update base extents", exception);
        }
    }

    private void updateToggle(long baseId, String column, boolean enabled) {
        String sql = "UPDATE clan_bases SET " + column + " = ? WHERE id = ?";
        try (Connection connection = api.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, enabled ? 1 : 0);
            statement.setLong(2, baseId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to update clan base toggle", exception);
        }
    }

    private static ClanBaseRecord mapRow(ResultSet resultSet) throws SQLException {
        return new ClanBaseRecord(
                resultSet.getLong("id"),
                resultSet.getLong("clan_id"),
                resultSet.getString("region_name"),
                resultSet.getString("world"),
                resultSet.getInt("flag_x"),
                resultSet.getInt("flag_y"),
                resultSet.getInt("flag_z"),
                resultSet.getString("border_material"),
                readOptionalInt(resultSet, "extent_x_pos"),
                readOptionalInt(resultSet, "extent_x_neg"),
                readOptionalInt(resultSet, "extent_z_pos"),
                readOptionalInt(resultSet, "extent_z_neg"),
                resultSet.getInt("pvp_enabled") == 1,
                resultSet.getInt("mob_spawn_enabled") == 1,
                resultSet.getLong("created_at")
        );
    }

    private static int readOptionalInt(ResultSet resultSet, String column) throws SQLException {
        int value = resultSet.getInt(column);
        if (resultSet.wasNull()) {
            return 0;
        }
        return value;
    }
}
