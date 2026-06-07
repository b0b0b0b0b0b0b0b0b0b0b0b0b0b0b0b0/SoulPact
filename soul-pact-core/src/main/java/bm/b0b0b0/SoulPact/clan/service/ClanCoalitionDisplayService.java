package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayBridge;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayExtras;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionProvider;
import bm.b0b0b0.SoulPact.clan.model.ClanListEntry;
import bm.b0b0b0.SoulPact.core.module.ExtensionRegistryImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanCoalitionDisplayService {

    private final ExtensionRegistryImpl extensionRegistry;

    public ClanCoalitionDisplayService(ExtensionRegistryImpl extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
    }

    public CompletableFuture<List<ClanListEntry>> enrichEntries(List<ClanListEntry> entries) {
        CoalitionDisplayBridge bridge = resolveDisplay();
        if (bridge == null || entries.isEmpty()) {
            return CompletableFuture.completedFuture(entries);
        }
        CompletableFuture<List<ClanListEntry>> chain = CompletableFuture.completedFuture(new ArrayList<>());
        for (ClanListEntry entry : entries) {
            chain = chain.thenCompose(accumulated -> bridge.coalitionLineForList(entry.clan().id())
                    .thenApply(line -> {
                        accumulated.add(new ClanListEntry(
                                entry.clan(),
                                entry.memberCount(),
                                entry.treasuryLine(),
                                line
                        ));
                        return accumulated;
                    }));
        }
        return chain;
    }

    public CompletableFuture<CoalitionDisplayExtras> alliesForInfo(long clanId) {
        CoalitionDisplayBridge bridge = resolveDisplay();
        if (bridge == null) {
            return CompletableFuture.completedFuture(CoalitionDisplayExtras.empty());
        }
        return bridge.alliesForInfo(clanId);
    }

    private CoalitionDisplayBridge resolveDisplay() {
        return extensionRegistry.find("coalition")
                .filter(CoalitionProvider.class::isInstance)
                .map(CoalitionProvider.class::cast)
                .map(CoalitionProvider::display)
                .orElse(null);
    }
}
