package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.config.GuiListConfig;
import java.util.concurrent.CompletableFuture;

public final class ClanListDataService {

    private final ClanRepository clanRepository;
    private final GuiListConfig guiListConfig;

    public ClanListDataService(ClanRepository clanRepository, GuiListConfig guiListConfig) {
        this.clanRepository = clanRepository;
        this.guiListConfig = guiListConfig;
    }

    public CompletableFuture<ClanListPage> loadPage(int page) {
        int safePage = Math.max(0, page);
        int pageSize = guiListConfig.pageSize();
        if (pageSize <= 0) {
            return CompletableFuture.completedFuture(new ClanListPage(java.util.List.of(), 0, 0, 0));
        }
        return clanRepository.countClans().thenCompose(totalClans -> {
            int totalPages = totalClans == 0 ? 0 : (int) Math.ceil((double) totalClans / pageSize);
            int clampedPage = totalPages == 0 ? 0 : Math.min(safePage, totalPages - 1);
            int offset = clampedPage * pageSize;
            return clanRepository.findPageEntries(offset, pageSize).thenApply(entries ->
                    new ClanListPage(entries, clampedPage, totalPages, totalClans)
            );
        });
    }
}
