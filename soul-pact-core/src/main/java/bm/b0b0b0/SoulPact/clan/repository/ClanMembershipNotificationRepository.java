package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanMembershipNotification;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanMembershipNotificationRepository {

    CompletableFuture<Void> create(
            UUID playerId,
            String kind,
            long clanId,
            String clanTag,
            String clanName,
            long createdAt
    );

    CompletableFuture<List<ClanMembershipNotification>> findByPlayerId(UUID playerId);

    CompletableFuture<Integer> deleteByPlayerId(UUID playerId);
}
