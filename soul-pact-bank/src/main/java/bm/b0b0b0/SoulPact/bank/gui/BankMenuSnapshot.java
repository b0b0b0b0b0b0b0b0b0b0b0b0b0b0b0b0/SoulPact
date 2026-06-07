package bm.b0b0b0.SoulPact.bank.gui;

import bm.b0b0b0.SoulPact.bank.service.ClanTreasuryService;

public final class BankMenuSnapshot {

    private final ClanTreasuryService.TreasuryView view;
    private final boolean canDeposit;
    private final boolean canWithdraw;

    public BankMenuSnapshot(ClanTreasuryService.TreasuryView view, boolean canDeposit, boolean canWithdraw) {
        this.view = view;
        this.canDeposit = canDeposit;
        this.canWithdraw = canWithdraw;
    }

    public ClanTreasuryService.TreasuryView view() {
        return view;
    }

    public boolean canDeposit() {
        return canDeposit;
    }

    public boolean canWithdraw() {
        return canWithdraw;
    }
}
