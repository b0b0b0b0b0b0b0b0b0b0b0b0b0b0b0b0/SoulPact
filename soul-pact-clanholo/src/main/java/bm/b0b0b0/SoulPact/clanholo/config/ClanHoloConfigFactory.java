package bm.b0b0b0.SoulPact.clanholo.config;

import bm.b0b0b0.SoulPact.clanholo.config.settings.ClanHoloSettings;

public final class ClanHoloConfigFactory {

    private ClanHoloConfigFactory() {
    }

    public static ClanHoloConfig from(ClanHoloSettings settings) {
        return new ClanHoloConfig(settings);
    }
}
