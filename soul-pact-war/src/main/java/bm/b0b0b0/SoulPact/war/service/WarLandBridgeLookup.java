package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.land.ClanLandProvider;
import java.util.Optional;

public final class WarLandBridgeLookup {

    private final SoulPactApi api;

    public WarLandBridgeLookup(SoulPactApi api) {
        this.api = api;
    }

    public Optional<ClanLandProvider> resolve() {
        return api.extensions()
                .find("land")
                .filter(ClanLandProvider.class::isInstance)
                .map(ClanLandProvider.class::cast);
    }
}
