package bm.b0b0b0.SoulPact.chest.gui;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import org.bukkit.entity.Player;

public final class ChestClanNavigation {

    private final SoulPactApi api;
    private final ChestGuiService guiService;

    public ChestClanNavigation(SoulPactApi api, ChestGuiService guiService) {
        this.api = api;
        this.guiService = guiService;
    }

    public void openHub(Player player) {
        api.scheduler().runSyncLater(1L, () -> {
            if (!player.isOnline()) {
                return;
            }
            api.clanGui().openHub(player);
        });
    }

    public void openBank(Player player) {
        api.extensions().find("bank").filter(SoulPactGuiExtension.class::isInstance).ifPresentOrElse(
                extension -> ((SoulPactGuiExtension) extension).openGui(player),
                () -> guiService.messages().send(player, "chest.error.bank-unavailable")
        );
    }
}
