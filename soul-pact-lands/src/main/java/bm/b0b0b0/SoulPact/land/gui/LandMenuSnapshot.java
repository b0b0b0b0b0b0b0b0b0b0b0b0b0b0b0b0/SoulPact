package bm.b0b0b0.SoulPact.land.gui;

import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import java.util.Optional;

public record LandMenuSnapshot(
        ClanSnapshot clan,
        Optional<ClanBaseRecord> base,
        boolean leader
) {
}
