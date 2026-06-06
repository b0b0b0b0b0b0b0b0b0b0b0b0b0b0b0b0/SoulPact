package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanMembersDataService {

    private final ClanRepository clanRepository;
    private final ClanBannerService clanBannerService;

    public ClanMembersDataService(ClanRepository clanRepository, ClanBannerService clanBannerService) {
        this.clanRepository = clanRepository;
        this.clanBannerService = clanBannerService;
    }

    public CompletableFuture<Optional<ClanMembersSnapshot>> load(Player viewer, long clanId) {
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            var clan = clanOptional.get();
            return clanRepository.findMembersByClanId(clan.id()).thenCombine(
                    clanBannerService.loadBanner(clan.id()),
                    (members, banner) -> Optional.of(new ClanMembersSnapshot(
                            clan,
                            members,
                            banner,
                            clan.leaderId().equals(viewer.getUniqueId())
                    ))
            );
        });
    }
}
