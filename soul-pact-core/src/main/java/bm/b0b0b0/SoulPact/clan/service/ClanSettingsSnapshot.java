package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import java.util.List;

public final class ClanSettingsSnapshot {

    private final Clan clan;
    private final List<String> roleKeys;

    public ClanSettingsSnapshot(Clan clan, List<String> roleKeys) {
        this.clan = clan;
        this.roleKeys = List.copyOf(roleKeys);
    }

    public Clan clan() {
        return clan;
    }

    public List<String> roleKeys() {
        return roleKeys;
    }
}
