package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import java.util.Map;
import java.util.UUID;

public final class ClanMembersPage {

    private final Map<Integer, ClanMember> slotMembers;
    private final int page;
    private final int totalPages;
    private final int totalMembers;

    public ClanMembersPage(
            Map<Integer, ClanMember> slotMembers,
            int page,
            int totalPages,
            int totalMembers
    ) {
        this.slotMembers = Map.copyOf(slotMembers);
        this.page = page;
        this.totalPages = totalPages;
        this.totalMembers = totalMembers;
    }

    public Map<Integer, ClanMember> slotMembers() {
        return slotMembers;
    }

    public int page() {
        return page;
    }

    public int totalPages() {
        return totalPages;
    }

    public int totalMembers() {
        return totalMembers;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean hasNext() {
        return page + 1 < totalPages;
    }

    public UUID memberIdAtSlot(int slot) {
        ClanMember member = slotMembers.get(slot);
        return member == null ? null : member.playerId();
    }
}
