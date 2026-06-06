package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import java.util.ArrayList;
import java.util.List;

public final class ClanExtensionsPage {

    private final List<SoulPactExtension> extensions;
    private final int page;
    private final int totalPages;
    private final int totalExtensions;

    public ClanExtensionsPage(List<SoulPactExtension> extensions, int page, int totalPages, int totalExtensions) {
        this.extensions = List.copyOf(extensions);
        this.page = page;
        this.totalPages = totalPages;
        this.totalExtensions = totalExtensions;
    }

    public List<SoulPactExtension> extensions() {
        return extensions;
    }

    public int page() {
        return page;
    }

    public int totalPages() {
        return totalPages;
    }

    public int totalExtensions() {
        return totalExtensions;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean hasNext() {
        return page + 1 < totalPages;
    }
}
