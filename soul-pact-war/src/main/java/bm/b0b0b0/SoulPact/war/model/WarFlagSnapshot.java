package bm.b0b0b0.SoulPact.war.model;

import bm.b0b0b0.SoulPact.api.land.ClanBaseSnapshot;

public record WarFlagSnapshot(String world, int x, int y, int z) {

    public static WarFlagSnapshot from(ClanBaseSnapshot base) {
        return new WarFlagSnapshot(base.world(), base.flagX(), base.flagY(), base.flagZ());
    }
}
