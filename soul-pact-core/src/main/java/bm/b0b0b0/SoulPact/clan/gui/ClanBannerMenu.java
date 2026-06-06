package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.core.config.GuiClanBannerConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class ClanBannerMenu implements InventoryHolder {

    private final GuiClanBannerConfig config;
    private final Inventory inventory;
    private final long clanId;
    private final String clanTag;
    private ItemStack workingBanner;
    private DyeColor patternColor;
    private final boolean clanLeader;
    private boolean standardOut;
    private boolean canDepositStandard;

    public ClanBannerMenu(
            GuiClanBannerConfig config,
            ClanBannerMenuPopulator populator,
            MessageService messageService,
            Player player,
            long clanId,
            String clanTag,
            ItemStack workingBanner,
            DyeColor patternColor,
            boolean clanLeader,
            boolean standardOut,
            boolean canDepositStandard
    ) {
        this.config = config;
        this.clanId = clanId;
        this.clanTag = clanTag;
        this.workingBanner = workingBanner;
        this.patternColor = patternColor;
        this.clanLeader = clanLeader;
        this.standardOut = standardOut;
        this.canDepositStandard = canDepositStandard;
        this.inventory = Bukkit.createInventory(
                this,
                config.size(),
                messageService.component(player, "clan.gui.banner.title", java.util.Map.of("tag", clanTag))
        );
        populator.populate(inventory, player, this);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiClanBannerConfig config() {
        return config;
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

    public void setWorkingBanner(ItemStack workingBanner) {
        this.workingBanner = workingBanner;
    }

    public DyeColor patternColor() {
        return patternColor;
    }

    public void setPatternColor(DyeColor patternColor) {
        this.patternColor = patternColor;
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

    public void setStandardOut(boolean standardOut) {
        this.standardOut = standardOut;
    }

    public void setCanDepositStandard(boolean canDepositStandard) {
        this.canDepositStandard = canDepositStandard;
    }

    public void refresh(ClanBannerMenuPopulator populator, Player player) {
        populator.populate(inventory, player, this);
    }
}
