package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ClanMembersDataService {

    private final ClanRepository clanRepository;

    public ClanMembersDataService(ClanRepository clanRepository) {
        this.clanRepository = clanRepository;
    }

    public CompletableFuture<Optional<ClanMembersSnapshot>> load(long clanId) {
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            var clan = clanOptional.get();
            return clanRepository.findMembersByClanId(clan.id())
                    .thenApply(members -> Optional.of(new ClanMembersSnapshot(clan, members)));
        });
    }
}
