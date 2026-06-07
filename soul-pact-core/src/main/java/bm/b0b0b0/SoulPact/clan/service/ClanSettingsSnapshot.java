package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public final class ClanSettingsSnapshot {

    private final Clan clan;
    private final List<String> roleKeys;
    private final ItemStack bannerItem;

    public ClanSettingsSnapshot(Clan clan, List<String> roleKeys, ItemStack bannerItem) {
        this.clan = clan;
        this.roleKeys = List.copyOf(roleKeys);
        this.bannerItem = bannerItem;
    }

    public Clan clan() {
        return clan;
    }

    public List<String> roleKeys() {
        return roleKeys;
    }

    public ItemStack bannerItem() {
        return bannerItem;
    }
}
