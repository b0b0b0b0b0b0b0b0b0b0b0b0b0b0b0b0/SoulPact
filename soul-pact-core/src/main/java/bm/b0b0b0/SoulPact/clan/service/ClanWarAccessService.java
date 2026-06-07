package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.war.ClanWarProvider;
import bm.b0b0b0.SoulPact.api.war.ClanWarUiBridge;
import bm.b0b0b0.SoulPact.core.module.ExtensionRegistryImpl;
import java.util.Optional;
import org.bukkit.entity.Player;

public final class ClanWarAccessService {

    private final ExtensionRegistryImpl extensionRegistry;

    public ClanWarAccessService(ExtensionRegistryImpl extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
    }

    public Optional<ClanWarUiBridge> resolveUi() {
        return extensionRegistry.find("war")
                .filter(ClanWarProvider.class::isInstance)
                .map(ClanWarProvider.class::cast)
                .map(ClanWarProvider::ui);
    }

    public void handleInfoDeclareClick(Player player, long targetClanId, int listPage) {
        resolveUi().ifPresent(ui -> ui.handleInfoDeclareClick(player, targetClanId, listPage));
    }
}
