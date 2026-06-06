package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.banner.ClanBannerPatternCatalog;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardService;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

public final class ClanBannerDataService {

    private final ClanRepository clanRepository;
    private final ClanBannerService clanBannerService;
    private final ClanStandardService clanStandardService;

    public ClanBannerDataService(
            ClanRepository clanRepository,
            ClanBannerService clanBannerService,
            ClanStandardService clanStandardService
    ) {
        this.clanRepository = clanRepository;
        this.clanBannerService = clanBannerService;
        this.clanStandardService = clanStandardService;
    }

    public CompletableFuture<Optional<ClanBannerSnapshot>> load(Player player) {
        return clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            var clan = clanOptional.get();
            return clanBannerService.loadBanner(clan.id()).thenApply(banner -> {
                boolean standardOut = clanStandardService.isStandardOut(clan.id());
                boolean canDepositStandard = clanStandardService.canDepositStandard(player, clan.id());
                return Optional.of(new ClanBannerSnapshot(
                        clan.id(),
                        clan.tag(),
                        banner,
                        ClanBannerPatternCatalog.patternColors().getFirst(),
                        clanBannerService.canEdit(player, clan),
                        standardOut,
                        canDepositStandard
                ));
            });
        });
    }
}
