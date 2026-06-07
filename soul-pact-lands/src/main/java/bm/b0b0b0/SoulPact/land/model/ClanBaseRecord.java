package bm.b0b0b0.SoulPact.land.model;

import bm.b0b0b0.SoulPact.api.land.ClanBaseSnapshot;

public record ClanBaseRecord(
        long id,
        long clanId,
        String regionName,
        String world,
        int flagX,
        int flagY,
        int flagZ,
        String borderMaterial,
        int extentXPos,
        int extentXNeg,
        int extentZPos,
        int extentZNeg,
        String standardUid,
        boolean pvpEnabled,
        boolean mobSpawnEnabled,
        long createdAt
) {
    public ClanBaseSnapshot toSnapshot() {
        return new ClanBaseSnapshot(
                id,
                clanId,
                regionName,
                world,
                flagX,
                flagY,
                flagZ,
                pvpEnabled,
                mobSpawnEnabled,
                createdAt
        );
    }

    public int resolvedExtentXPos(int defaultRadius) {
        return extentXPos > 0 ? extentXPos : defaultRadius;
    }

    public int resolvedExtentXNeg(int defaultRadius) {
        return extentXNeg > 0 ? extentXNeg : defaultRadius;
    }

    public int resolvedExtentZPos(int defaultRadius) {
        return extentZPos > 0 ? extentZPos : defaultRadius;
    }

    public int resolvedExtentZNeg(int defaultRadius) {
        return extentZNeg > 0 ? extentZNeg : defaultRadius;
    }

    public BaseBounds bounds(int defaultRadius, int minWorldY, int maxWorldY) {
        return new BaseBounds(
                flagX - resolvedExtentXNeg(defaultRadius),
                flagX + resolvedExtentXPos(defaultRadius),
                minWorldY,
                maxWorldY,
                flagZ - resolvedExtentZNeg(defaultRadius),
                flagZ + resolvedExtentZPos(defaultRadius)
        );
    }

    public ClanBaseRecord withExtents(int extentXPos, int extentXNeg, int extentZPos, int extentZNeg) {
        return new ClanBaseRecord(
                id,
                clanId,
                regionName,
                world,
                flagX,
                flagY,
                flagZ,
                borderMaterial,
                extentXPos,
                extentXNeg,
                extentZPos,
                extentZNeg,
                standardUid,
                pvpEnabled,
                mobSpawnEnabled,
                createdAt
        );
    }

    public java.util.UUID parsedStandardUid() {
        if (standardUid == null || standardUid.isBlank()) {
            return null;
        }
        try {
            return java.util.UUID.fromString(standardUid);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
