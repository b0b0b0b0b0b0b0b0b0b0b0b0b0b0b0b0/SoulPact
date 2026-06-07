package bm.b0b0b0.SoulPact.bank.repository;

import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryContributorSnapshot;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryEntrySnapshot;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClanTreasuryRepository {

    Optional<TreasuryState> findState(long clanId);

    double ensureAccount(long clanId);

    boolean isLocked(long clanId);

    boolean setLocked(long clanId, boolean locked);

    TreasuryMutationResult applyMutation(TreasuryMutation mutation);

    List<ClanTreasuryEntrySnapshot> recentEntries(long clanId, int limit);

    List<ClanTreasuryContributorSnapshot> topContributors(long clanId, int limit);

    record TreasuryState(long clanId, double balance, boolean locked) {
    }

    record TreasuryMutation(
            long clanId,
            UUID actorId,
            String entryType,
            double amountDelta,
            String note,
            boolean trackContribution,
            long createdAt
    ) {
    }

    record TreasuryMutationResult(boolean success, double balanceAfter) {
    }
}
