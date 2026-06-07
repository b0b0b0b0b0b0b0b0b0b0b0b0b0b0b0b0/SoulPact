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

    @Override
    public void openProfile(Player player) {
        guiOpenService.openProfile(player);
    }

    @Override
    public void openList(Player player, int page) {
        guiOpenService.openList(player, page);
    }

    @Override
    public void openInfo(Player player, long clanId, int listPage) {
        guiOpenService.openInfo(player, clanId, listPage);
    }

    @Override
    public void openBanner(Player player) {
        guiOpenService.openBanner(player);
    }

    @Override
    public void openBannerFromLand(Player player) {
        guiOpenService.openBannerFromLand(player);
    }
}
