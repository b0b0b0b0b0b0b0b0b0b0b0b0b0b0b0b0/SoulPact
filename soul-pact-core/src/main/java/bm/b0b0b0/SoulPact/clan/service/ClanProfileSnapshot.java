package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public final class ClanProfileSnapshot {

    private final Clan clan;
    private final List<ClanMember> members;
    private final int pendingRequestCount;
    private final boolean requestsView;
    private final ItemStack bannerItem;
    private final boolean viewerIsLeader;

    public ClanProfileSnapshot(
            Clan clan,
            List<ClanMember> members,
            int pendingRequestCount,
            boolean requestsView,
            ItemStack bannerItem,
            boolean viewerIsLeader
    ) {
        this.clan = clan;
        this.members = List.copyOf(members);
        this.pendingRequestCount = pendingRequestCount;
        this.requestsView = requestsView;
        this.bannerItem = bannerItem;
        this.viewerIsLeader = viewerIsLeader;
    }

    public Clan clan() {
        return clan;
    }

    public List<ClanMember> members() {
        return members;
    }

    public int pendingRequestCount() {
        return pendingRequestCount;
    }

    public boolean requestsView() {
        return requestsView;
    }

    public ItemStack bannerItem() {
        return bannerItem;
    }

    public boolean viewerIsLeader() {
        return viewerIsLeader;
    }
}
