package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanMail;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanMailRepository {

    CompletableFuture<ClanMail> send(long clanId, UUID senderId, String senderName, String message, long createdAt);

    CompletableFuture<List<ClanMail>> findPage(long clanId, int offset, int limit);

    CompletableFuture<Integer> countByClanId(long clanId);

    CompletableFuture<Integer> countUnread(long clanId, UUID playerId);

    CompletableFuture<Void> markRead(long clanId, UUID playerId, long readAt);

    CompletableFuture<Integer> clear(long clanId);

    CompletableFuture<Void> trimToLimit(long clanId, int maxStored);
}
