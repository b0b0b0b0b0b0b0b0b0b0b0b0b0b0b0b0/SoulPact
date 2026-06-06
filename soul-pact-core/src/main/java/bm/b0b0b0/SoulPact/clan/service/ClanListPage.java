package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanListEntry;
import java.util.List;

public final class ClanListPage {

    private final List<ClanListEntry> entries;
    private final int page;
    private final int totalPages;
    private final int totalClans;

    public ClanListPage(List<ClanListEntry> entries, int page, int totalPages, int totalClans) {
        this.entries = List.copyOf(entries);
        this.page = page;
        this.totalPages = totalPages;
        this.totalClans = totalClans;
    }

    public List<ClanListEntry> entries() {
        return entries;
    }

    public int page() {
        return page;
    }

    public int totalPages() {
        return totalPages;
    }

    public int totalClans() {
        return totalClans;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean hasNext() {
        return page + 1 < totalPages;
    }
}
