package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanListEntry;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryProvider;
import bm.b0b0b0.SoulPact.core.module.ExtensionRegistryImpl;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class ClanTreasuryDisplayService {

    private static final DecimalFormat FORMAT = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));

    private final ExtensionRegistryImpl extensionRegistry;

    public ClanTreasuryDisplayService(ExtensionRegistryImpl extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
    }

    public CompletableFuture<List<ClanListEntry>> enrichEntries(List<ClanListEntry> entries) {
        ClanTreasuryProvider treasuryProvider = resolveTreasury();
        if (treasuryProvider == null || entries.isEmpty()) {
            return CompletableFuture.completedFuture(entries);
        }
        CompletableFuture<List<ClanListEntry>> chain = CompletableFuture.completedFuture(new ArrayList<>());
        for (ClanListEntry entry : entries) {
            chain = chain.thenCompose(accumulated -> treasuryProvider.treasury()
                    .balance(entry.clan().id())
                    .thenApply(balance -> {
                        accumulated.add(new ClanListEntry(
                                entry.clan(),
                                entry.memberCount(),
                                FORMAT.format(balance),
                                entry.coalitionLine()
                        ));
                        return accumulated;
                    }));
        }
        return chain;
    }

    public CompletableFuture<String> formatBalance(long clanId) {
        ClanTreasuryProvider treasuryProvider = resolveTreasury();
        if (treasuryProvider == null) {
            return CompletableFuture.completedFuture("");
        }
        return treasuryProvider.treasury().balance(clanId).thenApply(FORMAT::format);
    }

    private ClanTreasuryProvider resolveTreasury() {
        return extensionRegistry.find("bank")
                .filter(ClanTreasuryProvider.class::isInstance)
                .map(ClanTreasuryProvider.class::cast)
                .orElse(null);
    }
}
