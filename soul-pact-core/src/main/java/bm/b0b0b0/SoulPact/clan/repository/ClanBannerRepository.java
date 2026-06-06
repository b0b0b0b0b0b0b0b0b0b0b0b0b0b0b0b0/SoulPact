package bm.b0b0b0.SoulPact.clan.repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ClanBannerRepository {

    CompletableFuture<Optional<String>> findDataByClanId(long clanId);

    CompletableFuture<Boolean> updateData(long clanId, String bannerData);

    CompletableFuture<Boolean> isStandardIssued(long clanId);

    CompletableFuture<Boolean> markStandardIssued(long clanId);
}
