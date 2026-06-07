package bm.b0b0b0.SoulPact.bank.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryProvider;
import bm.b0b0b0.SoulPact.bank.gui.BankGuiService;
import bm.b0b0b0.SoulPact.bank.service.ClanTreasuryService;
import org.bukkit.entity.Player;

public final class BankExtension implements ClanTreasuryProvider {

    private final ClanTreasuryService treasuryService;
    private final BankGuiService guiService;

    public BankExtension(ClanTreasuryService treasuryService, BankGuiService guiService) {
        this.treasuryService = treasuryService;
        this.guiService = guiService;
    }

    @Override
    public String id() {
        return "bank";
    }

    @Override
    public void enable(SoulPactApi api) {
    }

    @Override
    public void disable() {
    }

    @Override
    public void reload() {
    }

    @Override
    public ClanTreasuryApi treasury() {
        return treasuryService;
    }

    @Override
    public void openGui(Player player) {
        guiService.open(player);
    }
}
