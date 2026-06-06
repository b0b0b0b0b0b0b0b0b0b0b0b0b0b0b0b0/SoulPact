package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanMembershipHistoryEntry;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanMembershipHistoryRepository {

    CompletableFuture<Void> record(
            UUID playerId,
            long clanId,
            String clanTag,
            String clanName,
            String role,
            long joinedAt,
            long leftAt,
            String reason
    );

    CompletableFuture<List<ClanMembershipHistoryEntry>> findByPlayerId(UUID playerId, int limit);
}
