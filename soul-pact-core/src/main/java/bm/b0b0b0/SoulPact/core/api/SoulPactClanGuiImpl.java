package bm.b0b0b0.SoulPact.core.api;

import bm.b0b0b0.SoulPact.api.platform.SoulPactClanGui;
import bm.b0b0b0.SoulPact.clan.gui.ClanGuiOpenService;
import org.bukkit.entity.Player;

public final class SoulPactClanGuiImpl implements SoulPactClanGui {

    private final ClanGuiOpenService guiOpenService;

    public SoulPactClanGuiImpl(ClanGuiOpenService guiOpenService) {
        this.guiOpenService = guiOpenService;
    }

    @Override
    public void openHub(Player player) {
        guiOpenService.openHub(player);
    }
}
