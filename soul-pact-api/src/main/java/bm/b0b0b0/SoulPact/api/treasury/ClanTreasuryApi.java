package bm.b0b0b0.SoulPact.api.treasury;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public interface ClanTreasuryApi {

    CompletableFuture<Double> balance(long clanId);

    CompletableFuture<Boolean> isLocked(long clanId);

    CompletableFuture<Boolean> setLocked(long clanId, boolean locked);

    CompletableFuture<TreasuryOperationResult> deposit(Player player, long clanId, double amount);

    CompletableFuture<TreasuryOperationResult> withdraw(Player player, long clanId, double amount);

    CompletableFuture<TreasuryOperationResult> charge(long clanId, java.util.UUID actorId, double amount, String note);

    CompletableFuture<TreasuryOperationResult> transferAll(long fromClanId, long toClanId, String note);

    CompletableFuture<List<ClanTreasuryEntrySnapshot>> recentEntries(long clanId, int limit);

    CompletableFuture<List<ClanTreasuryContributorSnapshot>> topContributors(long clanId, int limit);
}
