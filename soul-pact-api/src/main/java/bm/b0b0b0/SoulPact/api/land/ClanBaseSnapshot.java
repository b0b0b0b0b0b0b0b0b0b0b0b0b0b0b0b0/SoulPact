package bm.b0b0b0.SoulPact.api.land;

public record ClanBaseSnapshot(
        long id,
        long clanId,
        String regionName,
        String world,
        int flagX,
        int flagY,
        int flagZ,
        boolean pvpEnabled,
        boolean mobSpawnEnabled,
        long createdAt
) {
}
