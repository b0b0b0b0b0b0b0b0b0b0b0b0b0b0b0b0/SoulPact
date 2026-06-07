package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.ClanStandardSettings;

public final class ClanStandardConfig {

    private final boolean openHubOnInteract;
    private final boolean requireClanMember;

    public ClanStandardConfig(ClanStandardSettings settings) {
        this.openHubOnInteract = settings.openHubOnInteract;
        this.requireClanMember = settings.requireClanMember;
    }

    public boolean openHubOnInteract() {
        return openHubOnInteract;
    }

    public boolean requireClanMember() {
        return requireClanMember;
    }
}
