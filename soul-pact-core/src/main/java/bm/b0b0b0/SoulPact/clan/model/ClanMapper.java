package bm.b0b0b0.SoulPact.clan.model;

import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;

public final class ClanMapper {

    private ClanMapper() {
    }

    public static ClanSnapshot toSnapshot(Clan clan) {
        return new ClanSnapshot(
                clan.id(),
                clan.tag(),
                clan.name(),
                clan.description(),
                clan.leaderId(),
                clan.points(),
                clan.maxSlots(),
                clan.verified(),
                clan.friendlyFire(),
                clan.createdAt()
        );
    }
}
