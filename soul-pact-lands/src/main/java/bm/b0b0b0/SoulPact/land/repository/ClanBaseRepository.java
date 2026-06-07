package bm.b0b0b0.SoulPact.land.repository;

import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import java.util.List;
import java.util.Optional;

public interface ClanBaseRepository {

    Optional<ClanBaseRecord> findByClanId(long clanId);

    Optional<ClanBaseRecord> findByFlag(String world, int x, int y, int z);

    List<ClanBaseRecord> findAllInWorld(String world);

    List<ClanBaseRecord> findAll();

    ClanBaseRecord insert(ClanBaseRecord record);

    void delete(long baseId);

    void saveBorderBlocks(long baseId, String world, List<BorderBlock> blocks);

    List<BorderBlock> findBorderBlocks(long baseId);

    void deleteBorderBlocks(long baseId);

    void updatePvp(long baseId, boolean enabled);

    void updateMobSpawn(long baseId, boolean enabled);

    void updateBorderMaterial(long baseId, String borderMaterial);

    void updateExtents(long baseId, int extentXPos, int extentXNeg, int extentZPos, int extentZNeg);

    void updateStandardUid(long baseId, String standardUid);

    record BorderBlock(int x, int y, int z, String originalMaterial) {
    }
}
