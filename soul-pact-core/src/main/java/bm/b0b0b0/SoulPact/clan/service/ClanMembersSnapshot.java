package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public final class ClanMembersSnapshot {

    private final Clan clan;
    private final List<ClanMember> members;
    private final ItemStack bannerItem;
    private final boolean viewerIsLeader;

    public ClanMembersSnapshot(
            Clan clan,
            List<ClanMember> members,
            ItemStack bannerItem,
            boolean viewerIsLeader
    ) {
        this.clan = clan;
        this.members = List.copyOf(members);
        this.bannerItem = bannerItem;
        this.viewerIsLeader = viewerIsLeader;
    }

    public Clan clan() {
        return clan;
    }

    public List<ClanMember> members() {
        return members;
    }

    public ItemStack bannerItem() {
        return bannerItem;
    }

    public boolean viewerIsLeader() {
        return viewerIsLeader;
    }
}
