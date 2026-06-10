package bm.b0b0b0.SoulPact.clanholo.repository;

import bm.b0b0b0.SoulPact.clanholo.model.ClanHologram;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface HologramRepository {

    CompletableFuture<Optional<ClanHologram>> findById(long id);

    CompletableFuture<Optional<ClanHologram>> findByName(long clanId, String name);

    CompletableFuture<List<ClanHologram>> findByClanId(long clanId);

    CompletableFuture<Integer> countByClanId(long clanId);

    CompletableFuture<ClanHologram> create(ClanHologram hologram);

    CompletableFuture<Boolean> delete(long id);

    CompletableFuture<Boolean> deleteByClanId(long clanId);

    CompletableFuture<Boolean> replaceLines(long hologramId, List<String> lines);

    CompletableFuture<List<ClanHologram>> findAll();
}
