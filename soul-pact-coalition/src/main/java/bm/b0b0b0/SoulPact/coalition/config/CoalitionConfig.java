package bm.b0b0b0.SoulPact.coalition.config;

import org.bukkit.boss.BarColor;

public final class CoalitionConfig {

    private final String locale;
    private final String fallbackLocale;
    private final int maxMembers;
    private final double warSharePercent;
    private final double captureSharePercent;
    private final double poolSharePercent;
    private final int hubRows;
    private final int memberStartSlot;
    private final int inviteSlot;
    private final int leaveSlot;
    private final BarColor declaredColor;
    private final BarColor activeColor;
    private final BarColor captureColor;

    public CoalitionConfig(
            String locale,
            String fallbackLocale,
            int maxMembers,
            double warSharePercent,
            double captureSharePercent,
            double poolSharePercent,
            int hubRows,
            int memberStartSlot,
            int inviteSlot,
            int leaveSlot,
            BarColor declaredColor,
            BarColor activeColor,
            BarColor captureColor
    ) {
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
        this.maxMembers = maxMembers;
        this.warSharePercent = warSharePercent;
        this.captureSharePercent = captureSharePercent;
        this.poolSharePercent = poolSharePercent;
        this.hubRows = hubRows;
        this.memberStartSlot = memberStartSlot;
        this.inviteSlot = inviteSlot;
        this.leaveSlot = leaveSlot;
        this.declaredColor = declaredColor;
        this.activeColor = activeColor;
        this.captureColor = captureColor;
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public int maxMembers() {
        return maxMembers;
    }

    public double warSharePercent() {
        return warSharePercent;
    }

    public double captureSharePercent() {
        return captureSharePercent;
    }

    public double poolSharePercent() {
        return poolSharePercent;
    }

    public int hubRows() {
        return hubRows;
    }

    public int memberStartSlot() {
        return memberStartSlot;
    }

    public int inviteSlot() {
        return inviteSlot;
    }

    public int leaveSlot() {
        return leaveSlot;
    }

    public BarColor declaredColor() {
        return declaredColor;
    }

    public BarColor activeColor() {
        return activeColor;
    }

    public BarColor captureColor() {
        return captureColor;
    }
}
