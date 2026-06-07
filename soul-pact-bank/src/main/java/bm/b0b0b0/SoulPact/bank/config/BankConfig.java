package bm.b0b0b0.SoulPact.bank.config;

import java.util.List;
import org.bukkit.Material;

public final class BankConfig {

    private final String locale;
    private final String fallbackLocale;
    private final double minAmount;
    private final double maxDeposit;
    private final double maxWithdraw;
    private final double notifyDepositAbove;
    private final int contributorTopSize;
    private final int ledgerPreviewSize;
    private final List<Double> depositPresets;
    private final List<Double> withdrawPresets;
    private final int guiRows;
    private final int balanceSlot;
    private final int depositStartSlot;
    private final int withdrawStartSlot;
    private final int depositAllSlot;
    private final int withdrawAllSlot;
    private final int backSlot;
    private final Material fillerMaterial;
    private final Material balanceMaterial;
    private final Material depositMaterial;
    private final Material withdrawMaterial;
    private final Material depositAllMaterial;
    private final Material withdrawAllMaterial;
    private final Material backMaterial;

    public BankConfig(
            String locale,
            String fallbackLocale,
            double minAmount,
            double maxDeposit,
            double maxWithdraw,
            double notifyDepositAbove,
            int contributorTopSize,
            int ledgerPreviewSize,
            List<Double> depositPresets,
            List<Double> withdrawPresets,
            int guiRows,
            int balanceSlot,
            int depositStartSlot,
            int withdrawStartSlot,
            int depositAllSlot,
            int withdrawAllSlot,
            int backSlot,
            Material fillerMaterial,
            Material balanceMaterial,
            Material depositMaterial,
            Material withdrawMaterial,
            Material depositAllMaterial,
            Material withdrawAllMaterial,
            Material backMaterial
    ) {
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
        this.minAmount = minAmount;
        this.maxDeposit = maxDeposit;
        this.maxWithdraw = maxWithdraw;
        this.notifyDepositAbove = notifyDepositAbove;
        this.contributorTopSize = contributorTopSize;
        this.ledgerPreviewSize = ledgerPreviewSize;
        this.depositPresets = List.copyOf(depositPresets);
        this.withdrawPresets = List.copyOf(withdrawPresets);
        this.guiRows = guiRows;
        this.balanceSlot = balanceSlot;
        this.depositStartSlot = depositStartSlot;
        this.withdrawStartSlot = withdrawStartSlot;
        this.depositAllSlot = depositAllSlot;
        this.withdrawAllSlot = withdrawAllSlot;
        this.backSlot = backSlot;
        this.fillerMaterial = fillerMaterial;
        this.balanceMaterial = balanceMaterial;
        this.depositMaterial = depositMaterial;
        this.withdrawMaterial = withdrawMaterial;
        this.depositAllMaterial = depositAllMaterial;
        this.withdrawAllMaterial = withdrawAllMaterial;
        this.backMaterial = backMaterial;
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public double minAmount() {
        return minAmount;
    }

    public double maxDeposit() {
        return maxDeposit;
    }

    public double maxWithdraw() {
        return maxWithdraw;
    }

    public double notifyDepositAbove() {
        return notifyDepositAbove;
    }

    public int contributorTopSize() {
        return contributorTopSize;
    }

    public int ledgerPreviewSize() {
        return ledgerPreviewSize;
    }

    public List<Double> depositPresets() {
        return depositPresets;
    }

    public List<Double> withdrawPresets() {
        return withdrawPresets;
    }

    public int guiRows() {
        return guiRows;
    }

    public int guiSize() {
        return guiRows * 9;
    }

    public int balanceSlot() {
        return balanceSlot;
    }

    public int depositSlot(int index) {
        return depositStartSlot + index;
    }

    public int depositStartSlot() {
        return depositStartSlot;
    }

    public int withdrawSlot(int index) {
        return withdrawStartSlot + index;
    }

    public int withdrawStartSlot() {
        return withdrawStartSlot;
    }

    public int depositAllSlot() {
        return depositAllSlot;
    }

    public int withdrawAllSlot() {
        return withdrawAllSlot;
    }

    public int backSlot() {
        return backSlot;
    }

    public Material fillerMaterial() {
        return fillerMaterial;
    }

    public Material balanceMaterial() {
        return balanceMaterial;
    }

    public Material depositMaterial() {
        return depositMaterial;
    }

    public Material withdrawMaterial() {
        return withdrawMaterial;
    }

    public Material depositAllMaterial() {
        return depositAllMaterial;
    }

    public Material withdrawAllMaterial() {
        return withdrawAllMaterial;
    }

    public Material backMaterial() {
        return backMaterial;
    }
}
