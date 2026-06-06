package bm.b0b0b0.SoulPact.clan.service;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public final class ClanBannerSnapshot {

    private final long clanId;
    private final String clanTag;
    private final ItemStack workingBanner;
    private final DyeColor patternColor;
    private final boolean clanLeader;
    private final boolean standardOut;
    private final boolean canDepositStandard;

    public ClanBannerSnapshot(
            long clanId,
            String clanTag,
            ItemStack workingBanner,
            DyeColor patternColor,
            boolean clanLeader,
            boolean standardOut,
            boolean canDepositStandard
    ) {
        this.clanId = clanId;
        this.clanTag = clanTag;
        this.workingBanner = workingBanner;
        this.patternColor = patternColor;
        this.clanLeader = clanLeader;
        this.standardOut = standardOut;
        this.canDepositStandard = canDepositStandard;
    }

    public long clanId() {
        return clanId;
    }

    public String clanTag() {
        return clanTag;
    }

    public ItemStack workingBanner() {
        return workingBanner;
    }

    public DyeColor patternColor() {
        return patternColor;
    }

    public boolean clanLeader() {
        return clanLeader;
    }

    public boolean standardOut() {
        return standardOut;
    }

    public boolean canDepositStandard() {
        return canDepositStandard;
    }
}
