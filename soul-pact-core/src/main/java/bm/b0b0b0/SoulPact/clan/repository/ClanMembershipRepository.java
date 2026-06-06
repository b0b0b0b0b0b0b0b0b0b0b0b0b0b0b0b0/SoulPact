package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanInvite;
import bm.b0b0b0.SoulPact.clan.model.ClanJoinRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanMembershipRepository {

    CompletableFuture<Optional<ClanInvite>> findInviteById(long inviteId);

    CompletableFuture<List<ClanInvite>> findInvitesByPlayerId(UUID playerId);

    CompletableFuture<Optional<ClanInvite>> findInvite(long clanId, UUID playerId);

    CompletableFuture<ClanInvite> createInvite(long clanId, UUID playerId, UUID inviterId, long createdAt);

    CompletableFuture<Boolean> deleteInvite(long inviteId);

    CompletableFuture<Optional<ClanJoinRequest>> findJoinRequestById(long requestId);

    CompletableFuture<List<ClanJoinRequest>> findJoinRequestsByClanId(long clanId);

    CompletableFuture<List<ClanJoinRequest>> findJoinRequestsByLeaderId(UUID leaderId);

    CompletableFuture<Optional<ClanJoinRequest>> findJoinRequest(long clanId, UUID playerId);

    CompletableFuture<ClanJoinRequest> createJoinRequest(long clanId, UUID playerId, long createdAt);

    CompletableFuture<Boolean> deleteJoinRequest(long requestId);

    CompletableFuture<Integer> deleteInvitesByPlayerId(UUID playerId);

    CompletableFuture<Integer> deleteJoinRequestsByPlayerId(UUID playerId);

    CompletableFuture<Boolean> isJoinBlocked(long clanId, UUID playerId);

    CompletableFuture<Boolean> createJoinBlock(long clanId, UUID playerId, long blockedAt);

    CompletableFuture<Integer> countJoinRequestsByClanId(long clanId);

    CompletableFuture<List<ClanJoinRequest>> deleteJoinRequestsByClanId(long clanId);
}
