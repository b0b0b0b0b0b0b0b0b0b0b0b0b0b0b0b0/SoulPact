package bm.b0b0b0.SoulPact.leaderboard.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.leaderboard.config.LeaderboardConfig;
import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import bm.b0b0b0.SoulPact.leaderboard.render.BoardRenderService;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public final class BoardUpdateService {

    private final SoulPactApi api;
    private final BoardCatalog catalog;
    private final StandingsCache standingsCache;
    private final BoardRenderService renderService;
    private final Supplier<LeaderboardConfig> configSupplier;
    private final AtomicBoolean updating = new AtomicBoolean();
    private final AtomicLong lastEventUpdateMillis = new AtomicLong();

    public BoardUpdateService(
            SoulPactApi api,
            BoardCatalog catalog,
            StandingsCache standingsCache,
            BoardRenderService renderService,
            Supplier<LeaderboardConfig> configSupplier
    ) {
        this.api = api;
        this.catalog = catalog;
        this.standingsCache = standingsCache;
        this.renderService = renderService;
        this.configSupplier = configSupplier;
    }

    public void updateAll() {
        if (catalog.isEmpty() || !updating.compareAndSet(false, true)) {
            return;
        }
        api.scheduler().runAsync(() -> {
            try {
                standingsCache.refresh(EnumSet.copyOf(catalog.usedStatistics()), configSupplier.get().topSize());
            } finally {
                api.scheduler().runSync(this::renderAllAndRelease);
            }
        });
    }

    public void requestEventUpdate() {
        LeaderboardConfig config = configSupplier.get();
        if (!config.eventUpdates()) {
            return;
        }
        long now = System.currentTimeMillis();
        long last = lastEventUpdateMillis.get();
        if (now - last < config.eventDebounceSeconds() * 1000L) {
            return;
        }
        if (lastEventUpdateMillis.compareAndSet(last, now)) {
            updateAll();
        }
    }

    private void renderAllAndRelease() {
        try {
            for (Board board : catalog.all()) {
                renderService.render(board);
            }
        } finally {
            updating.set(false);
        }
    }
}
