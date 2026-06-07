package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayBridge;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionProvider;
import bm.b0b0b0.SoulPact.core.module.ExtensionRegistryImpl;
import java.util.Optional;
import org.bukkit.entity.Player;

public final class ClanCoalitionAccessService {

    private final ExtensionRegistryImpl extensionRegistry;

    public ClanCoalitionAccessService(ExtensionRegistryImpl extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
    }

    public Optional<CoalitionDisplayBridge> resolveDisplay() {
        return extensionRegistry.find("coalition")
                .filter(CoalitionProvider.class::isInstance)
                .map(CoalitionProvider.class::cast)
                .map(CoalitionProvider::display);
    }

    public void handleInfoInviteClick(Player player, long targetClanId, int listPage) {
        resolveDisplay().ifPresent(bridge -> bridge.handleInfoInviteClick(player, targetClanId, listPage));
    }
}
