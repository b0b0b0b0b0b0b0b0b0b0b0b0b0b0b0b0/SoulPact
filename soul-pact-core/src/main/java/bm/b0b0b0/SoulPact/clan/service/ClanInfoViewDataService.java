package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanInfoViewDataService {

    private final ClanRepository clanRepository;

    public ClanInfoViewDataService(ClanRepository clanRepository) {
        this.clanRepository = clanRepository;
    }

    public CompletableFuture<Optional<ClanInfoViewSnapshot>> load(Player viewer, long clanId) {
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            Clan clan = clanOptional.get();
            return clanRepository.countMembers(clan.id()).thenCombine(
                    clanRepository.findByPlayerId(viewer.getUniqueId()),
                    (memberCount, viewerClanOptional) -> Optional.of(buildSnapshot(clan, memberCount, viewer, viewerClanOptional.orElse(null)))
            );
        });
    }

    private ClanInfoViewSnapshot buildSnapshot(
            Clan clan,
            int memberCount,
            Player viewer,
            Clan viewerClan
    ) {
        if (viewerClan == null) {
            return new ClanInfoViewSnapshot(clan, memberCount, ClanInfoViewSnapshot.ViewerRole.NONE);
        }
        if (viewerClan.id() != clan.id()) {
            return new ClanInfoViewSnapshot(clan, memberCount, ClanInfoViewSnapshot.ViewerRole.FOREIGN);
        }
        if (clan.leaderId().equals(viewer.getUniqueId())) {
            return new ClanInfoViewSnapshot(clan, memberCount, ClanInfoViewSnapshot.ViewerRole.LEADER);
        }
        return new ClanInfoViewSnapshot(clan, memberCount, ClanInfoViewSnapshot.ViewerRole.MEMBER);
    }
}
