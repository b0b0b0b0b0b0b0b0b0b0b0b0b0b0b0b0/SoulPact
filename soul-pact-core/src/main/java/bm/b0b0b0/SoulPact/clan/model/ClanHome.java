package bm.b0b0b0.SoulPact.clan.model;

public record ClanHome(
        long id,
        long clanId,
        String name,
        String world,
        double x,
        double y,
        double z,
        float yaw,
        float pitch,
        String passwordHash,
        long createdAt
) {

    public boolean passwordProtected() {
        return passwordHash != null && !passwordHash.isBlank();
    }
}
