package bm.b0b0b0.SoulPact.chest.gui;

import bm.b0b0b0.SoulPact.chest.config.ChestConfig;
import org.bukkit.entity.Player;

public final class ChestClickHandler {

    private final ChestGuiService guiService;
    private final ChestClanNavigation navigation;
    private final ChestConfig config;

    public ChestClickHandler(ChestGuiService guiService, ChestClanNavigation navigation, ChestConfig config) {
        this.guiService = guiService;
        this.navigation = navigation;
        this.config = config;
    }

    public void handle(ChestMenu menu, Player player, int slot) {
        if (slot == config.backSlot()) {
            guiService.persistAndLeave(menu, player, () -> navigation.openHub(player));
            return;
        }
        if (slot == config.bankLinkSlot() && menu.snapshot().bankAvailable()) {
            guiService.persistAndLeave(menu, player, () -> navigation.openBank(player));
            return;
        }
        if (slot == config.buyCellSlot() && menu.snapshot().leader() && menu.snapshot().unlockedCells() < menu.snapshot().maxCells()) {
            guiService.purchaseCell(menu, player);
            return;
        }
        for (int page = 0; page < config.pages(); page++) {
            if (slot == config.pageTabSlot(page) && page != menu.snapshot().page()) {
                guiService.switchPage(menu, player, page);
                return;
            }
        }
        if (slot == config.prevPageSlot() && menu.snapshot().page() > 0) {
            guiService.switchPage(menu, player, menu.snapshot().page() - 1);
            return;
        }
        if (slot == config.nextPageSlot() && menu.snapshot().page() < config.pages() - 1) {
            guiService.switchPage(menu, player, menu.snapshot().page() + 1);
        }
    }
}
