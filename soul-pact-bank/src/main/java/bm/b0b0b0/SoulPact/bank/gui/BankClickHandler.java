package bm.b0b0b0.SoulPact.bank.gui;

import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.bank.config.BankConfig;
import java.util.List;
import org.bukkit.entity.Player;

public final class BankClickHandler {

    private final BankGuiService guiService;
    private final BankClanNavigation clanNavigation;
    private final BankConfig config;

    public BankClickHandler(BankGuiService guiService, BankClanNavigation clanNavigation, BankConfig config) {
        this.guiService = guiService;
        this.clanNavigation = clanNavigation;
        this.config = config;
    }

    public void handle(BankMenu menu, Player player, int slot) {
        if (slot == config.backSlot()) {
            clanNavigation.openHub(player);
            return;
        }
        ClanSnapshot clan = menu.snapshot().view().clan();
        if (slot == config.depositAllSlot() && menu.snapshot().canDeposit() && !menu.snapshot().view().locked()) {
            guiService.depositAll(player, clan);
            return;
        }
        if (slot == config.withdrawAllSlot() && menu.snapshot().canWithdraw() && !menu.snapshot().view().locked()) {
            guiService.withdrawAll(player, clan, menu.snapshot().view().balance());
            return;
        }
        Double depositAmount = resolvePresetAmount(config.depositPresets(), config.depositStartSlot(), slot);
        if (depositAmount != null && menu.snapshot().canDeposit() && !menu.snapshot().view().locked()) {
            guiService.deposit(player, clan, depositAmount);
            return;
        }
        Double withdrawAmount = resolvePresetAmount(config.withdrawPresets(), config.withdrawStartSlot(), slot);
        if (withdrawAmount != null && menu.snapshot().canWithdraw() && !menu.snapshot().view().locked()) {
            guiService.withdraw(player, clan, withdrawAmount);
        }
    }

    private Double resolvePresetAmount(List<Double> presets, int startSlot, int slot) {
        for (int index = 0; index < presets.size(); index++) {
            if (slot == startSlot + index) {
                return presets.get(index);
            }
        }
        return null;
    }
}
