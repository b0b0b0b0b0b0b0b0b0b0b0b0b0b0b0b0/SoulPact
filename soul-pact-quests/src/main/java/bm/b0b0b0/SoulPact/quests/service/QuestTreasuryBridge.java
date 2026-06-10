package bm.b0b0b0.SoulPact.quests.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryProvider;
import java.util.Optional;

public final class QuestTreasuryBridge {

    private static final String BANK_EXTENSION_ID = "bank";

    private final SoulPactApi api;

    public QuestTreasuryBridge(SoulPactApi api) {
        this.api = api;
    }

    public boolean available() {
        return resolve().isPresent();
    }

    public Optional<ClanTreasuryApi> resolve() {
        return api.extensions()
                .find(BANK_EXTENSION_ID)
                .filter(ClanTreasuryProvider.class::isInstance)
                .map(ClanTreasuryProvider.class::cast)
                .map(ClanTreasuryProvider::treasury);
    }
}
