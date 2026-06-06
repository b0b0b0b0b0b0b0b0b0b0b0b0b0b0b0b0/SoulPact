package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.clan.model.ClanMapper;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ClanQueryService {

    private final ClanRepository clanRepository;

    public ClanQueryService(ClanRepository clanRepository) {
        this.clanRepository = clanRepository;
    }

    public CompletableFuture<Optional<ClanSnapshot>> findByTag(String tag) {
        return clanRepository.findByTag(tag).thenApply(optional -> optional.map(ClanMapper::toSnapshot));
    }

    public CompletableFuture<Optional<ClanSnapshot>> findByPlayerId(UUID playerId) {
        return clanRepository.findByPlayerId(playerId).thenApply(optional -> optional.map(ClanMapper::toSnapshot));
    }
}
