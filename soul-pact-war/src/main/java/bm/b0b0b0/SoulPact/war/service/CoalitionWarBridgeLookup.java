package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionProvider;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarBridge;
import java.util.Optional;

public final class CoalitionWarBridgeLookup {

    private final SoulPactApi api;

    public CoalitionWarBridgeLookup(SoulPactApi api) {
        this.api = api;
    }

    public Optional<CoalitionWarBridge> resolve() {
        return api.extensions()
                .find("coalition")
                .filter(CoalitionProvider.class::isInstance)
                .map(CoalitionProvider.class::cast)
                .map(CoalitionProvider::war);
    }
}
