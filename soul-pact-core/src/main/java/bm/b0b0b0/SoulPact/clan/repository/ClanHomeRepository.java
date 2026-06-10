package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanHome;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ClanHomeRepository {

    CompletableFuture<Optional<ClanHome>> create(ClanHome home);

    CompletableFuture<Boolean> delete(long clanId, String name);

    CompletableFuture<Optional<ClanHome>> findByName(long clanId, String name);

    CompletableFuture<List<ClanHome>> findByClanId(long clanId);

    CompletableFuture<Integer> countByClanId(long clanId);
}
