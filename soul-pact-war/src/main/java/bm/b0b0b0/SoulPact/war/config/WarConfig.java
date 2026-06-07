package bm.b0b0b0.SoulPact.war.config;

import org.bukkit.boss.BarColor;

public final class WarConfig {

    private final String locale;
    private final String fallbackLocale;
    private final double ransomPercent;
    private final int captureSeconds;
    private final int declareConfirmRows;
    private final int declareConfirmSlot;
    private final int declareDenySlot;
    private final int pendingListRows;
    private final int pendingListPageSize;
    private final int pendingListBackSlot;
    private final int pendingDetailRows;
    private final int pendingAcceptSlot;
    private final int pendingRansomSlot;
    private final int pendingBackSlot;
    private final int hubRows;
    private final int hubEnemyFlagSlot;
    private final int hubPendingSlot;
    private final int hubBackSlot;
    private final BarColor pendingColor;
    private final BarColor activeColor;
    private final BarColor captureDefendingColor;
    private final BarColor captureAttackingColor;

    public WarConfig(
            String locale,
            String fallbackLocale,
            double ransomPercent,
            int captureSeconds,
            int declareConfirmRows,
            int declareConfirmSlot,
            int declareDenySlot,
            int pendingListRows,
            int pendingListPageSize,
            int pendingListBackSlot,
            int pendingDetailRows,
            int pendingAcceptSlot,
            int pendingRansomSlot,
            int pendingBackSlot,
            int hubRows,
            int hubEnemyFlagSlot,
            int hubPendingSlot,
            int hubBackSlot,
            BarColor pendingColor,
            BarColor activeColor,
            BarColor captureDefendingColor,
            BarColor captureAttackingColor
    ) {
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
        this.ransomPercent = ransomPercent;
        this.captureSeconds = captureSeconds;
        this.declareConfirmRows = declareConfirmRows;
        this.declareConfirmSlot = declareConfirmSlot;
        this.declareDenySlot = declareDenySlot;
        this.pendingListRows = pendingListRows;
        this.pendingListPageSize = pendingListPageSize;
        this.pendingListBackSlot = pendingListBackSlot;
        this.pendingDetailRows = pendingDetailRows;
        this.pendingAcceptSlot = pendingAcceptSlot;
        this.pendingRansomSlot = pendingRansomSlot;
        this.pendingBackSlot = pendingBackSlot;
        this.hubRows = hubRows;
        this.hubEnemyFlagSlot = hubEnemyFlagSlot;
        this.hubPendingSlot = hubPendingSlot;
        this.hubBackSlot = hubBackSlot;
        this.pendingColor = pendingColor;
        this.activeColor = activeColor;
        this.captureDefendingColor = captureDefendingColor;
        this.captureAttackingColor = captureAttackingColor;
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public double ransomPercent() {
        return ransomPercent;
    }

    public int captureSeconds() {
        return captureSeconds;
    }

    public int declareConfirmRows() {
        return declareConfirmRows;
    }

    public int declareConfirmSize() {
        return declareConfirmRows * 9;
    }

    public int declareConfirmSlot() {
        return declareConfirmSlot;
    }

    public int declareDenySlot() {
        return declareDenySlot;
    }

    public int pendingListRows() {
        return pendingListRows;
    }

    public int pendingListSize() {
        return pendingListRows * 9;
    }

    public int pendingListPageSize() {
        return pendingListPageSize;
    }

    public int pendingListBackSlot() {
        return pendingListBackSlot;
    }

    public int pendingDetailRows() {
        return pendingDetailRows;
    }

    public int pendingDetailSize() {
        return pendingDetailRows * 9;
    }

    public int pendingAcceptSlot() {
        return pendingAcceptSlot;
    }

    public int pendingRansomSlot() {
        return pendingRansomSlot;
    }

    public int pendingBackSlot() {
        return pendingBackSlot;
    }

    public int hubRows() {
        return hubRows;
    }

    public int hubSize() {
        return hubRows * 9;
    }

    public int hubEnemyFlagSlot() {
        return hubEnemyFlagSlot;
    }

    public int hubPendingSlot() {
        return hubPendingSlot;
    }

    public int hubBackSlot() {
        return hubBackSlot;
    }

    public BarColor pendingColor() {
        return pendingColor;
    }

    public BarColor activeColor() {
        return activeColor;
    }

    public BarColor captureDefendingColor() {
        return captureDefendingColor;
    }

    public BarColor captureAttackingColor() {
        return captureAttackingColor;
    }
}
